package org.semanticweb.vlog4j.core.reasoner;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.Rule;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
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

	/**
	 * VLog reasoner
	 */
	private final VLog vlog = new VLog();
	private final Set<Rule> ruleSet;
	private final Set<String[]> edbProgramConfig;

	public ReasonerImpl(final Set<Rule> rules, final Set<String[]> edbConfig) {

		this.edbProgramConfig = edbConfig;
		this.ruleSet = rules;
	}

	@Override
	public Set<Rule> getRules() {
		return this.ruleSet;
	}

	@Override
	public Set<String[]> getEDBConfig() {
		return this.edbProgramConfig;
	}

	@Override
	public void applyReasoning() throws AlreadyStartedException, EDBConfigurationException, IOException {
		this.vlog.start(EDBConfigToFileFormat(), false);

		// vlog.setRules(arg0, arg1);
		// vlog.start(arg0, arg1);
		// vlog.materialize(arg0);
		// TODO Auto-generated method stub

	}

	private String EDBConfigToFileFormat() {
		String EDBConfigStr = new String("");
		for (final String[] edbConfigItem : this.edbProgramConfig) {
			final String predicateName = edbConfigItem[0];
			final String sourcePath = edbConfigItem[1];
			final String databaseType = edbConfigItem[2];
			EDBConfigStr += "PredicateName: " + predicateName + "\n" + "Source path: " + sourcePath + "\n" + "Source type: " + databaseType + "\n\n";
		}
		return EDBConfigStr;
	}

	@Override
	public Set<Atom> query(final Atom atom) {
		// TODO Auto-generated method stub
		return null;
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
