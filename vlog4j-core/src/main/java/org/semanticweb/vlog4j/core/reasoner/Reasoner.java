package org.semanticweb.vlog4j.core.reasoner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
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
 * <b>knowledge base</b> can be <b>loaded</b> into the reasoner. The following
 * <b>pre-condition</b> must be respected: the {@link Predicate}s appearing in
 * {@link Rule} heads (called IDBs) cannot also appear in knowledge base
 * <b>facts</b> (called EDBs). An {@link EdbIdbSeparationException} would be
 * thrown when loading the knowledge base.<br>
 * 
 * <br>
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
 * @author Irina Dragoste
 *
 */
public interface Reasoner extends AutoCloseable {

	/**
	 * Factory method that to instantiate a Reasoner.
	 * 
	 * @return a {@link VLogReasoner} instance.
	 */
	public static Reasoner getInstance() {
		return new VLogReasoner();
	}

	/**
	 * Sets the algorithm that will be used for reasoning over the knowledge base.
	 * If no algorithm is set, the default algorithm is
	 * {@link Algorithm#RESTRICTED_CHASE} will be used.
	 * 
	 * @param algorithm
	 *            the algorithm to be used for reasoning.
	 */
	void setAlgorithm(@NonNull Algorithm algorithm);

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
	 * @param seconds
	 *            interval after which reasoning will be interrupted, in seconds. If
	 *            {@code null}, reasoning will not be interrupted and will return
	 *            only after (if) it has reached completion.
	 */
	void setReasoningTimeout(@Nullable Integer seconds);

	/**
	 * This method returns the reasoning timeout, representing the interval (in
	 * {@code seconds}) after which reasoning will be interrupted if it has not
	 * reached completion. The default value is {@code null}, in which case
	 * reasoning terminates only after (if) it reaches completion.
	 * 
	 * @return if not {@code null}, number of seconds after which the reasoning will
	 *         be interrupted, if it has not reached completion.
	 */
	@Nullable
	Integer getReasoningTimeout();

	/**
	 * Loaded {@link Rule}s can be re-written internally to an equivalent set of
	 * rules, according to given {@code ruleRewritingStrategy}. If no staregy is
	 * set, the default value is {@link RuleRewriteStrategy#NONE}, meaning that the
	 * rules will not be re-written.
	 * 
	 * @param ruleRewritingStrategy
	 *            strategy according to which the rules will be rewritten before
	 *            reasoning.
	 * @throws ReasonerStateException
	 *             if the reasoner has already been loaded.
	 */
	void setRuleRewriteStrategy(@NonNull RuleRewriteStrategy ruleRewritingStrategy) throws ReasonerStateException;

	/**
	 * Getter for the strategy according to which rules will be rewritten before
	 * reasoning. The default value is {@link RuleRewriteStrategy#NONE}, meaning
	 * that the rules will not be re-written.
	 * 
	 * @return the current rule re-writing strategy
	 */
	@NonNull
	RuleRewriteStrategy getRuleRewriteStrategy();

	/**
	 * Sets the logging level of the internal VLog C++ resource. Default value is
	 * {@link LogLevel#WARNING}
	 * 
	 * @param logLevel
	 *            the logging level to be set for VLog C++ resource.
	 */
	void setLogLevel(@NonNull LogLevel logLevel);

	/**
	 * Returns the logging level of the internal VLog C++ resource. If no value has
	 * been set, the default is {@link LogLevel#WARNING}.
	 * 
	 * @return the logging level of the VLog C++ resource.
	 */
	@Nullable
	LogLevel getLogLevel();

	/**
	 * Redirects the logs of the internal VLog C++ resource to given file. If no log
	 * file is set or the given {@code filePath} is not a valid file path, VLog will
	 * log to the default system output.
	 * 
	 * @param filePath
	 *            the file for the internal VLog C++ resource to log to. If
	 *            {@code null} or an invalid file path, the reasoner will log to the
	 *            default system output.
	 */
	void setLogFile(@Nullable String filePath);

	/**
	 * Adds rules to the reasoner <b>knowledge base</b> in the given order. After
	 * the reasoner has been loaded ({@link #load()}), the rules may be rewritten
	 * internally according to the set {@link RuleRewriteStrategy}.
	 * 
	 * @param rules
	 *            non-null rules to be added to the <b>knowledge base</b> for
	 *            reasoning.
	 * @throws ReasonerStateException
	 *             if the reasoner has already been loaded.
	 * @throws IllegalArgumentException
	 *             if the {@code rules} atoms contain terms which are not of type
	 *             {@link TermType#CONSTANT} or {@link TermType#VARIABLE}.
	 */
	void addRules(@NonNull Rule... rules) throws ReasonerStateException;

