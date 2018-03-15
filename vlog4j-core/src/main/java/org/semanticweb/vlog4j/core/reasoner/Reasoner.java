package org.semanticweb.vlog4j.core.reasoner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.Rule;
import org.semanticweb.vlog4j.core.model.validation.AtomValidationException;
import org.semanticweb.vlog4j.core.model.validation.BlankNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.ConstantNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.PredicateNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.VariableNameValidationException;

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

	void addFacts(Atom... fact) throws AtomValidationException, PredicateNameValidationException, BlankNameValidationException, ConstantNameValidationException,
			VariableNameValidationException;

	void addFacts(Collection<Atom> facts) throws AtomValidationException, PredicateNameValidationException, BlankNameValidationException,
			ConstantNameValidationException, VariableNameValidationException;

	void addEDBConfigInfo(EDBPredicateConfig... edbConfig);

	void addEDBConfigInfo(Collection<EDBPredicateConfig> edbConfig);

	void load() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException;

	void reason() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException;

	StringQueryResultEnumeration compileQueryIterator(Atom query) throws NotStartedException;

	void exportAtomicQueryAnswers(Atom atom, String outputFilePath);

	// TODO arity should be in the EDB config file,
	// do not read the files, have low-level API check if the file content corresponds the arity

	// TODO check if URIs can be file names
	// Set<EDBPredicateConfig> exportDBToFolder(File location);

	List<List<String>> compileQuerySet(Atom atomAx) throws NotStartedException;

	void dispose();
}
