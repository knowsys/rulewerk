package org.semanticweb.vlog4j.core.reasoner;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;

import karmaresearch.vlog.Atom;

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

/**
 * Interface that exposes the existential rule reasoning capabilities of VLog.
 * <br>
 * The <b>knowledge base</b> of the reasoner can be loaded with explicit facts
 * and <b>existential rules</b> that would infer implicit <b>facts</b> trough
 * reasoning. <br>
 * <b>Facts</b> can be added to the knowledge base:
 * <ul>
 * <li>as in-memory Java objects ({@link #addFacts(Atom...)}</li>
 * <li>from a persistent data source
 * ({@link #addFactsFromDataSource(Predicate, DataSource)})</li>
 * </ul>
 * Note that facts with the same predicate cannot come from multiple sources
 * (where a source can be a collection of in-memory {@link Atom} objects, or a
 * {@link DataSource} .<br>
 * <b>Rules</b> added to the knowledge base ({@link #addRules(Rule...)}) can be
 * re-written internally by VLog, using the corresponding set
 * {@link RuleRewriteStrategy}. <br>
 * <br>
 * Once adding facts and rules to the knowledge base has been completed, the
 * <b>knowledge base</b> can be <b>loaded</b> into the reasoner.
 *
 * The loaded reasoner can perform <b>atomic queries</b> on explicit facts
 * before reasoning, and all implicit and explicit facts after calling
 * {@link Reasoner#reason()}. Queries can provide an iterator for the results
 * ({@link #answerQuery(Atom, boolean)}, or the results can be exported to a
 * file ({@link #exportQueryAnswersToCsv(Atom, String, boolean)}). <br>
 * <br>
 * <b>Reasoning</b> with various {@link Algorithm}s is supported, that can lead
 * to different sets of inferred facts and different termination behavior. In
 * some cases, reasoning with rules with existentially quantified variables
 * {@link Rule#getExistentiallyQuantifiedVariables()} may not terminate. We
 * recommend reasoning with algorithm {@link Algorithm#RESTRICTED_CHASE}, as it
 * leads to termination in more cases. To avoid non-termination, a reasoning
 * timeout can be set ({@link Reasoner#setReasoningTimeout(Integer)}). <br>
 * <b>Incremental reasoning</b> is not supported. To add more facts and rule to
 * the <b>knowledge base</b> and reason again, the reasoner needs to be
 * <b>reset</b> ({@link #resetReasoner()}) to the state of its knowledge base
 * before loading. Then, more information can be added to the knowledge base,
 * the reasoner can be loaded again, and querying and reasoning can be
 * performed.
 * 
 * @FIXME Update the outdated JavaDoc
 *
 * @author Irina Dragoste
 *
 */

public interface Reasoner extends AutoCloseable, KnowledgeBaseListener {

	/**
	 * Factory method that to instantiate a Reasoner with an empty knowledge base.
	 *
	 * @return a {@link VLogReasoner} instance.
	 */
	public static Reasoner getInstance() {
		final KnowledgeBase knowledgeBase = new KnowledgeBase();
		return new VLogReasoner(knowledgeBase);
	}

	/**
	 * Getter for the knowledge base to reason on.
	 * 
	 * @return the reasoner's knowledge base
	 */
	KnowledgeBase getKnowledgeBase();

	/**
	 * Sets the algorithm that will be used for reasoning over the knowledge base.
	 * If no algorithm is set, the default algorithm is
	 * {@link Algorithm#RESTRICTED_CHASE} will be used.
	 *
	 * @param algorithm the algorithm to be used for reasoning.
	 */
	void setAlgorithm(Algorithm algorithm);

	/**
	 * Getter for the algorithm that will be used for reasoning over the knowledge
	 * base. The default value is {@link Algorithm#RESTRICTED_CHASE}.
	 *
	 * @return the reasoning algorithm.
	 */
	Algorithm getAlgorithm();