	/**
	 * Adds rules to the reasoner <b>knowledge base</b> in the given order. Rules
	 * can only be added before loading ({@link #load()}). After the reasoner has
	 * been loaded, the rules may be rewritten internally according to the set
	 * {@link RuleRewriteStrategy}.
	 * 
	 * @param rules
	 *            non-null rules to be added to the <b>knowledge base</b> for
	 *            reasoning.
	 * @throws ReasonerStateException
	 *             if the reasoner has already been loaded.
	 * @throws IllegalArgumentException
	 *             if the {@code rules} atoms contain terms which are not of type
	 *             {@link TermType#CONSTANT} or {@link TermType#VARIABLE}.
	 */
	void addRules(@NonNull List<Rule> rules) throws ReasonerStateException;

	/**
	 * Adds non-null facts to the reasoner <b>knowledge base</b>. A <b>fact</b> is
	 * an {@link Atom} with all terms ({@link Atom#getTerms()}) of type
	 * {@link TermType#CONSTANT}. <br>
	 * Facts can only be added before loading ({@link #load()}). <br>
	 * Facts predicates ({@link Atom#getPredicate()}) cannot have multiple data
	 * sources.
	 * 
	 * @param facts
	 *            facts to be added to the <b>knowledge base</b>. The given order is
	 *            not maintained.
	 * @throws ReasonerStateException
	 *             if the reasoner has already been loaded ({@link #load()}).
	 * @throws IllegalArgumentException
	 *             if the <b>knowledge base</b> contains facts from a data source
	 *             with the same predicate ({@link Atom#getPredicate()}) as an
	 *             {@link Atom} among given {@code facts}.
	 * @throws IllegalArgumentException
	 *             if the {@code facts} atoms contain terms which are not of type
	 *             {@link TermType#CONSTANT}.
	 */
	// TODO add examples to javadoc about multiple sources per predicate and EDB/IDB
	void addFacts(@NonNull Atom... facts) throws ReasonerStateException;

	/**
	 * Adds non-null facts to the reasoner <b>knowledge base</b>. A <b>fact</b> is
	 * an {@link Atom} with all terms ({@link Atom#getTerms()}) of type
	 * {@link TermType#CONSTANT}. <br>
	 * Facts can only be added before loading ({@link #load()}). <br>
	 * Facts predicates ({@link Atom#getPredicate()}) cannot have multiple data
	 * sources.
	 * 
	 * @param facts
	 *            facts to be added to the <b>knowledge base</b>.
	 * @throws ReasonerStateException
	 *             if the reasoner has already been loaded ({@link #load()}).
	 * @throws IllegalArgumentException
	 *             if the <b>knowledge base</b> contains facts from a data source
	 *             with the same predicate ({@link Atom#getPredicate()}) as an
	 *             {@link Atom} among given {@code facts}.
	 * @throws IllegalArgumentException
	 *             if the {@code facts} atoms contain terms which are not of type
	 *             {@link TermType#CONSTANT}.
	 */
	// TODO add examples to javadoc about multiple sources per predicate and EDB/IDB
	void addFacts(@NonNull Collection<Atom> facts) throws ReasonerStateException;

	/**
	 * Adds facts stored in given {@code dataSource} for given {@code predicate} to
	 * the reasoner <b>knowledge base</b>. Facts predicates cannot have multiple
	 * data sources, including in-memory {@link Atom} objects added trough
	 * {@link #addFacts}.
	 * 
	 * @param predicate
	 *            the {@link Predicate} for which the given {@code dataSource}
	 *            contains <b>facts</b>.
	 * @param dataSource
	 * @throws ReasonerStateException
	 *             if the reasoner has already been loaded ({@link #load()}).
	 * @throws IllegalArgumentException
	 *             if the <b>knowledge base</b> contains facts in memory (added
	 *             using {@link #addFacts}) or from a data source with the same
	 *             {@link Predicate} as given {@code predicate}.
	 */
	// TODO add example to javadoc with two datasources and with in-memory facts for
	// the same predicate.
	// TODO validate predicate arity corresponds to the dataSource facts arity
	void addFactsFromDataSource(@NonNull Predicate predicate, @NonNull DataSource dataSource)
			throws ReasonerStateException;

