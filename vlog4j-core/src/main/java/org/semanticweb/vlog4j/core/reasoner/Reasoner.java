package org.semanticweb.vlog4j.core.reasoner;

import java.io.IOException;
import java.util.Collection;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.FactsSourceConfigException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;

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

	void addRules(Rule... rules) throws ReasonerStateException;

	void addRules(Collection<Rule> rules) throws ReasonerStateException;

	RuleRewriteStrategy getRuleRewriteStrategy();

	void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) throws ReasonerStateException;

	void addFacts(Atom... fact) throws ReasonerStateException;

	void addFacts(Collection<Atom> facts) throws ReasonerStateException;

	// void addFactsSource(FactsSourceConfig... edbConfig) throws
	// ReasonerStateException;

	// void addFactsSource(Collection<FactsSourceConfig> edbConfig) throws
	// ReasonerStateException;

	void load() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException,
			EdbIdbSeparationException;

	void reason() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException,
			ReasonerStateException;

	QueryResultIterator answerQuery(Atom atom) throws NotStartedException, ReasonerStateException;

	void exportQueryAnswersToCSV(Atom atom, String outputFilePath)
			throws ReasonerStateException, NotStartedException, IOException, FactsSourceConfigException;

	// TODO arity should be in the EDB config file,
	// do not read the files, have low-level API check if the file content
	// corresponds the arity

	// TODO check if URIs can be file names
	// Set<EDBPredicateConfig> exportDBToFolder(File location);

	void dispose();

}