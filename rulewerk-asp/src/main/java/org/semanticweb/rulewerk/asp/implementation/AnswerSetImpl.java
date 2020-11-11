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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.asp.model.AnswerSet;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Implementation of an answer set
 *
 * @author Philipp Hanisch
 */
public class AnswerSetImpl implements AnswerSet {

	private final Map<Predicate, Set<Literal>> answerSet;

	/**
	 * Constructor. Takes a map of literals per predicates and extend its by the string representation of an answer set,
	 * i.e., an space separated list of integers, and a map that resolves the integers to the corresponding literals.
	 *
	 * @param core a map of literals per predicates
	 * @param answerSetString   the string representation of an answer set
	 * @param integerLiteralMap a integer-to-literal map
	 */
	public AnswerSetImpl(Map<Predicate, Set<Literal>> core, String answerSetString, Map<Integer, Literal> integerLiteralMap) {
		Validate.notNull(answerSetString, "The string representation of an answer set cannot be null");
		Validate.notNull(integerLiteralMap);
		Validate.notNull(core);

		answerSet = new HashMap<>(core);
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
		Set<Literal> answers = new HashSet<>();
		List<Term> queryTerms = query.getArguments();
		Map<Term, Integer> firstOccurences = new HashMap<>();
		for (int idx = 0; idx < queryTerms.size(); idx++) {
			Term term = queryTerms.get(idx);
			firstOccurences.putIfAbsent(term, idx);
		}

		for (Literal literal : getLiterals(query.getPredicate())) {
			if (IntStream.range(0, queryTerms.size()).allMatch(idx -> {
				Term term = queryTerms.get(idx);
				if (term.isConstant()) {
					return queryTerms.get(idx).equals(literal.getArguments().get(idx));
				} else {
					return firstOccurences.get(term).equals(idx)
						|| literal.getArguments().get(idx).equals(literal.getArguments().get(firstOccurences.get(term)));
				}
			})) {
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

	@Override
	public void exportQueryAnswersToCsv(PositiveLiteral query, String csvFilePath) throws IOException {
		Validate.notNull(csvFilePath, "File to export query answer to must not be null!");
		Validate.isTrue(csvFilePath.endsWith(".csv"), "Expected .csv extension for file [%s]!", csvFilePath);


		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFilePath)));
		CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
		QueryResultIterator queryResultIterator = getQueryResults(query);

		while (queryResultIterator.hasNext()) {
			printer.printRecord(queryResultIterator.next().getTerms());
		}
		printer.close();
	}
}