	/**
	 * Loads the <b>knowledge base</b>, consisting of the current rules and facts,
	 * into the reasoner (if it has not been loaded yet). If the reasoner has
	 * already been loaded, this call does nothing. After loading, the reasoner is
	 * ready for reasoning and querying.<br>
	 * Loading <b>pre-condition</b>: the {@link Predicate}s appearing in
	 * {@link Rule} heads ({@link Rule#getHead()}), called IDB predicates, cannot
	 * also appear in knowledge base <b>facts</b>, called EDB predicates. An
	 * {@link EdbIdbSeparationException} would be thrown in this case.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs related to the resources in the
	 *             <b>knowledge base</b> to be loaded.
	 * @throws EdbIdbSeparationException
	 *             if a {@link Predicate} appearing in a {@link Rule} <b>head</b>
	 *             (IDB predicate) also appears in a knowledge base <b>fact</b> (EDB
	 *             predicate).
	 * @throws IncompatiblePredicateArityException
	 *             if the arity of a {@link Predicate} of a fact loaded from a data
	 *             source ({@link #addFactsFromDataSource(Predicate, DataSource)})
	 *             does nor match the arity of the facts in the corresponding data
	 *             source.
	 */
	// FIXME should EdbIdbSeparationException be thrown when users try to add
	// facts/rules?
	void load() throws IOException, EdbIdbSeparationException, IncompatiblePredicateArityException;

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
	 * @throws IOException
	 *             if I/O exceptions occur during reasoning.
	 * @throws ReasonerStateException
	 *             if this method is called before loading ({@link Reasoner#load()}.
	 */
	boolean reason() throws IOException, ReasonerStateException;

	// TODO add examples to query javadoc
	/**
	 * Evaluates an atomic query ({@code queryAtom}) on the current state of the
	 * reasoner knowledge base:
	 * <ul>
	 * <li>If the reasoner is <b>loaded</b> (see {@link #load()}), but has not
	 * reasoned yet, the query will be evaluated on the explicit set of facts.</li>
	 * <li>Otherwise, if this method is called after </b>reasoning</b> (see
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
	 * @param queryAtom
	 *            an {@link Atom} representing the query to be answered.
	 * @param includeBlanks
	 *            if {@code true}, facts containing terms of type
	 *            {@link TermType#BLANK} (representing anonymous individuals
	 *            introduced to satisfy rule existentially quantified variables)
	 *            will be included into the query results. Otherwise, the query
	 *            results will only contain the facts with terms of type
	 *            {@link TermType#CONSTANT} (representing named individuals).
	 * @return an {@link AutoCloseable} iterator for {@link QueryResult}s,
	 *         representing distinct answers to the query.
	 * @throws ReasonerStateException
	 *             if this method is called before loading ({@link Reasoner#load()}.
	 * @throws IllegalArgumentException
	 *             if the given {@code queryAtom} contains terms
	 *             ({@link Atom#getTerms()}) which are not of type
	 *             {@link TermType#CONSTANT} or {@link TermType#VARIABLE}.
	 */
	QueryResultIterator answerQuery(@NonNull Atom queryAtom, boolean includeBlanks) throws ReasonerStateException;

	// TODO add examples to query javadoc
	/**
	 * Evaluates an atomic query ({@code queryAtom}) on the current state of the
	 * reasoner knowledge base, and writes its results the <i><b>.csv</b></i> file
	 * at given path {@code csvFilePath}:
	 * <ul>
	 * <li>If the reasoner is <b>loaded</b> (see {@link #load()}), but has not
	 * reasoned yet, the query will be evaluated on the explicit set of facts.</li>
	 * <li>Otherwise, if this method is called after </b>reasoning</b> (see
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
	 * @param queryAtom
	 *            an {@link Atom} representing the query to be answered.
	 * @param csvFilePath
	 *            path to a <i><b>.csv</b></i> file where the query answers will be
	 *            written. Each line of the <i><b>.csv</b></i> file represents a
	 *            query answer fact, and it will contain the fact term names as
	 *            columns.
	 * @param includeBlanks
	 *            if {@code true}, facts containing terms of type
	 *            {@link TermType#BLANK} (representing anonymous individuals
	 *            introduced to satisfy rule existentially quantified variables)
	 *            will be included into the query answers. Otherwise, the query
	 *            answers will only contain the facts with terms of type
	 *            {@link TermType#CONSTANT} (representing named individuals).
	 * 
	 * @throws ReasonerStateException
	 *             if this method is called before loading ({@link Reasoner#load()}.
	 * @throws IOException
	 *             if an I/O error occurs regarding given file
	 *             ({@code csvFilePath)}.
	 * @throws IllegalArgumentException
	 *             <ul>
	 *             <li>if the given {@code queryAtom} contains terms
	 *             ({@link Atom#getTerms()}) which are not of type
	 *             {@link TermType#CONSTANT} or {@link TermType#VARIABLE}.</li>
	 *             <li>if the given {@code csvFilePath} does not end with
	 *             <i><b>.csv</b></i> extension.</li>
	 *             </ul>
	 */
	void exportQueryAnswersToCsv(@NonNull Atom queryAtom, @NonNull String csvFilePath, boolean includeBlanks)
			throws ReasonerStateException, IOException;

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
