package org.semanticweb.rulewerk.reasoner.vlog;

/*
 * #%L
 * Rulewerk VLog Reasoner Support
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.rulewerk.core.exceptions.ReasonerStateException;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.AcyclicityNotion;
import org.semanticweb.rulewerk.core.reasoner.Algorithm;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.CyclicityResult;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.QueryAnswerCount;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.ReasonerState;
import org.semanticweb.rulewerk.core.reasoner.RuleRewriteStrategy;
import org.semanticweb.rulewerk.core.reasoner.implementation.EmptyQueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.implementation.QueryAnswerCountImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.MaterializationException;
import karmaresearch.vlog.NonExistingPredicateException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.CyclicCheckResult;

/**
 * Reasoner implementation using the VLog backend.
 *
 *
 *
 * @author Markus Kroetzsch
 *
 */
public class VLogReasoner implements Reasoner {
	private static Logger LOGGER = LoggerFactory.getLogger(VLogReasoner.class);

	final KnowledgeBase knowledgeBase;
	final VLog vLog = new VLog();

	private ReasonerState reasonerState = ReasonerState.KB_NOT_LOADED;
	private Correctness correctness = Correctness.SOUND_BUT_INCOMPLETE;

	private LogLevel internalLogLevel = LogLevel.WARNING;
	private Algorithm algorithm = Algorithm.RESTRICTED_CHASE;
	private Integer timeoutAfterSeconds;
	private RuleRewriteStrategy ruleRewriteStrategy = RuleRewriteStrategy.NONE;

	/**
	 * Holds the state of the reasoning result. Has value {@code true} if reasoning
	 * has completed, {@code false} if it has been interrupted.
	 */
	private boolean reasoningCompleted;

	public VLogReasoner(KnowledgeBase knowledgeBase) {
		super();
		this.knowledgeBase = knowledgeBase;
		this.knowledgeBase.addListener(this);

		this.setLogLevel(this.internalLogLevel);
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.knowledgeBase;
	}

	@Override
	public void setAlgorithm(final Algorithm algorithm) {
		Validate.notNull(algorithm, "Algorithm cannot be null!");
		this.validateNotClosed();
		this.algorithm = algorithm;
	}

	@Override
	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public void setReasoningTimeout(Integer seconds) {
		this.validateNotClosed();
		if (seconds != null) {
			Validate.isTrue(seconds > 0, "Only strictly positive timeout period allowed!", seconds);
		}
		this.timeoutAfterSeconds = seconds;
	}

	@Override
	public Integer getReasoningTimeout() {
		return this.timeoutAfterSeconds;
	}

