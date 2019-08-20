package org.semanticweb.vlog4j.core.reasoner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
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

public class KnowledgeBase extends Observable {

	private final List<Rule> rules = new ArrayList<>();
	private final Map<Predicate, Set<PositiveLiteral>> factsForPredicate = new HashMap<>();
	private final Map<Predicate, DataSource> dataSourceForPredicate = new HashMap<>();

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
	public void addRules(@NonNull Rule... rules) {
		addRules(Arrays.asList(rules));
	}

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
	public void addRules(@NonNull List<Rule> rules) {
		Validate.noNullElements(rules, "Null rules are not alowed! The list contains a null at position [%d].");
		this.rules.addAll(new ArrayList<>(rules));

		// TODO setChanged
		// TODO notify listeners with the diff
	}

	/**
	 * Get the list of all rules that have been added to the reasoner. The list is
	 * read-only and cannot be modified to add or delete rules.
	 * 
	 * @return list of {@link Rule}
	 */
	public List<Rule> getRules() {
		return Collections.unmodifiableList(this.rules);
	}

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
	public void addFacts(final Fact... facts) {
		addFacts(Arrays.asList(facts));

		// TODO setChanged
		// TODO notify listeners with the diff
	}

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
	public void addFacts(final Collection<Fact> facts) {
		Validate.noNullElements(facts, "Null facts are not alowed! The list contains a fact at position [%d].");
		for (final PositiveLiteral fact : facts) {
			validateFactTermsAreConstant(fact);

			final Predicate predicate = fact.getPredicate();
			validateNoDataSourceForPredicate(predicate);

			this.factsForPredicate.putIfAbsent(predicate, new HashSet<>());
			this.factsForPredicate.get(predicate).add(fact);
		}
	}

	/**
	 * Adds facts stored in given {@code dataSource} for given {@code predicate} to
	 * the <b>knowledge base</b>. Facts predicates cannot have multiple data
	 * sources, including in-memory {@link Atom} objects added trough
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
	public void addFactsFromDataSource(Predicate predicate, DataSource dataSource) {
		Validate.notNull(predicate, "Null predicates are not allowed!");
		Validate.notNull(dataSource, "Null dataSources are not allowed!");
		validateNoDataSourceForPredicate(predicate);
		Validate.isTrue(!this.factsForPredicate.containsKey(predicate),
				"Multiple data sources for the same predicate are not allowed! Facts for predicate [%s] alredy added in memory: %s",
				predicate, this.factsForPredicate.get(predicate));

		this.dataSourceForPredicate.put(predicate, dataSource);
	}

	public boolean hasFacts() {
		return !this.dataSourceForPredicate.isEmpty() || !this.factsForPredicate.isEmpty();
	}

	public Map<Predicate, DataSource> getDataSourceForPredicate() {
		return this.dataSourceForPredicate;
	}

	public Map<Predicate, Set<PositiveLiteral>> getFactsForPredicate() {
		return this.factsForPredicate;
	}

	public Set<Predicate> getEdbPredicates() {
		// TODO use cache
		return collectEdbPredicates();
	}

	public Set<Predicate> getIdbPredicates() {
		// TODO use cache
		return collectIdbPredicates();
	}

	private void validateFactTermsAreConstant(PositiveLiteral fact) {
		final Set<Term> nonConstantTerms = new HashSet<>(fact.getTerms());
		nonConstantTerms.removeAll(fact.getConstants());
		Validate.isTrue(nonConstantTerms.isEmpty(),
				"Only Constant terms alowed in Fact literals! The following non-constant terms [%s] appear for fact [%s]!",
				nonConstantTerms, fact);

	}

	private void validateNoDataSourceForPredicate(final Predicate predicate) {
		Validate.isTrue(!this.dataSourceForPredicate.containsKey(predicate),
				"Multiple data sources for the same predicate are not allowed! Facts for predicate [%s] alredy added from data source: %s",
				predicate, this.dataSourceForPredicate.get(predicate));
	}

	private Set<Predicate> collectEdbPredicates() {
		final Set<Predicate> edbPredicates = new HashSet<>();
		edbPredicates.addAll(this.dataSourceForPredicate.keySet());
		edbPredicates.addAll(this.factsForPredicate.keySet());
		return edbPredicates;
	}

	private Set<Predicate> collectIdbPredicates() {
		final Set<Predicate> idbPredicates = new HashSet<>();
		for (final Rule rule : this.rules) {
			for (final Literal headAtom : rule.getHead()) {
				idbPredicates.add(headAtom.getPredicate());
			}
		}
		return idbPredicates;
	}

}
