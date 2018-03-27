package org.semanticweb.vlog4j.core.reasoner;

import java.io.IOException;
import java.util.Collection;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

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
public interface ReasonerInterface {

	Algorithm getAlgorithm();

	void setAlgorithm(Algorithm algorithmType);

	void addRules(Rule... rules) throws ReasonerStateException;

	void addRules(Collection<Rule> rules) throws ReasonerStateException;

	RuleRewriteStrategy getRuleRewriteStrategy();

	void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) throws ReasonerStateException;

	void addFacts(Atom... fact) throws ReasonerStateException;

	void addFacts(Collection<Atom> facts) throws ReasonerStateException;

	void addDataSource(Predicate predicate, DataSource dataSource) throws ReasonerStateException;

	// void addFactsSource(FactsSourceConfig... edbConfig) throws
	// ReasonerStateException;

	// void addFactsSource(Collection<FactsSourceConfig> edbConfig) throws
	// ReasonerStateException;

	void load() throws EDBConfigurationException, IOException, EdbIdbSeparationException;

	void reason() throws EDBConfigurationException, IOException, ReasonerStateException;

	QueryResultIterator answerQuery(Atom atom) throws ReasonerStateException;

	void exportQueryAnswersToCsv(Atom atom, String outputFilePath) throws ReasonerStateException, IOException;

	// TODO arity should be in the EDB config file,
	// do not read the files, have low-level API check if the file content
	// corresponds the arity

	// TODO check if URIs can be file names
	// Set<EDBPredicateConfig> exportDBToFolder(File location);

	void dispose();

	void exportQueryAnswersToCsv(Atom atom, String csvFilePath, boolean includeBlanks)
			throws ReasonerStateException, IOException;

}
