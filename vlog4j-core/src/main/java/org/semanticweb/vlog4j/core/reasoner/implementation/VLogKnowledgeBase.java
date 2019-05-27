package org.semanticweb.vlog4j.core.reasoner.implementation;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;

public class VLogKnowledgeBase extends KnowledgeBase {

	private final List<Rule> rules = new ArrayList<>();
	private final Map<Predicate, Set<PositiveLiteral>> factsForPredicate = new HashMap<>();
	private final Map<Predicate, DataSource> dataSourceForPredicate = new HashMap<>();

	

	@Override
	public void addRules(final Rule... rules) {
		addRules(Arrays.asList(rules));
	}

	@Override
	public void addRules(final List<Rule> rules) {
		Validate.noNullElements(rules, "Null rules are not alowed! The list contains a null at position [%d].");
		this.rules.addAll(new ArrayList<>(rules));

		// TODO setChanged
		// TODO notify listeners with the diff
	}

	@Override
	public List<Rule> getRules() {
		return Collections.unmodifiableList(this.rules);
	}

	@Override
	public void addFacts(final PositiveLiteral... facts) {
		addFacts(Arrays.asList(facts));

		// TODO setChanged
		// TODO notify listeners with the diff
	}

	@Override
	public void addFacts(final Collection<PositiveLiteral> facts) {
		Validate.noNullElements(facts, "Null facts are not alowed! The list contains a fact at position [%d].");
		for (final PositiveLiteral fact : facts) {
			validateFactTermsAreConstant(fact);

			final Predicate predicate = fact.getPredicate();
			validateNoDataSourceForPredicate(predicate);

			this.factsForPredicate.putIfAbsent(predicate, new HashSet<>());
			this.factsForPredicate.get(predicate).add(fact);
		}
	}

	@Override
	public void addFactsFromDataSource(final Predicate predicate, final DataSource dataSource) {
		Validate.notNull(predicate, "Null predicates are not allowed!");
		Validate.notNull(dataSource, "Null dataSources are not allowed!");
		validateNoDataSourceForPredicate(predicate);
		Validate.isTrue(!this.factsForPredicate.containsKey(predicate),
				"Multiple data sources for the same predicate are not allowed! Facts for predicate [%s] alredy added in memory: %s",
				predicate, this.factsForPredicate.get(predicate));

		this.dataSourceForPredicate.put(predicate, dataSource);
	}
	
	boolean hasFacts() {
		return !this.dataSourceForPredicate.isEmpty() || !this.factsForPredicate.isEmpty();
	}
	
	Map<Predicate, DataSource> getDataSourceForPredicate() {
		return this.dataSourceForPredicate;
	}

	Map<Predicate, Set<PositiveLiteral>> getFactsForPredicate() {
		return this.factsForPredicate;
	}
	
	Set<Predicate> getEdbPredicates() {
		// TODO use cache
		return collectEdbPredicates();
	}

	Set<Predicate> getIdbPredicates() {
		// TODO use cache
		return collectIdbPredicates();
	}
	
	String generateDataSourcesConfig() {
		final StringBuilder configStringBuilder = new StringBuilder();
		int dataSourceIndex = 0;
		for (final Predicate predicate : this.dataSourceForPredicate.keySet()) {
			final DataSource dataSource = this.dataSourceForPredicate.get(predicate);
			try (final Formatter formatter = new Formatter(configStringBuilder);) {
				formatter.format(dataSource.toConfigString(), dataSourceIndex,
						ModelToVLogConverter.toVLogPredicate(predicate));
			}
			dataSourceIndex++;
		}
		return configStringBuilder.toString();
	}
	
	void validateEdbIdbSeparation() throws EdbIdbSeparationException {
		final Set<Predicate> edbPredicates = getEdbPredicates();
		final Set<Predicate> idbPredicates = getIdbPredicates();
		final Set<Predicate> intersection = new HashSet<>(edbPredicates);
		intersection.retainAll(idbPredicates);
		if (!intersection.isEmpty()) {
			throw new EdbIdbSeparationException(intersection);
		}
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
