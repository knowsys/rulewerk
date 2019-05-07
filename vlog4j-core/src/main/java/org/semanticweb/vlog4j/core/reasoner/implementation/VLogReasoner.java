package org.semanticweb.vlog4j.core.reasoner.implementation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.reasoner.AcyclicityNotion;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.CyclicityResult;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.ReasonerState;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
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

	private LogLevel internalLogLevel = LogLevel.WARNING;
	private Algorithm algorithm = Algorithm.RESTRICTED_CHASE;
	private Integer timeoutAfterSeconds;
	private RuleRewriteStrategy ruleRewriteStrategy = RuleRewriteStrategy.NONE;

	private final List<Rule> rules = new ArrayList<>();
	private final Map<Predicate, Set<PositiveLiteral>> factsForPredicate = new HashMap<>();
	private final Map<Predicate, DataSource> dataSourceForPredicate = new HashMap<>();

	public VLogReasoner(KnowledgeBase knowledgeBase) {
		super();
		this.knowledgeBase = knowledgeBase;
		this.knowledgeBase.addObserver(this);
	}

	/**
	 * Holds the state of the reasoning result. Has value {@code true} if reasoning
	 * has completed, {@code false} if it has been interrupted.
	 */
	private boolean reasoningCompleted;

	@Override
	public void setAlgorithm(final Algorithm algorithm) {
		Validate.notNull(algorithm, "Algorithm cannot be null!");
		this.algorithm = algorithm;
		if (reasonerState.equals(ReasonerState.AFTER_CLOSING))
			LOGGER.warn("Setting algorithm on a closed reasoner.");
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
		if (reasonerState.equals(ReasonerState.AFTER_CLOSING))
			LOGGER.warn("Setting timeout on a closed reasoner.");
	}

	@Override
	public Integer getReasoningTimeout() {
		return this.timeoutAfterSeconds;
	}

	@Override
	public void addRules(final Rule... rules) throws ReasonerStateException {
		addRules(Arrays.asList(rules));
	}

	@Override
	public void addRules(final List<Rule> rules) throws ReasonerStateException {
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"Rules cannot be added after the reasoner has been loaded! Call reset() to undo loading and reasoning.");
		}
		Validate.noNullElements(rules, "Null rules are not alowed! The list contains a null at position [%d].");
		this.rules.addAll(new ArrayList<>(rules));
		if (reasonerState.equals(ReasonerState.AFTER_CLOSING))
			LOGGER.warn("Adding rules to a closed reasoner.");
	}

	@Override
	public List<Rule> getRules() {
		return Collections.unmodifiableList(this.rules);
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
	public void addFacts(final PositiveLiteral... facts) throws ReasonerStateException {
		addFacts(Arrays.asList(facts));
	}

	@Override
	public void addFacts(final Collection<PositiveLiteral> facts) throws ReasonerStateException {
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"Facts cannot be added after the reasoner has been loaded! Call reset() to undo loading and reasoning.");
		}
		Validate.noNullElements(facts, "Null facts are not alowed! The list contains a fact at position [%d].");
		for (final PositiveLiteral fact : facts) {
			validateFactTermsAreConstant(fact);

			final Predicate predicate = fact.getPredicate();
			validateNoDataSourceForPredicate(predicate);

			this.factsForPredicate.putIfAbsent(predicate, new HashSet<>());
			this.factsForPredicate.get(predicate).add(fact);
		}
		if (reasonerState.equals(ReasonerState.AFTER_CLOSING))
			LOGGER.warn("Adding facts to a closed reasoner.");
	}

	@Override
	public void addFactsFromDataSource(final Predicate predicate, final DataSource dataSource)
			throws ReasonerStateException {
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"Data sources cannot be added after the reasoner has been loaded! Call reset() to undo loading and reasoning.");
		}
		Validate.notNull(predicate, "Null predicates are not allowed!");
		Validate.notNull(dataSource, "Null dataSources are not allowed!");
		validateNoDataSourceForPredicate(predicate);
		Validate.isTrue(!this.factsForPredicate.containsKey(predicate),
				"Multiple data sources for the same predicate are not allowed! Facts for predicate [%s] alredy added in memory: %s",
				predicate, this.factsForPredicate.get(predicate));

		this.dataSourceForPredicate.put(predicate, dataSource);
		if (reasonerState.equals(ReasonerState.AFTER_CLOSING))
			LOGGER.warn("Adding facts to a closed reasoner.");
	}

	private void validateFactTermsAreConstant(PositiveLiteral fact) {
		final Set<Term> nonConstantTerms = new HashSet<>(fact.getTerms());
		nonConstantTerms.removeAll(fact.getConstants());
		Validate.isTrue(nonConstantTerms.isEmpty(),
				"Only Constant terms alowed in Fact literals! The following non-constant terms [%s] appear for fact [%s]!",
				nonConstantTerms, fact);

	}

	private void validateNoDataSourceForPredicate(final Predicate predicate) {
		Validate.isTrue(!this.dataSourceForPredicate.containsKey(predicate),
				"Multiple data sources for the same predicate are not allowed! Facts for predicate [%s] alredy added from data source: %s",
				predicate, this.dataSourceForPredicate.get(predicate));
	}

	@Override
	public void load()
			throws EdbIdbSeparationException, IOException, IncompatiblePredicateArityException, ReasonerStateException {
		if (reasonerState.equals(ReasonerState.AFTER_CLOSING))
			throw new ReasonerStateException(reasonerState, "Loading is not allowed after closing.");
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			LOGGER.warn("This method call is ineffective: the Reasoner has already been loaded.");
		} else {
			validateEdbIdbSeparation();

			this.reasonerState = ReasonerState.AFTER_LOADING;

			if (this.dataSourceForPredicate.isEmpty() && this.factsForPredicate.isEmpty()) {
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

			if (this.rules.isEmpty()) {
				LOGGER.warn("No rules have been provided for reasoning.");
			} else {
				loadRules();
			}

			setLogLevel(this.internalLogLevel);
		}
	}

	private void validateDataSourcePredicateArities() throws IncompatiblePredicateArityException {
		for (final Predicate predicate : this.dataSourceForPredicate.keySet()) {
			final int dataSourcePredicateArity;
			try {
				dataSourcePredicateArity = this.vLog.getPredicateArity(ModelToVLogConverter.toVLogPredicate(predicate));
			} catch (final NotStartedException e) {
				throw new RuntimeException("Inconsistent reasoner state.", e);
			}
			if (dataSourcePredicateArity == -1) {
				LOGGER.warn("Data source {} for predicate {} is empty: ", this.dataSourceForPredicate.get(predicate),
						predicate);
			} else if (predicate.getArity() != dataSourcePredicateArity) {
				throw new IncompatiblePredicateArityException(predicate, dataSourcePredicateArity,
						this.dataSourceForPredicate.get(predicate));
			}
		}

	}

	@Override
	public boolean reason() throws IOException, ReasonerStateException {
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Reasoning is not allowed before loading!");
		} else if (reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			throw new ReasonerStateException(reasonerState, "Reasoning is not allowed after closing.");
		} else if (this.reasonerState == ReasonerState.AFTER_REASONING) {
			LOGGER.warn(
					"This method call is ineffective: this Reasoner has already reasoned. Successive reason() calls are not supported. Call reset() to undo loading and reasoning and reload to be able to reason again");
		} else {
			this.reasonerState = ReasonerState.AFTER_REASONING;

			final boolean skolemChase = this.algorithm == Algorithm.SKOLEM_CHASE;
			try {
				if (this.timeoutAfterSeconds == null) {
					this.vLog.materialize(skolemChase);
					this.reasoningCompleted = true;
				} else {
					this.reasoningCompleted = this.vLog.materialize(skolemChase, this.timeoutAfterSeconds);
				}
			} catch (final NotStartedException e) {
				throw new RuntimeException("Inconsistent reasoner state.", e);
			} catch (final MaterializationException e) {
				throw new RuntimeException(
						"Knowledge base incompatible with stratified negation: either the Rules are not stratifiable, or the variables in negated atom cannot be bound.",
						e);
			}
		}
		return this.reasoningCompleted;
	}

	@Override
	public QueryResultIterator answerQuery(PositiveLiteral query, boolean includeBlanks) throws ReasonerStateException {
		final boolean filterBlanks = !includeBlanks;
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		} else if (reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			throw new ReasonerStateException(reasonerState, "Querying is not allowed after closing.");
		}
		Validate.notNull(query, "Query atom must not be null!");

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(query);
		TermQueryResultIterator stringQueryResultIterator;
		try {
			stringQueryResultIterator = this.vLog.query(vLogAtom, true, filterBlanks);
		} catch (final NotStartedException e) {
			throw new RuntimeException("Inconsistent reasoner state.", e);
		}
		return new QueryResultIterator(stringQueryResultIterator);
	}

	@Override
	public void exportQueryAnswersToCsv(final PositiveLiteral query, final String csvFilePath,
			final boolean includeBlanks) throws ReasonerStateException, IOException {
		final boolean filterBlanks = !includeBlanks;
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		} else if (reasonerState.equals(ReasonerState.AFTER_CLOSING)) {
			throw new ReasonerStateException(reasonerState, "Querying is not allowed after closing.");
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
	}

	@Override
	public void resetReasoner() throws ReasonerStateException {
		// TODO what should happen to the KB?
		if (this.reasonerState.equals(ReasonerState.AFTER_CLOSING))
			throw new ReasonerStateException(reasonerState, "Resetting is not allowed after closing.");
		this.reasonerState = ReasonerState.BEFORE_LOADING;
		this.vLog.stop();
		LOGGER.warn(
				"Reasoner has been reset. All inferences computed during reasoning have been discarded. More data and rules can be added after resetting. The reasoner needs to be loaded again to perform querying and reasoning.");
	}

	@Override
	public void close() {
		this.reasonerState = ReasonerState.AFTER_CLOSING;

		this.knowledgeBase.deleteObserver(this);
		this.vLog.stop();
	}

	private void validateEdbIdbSeparation() throws EdbIdbSeparationException {
		final Set<Predicate> edbPredicates = collectEdbPredicates();
		final Set<Predicate> idbPredicates = collectIdbPredicates();
		final Set<Predicate> intersection = new HashSet<>(edbPredicates);
		intersection.retainAll(idbPredicates);

		if (!intersection.isEmpty()) {
			throw new EdbIdbSeparationException(intersection);
		}
	}

	private Set<Predicate> collectEdbPredicates() {
		final Set<Predicate> edbPredicates = new HashSet<>();
		edbPredicates.addAll(this.dataSourceForPredicate.keySet());
		edbPredicates.addAll(this.factsForPredicate.keySet());
		return edbPredicates;
	}

	private Set<Predicate> collectIdbPredicates() {
		final Set<Predicate> idbPredicates = new HashSet<>();
		for (final Rule rule : this.rules) {
			for (final Literal headAtom : rule.getHead()) {
				idbPredicates.add(headAtom.getPredicate());
			}
		}
		return idbPredicates;
	}

	String generateDataSourcesConfig() {
		final StringBuilder configStringBuilder = new StringBuilder();
		int dataSourceIndex = 0;
		for (final Predicate predicate : this.dataSourceForPredicate.keySet()) {
			final DataSource dataSource = this.dataSourceForPredicate.get(predicate);
			try (final Formatter formatter = new Formatter(configStringBuilder);) {
				formatter.format(dataSource.toConfigString(), dataSourceIndex,
						ModelToVLogConverter.toVLogPredicate(predicate));
			}
			dataSourceIndex++;
		}
		return configStringBuilder.toString();
	}

	private void loadInMemoryFacts() {
		for (final Predicate predicate : this.factsForPredicate.keySet()) {
			final Set<PositiveLiteral> factsForPredicate = this.factsForPredicate.get(predicate);

			final String vLogPredicate = ModelToVLogConverter.toVLogPredicate(predicate);
			final String[][] tuplesForPredicate = ModelToVLogConverter.toVLogFactTuples(factsForPredicate);
			try {
				this.vLog.addData(vLogPredicate, tuplesForPredicate);
			} catch (final EDBConfigurationException e) {
				throw new RuntimeException("Invalid data sources configuration.", e);
			}
		}
	}

	private void loadRules() {
		final karmaresearch.vlog.Rule[] vLogRuleArray = ModelToVLogConverter.toVLogRuleArray(this.rules);
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
		if (reasonerState.equals(ReasonerState.AFTER_CLOSING))
			throw new ReasonerStateException(reasonerState, "Setting log level is not allowed after closing.");
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
		if (reasonerState.equals(ReasonerState.AFTER_CLOSING))
			throw new ReasonerStateException(reasonerState, "Setting log file is not allowed after closing.");
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
	public void update(Observable o, Object arg) {
		// TODO update reasoning state for query answering
		// TODO compute KB diff

	}

}
