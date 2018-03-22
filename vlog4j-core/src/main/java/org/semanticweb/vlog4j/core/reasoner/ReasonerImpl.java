package org.semanticweb.vlog4j.core.reasoner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.FactsSourceConfigException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.util.ModelToVLogConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.StringQueryResultIterator;
import karmaresearch.vlog.VLog;

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

public class ReasonerImpl implements Reasoner {
	private static Logger LOGGER = LoggerFactory.getLogger(ReasonerImpl.class);

	private final VLog vlog = new VLog();
	private ReasonerState reasonerState = ReasonerState.BEFORE_LOADING;

	private Algorithm algorithm = Algorithm.SKOLEM_CHASE;
	private RuleRewriteStrategy ruleRewriteStrategy = RuleRewriteStrategy.NONE;

	private final List<Rule> rules = new ArrayList<>();
	private final Map<Predicate, Set<Atom>> factsForPredicate = new HashMap<>();
	// private final List<FactsSourceConfig> edbPredicatesConfig = new
	// ArrayList<>();
	// private final Map<Predicate, DataSource> dataSourceConfiguration = new
	// HashMap<>();

	@Override
	public void setAlgorithm(final Algorithm algorithm) {
		Validate.notNull(algorithm);

		if (this.reasonerState == ReasonerState.AFTER_REASONING) {
			LOGGER.warn(
					"Argument algorithm will not be used: this Reasoner has already reasoned. Successive reason() calls are not supported.");
		}
		this.algorithm = algorithm;
	}

	@Override
	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public void addRules(final Rule... rules) throws ReasonerStateException {
		addRules(Arrays.asList(rules));
	}

