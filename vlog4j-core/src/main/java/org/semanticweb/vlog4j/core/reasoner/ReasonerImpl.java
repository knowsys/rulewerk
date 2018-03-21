package org.semanticweb.vlog4j.core.reasoner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.util.ModelToVLogConverter;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.StringQueryResultEnumeration;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

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

	private final VLog vlog = new VLog();
	private ReasonerState reasonerState = ReasonerState.BEFORE_LOADING;

	private Algorithm algorithm = Algorithm.SKOLEM_CHASE;

	private final List<Rule> rules = new ArrayList<>();
	private final List<Atom> facts = new ArrayList<>();
	private final List<FactsSourceConfig> edbPredicatesConfig = new ArrayList<>();

	@Override
	public void setAlgorithm(final Algorithm algorithm) {
		this.algorithm = algorithm;
		if (this.reasonerState.equals(ReasonerState.AFTER_REASONING)) {
			// TODO Log Warning: VLog was already reasoned, this call is ineffective
		}
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
		if (!this.reasonerState.equals(ReasonerState.BEFORE_LOADING)) {
			throw new ReasonerStateException(this.reasonerState, "Rules cannot be added after the reasoner was loaded!");
		}
		this.rules.addAll(new ArrayList<>(rules));
	}

	@Override
	public void addFacts(final Atom... facts) throws ReasonerStateException {
		addFacts(Arrays.asList(facts));
	}

	@Override
	public void addFacts(final Collection<Atom> facts) throws ReasonerStateException {
		if (!this.reasonerState.equals(ReasonerState.BEFORE_LOADING)) {
			throw new ReasonerStateException(this.reasonerState, "Facts cannot be added after the reasoner was loaded!");
		}
		for (final Atom fact : facts) {
			if (fact.getVariables().isEmpty()) {
				this.facts.add(fact);
			} else {
				// TODO Throw Exception: not a fact
			}
		}
	}

	@Override
	public void addFactsSource(final FactsSourceConfig... factsSourceConfigs) throws ReasonerStateException {
		addFactsSource(Arrays.asList(factsSourceConfigs));
	}

	@Override
	public void addFactsSource(final Collection<FactsSourceConfig> factsSourceConfigs) throws ReasonerStateException {
		if (!this.reasonerState.equals(ReasonerState.BEFORE_LOADING)) {
			throw new ReasonerStateException(this.reasonerState, "Facts source configurations cannot be added after the reasoner was loaded!");
		}
		this.edbPredicatesConfig.addAll(factsSourceConfigs);
	}

	@Override
	public void load() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		if (this.reasonerState.equals(ReasonerState.BEFORE_LOADING)) {
			if (!Collections.disjoint(collectEDBPredicates(), collectIDBPredicates())) {
				// TODO Throw Exception: The program violates EDB/IDB separation
				return;
			}
			this.reasonerState = ReasonerState.AFTER_LOADING;

			this.vlog.start(edbPredicatesConfigToString(), false);
			loadInMemoryFacts();
			this.vlog.setRules(ModelToVLogConverter.toVLogRuleArray(this.rules), RuleRewriteStrategy.NONE);

		} else {
			// TODO Log Warning: VLog was already loaded, this call is ineffective
		}
	}

	private String edbPredicatesConfigToString() {
		final StringBuilder edbPredicatesConfigSB = new StringBuilder();
		final int i = 0;
		for (int j = 0; j < this.edbPredicatesConfig.size(); j++) {
			final FactsSourceConfig factsSourceConfig = this.edbPredicatesConfig.get(i);
			final String predicate = factsSourceConfig.getPredicate();
			final File sourceFile = factsSourceConfig.getSourceFile();

			edbPredicatesConfigSB.append("EDB").append(i).append("_predname=").append(predicate).append("\n");
			edbPredicatesConfigSB.append("EDB").append(i).append("_type=INMEMORY" + "\n");
			edbPredicatesConfigSB.append("EDB").append(i).append("_param0=").append(sourceFile.getParent()).append("\n");
			edbPredicatesConfigSB.append("EDB").append(i).append("_param1=").append(sourceFile.getName().substring(0, sourceFile.getName().length() - 3))
					.append("\n" + "\n");
		}
		return edbPredicatesConfigSB.toString();
	}

	private Set<String> collectEDBPredicates() {
		final Set<String> edbPredicates = new HashSet<>();
		for (final FactsSourceConfig edbPredConfig : this.edbPredicatesConfig) {
			edbPredicates.add(edbPredConfig.getPredicate());
		}
		for (final Atom fact : this.facts) {
			edbPredicates.add(fact.getPredicate().getName());
		}
		return edbPredicates;
	}

	private Set<String> collectIDBPredicates() {
		final Set<String> idbPredicates = new HashSet<>();
		for (final Rule rule : this.rules) {
			for (final Atom headAtom : rule.getHead()) {
				idbPredicates.add(headAtom.getPredicate().getName());
			}
		}
		return idbPredicates;
	}



	private void loadInMemoryFacts() throws EDBConfigurationException {
		final Map<Predicate, List<List<Term>>> factsMap = new HashMap<>();
		for (final Atom fact : this.facts) {
			factsMap.putIfAbsent(fact.getPredicate(), new ArrayList<>());
			factsMap.get(fact.getPredicate()).add(fact.getTerms());
		}
		for (final Predicate pred : factsMap.keySet()) {
			final List<List<Term>> predArgs = factsMap.get(pred);
			final int arity = predArgs.get(0).size();
			final String[][] tuplesMatrix = new String[predArgs.size()][arity];
			for (int i = 0; i < predArgs.size(); i++) {
				for (int j = 0; j < arity; j++) {
					tuplesMatrix[i][j] = predArgs.get(i).get(j).getName();
				}
			}
			this.vlog.addData(pred.getName(), tuplesMatrix);
		}
	}

	@Override
	public void reason() throws EDBConfigurationException, IOException, NotStartedException, ReasonerStateException {
		if (this.reasonerState.equals(ReasonerState.BEFORE_LOADING)) {
			// TODO exception message
			throw new ReasonerStateException(this.reasonerState, "Reasoning is not alowed before loading!");
		} else if (this.reasonerState.equals(ReasonerState.AFTER_REASONING)) {
			// TODO Log Warning: VLog already materialised.
		} else {
			this.reasonerState = ReasonerState.AFTER_REASONING;

			final boolean skolemChase = this.algorithm == Algorithm.SKOLEM_CHASE;
			this.vlog.materialize(skolemChase);
		}
	}

	@Override
	public QueryResultIterator answerQuery(final Atom atom) throws NotStartedException, ReasonerStateException {
		if (this.reasonerState.equals(ReasonerState.BEFORE_LOADING)) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		}
		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(atom);
		final StringQueryResultEnumeration stringQueryResultEnumeration = this.vlog.query(vLogAtom);
		return new QueryResultIterator(stringQueryResultEnumeration);
	}

	@Override
	public void exportAtomicQueryAnswers(final Atom queryAtom, final String outputFilePath) throws ReasonerStateException {
		if (this.reasonerState.equals(ReasonerState.BEFORE_LOADING)) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		}
		// vlog.writePredicateToCsv(arg0, arg1);
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		this.vlog.stop();
	}

}