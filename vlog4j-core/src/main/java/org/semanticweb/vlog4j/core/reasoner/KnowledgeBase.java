package org.semanticweb.vlog4j.core.reasoner;

import java.util.Collection;
import java.util.List;
import java.util.Observable;

import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.TermType;

import karmaresearch.vlog.Atom;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

public abstract class KnowledgeBase extends Observable {

	/**
	 * Adds rules to the <b>knowledge base</b> in the given order. The reasoner may
	 * rewrite the rules internally according to the set
	 * {@link RuleRewriteStrategy}.
	 *
	 * @param rules non-null rules to be added to the <b>knowledge base</b> for
	 *              reasoning.
	 * @throws IllegalArgumentException if the {@code rules} literals contain terms
	 *                                  which are not of type
	 *                                  {@link TermType#CONSTANT} or
	 *                                  {@link TermType#VARIABLE}.
	 */
	public abstract void addRules(@NonNull Rule... rules);

	/**
	 * Adds rules to the <b>knowledge base</b> in the given order. The reasoner may
	 * rewrite the rules internally according to the set
	 * {@link RuleRewriteStrategy}.
	 *
	 * @param rules non-null rules to be added to the <b>knowledge base</b> for
	 *              reasoning.
	 * @throws IllegalArgumentException if the {@code rules} literals contain terms
	 *                                  which are not of type
	 *                                  {@link TermType#CONSTANT} or
	 *                                  {@link TermType#VARIABLE}.
	 */
	public abstract void addRules(@NonNull List<Rule> rules);

	/**
	 * Get the list of all rules that have been added to the reasoner. The list is
	 * read-only and cannot be modified to add or delete rules.
	 * 
	 * @return list of {@link Rule}
	 */
	public abstract List<Rule> getRules();
	
	/**
	 * Adds non-null facts to the <b>knowledge base</b>. A <b>fact</b> is a
	 * {@link PositiveLiteral} with all terms ({@link PositiveLiteral#getTerms()})
	 * of type {@link TermType#CONSTANT}. <br>
	 * Facts predicates ({@link PositiveLiteral#getPredicate()}) cannot have
	 * multiple data sources.
	 *
	 * @param facts facts to be added to the <b>knowledge base</b>. The given order
	 *              is not maintained.
	 * @throws IllegalArgumentException if the <b>knowledge base</b> contains facts
	 *                                  from a data source with the same predicate
	 *                                  ({@link PositiveLiteral#getPredicate()}) as
	 *                                  a {@link PositiveLiteral} among given
	 *                                  {@code facts}.
	 * @throws IllegalArgumentException if the {@code facts} literals contain terms
	 *                                  which are not of type
	 *                                  {@link TermType#CONSTANT}.
	 */
	// TODO add examples to javadoc about multiple sources per predicate and EDB/IDB
	public abstract void addFacts(@NonNull PositiveLiteral... facts);

	/**
	 * Adds non-null facts to the <b>knowledge base</b>. A <b>fact</b> is a
	 * {@link PositiveLiteral} with all terms ({@link PositiveLiteral#getTerms()})
	 * of type {@link TermType#CONSTANT}. <br>
	 * Facts predicates ({@link PositiveLiteral#getPredicate()}) cannot have
	 * multiple data sources.
	 *
	 * @param facts facts to be added to the <b>knowledge base</b>.
	 * @throws IllegalArgumentException if the <b>knowledge base</b> contains facts
	 *                                  from a data source with the same predicate
	 *                                  ({@link PositiveLiteral#getPredicate()}) as
	 *                                  an {@link PositiveLiteral} among given
	 *                                  {@code facts}.
	 * @throws IllegalArgumentException if the {@code facts} literals contain terms
	 *                                  which are not of type
	 *                                  {@link TermType#CONSTANT}.
	 */
	// TODO add examples to javadoc about multiple sources per predicate and EDB/IDB
	public abstract void addFacts(@NonNull Collection<PositiveLiteral> facts);

	/**
	 * Adds facts stored in given {@code dataSource} for given {@code predicate} to
	 * the <b>knowledge base</b>. Facts predicates cannot have multiple
	 * data sources, including in-memory {@link Atom} objects added trough
	 * {@link #addFacts}.
	 *
	 * @param predicate  the {@link Predicate} for which the given
	 *                   {@code dataSource} contains <b>fact terms</b>.
	 * @param dataSource data source containing the fact terms to be associated to
	 *                   given predicate and added to the reasoner
	 * @throws IllegalArgumentException if the <b>knowledge base</b> contains facts
	 *                                  in memory (added using {@link #addFacts}) or
	 *                                  from a data source with the same
	 *                                  {@link Predicate} as given
	 *                                  {@code predicate}.
	 */
	// TODO add example to javadoc with two datasources and with in-memory facts for
	// the same predicate.
	// TODO validate predicate arity corresponds to the dataSource facts arity
	public abstract void addFactsFromDataSource(@NonNull Predicate predicate, @NonNull DataSource dataSource);

}
