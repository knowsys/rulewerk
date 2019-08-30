package org.semanticweb.vlog4j.core.reasoner;

import java.io.IOException;

import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.TermType;
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
 * <li>as in-memory Java objects ({@link Fact})</li>
 * <li>from a persistent data source ({@link DataSourceDeclaration})</li>
 * </ul>
 * <br>
 * <b>Rules</b> added to the knowledge base can be re-written internally by
 * VLog, using the corresponding set {@link RuleRewriteStrategy}. <br>
 * <br>
 *
 * The loaded reasoner can perform <b>atomic queries</b> on explicit and
 * implicit facts after calling {@link Reasoner#reason()}. Queries can provide
 * an iterator for the results ({@link #answerQuery(Atom, boolean)}, or the
 * results can be exported to a file
 * ({@link #exportQueryAnswersToCsv(Atom, String, boolean)}). <br>
 * <br>
 * <b>Reasoning</b> with various {@link Algorithm}s is supported, that can lead
 * to different sets of inferred facts and different termination behavior. In
 * some cases, reasoning with rules with existentially quantified variables
 * {@link Rule#getExistentiallyQuantifiedVariables()} may not terminate. We
 * recommend reasoning with algorithm {@link Algorithm#RESTRICTED_CHASE}, as it
 * leads to termination in more cases. To avoid non-termination, a reasoning
 * timeout can be set ({@link Reasoner#setReasoningTimeout(Integer)}). <br>
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
	 * Checks whether the loaded rules and loaded fact EDB predicates are Acyclic,
	 * Cyclic, or cyclicity cannot be determined.
	 * 
	 * @return
	 */
	CyclicityResult checkForCycles();

	/**
	 * Check the <b>Joint Acyclicity (JA)</b> property of loaded rules and EDB
	 * predicates of loaded facts. If a set of rules and EDB predicates is
	 * <b>J</b>A, then, for the given set of rules and any facts over the given EDB
	 * predicates, reasoning by {@link Algorithm#SKOLEM_CHASE Skolem chase} (and,
	 * implicitly, the {@link Algorithm#RESTRICTED_CHASE Restricted chase}) will
	 * always terminate.
	 * 
	 * @return {@code true}, if the loaded set of rules is Joint Acyclic with
	 *         respect to the EDB predicates of loaded facts.<br>
	 *         {@code false}, otherwise
	 */
	boolean isJA();

	/**
	 * Check the <b>Restricted Joint Acyclicity (RJA)</b> property of loaded rules
	 * and EDB predicates of loaded facts. If a set of rules and EDB predicates is
	 * <b>RJA</b>, then, for the given set of rules and any facts over the given EDB
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
	 * EDB predicates of loaded facts. If a set of rules and EDB predicates is
	 * <b>MFA</b>, then, for the given set of rules and any facts over the given EDB
	 * predicates, reasoning by {@link Algorithm#SKOLEM_CHASE Skolem chase} (and,
	 * implicitly, the {@link Algorithm#RESTRICTED_CHASE Restricted chase}) will
	 * always terminate
	 * 
	 * @return {@code true}, if the loaded set of rules is Model-Faithful Acyclic
	 *         with respect to the EDB predicates of loaded facts.<br>
	 *         {@code false}, otherwise
	 */
	boolean isMFA();

	/**
	 * Check the <b>Restricted Model-Faithful Acyclicity (RMFA)</b> property of
	 * loaded rules and EDB predicates of loaded facts. If a set of rules and EDB
	 * predicates is <b>RMFA</b>, then, for the given set of rules and any facts
	 * over the given EDB predicates, reasoning by {@link Algorithm#RESTRICTED_CHASE
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
	 * EDB predicates of loaded facts. If a set of rules and EDB predicates is
	 * <b>MFC</b>, then there exists a set of facts over the given EDB predicates
	 * for which reasoning by {@link Algorithm#SKOLEM_CHASE Skolem chase} algorithm
	 * is guaranteed not to terminate for the loaded rules. If a set of rules and
	 * EDB predicates is RMFA, then it is also RJA. Therefore, if a set or rules and
	 * EDB predicates is MFC, it is not MFA, nor JA.
	 * 
	 * @return {@code true}, if the loaded set of rules is Model-Faithful Cyclic
	 *         with respect to the EDB predicates of loaded facts.<br>
	 *         {@code false}, otherwise
	 */
	boolean isMFC();

	/**
	 * Performs materialisation on the reasoner {@link KnowledgeBase}, depending on
	 * the set {@link Algorithm}. Materialisation implies extending the set of
	 * explicit facts in the knowledge base with implicit facts inferred by
	 * knowledge base rules. <br>
	 * <br>
	 * In some cases, reasoning with rules with existentially quantified variables
	 * {@link Rule#getExistentiallyQuantifiedVariables()} may not terminate. We
	 * recommend reasoning with algorithm {@link Algorithm#RESTRICTED_CHASE}, as it
	 * leads to termination in more cases. <br>
	 * To avoid non-termination, a reasoning timeout can be set
	 * ({@link Reasoner#setReasoningTimeout(Integer)}). <br>
	 * 
	 * @return
	 *         <ul>
	 *         <li>{@code true}, if materialisation reached completion.</li>
	 *         <li>{@code false}, if materialisation has been interrupted before
	 *         completion.</li>
	 *         </ul>
	 * @throws IOException if I/O exceptions occur during reasoning.
	 */
	boolean reason() throws IOException;

	// TODO add examples to query javadoc
	/**
	 * Evaluates an atomic query ({@code query}) on the implicit facts loaded into
	 * the reasoner and the explicit facts materialised by the reasoner. <br>
	 * An answer to the query is the terms a fact that matches the {@code query}:
	 * the fact predicate is the same as the {@code query} predicate, the
	 * {@link TermType#CONSTANT} terms of the {@code query} appear in the answer
	 * fact at the same term position, and the {@link TermType#VARIABLE} terms of
	 * the {@code query} are matched by terms in the fact, either named
	 * ({@link TermType#CONSTANT}) or anonymous ({@link TermType#BLANK}). The same
	 * variable name identifies the same term in the answer fact. <br>
	 * A query answer is represented by a {@link QueryResult}. A query can have
	 * multiple, distinct query answers. This method returns an Iterator over these
	 * answers. <br>
	 * 
	 * Depending on the state of the reasoning (materialisation) and its
	 * {@link KnowledgeBase}, the answers can have a different {@link Correctness}
	 * ({@link QueryResultIterator#getCorrectness()}):
	 * <ul>
	 * <li>If {@link Correctness#SOUND_AND_COMPLETE}, materialisation over current
	 * knowledge base has completed, and the query answers are guaranteed to be
	 * correct.</li>
	 * <li>If {@link Correctness#SOUND_BUT_INCOMPLETE}, the results are guaranteed
	 * to be sound, but may be incomplete. This can happen
	 * <ul>
	 * <li>when materialisation has not completed ({@link Reasoner#reason()} returns
	 * {@code false}),</li>
	 * <li>or when the knowledge base was modified after reasoning, and the
	 * materialisation does not reflect the current knowledge base.
	 * Re-materialisation ({@link Reasoner#reason()}) is required in order to obtain
	 * complete query answers with respect to the current knowledge base.</li>
	 * </ul>
	 * </li>
	 * <li>If {@link Correctness#INCORRECT}, the results may be incomplete, and some
	 * results may be unsound. This can happen when the knowledge base was modified
	 * and the reasoner materialisation is no longer consistent with the current
	 * knowledge base. Re-materialisation ({@link Reasoner#reason()}) is required,
	 * in order to obtain correct query answers.
	 * </ul>
	 * 
	 *
	 * @param query         a {@link PositiveLiteral} representing the query to be
	 *                      answered.
	 * @param includeBlanks if {@code true}, {@link QueryResult}s containing terms
	 *                      of type {@link TermType#BLANK} (representing anonymous
	 *                      individuals introduced to satisfy rule existentially
	 *                      quantified variables) will be included. Otherwise, the
	 *                      answers will only contain the {@link QueryResult}s with
	 *                      terms of type {@link TermType#CONSTANT} (representing
	 *                      named individuals).
	 * @return QueryResultIterator that iterates over distinct answers to the query.
	 *         It also contains the {@link Correctness} of the query answers.
	 */
	QueryResultIterator answerQuery(PositiveLiteral query, boolean includeBlanks);

	// TODO add examples to query javadoc
	/**
	 * Evaluates an atomic query ({@code query}) on the implicit facts loaded into
	 * the reasoner and the explicit facts materialised by the reasoner, and writes
	 * its answers the <i><b>.csv</b></i> file at given path {@code csvFilePath}:
	 * <br>
	 * An answer to the query is the terms a fact that matches the {@code query}:
	 * the fact predicate is the same as the {@code query} predicate, the
	 * {@link TermType#CONSTANT} terms of the {@code query} appear in the answer
	 * fact at the same term position, and the {@link TermType#VARIABLE} terms of
	 * the {@code query} are matched by terms in the fact, either named
	 * ({@link TermType#CONSTANT}) or anonymous ({@link TermType#BLANK}). The same
	 * variable name identifies the same term in the answer fact. <br>
	 * A query can have multiple, distinct query answers. Each answers is written on
	 * a separate line in the given file.
	 *
	 * @param query         a {@link PositiveLiteral} representing the query to be
	 *                      answered.
	 * @param csvFilePath   path to a <i><b>.csv</b></i> file where the query
	 *                      answers will be written. Each line of the
	 *                      <i><b>.csv</b></i> file represents a query answer, and
	 *                      it will contain the fact term names as columns.
	 * @param includeBlanks if {@code true}, answers containing terms of type
	 *                      {@link TermType#BLANK} (representing anonymous
	 *                      individuals introduced to satisfy rule existentially
	 *                      quantified variables) will be included. Otherwise, the
	 *                      answers will only contain those with terms of type
	 *                      {@link TermType#CONSTANT} (representing named
	 *                      individuals).
	 *
	 * @throws IOException if an I/O error occurs regarding given file
	 *                     ({@code csvFilePath)}.
	 * @return the correctness of the query answers, depending on the state of the
	 *         reasoning (materialisation) and its {@link KnowledgeBase}:
	 *         <ul>
	 *         <li>If {@link Correctness#SOUND_AND_COMPLETE}, materialisation over
	 *         current knowledge base has completed, and the query answers are
	 *         guaranteed to be correct.</li>
	 *         <li>If {@link Correctness#SOUND_BUT_INCOMPLETE}, the results are
	 *         guaranteed to be sound, but may be incomplete. This can happen
	 *         <ul>
	 *         <li>when materialisation has not completed ({@link Reasoner#reason()}
	 *         returns {@code false}),</li>
	 *         <li>or when the knowledge base was modified after reasoning, and the
	 *         materialisation does not reflect the current knowledge base.
	 *         Re-materialisation ({@link Reasoner#reason()}) is required in order
	 *         to obtain complete query answers with respect to the current
	 *         knowledge base.</li>
	 *         </ul>
	 *         </li>
	 *         <li>If {@link Correctness#INCORRECT}, the results may be incomplete,
	 *         and some results may be unsound. This can happen when the knowledge
	 *         base was modified and the reasoner materialisation is no longer
	 *         consistent with the current knowledge base. Re-materialisation
	 *         ({@link Reasoner#reason()}) is required, in order to obtain correct
	 *         query answers.
	 *         </ul>
	 * 
	 */
	Correctness exportQueryAnswersToCsv(PositiveLiteral query, String csvFilePath, boolean includeBlanks)
			throws IOException;

	/**
	 * Resets the reasoner. All implicit facts inferred by reasoning are discarded.
	 */
	void resetReasoner();

	// TODO Map<Predicate,DataSource> exportDBToDir(File location);

	@Override
	void close();

}
