package org.semanticweb.rulewerk.asp.implementation;

/*-
 * #%L
 * Rulewerk Core Components
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
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

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.asp.model.AnswerSet;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Implementation of an answer set
 *
 * @author Philipp Hanisch
 */
public class AnswerSetImpl implements AnswerSet {

	private Map<Predicate, Set<Literal>> answerSet;

	/**
	 * Constructor. Takes a string representation of an answer set, i.e., an space separated list of integers, and a map
	 * that resolves the integers to the corresponding literals.
	 *
	 * @param answerSetString   the string representation of an answer set
	 * @param integerLiteralMap a integer-to-literal map
	 */
	public AnswerSetImpl(String answerSetString, Map<Integer, Literal> integerLiteralMap) {
		Validate.notNull(answerSetString, "The string representation of an answer set cannot be null");
		Validate.notNull(integerLiteralMap);

		answerSet = new HashMap<>();
		if (!answerSetString.isEmpty()) {
			for (String integerString : answerSetString.trim().split(" ")) {
				Integer literalInteger = Integer.parseInt(integerString);
				Literal literal = integerLiteralMap.get(literalInteger);
				Validate.notNull(literal, "The integer of a literal in the answer set is unknown");
				Set<Literal> answersByPredicate;
				if ((answersByPredicate = answerSet.get(literal.getPredicate())) == null) {
					answersByPredicate = new HashSet<>();
					answerSet.put(literal.getPredicate(), answersByPredicate);
				}
				answersByPredicate.add(literal);
			};
		}
	};

	@Override
	public QueryResultIterator getQueryResults(Predicate predicate) {
		return new AspQueryResultIterator(getLiterals(predicate));
	}

	@Override
	public QueryResultIterator getQueryResults(PositiveLiteral query) {
		if (query.getArguments().stream().noneMatch(Term::isConstant)) {
			return getQueryResults(query.getPredicate());
		}

		Set<Literal> answers = new HashSet<>();
		List<Term> queryTerms = query.getArguments();
		for (Literal literal : getLiterals(query.getPredicate())) {
			if (IntStream.range(0, queryTerms.size()).allMatch(i -> !queryTerms.get(i).isConstant()
				|| (queryTerms.get(i).equals(literal.getArguments().get(i))))) {
				answers.add(literal);
			};
		}
		return new AspQueryResultIterator(answers);
	}

	@Override
	public Set<Literal> getLiterals() {
		Set<Literal> answers = new HashSet<>();
		for (Set<Literal> answersByPredicate : answerSet.values()) {
			answers.addAll(answersByPredicate);
		}
		return Collections.unmodifiableSet(answers);
	}

	@Override
	public Set<Literal> getLiterals(Predicate predicate) {
		return Collections.unmodifiableSet(answerSet.getOrDefault(predicate, Collections.emptySet()));
	}
}