	@Override
	public void addRules(final Collection<Rule> rules) throws ReasonerStateException {
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"Rules cannot be added after the reasoner was loaded!");
		}
		Validate.noNullElements(rules, "Null rules are not alowed! The list contains a null at position [%d].");
		this.rules.addAll(new ArrayList<>(rules));
	}

	@Override
	public void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) throws ReasonerStateException {
		Validate.notNull(ruleRewritingStrategy);
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"Rules cannot be re-writen after the reasoner was loaded!");
		}
		this.ruleRewriteStrategy = ruleRewritingStrategy;
	}

	@Override
	public RuleRewriteStrategy getRuleRewriteStrategy() {
		return this.ruleRewriteStrategy;
	}

	@Override
	public void addFacts(final Atom... facts) throws ReasonerStateException {
		addFacts(Arrays.asList(facts));
	}

	@Override
	public void addFacts(final Collection<Atom> facts) throws ReasonerStateException {
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"Facts cannot be added after the reasoner was loaded!");
		}
		Validate.noNullElements(this.rules, "Null facts are not alowed! The list contains a fact at position [%d].");
		for (final Atom fact : facts) {
			// TODO validate Term does not have Blanks
			if (fact.getVariables().isEmpty()) {
				final Predicate predicate = fact.getPredicate();
				this.factsForPredicate.putIfAbsent(predicate, new HashSet<>());
				this.factsForPredicate.get(predicate).add(fact);
			} else {
				// TODO Throw Exception: not a fact
			}
		}
	}

	@Override
	public void load() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException,
			EdbIdbSeparationException {
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			validateEdbIdbSeparation();

			this.reasonerState = ReasonerState.AFTER_LOADING;
			// this.vlog.start(edbPredicatesConfigToString(), false);
			if (factsForPredicate.isEmpty()) {
				this.vlog.start(StringUtils.EMPTY, false);
			}
			// TODO log warning if both in memory and on disk facts are empty.
			loadInMemoryFacts();
			if (this.rules.isEmpty()) {
				LOGGER.warn("No rules have been provided for reasoning.");
			}
			loadRules();

		} else {
			LOGGER.warn(
					"This method call is ineffective: the Reasoner was already loaded. Successive load() calls are not supported.");
		}
	}

	@Override
	public void reason() throws EDBConfigurationException, IOException, NotStartedException, ReasonerStateException {
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			// TODO exception message
			throw new ReasonerStateException(this.reasonerState, "Reasoning is not alowed before loading!");
		} else if (this.reasonerState == ReasonerState.AFTER_REASONING) {
			LOGGER.warn(
					"This method call is ineffective: this Reasoner was already reasoned. Successive reason() calls are not supported.");
		} else {
			this.reasonerState = ReasonerState.AFTER_REASONING;

			final boolean skolemChase = this.algorithm == Algorithm.SKOLEM_CHASE;
			this.vlog.materialize(skolemChase);
		}
	}

	@Override
	public QueryResultIterator answerQuery(final Atom queryAtom) throws NotStartedException, ReasonerStateException {
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		}
		Validate.notNull(queryAtom, "Query atom must not be null!");

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(queryAtom);
		final StringQueryResultIterator stringQueryResultIterator = this.vlog.query(vLogAtom);
		return new QueryResultIterator(stringQueryResultIterator);
	}

	@Override
	public void exportQueryAnswersToCSV(final Atom queryAtom, final String csvFilePath)
			throws ReasonerStateException, NotStartedException, IOException, FactsSourceConfigException {
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		}
		Validate.notNull(queryAtom, "Query atom must not be null!");
		Validate.notNull(csvFilePath, "File to export query answer to must not be null!");
		if (!csvFilePath.endsWith(CSVFileDataSource.CSV_FILE_EXTENSION)) {
			throw new FactsSourceConfigException("Expected .csv extension for data source file [" + csvFilePath + "]!");
		}

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(queryAtom);
		this.vlog.writeQueryResultsToCsv(vLogAtom, csvFilePath);
	}

	@Override
	public void dispose() {
		this.vlog.stop();
	}

	private void validateEdbIdbSeparation() throws EdbIdbSeparationException {
		final Set<Predicate> EdbPredicates = collectEDBPredicates();
		final Set<Predicate> IdbPredicates = collectIDBPredicates();
		final Set<Predicate> intersection = new HashSet<>(EdbPredicates);
		intersection.retainAll(IdbPredicates);
		if (!intersection.isEmpty()) {
			// TODO exception message
			throw new EdbIdbSeparationException(intersection);
		}
	}

	private Set<Predicate> collectEDBPredicates() {
		final Set<Predicate> edbPredicates = new HashSet<>();
		// for (final FactsSourceConfig edbPredConfig : this.edbPredicatesConfig) {
		// edbPredicates.add(edbPredConfig.getPredicate());
		// }
		edbPredicates.addAll(this.factsForPredicate.keySet());
		return edbPredicates;
	}

	private Set<Predicate> collectIDBPredicates() {
		final Set<Predicate> idbPredicates = new HashSet<>();
		for (final Rule rule : this.rules) {
			for (final Atom headAtom : rule.getHead()) {
				idbPredicates.add(headAtom.getPredicate());
			}
		}
		return idbPredicates;
	}

	private void loadInMemoryFacts() throws EDBConfigurationException {
		for (final Predicate predicate : this.factsForPredicate.keySet()) {
			final Set<Atom> factsForPredicate = this.factsForPredicate.get(predicate);

			final String vlogPredicate = ModelToVLogConverter.toVlogPredicate(predicate);
			final String[][] tuplesForPredicate = ModelToVLogConverter.toVLogFactTuples(factsForPredicate);
			this.vlog.addData(vlogPredicate, tuplesForPredicate);
		}
	}

	private void loadRules() throws NotStartedException {
		final karmaresearch.vlog.Rule[] vLogRuleArray = ModelToVLogConverter.toVLogRuleArray(this.rules);
		final karmaresearch.vlog.VLog.RuleRewriteStrategy vLogRuleRewriteStrategy = ModelToVLogConverter
				.toVLogRuleRewriteStrategy(this.ruleRewriteStrategy);
		this.vlog.setRules(vLogRuleArray, vLogRuleRewriteStrategy);
	}

}