	/**
	 * In some cases, reasoning with rules with existentially quantified variables
	 * {@link Rule#getExistentiallyQuantifiedVariables()} may not terminate. We
	 * recommend reasoning with algorithm {@link Algorithm#RESTRICTED_CHASE}, as it
	 * leads to termination in more cases. <br>
	 * This method sets a timeout (in seconds) after which reasoning can be
	 * artificially interrupted if it has not reached completion.
	 *
	 * @param seconds interval after which reasoning will be interrupted, in
	 *                seconds. If {@code null}, reasoning will not be interrupted
	 *                and will return only after (if) it has reached completion.
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

	/**
	 * Loaded {@link Rule}s can be re-written internally to an equivalent set of
	 * rules, according to given {@code ruleRewritingStrategy}. If no strategy is
	 * set, the default value is {@link RuleRewriteStrategy#NONE}, meaning that the
	 * rules will not be re-written.
	 *
	 * @param ruleRewritingStrategy strategy according to which the rules will be
	 *                              rewritten before reasoning.
	 */
	void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy);

	/**
	 * Getter for the strategy according to which rules will be rewritten before
	 * reasoning. The default value is {@link RuleRewriteStrategy#NONE}, meaning
	 * that the rules will not be re-written.
	 *
	 * @return the current rule re-writing strategy
	 */
	RuleRewriteStrategy getRuleRewriteStrategy();

	/**
	 * Sets the logging level of the internal VLog C++ resource. Default value is
	 * {@link LogLevel#WARNING}
	 *
	 * @param logLevel the logging level to be set for VLog C++ resource.
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
	 * log to the default system output.
	 *
	 * @param filePath the file for the internal VLog C++ resource to log to. If
	 *                 {@code null} or an invalid file path, the reasoner will log
	 *                 to the default system output.
	 */
	void setLogFile(String filePath);

	/**
	 * Loads the <b>knowledge base</b>, consisting of the current rules and facts,
	 * into the reasoner (if it has not been loaded yet). After loading, the
	 * reasoner is ready for reasoning and querying.
	 *
	 * @throws IOException if an I/O error occurs related to the resources in the
	 *                     <b>knowledge base</b> to be loaded.
	 */
	void load() throws IOException;

	/**
	 * Checks whether the loaded rules and loaded fact EDB predicates are Acyclic,
	 * Cyclic, or cyclicity cannot be determined.
	 * 
	 * @return
	 */
	CyclicityResult checkForCycles();

	/**
	 * Check the <b>Joint Acyclicity (JA)</b> property of loaded rules and EDB
	 * predicates of loaded facts. If a set of rules and EDB predicates is JA, then,
	 * for the given set of rules and any facts over the given EDB predicates,
	 * reasoning by {@link Algorithm#SKOLEM_CHASE Skolem chase} (and, implicitly,
	 * the {@link Algorithm#RESTRICTED_CHASE Restricted chase}) will always
	 * terminate
	 * 
	 * @return {@code true}, if the loaded set of rules is Joint Acyclic with
	 *         respect to the EDB predicates of loaded facts.<br>
	 *         {@code false}, otherwise
	 */
	boolean isJA();

	/**
	 * Check the <b>Restricted Joint Acyclicity (RJA)</b> property of loaded rules
	 * and EDB predicates of loaded facts. If a set of rules and EDB predicates is
	 * RJA, then, for the given set of rules and any facts over the given EDB
	 * predicates, reasoning by {@link Algorithm#RESTRICTED_CHASE Restricted chase}
	 * will always terminate
	 * 
	 * @return {@code true}, if the loaded set of rules is Restricted Joint Acyclic
	 *         with respect to the EDB predicates of loaded facts.<br>
	 *         {@code false}, otherwise
	 */
	boolean isRJA();

	/**
	 * Check the <b>Model-Faithful Acyclicity (MFA)</b> property of loaded rules and
	 * EDB predicates of loaded facts. If a set of rules and EDB predicates is MFA,
	 * then, for the given set of rules and any facts over the given EDB predicates,
	 * reasoning by {@link Algorithm#SKOLEM_CHASE Skolem chase} (and, implicitly,
	 * the {@link Algorithm#RESTRICTED_CHASE Restricted chase}) will always
	 * terminate
	 * 
	 * @return {@code true}, if the loaded set of rules is Model-Faithful Acyclic
	 *         with respect to the EDB predicates of loaded facts.<br>
	 *         {@code false}, otherwise
	 */
	boolean isMFA();

	/**
	 * Check the <b>Restricted Model-Faithful Acyclicity (RMFA)</b> property of
	 * loaded rules and EDB predicates of loaded facts. If a set of rules and EDB
	 * predicates is RMFA, then, for the given set of rules and any facts over the
	 * given EDB predicates, reasoning by {@link Algorithm#RESTRICTED_CHASE
	 * Restricted chase} will always terminate. If a set of rules and EDB predicates
	 * is MFA, then it is also JA.
	 * 
	 * @return {@code true}, if the loaded set of rules is Restricted Model-Faithful
	 *         Acyclic with respect to the EDB predicates of loaded facts.<br>
	 *         {@code false}, otherwise
	 */
	boolean isRMFA();

	/**
	 * Check the <b>Model-Faithful Cyclicity (MFC)</b> property of loaded rules and
	 * EDB predicates of loaded facts. If a set of rules and EDB predicates is MFC,
	 * then there exists a set of facts over the given EDB predicates for which
	 * reasoning by {@link Algorithm#SKOLEM_CHASE Skolem chase} algorithm is
	 * guaranteed not to terminate for the loaded rules. If a set of rules and EDB
	 * predicates is RMFA, then it is also RJA. Therefore, if a set or rules and EDB
	 * predicates is MFC, it is not MFA, nor JA.
	 * 
	 * @return {@code true}, if the loaded set of rules is Model-Faithful Cyclic
	 *         with respect to the EDB predicates of loaded facts.<br>
	 *         {@code false}, otherwise
	 */
	boolean isMFC();

	/**
	 * Performs reasoning on the loaded <b>knowledge base</b>, depending on the set
	 * {@link Algorithm}. Reasoning implies extending the set of explicit facts in
	 * the knowledge base with implicit facts inferred by knowledge base rules. <br>
	 * <br>
	 * In some cases, reasoning with rules with existentially quantified variables
	 * {@link Rule#getExistentiallyQuantifiedVariables()} may not terminate. We
	 * recommend reasoning with algorithm {@link Algorithm#RESTRICTED_CHASE}, as it
	 * leads to termination in more cases. <br>
	 * To avoid non-termination, a reasoning timeout can be set
	 * ({@link Reasoner#setReasoningTimeout(Integer)}). <br>
	 * <br>
	 * <b>Incremental reasoning</b> is not supported. To add more facts and rule to
	 * the <b>knowledge base</b> and reason again, the reasoner needs to be
	 * <b>reset</b> ({@link #resetReasoner()}) to the state of its knowledge base
	 * before loading. Then, more information can be added to the knowledge base,
	 * the reasoner can be loaded again, and querying and reasoning can be
	 * performed.
	 *
	 * @return
	 *         <ul>
	 *         <li>the value returned by the previous {@link Reasoner#reason()}
	 *         call, if successive reasoning is attempted before a
	 *         {@link Reasoner#resetReasoner()}.</li>
	 *         <li>{@code true}, if reasoning reached completion.</li>
	 *         <li>{@code false}, if reasoning has been interrupted before
	 *         completion.</li>
	 *         </ul>
	 * @throws IOException if I/O exceptions occur during reasoning.
	 */
	boolean reason() throws IOException;

	// TODO add examples to query javadoc
	/**
	 * Evaluates an atomic query ({@code queryAtom}) on the current state of the
	 * reasoner knowledge base:
	 * <ul>
	 * <li>If the reasoner is <b>loaded</b> (see {@link #load()}), but has not
	 * reasoned yet, the query will be evaluated on the explicit set of facts.</li>
	 * <li>Otherwise, if this method is called after <b>reasoning</b> (see
	 * {@link #reason()}, the query will be evaluated on the explicit and implicit
	 * facts inferred trough reasoning.</li>
	 * </ul>
	 * <br>
	 * An answer to the query is the terms a fact that matches the {@code quryAtom}:
	 * the fact predicate is the same as the {@code quryAtom} predicate, the
	 * {@link TermType#CONSTANT} terms of the {@code quryAtom} appear in the answer
	 * fact at the same term position, and the {@link TermType#VARIABLE} terms of
	 * the {@code quryAtom} are matched by terms in the fact, either named
	 * ({@link TermType#CONSTANT}) or anonymous ({@link TermType#BLANK}). The same
	 * variable name identifies the same term in the answer fact. <br>
	 * A query answer is represented by a {@link QueryResult}. A query can have
	 * multiple, distinct query answers. This method returns an Iterator over these
	 * answers.
	 *
	 * @param query         a {@link PositiveLiteral} representing the query to be
	 *                      answered.
	 * @param includeBlanks if {@code true}, facts containing terms of type
	 *                      {@link TermType#BLANK} (representing anonymous
	 *                      individuals introduced to satisfy rule existentially
	 *                      quantified variables) will be included into the query
	 *                      results. Otherwise, the query results will only contain
	 *                      the facts with terms of type {@link TermType#CONSTANT}
	 *                      (representing named individuals).
	 * @return QueryResultIterator that represents distinct answers to the query.
	 */
	QueryResultIterator answerQuery(PositiveLiteral query, boolean includeBlanks);

	// TODO add examples to query javadoc
	/**
	 * Evaluates an atomic query ({@code queryAtom}) on the current state of the
	 * reasoner knowledge base, and writes its results the <i><b>.csv</b></i> file
	 * at given path {@code csvFilePath}:
	 * <ul>
	 * <li>If the reasoner is <b>loaded</b> (see {@link #load()}), but has not
	 * reasoned yet, the query will be evaluated on the explicit set of facts.</li>
	 * <li>Otherwise, if this method is called after <b>reasoning</b> (see
	 * {@link #reason()}, the query will be evaluated on the explicit and implicit
	 * facts inferred trough reasoning.</li>
	 * </ul>
	 * <br>
	 * An answer to the query is the terms a fact that matches the {@code quryAtom}:
	 * the fact predicate is the same as the {@code quryAtom} predicate, the
	 * {@link TermType#CONSTANT} terms of the {@code quryAtom} appear in the answer
	 * fact at the same term position, and the {@link TermType#VARIABLE} terms of
	 * the {@code quryAtom} are matched by terms in the fact, either named
	 * ({@link TermType#CONSTANT}) or anonymous ({@link TermType#BLANK}). The same
	 * variable name identifies the same term in the answer fact. <br>
	 * A query answer is represented by a {@link QueryResult}. A query can have
	 * multiple, distinct query answers.
	 *
	 * @param query         a {@link PositiveLiteral} representing the query to be
	 *                      answered.
	 * @param csvFilePath   path to a <i><b>.csv</b></i> file where the query
	 *                      answers will be written. Each line of the
	 *                      <i><b>.csv</b></i> file represents a query answer fact,
	 *                      and it will contain the fact term names as columns.
	 * @param includeBlanks if {@code true}, facts containing terms of type
	 *                      {@link TermType#BLANK} (representing anonymous
	 *                      individuals introduced to satisfy rule existentially
	 *                      quantified variables) will be included into the query
	 *                      answers. Otherwise, the query answers will only contain
	 *                      the facts with terms of type {@link TermType#CONSTANT}
	 *                      (representing named individuals).
	 *
	 * @throws IOException if an I/O error occurs regarding given file
	 *                     ({@code csvFilePath)}.
	 */
	// TODO update javadoc with return type
	MaterialisationState exportQueryAnswersToCsv(PositiveLiteral query, String csvFilePath, boolean includeBlanks)
			throws IOException;

	/**
	 * Resets the reasoner to a pre-loading state (before the call of
	 * {@link #load()} method). All facts inferred by reasoning are discarded. Rules
	 * and facts added to the reasoner need to be loaded again, to be able to
	 * perform querying and reasoning.
	 */
	void resetReasoner();

	// TODO Map<Predicate,DataSource> exportDBToDir(File location);

	// TODO not allow any operation after closing, except close();

	@Override
	void close();

}