	@Override
	public void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) {
		this.validateNotClosed();
		Validate.notNull(ruleRewritingStrategy, "Rewrite strategy cannot be null!");
		this.ruleRewriteStrategy = ruleRewritingStrategy;
	}

	@Override
	public RuleRewriteStrategy getRuleRewriteStrategy() {
		return this.ruleRewriteStrategy;
	}

	@Override
	public Correctness getCorrectness() {
		return this.correctness;
	}

	/*
	 * TODO Due to automatic predicate renaming, it can happen that an EDB predicate
	 * cannot be queried after loading unless reasoning has already been invoked
	 * (since the auxiliary rule that imports the EDB facts to the "real" predicate
	 * must be used). This issue could be weakened by rewriting queries to
	 * (single-source) EDB predicates internally when in such a state.
	 */
	void load() throws IOException {
		this.validateNotClosed();

		switch (this.reasonerState) {
		case KB_NOT_LOADED:
			this.loadKnowledgeBase();
			break;
		case KB_LOADED:
		case MATERIALISED:
			// do nothing, all KB is already loaded
			break;
		case KB_CHANGED:
			this.resetReasoner();
			this.loadKnowledgeBase();
		default:
			break;
		}
	}

	void loadKnowledgeBase() throws IOException {
		LOGGER.info("Started loading knowledge base ...");

		final VLogKnowledgeBase vLogKB = new VLogKnowledgeBase(this.knowledgeBase);

		if (!vLogKB.hasData()) {
			LOGGER.warn("No data statements (facts or datasource declarations) have been provided.");
		}

		// 1. vLog is initialized by loading VLog data sources
		this.loadVLogDataSources(vLogKB);

		// 2. in-memory data is loaded
		this.loadInMemoryDataSources(vLogKB);
		this.validateDataSourcePredicateArities(vLogKB);

		this.loadFacts(vLogKB);

		// 3. rules are loaded
		this.loadRules(vLogKB);

		this.reasonerState = ReasonerState.KB_LOADED;

		// if there are no rules, then materialisation state is complete
		this.correctness = !vLogKB.hasRules() ? Correctness.SOUND_AND_COMPLETE : Correctness.SOUND_BUT_INCOMPLETE;

		LOGGER.info("Finished loading knowledge base.");
	}

	void loadVLogDataSources(final VLogKnowledgeBase vLogKB) throws IOException {
		try {
			this.vLog.start(vLogKB.getVLogDataSourcesConfigurationString(), false);
		} catch (final AlreadyStartedException e) {
			throw new RulewerkRuntimeException("Inconsistent reasoner state.", e);
		} catch (final EDBConfigurationException e) {
			throw new RulewerkRuntimeException("Invalid data sources configuration.", e);
		}
	}

	void loadInMemoryDataSources(final VLogKnowledgeBase vLogKB) {
		vLogKB.getEdbPredicates().forEach((k, v) -> this.loadInMemoryDataSource(v.getDataSource(), k));

		vLogKB.getAliasesForEdbPredicates().forEach((k, v) -> this.loadInMemoryDataSource(k.getDataSource(), v));
	}

	void loadInMemoryDataSource(final DataSource dataSource, final Predicate predicate) {
		if (dataSource instanceof VLogInMemoryDataSource) {

			final VLogInMemoryDataSource inMemoryDataSource = (VLogInMemoryDataSource) dataSource;
			try {
				this.load(predicate, inMemoryDataSource);
			} catch (final EDBConfigurationException e) {
				throw new RulewerkRuntimeException("Invalid data sources configuration!", e);
			}
		}
	}

	void load(final Predicate predicate, final VLogInMemoryDataSource inMemoryDataSource)
			throws EDBConfigurationException {
		final String vLogPredicateName = ModelToVLogConverter.toVLogPredicate(predicate);

		this.vLog.addData(vLogPredicateName, inMemoryDataSource.getData());

		if (LOGGER.isDebugEnabled()) {
			for (final String[] tuple : inMemoryDataSource.getData()) {
				LOGGER.debug("Loaded direct fact {}{}.", vLogPredicateName, Arrays.toString(tuple));
			}
		}
	}

	/**
	 * Checks if the loaded external data sources do in fact contain data of the
	 * correct arity.
	 *
	 * @throws IncompatiblePredicateArityException to indicate a problem
	 *                                             (non-checked exception)
	 */
	void validateDataSourcePredicateArities(final VLogKnowledgeBase vLogKB) throws IncompatiblePredicateArityException {

		vLogKB.getEdbPredicates().forEach((k, v) -> this.validateDataSourcePredicateArity(k, v.getDataSource()));

		vLogKB.getAliasesForEdbPredicates()
				.forEach((k, v) -> this.validateDataSourcePredicateArity(v, k.getDataSource()));
	}

	/**
	 * Checks if the loaded external data for a given source does in fact contain
	 * data of the correct arity for the given predidate.
	 *
	 * @param predicate  the predicate for which data is loaded
	 * @param dataSource the data source used
	 *
	 * @throws IncompatiblePredicateArityException to indicate a problem
	 *                                             (non-checked exception)
	 */
	void validateDataSourcePredicateArity(Predicate predicate, DataSource dataSource)
			throws IncompatiblePredicateArityException {
		if (dataSource == null) {
			return;
		}
		try {
			final int dataSourcePredicateArity = this.vLog
					.getPredicateArity(ModelToVLogConverter.toVLogPredicate(predicate));
			if (dataSourcePredicateArity == -1) {
				LOGGER.warn("Data source {} for predicate {} is empty! ", dataSource, predicate);
			} else if (predicate.getArity() != dataSourcePredicateArity) {
				throw new IncompatiblePredicateArityException(predicate, dataSourcePredicateArity, dataSource);
			}
		} catch (final NotStartedException e) {
			throw new RulewerkRuntimeException("Inconsistent reasoner state!", e);
		}
	}

	void loadFacts(final VLogKnowledgeBase vLogKB) {
		final Map<Predicate, List<Fact>> directEdbFacts = vLogKB.getDirectEdbFacts();

		directEdbFacts.forEach((k, v) -> {
			try {
				final String vLogPredicateName = ModelToVLogConverter.toVLogPredicate(vLogKB.getAlias(k));
				final String[][] vLogPredicateTuples = ModelToVLogConverter.toVLogFactTuples(v);

				this.vLog.addData(vLogPredicateName, vLogPredicateTuples);

				if (LOGGER.isDebugEnabled()) {
					for (final String[] tuple : vLogPredicateTuples) {
						LOGGER.debug("Loaded direct fact {}{}.", vLogPredicateName, Arrays.toString(tuple));
					}
				}
			} catch (final EDBConfigurationException e) {
				throw new RulewerkRuntimeException("Invalid data sources configuration!", e);
			}

		});
	}

	void loadRules(final VLogKnowledgeBase vLogKB) {
		final karmaresearch.vlog.Rule[] vLogRuleArray = ModelToVLogConverter.toVLogRuleArray(vLogKB.getRules());
		final karmaresearch.vlog.VLog.RuleRewriteStrategy vLogRuleRewriteStrategy = ModelToVLogConverter
				.toVLogRuleRewriteStrategy(this.ruleRewriteStrategy);
		try {
			this.vLog.setRules(vLogRuleArray, vLogRuleRewriteStrategy);
			if (LOGGER.isDebugEnabled()) {
				for (final karmaresearch.vlog.Rule rule : vLogRuleArray) {
					LOGGER.debug("Loaded rule {}.", rule.toString());
				}
			}
		} catch (final NotStartedException e) {
			throw new RulewerkRuntimeException("Inconsistent reasoner state!", e);
		}
	}

	@Override
	public boolean reason() throws IOException {
		this.validateNotClosed();

		switch (this.reasonerState) {
		case KB_NOT_LOADED:
			this.load();
			this.runChase();
			break;
		case KB_LOADED:
			this.runChase();
			break;
		case KB_CHANGED:
			this.resetReasoner();
			this.load();
			this.runChase();
			break;
		case MATERIALISED:
			this.runChase();
			break;
		default:
			break;
		}

		return this.reasoningCompleted;
	}

	private void runChase() {
		LOGGER.info("Started materialisation of inferences ...");
		this.reasonerState = ReasonerState.MATERIALISED;

		final boolean skolemChase = this.algorithm == Algorithm.SKOLEM_CHASE;
		try {
			if (this.timeoutAfterSeconds == null) {
				this.vLog.materialize(skolemChase);
				this.reasoningCompleted = true;
			} else {
				this.reasoningCompleted = this.vLog.materialize(skolemChase, this.timeoutAfterSeconds);
			}
		} catch (final NotStartedException e) {
			throw new RulewerkRuntimeException("Inconsistent reasoner state.", e);
		} catch (final MaterializationException e) {
			throw new RulewerkRuntimeException("VLog encounterd an error during materialization: " + e.getMessage(), e);
		}

		if (this.reasoningCompleted) {
			this.correctness = Correctness.SOUND_AND_COMPLETE;
			LOGGER.info("Completed materialisation of inferences.");
		} else {
			this.correctness = Correctness.SOUND_BUT_INCOMPLETE;
			LOGGER.info("Stopped materialisation of inferences (possibly incomplete).");
		}
	}

	@Override
	public QueryResultIterator answerQuery(PositiveLiteral query, boolean includeNulls) {
		this.validateBeforeQuerying(query);

		final boolean filterBlanks = !includeNulls;
		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(query);

		final karmaresearch.vlog.QueryResultIterator queryResultIterator;

		try {
			final int predicateId = this.vLog.getPredicateId(vLogAtom.getPredicate());
			final long[] terms = this.extractTerms(vLogAtom.getTerms());
			queryResultIterator = this.vLog.query(predicateId, terms, true, filterBlanks);
		} catch (final NotStartedException e) {
			throw new RulewerkRuntimeException("Inconsistent reasoner state.", e);
		} catch (final NonExistingPredicateException e1) {
			return this.createEmptyResultIterator(query);
		}

		this.logWarningOnCorrectness(this.correctness);
		return new VLogFastQueryResultIterator(queryResultIterator, this.correctness, this.vLog);
	}

	private QueryResultIterator createEmptyResultIterator(final PositiveLiteral query) {
		final Correctness answerCorrectness = this.getCorrectnessUnknownPredicate(query);
		this.logWarningOnCorrectness(answerCorrectness);
		return new EmptyQueryResultIterator(answerCorrectness);
	}

	private Correctness getCorrectnessUnknownPredicate(final PositiveLiteral query) {
		final Correctness answerCorrectness;
		if (this.reasonerState == ReasonerState.MATERIALISED) {
			this.warnUnknownPredicate(query);
			answerCorrectness = Correctness.SOUND_AND_COMPLETE;
		} else {
			answerCorrectness = Correctness.SOUND_BUT_INCOMPLETE;
		}
		return answerCorrectness;
	}

	private void warnUnknownPredicate(final PositiveLiteral query) {
		LOGGER.warn("Query uses predicate " + query.getPredicate()
				+ " that does not occur in the materialised knowledge base. Answer must be empty!");
	}

	/**
	 * Utility method copied from {@link karmaresearch.vlog.VLog}.
	 * 
	 * @FIXME This should be provided by VLog and made visible to us rather than
	 *        being copied here.
	 * @param terms
	 * @return
	 * @throws NotStartedException
	 */
	private long[] extractTerms(karmaresearch.vlog.Term[] terms) throws NotStartedException {
		ArrayList<String> variables = new ArrayList<>();
		long[] longTerms = new long[terms.length];
		for (int i = 0; i < terms.length; i++) {
			if (terms[i].getTermType() == karmaresearch.vlog.Term.TermType.VARIABLE) {
				boolean found = false;
				for (int j = 0; j < variables.size(); j++) {
					if (variables.get(j).equals(terms[i].getName())) {
						found = true;
						longTerms[i] = -j - 1;
						break;
					}
				}
				if (!found) {
					variables.add(terms[i].getName());
					longTerms[i] = -variables.size();
				}
			} else {
				longTerms[i] = this.vLog.getOrAddConstantId(terms[i].getName());
			}
		}
		return longTerms;
	}

	@Override
	public QueryAnswerCount countQueryAnswers(PositiveLiteral query, boolean includeNulls) {
		this.validateBeforeQuerying(query);

		final boolean filterBlanks = !includeNulls;
		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(query);

		long result;
		try {
			result = this.vLog.querySize(vLogAtom, true, filterBlanks);
		} catch (NotStartedException e) {
			throw new RulewerkRuntimeException("Inconsistent reasoner state.", e);
		} catch (NonExistingPredicateException e) {
			return this.createEmptyResultCount(query);
		}
		this.logWarningOnCorrectness(this.correctness);
		return new QueryAnswerCountImpl(this.correctness, result);
	}

	private QueryAnswerCount createEmptyResultCount(final PositiveLiteral query) {
		final Correctness correctness = this.getCorrectnessUnknownPredicate(query);
		this.logWarningOnCorrectness(correctness);
		return new QueryAnswerCountImpl(correctness, 0);
	}

	@Override
	public Correctness exportQueryAnswersToCsv(final PositiveLiteral query, final String csvFilePath,
			final boolean includeBlanks) throws IOException {
		this.validateBeforeQuerying(query);

		Validate.notNull(csvFilePath, "File to export query answer to must not be null!");
		Validate.isTrue(csvFilePath.endsWith(".csv"), "Expected .csv extension for file [%s]!", csvFilePath);

		final boolean filterBlanks = !includeBlanks;
		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(query);
		try {
			this.vLog.writeQueryResultsToCsv(vLogAtom, csvFilePath, filterBlanks);
		} catch (final NotStartedException e) {
			throw new RulewerkRuntimeException("Inconsistent reasoner state!", e);
		} catch (final NonExistingPredicateException e1) {
			final Correctness correctness = this.getCorrectnessUnknownPredicate(query);
			this.logWarningOnCorrectness(correctness);
			return correctness;
		}
		this.logWarningOnCorrectness(this.correctness);
		return this.correctness;
	}

	private void validateBeforeQuerying(final PositiveLiteral query) {
		this.validateNotClosed();
		if (this.reasonerState == ReasonerState.KB_NOT_LOADED) {
			throw new ReasonerStateException(this.reasonerState,
					"Querying is not allowed before Reasoner#reason() was first called!");
		}
		Validate.notNull(query, "Query atom must not be null!");
	}

	@Override
	public Correctness forEachInference(InferenceAction action) throws IOException {
		this.validateNotClosed();
		if (this.reasonerState == ReasonerState.KB_NOT_LOADED) {
			throw new ReasonerStateException(this.reasonerState,
					"Obtaining inferences is not alowed before reasoner is loaded!");
		}
		final Set<Predicate> toBeQueriedHeadPredicates = this.getKnowledgeBasePredicates();

		for (final Predicate predicate : toBeQueriedHeadPredicates) {
			final PositiveLiteral queryAtom = this.getQueryAtom(predicate);
			final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(queryAtom);
			try (final TermQueryResultIterator answers = this.vLog.query(vLogAtom, true, false)) {
				while (answers.hasNext()) {
					final karmaresearch.vlog.Term[] vlogTerms = answers.next();
					final List<Term> termList = VLogToModelConverter.toTermList(vlogTerms);
					action.accept(predicate, termList);
				}
			} catch (final NotStartedException e) {
				throw new RulewerkRuntimeException("Inconsistent reasoner state.", e);
			} catch (final NonExistingPredicateException e1) {
				throw new RulewerkRuntimeException("Inconsistent knowledge base state.", e1);
			}
		}

		this.logWarningOnCorrectness(this.correctness);
		return this.correctness;
	}

	private void logWarningOnCorrectness(final Correctness correctness) {
		if (correctness != Correctness.SOUND_AND_COMPLETE) {
			LOGGER.warn("Query answers may be {} with respect to the current Knowledge Base!", this.correctness);
		}
	}

	@Override
	public void resetReasoner() {
		this.validateNotClosed();
		this.reasonerState = ReasonerState.KB_NOT_LOADED;
		this.vLog.stop();
		LOGGER.info("Reasoner has been reset. All inferences computed during reasoning have been discarded.");
	}

	@Override
	public void close() {
		if (this.reasonerState == ReasonerState.CLOSED) {
			LOGGER.info("Reasoner is already closed.");
		} else {
			this.reasonerState = ReasonerState.CLOSED;
			this.knowledgeBase.deleteListener(this);
			this.vLog.stop();
			LOGGER.info("Reasoner closed.");
		}
	}

	@Override
	public void setLogLevel(LogLevel logLevel) {
		this.validateNotClosed();
		Validate.notNull(logLevel, "Log level cannot be null!");
		this.internalLogLevel = logLevel;
		this.vLog.setLogLevel(ModelToVLogConverter.toVLogLogLevel(this.internalLogLevel));
	}

	@Override
	public LogLevel getLogLevel() {
		return this.internalLogLevel;
	}

	@Override
	public void setLogFile(String filePath) {
		this.validateNotClosed();
		this.vLog.setLogFile(filePath);
	}

	@Override
	public boolean isJA() {
		return this.checkAcyclicity(AcyclicityNotion.JA);
	}

	@Override
	public boolean isRJA() {
		return this.checkAcyclicity(AcyclicityNotion.RJA);
	}

	@Override
	public boolean isMFA() {
		return this.checkAcyclicity(AcyclicityNotion.MFA);
	}

	@Override
	public boolean isRMFA() {
		return this.checkAcyclicity(AcyclicityNotion.RMFA);
	}

	@Override
	public boolean isMFC() {
		this.validateNotClosed();
		if (this.reasonerState == ReasonerState.KB_NOT_LOADED) {
			throw new ReasonerStateException(this.reasonerState,
					"Checking rules acyclicity is not allowed before loading!");
		}

		CyclicCheckResult checkCyclic;
		try {
			checkCyclic = this.vLog.checkCyclic("MFC");
		} catch (final NotStartedException e) {
			throw new RulewerkRuntimeException(e.getMessage(), e); // should be impossible
		}
		return checkCyclic.equals(CyclicCheckResult.CYCLIC);
	}

	@Override
	public CyclicityResult checkForCycles() {
		final boolean acyclic = this.isJA() || this.isRJA() || this.isMFA() || this.isRMFA();
		if (acyclic) {
			return CyclicityResult.ACYCLIC;
		} else {
			final boolean cyclic = this.isMFC();
			if (cyclic) {
				return CyclicityResult.CYCLIC;
			}
			return CyclicityResult.UNDETERMINED;
		}
	}

	@Override
	public void onStatementsAdded(List<Statement> statementsAdded) {
		// TODO more elaborate materialisation state handling

		this.updateReasonerToKnowledgeBaseChanged();

		// updateCorrectnessOnStatementsAdded(statementsAdded);
		this.updateCorrectnessOnStatementsAdded();
	}

	@Override
	public void onStatementAdded(Statement statementAdded) {
		// TODO more elaborate materialisation state handling

		this.updateReasonerToKnowledgeBaseChanged();

		// updateCorrectnessOnStatementAdded(statementAdded);
		this.updateCorrectnessOnStatementsAdded();
	}

	@Override
	public void onStatementRemoved(Statement statementRemoved) {
		this.updateReasonerToKnowledgeBaseChanged();
		this.updateCorrectnessOnStatementsRemoved();
	}

	@Override
	public void onStatementsRemoved(List<Statement> statementsRemoved) {
		this.updateReasonerToKnowledgeBaseChanged();
		this.updateCorrectnessOnStatementsRemoved();
	}

	Set<Predicate> getKnowledgeBasePredicates() {
		final Set<Predicate> toBeQueriedHeadPredicates = new HashSet<>();
		for (final Rule rule : this.knowledgeBase.getRules()) {
			for (final Literal literal : rule.getHead()) {
				toBeQueriedHeadPredicates.add(literal.getPredicate());
			}
		}
		for (final DataSourceDeclaration dataSourceDeclaration : this.knowledgeBase.getDataSourceDeclarations()) {
			toBeQueriedHeadPredicates.add(dataSourceDeclaration.getPredicate());
		}
		for (final Fact fact : this.knowledgeBase.getFacts()) {
			toBeQueriedHeadPredicates.add(fact.getPredicate());
		}
		return toBeQueriedHeadPredicates;
	}

	private PositiveLiteral getQueryAtom(final Predicate predicate) {
		final List<Term> toBeGroundedVariables = new ArrayList<>(predicate.getArity());
		for (int i = 0; i < predicate.getArity(); i++) {
			toBeGroundedVariables.add(Expressions.makeUniversalVariable("X" + i));
		}
		return Expressions.makePositiveLiteral(predicate, toBeGroundedVariables);
	}

	private boolean checkAcyclicity(final AcyclicityNotion acyclNotion) {
		this.validateNotClosed();
		if (this.reasonerState == ReasonerState.KB_NOT_LOADED) {
			try {
				this.load();
			} catch (final IOException e) { // FIXME: quick fix for https://github.com/knowsys/rulewerk/issues/128
				throw new RulewerkRuntimeException(e);
			}
		}

		CyclicCheckResult checkCyclic;
		try {
			checkCyclic = this.vLog.checkCyclic(acyclNotion.name());
		} catch (final NotStartedException e) {
			throw new RulewerkRuntimeException(e.getMessage(), e); // should be impossible
		}
		return checkCyclic.equals(CyclicCheckResult.NON_CYCLIC);
	}

	private void updateReasonerToKnowledgeBaseChanged() {
		if (this.reasonerState.equals(ReasonerState.KB_LOADED)
				|| this.reasonerState.equals(ReasonerState.MATERIALISED)) {

			this.reasonerState = ReasonerState.KB_CHANGED;
		}
	}

	private void updateCorrectnessOnStatementsAdded() {
		if (this.reasonerState == ReasonerState.KB_CHANGED) {
			// TODO refine
			this.correctness = Correctness.INCORRECT;
		}
	}

	private void updateCorrectnessOnStatementsRemoved() {
		if (this.reasonerState == ReasonerState.KB_CHANGED) {
			// TODO refine
			this.correctness = Correctness.INCORRECT;
		}
	}

	/**
	 * Check if reasoner is closed and throw an exception if it is.
	 *
	 * @throws ReasonerStateException
	 */
	void validateNotClosed() throws ReasonerStateException {
		if (this.reasonerState == ReasonerState.CLOSED) {
			LOGGER.error("Invalid operation requested on a closed reasoner object!");
			throw new ReasonerStateException(this.reasonerState, "Operation not allowed after closing reasoner!");
		}
	}

	ReasonerState getReasonerState() {
		return this.reasonerState;
	}

	void setReasonerState(ReasonerState reasonerState) {
		this.reasonerState = reasonerState;
	}
}
