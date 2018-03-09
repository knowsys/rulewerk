package org.semanticweb.vlog4j.core.reasoner;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.Rule;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;

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
public interface Reasoner {

	// TODO or do we prefer
	// addRule(),removeRule(),addRules(),removeRules(),removeAllRules()
	// TODO how should this behave once we started the VLog reasoner/once we
	// materialized
	Set<Rule> getRules();

	Set<String[]> getEDBConfig();

	// TODO do we want a start() method? or do we pass the config in the Reasoner
	// constructor?

	// TODO do we want to return anything?
	void applyReasoning() throws AlreadyStartedException, EDBConfigurationException, IOException;

	Set<Atom> query(Atom query);

	// TODO is it more inconvenient for the user to provide the arity at export? Or
	// create a predicate with a given arity when creating rules?
	void exportFactsToCSV(String predicate, int arity, File csvFile);

	// TODO what would happen in the case of CSVs? Where will they be stored?
	void updateDB();

	// TODO: will this export all EDBs and IDBs? or only materialization result?
	// Should we have a separate method for exportIDBs?
	// TODO: where will we get the file names? what if IDBs do not have valid file
	// names?
	// TODO what about parameters with same name and different arties?
	void exportDB(File directoryLocation);

	void dispose();
}
