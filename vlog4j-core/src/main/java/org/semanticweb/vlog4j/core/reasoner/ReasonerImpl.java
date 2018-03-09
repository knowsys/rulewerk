package org.semanticweb.vlog4j.core.reasoner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.Rule;
import org.semanticweb.vlog4j.core.model.Term;
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

	/**
	 * VLog reasoner
	 */
	private final VLog vlog = new VLog();
	private final List<Rule> rules = new ArrayList<>();
	private final List<Atom> facts = new ArrayList<>();
	private final List<EDBPredConfig> edbProgramConfig = new ArrayList<>();

	@Override
	public List<Rule> getRules() {
		return this.rules;
	}

	@Override
	public List<Atom> getFacts() {
		return this.facts;
	}

	@Override
	public List<EDBPredConfig> getEDBConfig() {
		return this.edbProgramConfig;
	}

	@Override
	public void applyReasoning() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		// Start reasoner
		this.vlog.start("", false);

		// Load rules
		this.vlog.setRules(ModelToVLogConverter.toVLogRuleArray(this.rules), RuleRewriteStrategy.NONE);

		// Load in memory facts
		// TODO Fix this so it deals with punning
		final Map<String, List<List<Term>>> factsMap = new HashMap<>();
		for (final Atom fact : this.facts) {
			factsMap.putIfAbsent(fact.getPredicateName(), new ArrayList<>());
			factsMap.get(fact.getPredicateName()).add(fact.getArguments());
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

		this.vlog.materialize(true);
	}

	@Override
	public StringQueryResultEnumeration query(final Atom atom) throws NotStartedException {
		return this.vlog.query(ModelToVLogConverter.toVLogAtom(atom));
	}

	@Override
	public void updateDB() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exportDB(final File directoryLocation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		this.vlog.stop();
	}

	@Override
	public void exportFactsToCSV(final String predicate, final int arity, final File csvFile) {
		// vlog.writePredicateToCsv(arg0, arg1);
		// TODO Auto-generated method stub

	}

}
