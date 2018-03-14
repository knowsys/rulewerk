package org.semanticweb.vlog4j.core.reasoner;

import java.io.IOException;
import java.util.Collection;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.Rule;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.StringQueryResultEnumeration;

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

	Algorithm getAlgorithm();

	void setAlgorithm(Algorithm algorithmType);

	void addRules(Rule... rules);

	void addRules(Collection<Rule> rules);

	void addFacts(Atom... fact);

	void addFacts(Collection<Atom> facts);

	void addEDBConfigInfo(EDBPredicateConfig... edbConfig);

	void addEDBConfigInfo(Collection<EDBPredicateConfig> edbConfig);

	void load() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException;

	void reason() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException;

	StringQueryResultEnumeration compileAtomicQuery(Atom query) throws NotStartedException;

	// TODO is it more inconvenient for the user to provide the arity at export? Or
	// create a predicate with a given arity when creating rules?
	void exportAtomicQueryAnswers(Atom atom, String outputFilePath);

}
