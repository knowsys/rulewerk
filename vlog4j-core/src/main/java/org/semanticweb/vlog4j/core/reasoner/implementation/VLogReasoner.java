package org.semanticweb.vlog4j.core.reasoner.implementation;

import java.io.IOException;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.reasoner.AcyclicityNotion;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.CyclicityResult;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.MaterialisationState;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.ReasonerState;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.MaterializationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.CyclicCheckResult;

/*
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

public class VLogReasoner implements Reasoner {

	private static Logger LOGGER = LoggerFactory.getLogger(VLogReasoner.class);

	private final KnowledgeBase knowledgeBase;

	private final VLog vLog = new VLog();
	private ReasonerState reasonerState = ReasonerState.BEFORE_LOADING;
	private MaterialisationState materialisationState = MaterialisationState.INCOMPLETE;

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
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.knowledgeBase;
	}

	@Override
	public void setAlgorithm(final Algorithm algorithm) {
		Validate.notNull(algorithm, "Algorithm cannot be null!");
		this.algorithm = algorithm;
		if (this.reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			LOGGER.warn("Setting algorithm on a closed reasoner.");
		}
	}

	@Override
	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public void setReasoningTimeout(Integer seconds) {
		if (seconds != null) {
			Validate.isTrue(seconds > 0, "Only strictly positive timeout period alowed!", seconds);
		}
		this.timeoutAfterSeconds = seconds;
		if (this.reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			LOGGER.warn("Setting timeout on a closed reasoner.");
		}
	}

	@Override
	public Integer getReasoningTimeout() {
		return this.timeoutAfterSeconds;
	}

	@Override
	public void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) throws ReasonerStateException {
		Validate.notNull(ruleRewritingStrategy, "Rewrite strategy cannot be null!");
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"Rules cannot be re-writen after the reasoner has been loaded! Call reset() to undo loading and reasoning.");
		}
		this.ruleRewriteStrategy = ruleRewritingStrategy;
		LOGGER.warn("Setting rule rewrite strategy on a closed reasoner.");
	}

	@Override
	public RuleRewriteStrategy getRuleRewriteStrategy() {
		return this.ruleRewriteStrategy;
	}

	@Override
	public void load()
			throws EdbIdbSeparationException, IOException, IncompatiblePredicateArityException, ReasonerStateException {
		if (this.reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			throw new ReasonerStateException(this.reasonerState, "Loading is not allowed after closing.");
		}
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			// TODO check if this is correct.
			LOGGER.warn("This method call is ineffective: the Reasoner has already been loaded.");
		} else {
			validateEdbIdbSeparation();

			this.reasonerState = ReasonerState.AFTER_LOADING;

			if (!this.knowledgeBase.hasFacts()) {
				LOGGER.warn("No facts have been provided.");
			}

			try {
				this.vLog.start(generateDataSourcesConfig(), false);
			} catch (final AlreadyStartedException e) {
				throw new RuntimeException("Inconsistent reasoner state.", e);
			} catch (final EDBConfigurationException e) {
				throw new RuntimeException("Invalid data sources configuration.", e);
			}

			validateDataSourcePredicateArities();

			loadInMemoryFacts();

			if (this.knowledgeBase.getRules().isEmpty()) {
				LOGGER.warn("No rules have been provided for reasoning.");
			} else {
				loadRules();
			}

			setLogLevel(this.internalLogLevel);
		}
	}

	String generateDataSourcesConfig() {
		final StringBuilder configStringBuilder = new StringBuilder();
		int dataSourceIndex = 0;
		for (final Predicate predicate : this.knowledgeBase.getDataSourceForPredicate().keySet()) {
			final DataSource dataSource = this.knowledgeBase.getDataSourceForPredicate().get(predicate);
			try (final Formatter formatter = new Formatter(configStringBuilder);) {
				formatter.format(dataSource.toConfigString(), dataSourceIndex,
						ModelToVLogConverter.toVLogPredicate(predicate));
			}
			dataSourceIndex++;
		}
		return configStringBuilder.toString();
	}

	private void validateEdbIdbSeparation() throws EdbIdbSeparationException {
		final Set<Predicate> edbPredicates = this.knowledgeBase.getEdbPredicates();
		final Set<Predicate> idbPredicates = this.knowledgeBase.getIdbPredicates();
		final Set<Predicate> intersection = new HashSet<>(edbPredicates);
		intersection.retainAll(idbPredicates);
		if (!intersection.isEmpty()) {
			throw new EdbIdbSeparationException(intersection);
		}
	}

	private void validateDataSourcePredicateArities() throws IncompatiblePredicateArityException {
		final Map<Predicate, DataSource> dataSourceForPredicate = this.knowledgeBase.getDataSourceForPredicate();
		for (final Predicate predicate : dataSourceForPredicate.keySet()) {
			final int dataSourcePredicateArity;
			try {
				dataSourcePredicateArity = this.vLog.getPredicateArity(ModelToVLogConverter.toVLogPredicate(predicate));
			} catch (final NotStartedException e) {
				throw new RuntimeException("Inconsistent reasoner state.", e);
			}
			if (dataSourcePredicateArity == -1) {
				LOGGER.warn("Data source {} for predicate {} is empty: ", dataSourceForPredicate.get(predicate),
						predicate);
			} else if (predicate.getArity() != dataSourcePredicateArity) {
				throw new IncompatiblePredicateArityException(predicate, dataSourcePredicateArity,
						dataSourceForPredicate.get(predicate));
			}
		}

	}

	@Override
	public boolean reason()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		switch (this.reasonerState) {
		case BEFORE_LOADING:
			load();
			runChase();
			break;
		case AFTER_LOADING:
			// TODO check if changes occurred in the KB. If yes, only runChase(); otherwise,
			// reset and reload.
			runChase();
			break;
		case AFTER_REASONING:
			// TODO check if changes occurred in the KB. If yes, reset, reload, and run
			// chase. If not, do nothing.
			resetReasoner();
			load();
			runChase();
			break;
		case AFTER_CLOSING:
			throw new ReasonerStateException(this.reasonerState, "Reasoning is not allowed after closing.");
		}
		return this.reasoningCompleted;
	}

	private void runChase() {
		this.reasonerState = ReasonerState.AFTER_REASONING;

		final boolean skolemChase = this.algorithm == Algorithm.SKOLEM_CHASE;
		try {
			if (this.timeoutAfterSeconds == null) {
				this.vLog.materialize(skolemChase);
				this.reasoningCompleted = true;
			} else {
				this.reasoningCompleted = this.vLog.materialize(skolemChase, this.timeoutAfterSeconds);
			}
			this.materialisationState = this.reasoningCompleted ? MaterialisationState.COMPLETE
					: MaterialisationState.INCOMPLETE;

		} catch (final NotStartedException e) {
			throw new RuntimeException("Inconsistent reasoner state.", e);
		} catch (final MaterializationException e) {
			throw new RuntimeException(
					"Knowledge base incompatible with stratified negation: either the Rules are not stratifiable, or the variables in negated atom cannot be bound.",
					e);
		}
	}

	@Override
	public QueryResultIterator answerQuery(PositiveLiteral query, boolean includeBlanks) throws ReasonerStateException {
		final boolean filterBlanks = !includeBlanks;
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		} else if (this.reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not allowed after closing.");
		}
		Validate.notNull(query, "Query atom must not be null!");

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(query);

		TermQueryResultIterator stringQueryResultIterator;
		try {
			stringQueryResultIterator = this.vLog.query(vLogAtom, true, filterBlanks);
		} catch (final NotStartedException e) {
			throw new RuntimeException("Inconsistent reasoner state.", e);
		}

		return new QueryResultIterator(stringQueryResultIterator, this.materialisationState);
	}

	@Override
	public MaterialisationState exportQueryAnswersToCsv(final PositiveLiteral query, final String csvFilePath,
			final boolean includeBlanks) throws ReasonerStateException, IOException {
		final boolean filterBlanks = !includeBlanks;
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		} else if (this.reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not allowed after closing.");
		}
		Validate.notNull(query, "Query atom must not be null!");
		Validate.notNull(csvFilePath, "File to export query answer to must not be null!");
		Validate.isTrue(csvFilePath.endsWith(".csv"), "Expected .csv extension for file [%s]!", csvFilePath);

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(query);
		try {
			this.vLog.writeQueryResultsToCsv(vLogAtom, csvFilePath, filterBlanks);
		} catch (final NotStartedException e) {
			throw new RuntimeException("Inconsistent reasoner state.", e);
		}
		return this.materialisationState;
	}

	@Override
	public void resetReasoner() throws ReasonerStateException {
		// TODO what should happen to the KB?
		if (this.reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			throw new ReasonerStateException(this.reasonerState, "Resetting is not allowed after closing.");
		}
		this.reasonerState = ReasonerState.BEFORE_LOADING;
		this.vLog.stop();
		LOGGER.warn(
				"Reasoner has been reset. All inferences computed during reasoning have been discarded. More data and rules can be added after resetting. The reasoner needs to be loaded again to perform querying and reasoning.");
	}

	@Override
	public void close() {
		this.reasonerState = ReasonerState.AFTER_CLOSING;
		this.knowledgeBase.deleteListener(this);
		this.vLog.stop();
	}

	private void loadInMemoryFacts() {
		final Map<Predicate, Set<PositiveLiteral>> factsForPredicate = this.knowledgeBase.getFactsForPredicate();
		for (final Predicate predicate : factsForPredicate.keySet()) {
			final Set<PositiveLiteral> facts = factsForPredicate.get(predicate);

			final String vLogPredicate = ModelToVLogConverter.toVLogPredicate(predicate);
			final String[][] tuplesForPredicate = ModelToVLogConverter.toVLogFactTuples(facts);
			try {
				this.vLog.addData(vLogPredicate, tuplesForPredicate);
			} catch (final EDBConfigurationException e) {
				throw new RuntimeException("Invalid data sources configuration.", e);
			}
		}
	}

	private void loadRules() {
		final karmaresearch.vlog.Rule[] vLogRuleArray = ModelToVLogConverter
				.toVLogRuleArray(this.knowledgeBase.getRules());
		final karmaresearch.vlog.VLog.RuleRewriteStrategy vLogRuleRewriteStrategy = ModelToVLogConverter
				.toVLogRuleRewriteStrategy(this.ruleRewriteStrategy);
		try {
			this.vLog.setRules(vLogRuleArray, vLogRuleRewriteStrategy);
		} catch (final NotStartedException e) {
			throw new RuntimeException("Inconsistent reasoner state.", e);
		}
	}

	@Override
	public void setLogLevel(LogLevel logLevel) throws ReasonerStateException {
		if (this.reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			throw new ReasonerStateException(this.reasonerState, "Setting log level is not allowed after closing.");
		}
		Validate.notNull(logLevel, "Log level cannot be null!");
		this.internalLogLevel = logLevel;
		this.vLog.setLogLevel(ModelToVLogConverter.toVLogLogLevel(this.internalLogLevel));
	}

	@Override
	public LogLevel getLogLevel() {
		return this.internalLogLevel;
	}

	@Override
	public void setLogFile(String filePath) throws ReasonerStateException {
		if (this.reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			throw new ReasonerStateException(this.reasonerState, "Setting log file is not allowed after closing.");
		}
		this.vLog.setLogFile(filePath);
	}

	@Override
	public boolean isJA() throws ReasonerStateException, NotStartedException {
		return checkAcyclicity(AcyclicityNotion.JA);
	}

	@Override
	public boolean isRJA() throws ReasonerStateException, NotStartedException {
		return checkAcyclicity(AcyclicityNotion.RJA);
	}

	@Override
	public boolean isMFA() throws ReasonerStateException, NotStartedException {
		return checkAcyclicity(AcyclicityNotion.MFA);
	}

	@Override
	public boolean isRMFA() throws ReasonerStateException, NotStartedException {
		return checkAcyclicity(AcyclicityNotion.RMFA);
	}

	@Override
	public boolean isMFC() throws ReasonerStateException, NotStartedException {
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"checking rules acyclicity is not allowed before loading!");
		}

		final CyclicCheckResult checkCyclic = this.vLog.checkCyclic("MFC");
		if (checkCyclic.equals(CyclicCheckResult.CYCLIC)) {
			return true;
		}
		return false;
	}

	private boolean checkAcyclicity(final AcyclicityNotion acyclNotion)
			throws ReasonerStateException, NotStartedException {
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"checking rules acyclicity is not allowed before loading!");
		}

		final CyclicCheckResult checkCyclic = this.vLog.checkCyclic(acyclNotion.name());
		if (checkCyclic.equals(CyclicCheckResult.NON_CYCLIC)) {
			return true;
		}
		return false;
	}

	@Override
	public CyclicityResult checkForCycles() throws ReasonerStateException, NotStartedException {
		final boolean acyclic = isJA() || isRJA() || isMFA() || isRMFA();
		if (acyclic) {
			return CyclicityResult.ACYCLIC;
		} else {
			final boolean cyclic = isMFC();
			if (cyclic) {
				return CyclicityResult.CYCLIC;
			}
			return CyclicityResult.UNDETERMINED;
		}
	}

	@Override
	public void onFactsAdded(Set<Fact> factsAdded) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDataSourceDeclarationsAdded(Set<DataSourceDeclaration> dataSourceDeclarationsAdded) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRulesAdded(Set<Rule> rulesAdded) {
		// TODO Auto-generated method stub
		
	}

}
