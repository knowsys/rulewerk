package org.semanticweb.vlog4j.core.reasoner;

import java.io.IOException;
import java.util.Collection;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;

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
public interface Reasoner extends AutoCloseable {

	public static VLogReasoner getInstance() {
		return new VLogReasoner();
	}

	void setAlgorithm(Algorithm algorithmType);

	Algorithm getAlgorithm();

	/**
	 * In some cases, reasoning may not terminate. We recommend reasoning with
	 * algorithm {@link Algorithm#RESTRICTED_CHASE}, as it leads to termination in
	 * more cases. <br>
	 * This method sets a timeout (in seconds) after which reasoning can be
	 * artificially interrupted if it has not reached completion.
	 * 
	 * @param seconds
	 *            interval after which reasoning will be interrupted, in seconds. If
	 *            {@code null}, reasoning will not be interrupted and will return
	 *            only after (if) it has reached completion.
	 */
	void setReasoningTimeout(Integer seconds);

	/**
	 * This method returns the reasoning timeout, representing the interval (in
	 * {@code seconds}) after which reasoning will be interrupted if it has not
	 * reached completion. The default value is {@code null}, in which case
	 * reasoning terminates only after (if) it reaches completion.
	 * 
	 * @return if not {@code null}, number of seconds after which the reasoning will
	 *         be interrupted, if it has not reached completion.
	 */
	Integer getReasoningTimeout();

	void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) throws ReasonerStateException;

	RuleRewriteStrategy getRuleRewriteStrategy();

	/**
	 * Sets the logging level of the internal VLog C++ resource. Default value is
	 * {@link LogLevel#WARNING}
	 * 
	 * @param logLevel
	 *            the logging level to be set for VLog C++ resource.
	 */
	void setLogLevel(LogLevel logLevel);

	/**
	 * Returns the logging level of the internal VLog C++ resource. If no value has
	 * been set, the default is {@link LogLevel#WARNING}.
	 * 
	 * @return the logging level of the VLog C++ resource.
	 */
	LogLevel getLogLevel();

	/**
	 * Redirects the logs of the internal VLog C++ resource to given file. If no log
	 * file is set or the given {@code filePath} is not a valid file path, VLog will
	 * only log to the default system output.
	 * 
	 * @param filePath
	 *            the file for the internal VLog C++ resource to log to.
	 */
	void setLogFile(String filePath);

	void addRules(Rule... rules) throws ReasonerStateException;

	void addRules(Collection<Rule> rules) throws ReasonerStateException;

	void addFacts(Atom... fact) throws ReasonerStateException;

	void addFacts(Collection<Atom> facts) throws ReasonerStateException;

	void addDataSource(Predicate predicate, DataSource dataSource) throws ReasonerStateException;

	void load() throws IOException, EdbIdbSeparationException;

	/**
	 * 
	 * @return
	 *         <ul>
	 *         <li>the value returned by the previous {@link Reasoner#reason()}
	 *         call, if successive reasoning is attempted before a
	 *         {@link Reasoner#reset()}.</li>
	 *         <li>{@code true}, if reasoning reached completion.</li>
	 *         <li>{@code false}, if reasoning has been interrupted before
	 *         completion.</li>
	 *         </ul>
	 * @throws IOException
	 *             if I/O exceptions occur during reasoning.
	 * @throws ReasonerStateException
	 *             if this method is called before loading ({@link Reasoner#load()}.
	 */
	boolean reason() throws IOException, ReasonerStateException;

	QueryResultIterator answerQuery(Atom atom, boolean includeBlanks) throws ReasonerStateException;

	void exportQueryAnswersToCsv(Atom atom, String csvFilePath, boolean includeBlanks)
			throws ReasonerStateException, IOException;

	/**
	 * Resets the reasoner to the state it had before loading (before the call of
	 * {@link Reasoner#load()} method). All facts inferred by reasoning are
	 * discarded. Calling {@link Reasoner#load()} again after
	 * {@link Reasoner#reset()} reloads the reasoner with {@link Reasoner#load()}the
	 * current given knowledge base (added facts, data sources and rules).
	 */
	void reset();

	// TODO arity should be in the EDB config file,
	// do not read the files, have low-level API check if the file content
	// corresponds the arity

	// TODO Set<EDBPredicateConfig> exportDBToFolder(File location);

	// TODO not allow any operation after closing, except close();
	@Override
	void close();

}
