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
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.impl.AtomImpl;
import org.semanticweb.vlog4j.core.model.validation.AtomValidationException;
import org.semanticweb.vlog4j.core.model.validation.IllegalEntityNameException;
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
	private Algorithm algorithm = Algorithm.SKOLEM_CHASE;

	private final List<Rule> rules = new ArrayList<>();
	private final List<Atom> facts = new ArrayList<>();
	private final List<EDBPredicateConfig> edbPredicatesConfig = new ArrayList<>();

	private boolean loaded = false;
	private boolean reasoned = false;

	@Override
	public void setAlgorithm(final Algorithm algorithm) {
		this.algorithm = algorithm;
		if (this.reasoned) {
			// TODO Log Warning: VLog was already reasoned
		}
	}

	@Override
	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public void addRules(final Rule... rules) {
		addRules(Arrays.asList(rules));
	}

	@Override
	public void addRules(final Collection<Rule> rules) {
		if (!this.loaded) {
			this.rules.addAll(new ArrayList<>(rules));
		} else {
			// TODO Throw Exception: VLog has already loaded
		}
	}

	@Override
	public void addFacts(final Atom... facts) throws AtomValidationException, IllegalEntityNameException {
		addFacts(Arrays.asList(facts));
	}

	@Override
	public void addFacts(final Collection<Atom> facts) throws AtomValidationException, IllegalEntityNameException {
		if (!this.loaded) {
			for (final Atom fact : facts) {
				if (fact.getVariables().isEmpty()) {
					this.facts.add(new AtomImpl(fact));
				} else {
					// TODO Throw Exception: not a fact
				}
			}
		} else {
			// TODO Throw Exception: VLog was already loaded
		}
	}

	@Override
	public void addEDBConfigInfo(final EDBPredicateConfig... edbConfig) {
		addEDBConfigInfo(Arrays.asList(edbConfig));
	}

	@Override
	public void addEDBConfigInfo(final Collection<EDBPredicateConfig> edbConfig) {
		if (!this.loaded) {
			this.edbPredicatesConfig.addAll(edbConfig);
		} else {
			// TODO Throw Exception: VLog was already loaded
		}
	}

	@Override
	public void load() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		if (!this.loaded) {
			if (!Collections.disjoint(collectEDBPredicates(), collectIDBPredicates())) {
				// TODO Throw Exception: The program violates EDB/IDB separation
				return;
			}
			this.vlog.start(edbPredicatesConfigToString(), false);
			loadInMemoryFacts();
			this.vlog.setRules(ModelToVLogConverter.toVLogRuleArray(this.rules), RuleRewriteStrategy.NONE);
			this.loaded = true;
		} else {
			// TODO Log Warning: VLog was already loaded
		}
	}

	private Set<String> collectEDBPredicates() {
		final Set<String> edbPredicates = new HashSet<>();
		for (final EDBPredicateConfig edbPredConfig : this.edbPredicatesConfig) {
			edbPredicates.add(edbPredConfig.getPredicate());
		}
		for (final Atom fact : this.facts) {
			edbPredicates.add(fact.getPredicate());
		}
		return edbPredicates;
	}

	private Set<String> collectIDBPredicates() {
		final Set<String> idbPredicates = new HashSet<>();
		for (final Rule rule : this.rules) {
			for (final Atom headAtom : rule.getHead()) {
				idbPredicates.add(headAtom.getPredicate());
			}
		}
		return idbPredicates;
	}

	private String edbPredicatesConfigToString() {
		final StringBuilder edbPredicatesConfigSB = new StringBuilder();
		for (int i = 0; i < this.edbPredicatesConfig.size(); i++) {
			final EDBPredicateConfig currenEDBPredicateConfig = this.edbPredicatesConfig.get(i);
			edbPredicatesConfigSB.append("EDB").append(i).append("_predname=").append(currenEDBPredicateConfig.getPredicate()).append("\n");
			edbPredicatesConfigSB.append("EDB").append(i).append("_type=INMEMORY" + "\n");
			final File sourceFile = currenEDBPredicateConfig.getSourceFile();
			edbPredicatesConfigSB.append("EDB").append(i).append("_param0=").append(sourceFile.getParent()).append("\n");
			edbPredicatesConfigSB.append("EDB").append(i).append("_param1=").append(sourceFile.getName().substring(0, sourceFile.getName().length() - 3))
					.append("\n" + "\n");
		}
		return edbPredicatesConfigSB.toString();
	}

	private void loadInMemoryFacts() throws EDBConfigurationException {
		final Map<String, List<List<Term>>> factsMap = new HashMap<>();
		for (final Atom fact : this.facts) {
			factsMap.putIfAbsent(fact.getPredicate(), new ArrayList<>());
			factsMap.get(fact.getPredicate()).add(fact.getArguments());
		}
		for (final String predName : factsMap.keySet()) {
			final List<List<Term>> predNameArgs = factsMap.get(predName);
			final int arity = predNameArgs.get(0).size();
			final String[][] tuplesMatrix = new String[predNameArgs.size()][arity];
			for (int i = 0; i < predNameArgs.size(); i++) {
				for (int j = 0; j < arity; j++) {
					tuplesMatrix[i][j] = predNameArgs.get(i).get(j).getName();
				}
			}
			this.vlog.addData(predName, tuplesMatrix);
		}
	}

	@Override
	public void reason() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		if (!this.reasoned) {
			final boolean skolemChase = this.algorithm == Algorithm.SKOLEM_CHASE;
			this.vlog.materialize(skolemChase);
			this.reasoned = true;
		} else {
			// TODO Log Warning: VLog already materialised- ok
		}
	}

	@Override
	public StringQueryResultEnumeration compileQueryIterator(final Atom atom) throws NotStartedException {
		if (this.loaded) {
			return this.vlog.query(ModelToVLogConverter.toVLogAtom(atom));
		} else {
			// TODO throw exception
			return null;
		}
	}

	@Override
	public void exportAtomicQueryAnswers(final Atom queryAtom, final String outputFilePath) {
		// vlog.writePredicateToCsv(arg0, arg1);
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		this.vlog.stop();

	}

}
