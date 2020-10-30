package org.semanticweb.rulewerk.asp.implementation;

/*-
 * #%L
 * Rulewerk ASP Components
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

import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.RuleImpl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class that provides templates of a rule w.r.t. a specific query.
 * For a given list of answer {@link Term}s, the template can be grounded (and written) performantly.
 */
public class RuleAspifTemplate extends RuleImpl {

	private final BufferedWriter writer;
	private final List<LiteralQueryTemplate> headTemplates;
	private final List<LiteralQueryTemplate> bodyTemplates;

	/**
	 * Constructor.
	 *  @param rule   the rule
	 * @param writer the writer
	 * @param query  the query that will be used to ground the rule
	 * @param overApproximatedPredicates set of over-approximated {@link Predicate}s
	 */
	public RuleAspifTemplate(Rule rule, BufferedWriter writer, PositiveLiteral query, Set<Predicate> overApproximatedPredicates) {
		super(rule.getHead(), rule.getBody());
		this.writer = writer;

		headTemplates = new ArrayList<>();
		for (Literal literal : rule.getHead()) {
			headTemplates.add(new LiteralQueryTemplate(literal, query));
		}

		bodyTemplates = new ArrayList<>();
		for (Literal literal : rule.getBody()) {
			if (overApproximatedPredicates.contains(literal.getPredicate())) {
				bodyTemplates.add(new LiteralQueryTemplate(literal, query));
			}
		}
	}

	/**
	 * Auxiliary class that provides a template for grounding a literal with respect to a specific query.
	 * The answer terms to the variables in the query literal are used to instantiate the literal, i.e., the variables
	 * of the literal are replaced with the constants assigned to a query result of the query.
	 */
	static class LiteralQueryTemplate {

		Literal literal;
		List<Integer> answerTermPositions;

		/**
		 * Constructor. Create the template.
		 *
		 * @param literal the literal
		 * @param query   the query
		 */
		public LiteralQueryTemplate(Literal literal, PositiveLiteral query) {
			this.literal = literal;
			answerTermPositions = new ArrayList<>();
			for (Term term : literal.getArguments()) {
				if (term.isVariable()) {
					int position = query.getArguments().indexOf(term);
					if (position == -1) {
						throw new IllegalArgumentException(
							"Query to ground literal must contain all variables of this literal");
					}
					answerTermPositions.add(position);
				} else {
					// we use -1 to indicate that it is already a ground term and we have to use this ground term later
					answerTermPositions.add(-1);
				}
			}
		}

		/**
		 * Get the aspif value for the ground literal created by the given answer terms.
		 *
		 * @param answerTerms the answer terms to ground the template literal
		 * @return 			  the aspif value
		 */
		public Integer getAspifValue(List<Term> answerTerms) {
			List<Term> terms = new ArrayList<>();
			int countTerms = 0;
			for (int position : answerTermPositions) {
				if (position >= 0) {
					terms.add(answerTerms.get(position));
				} else {
					terms.add(literal.getArguments().get(countTerms));
				}
				countTerms++;
			}

			return AspifIdentifier.getAspifValue(literal, terms);
		}
	}

	/**
	 * Write all instances the rule gives rise to for the answer terms.
	 *
	 * @param answerTerms the answer terms representing the grounding information
	 * @throws IOException an IO exception
	 */
	public void writeGroundInstances(List<Term> answerTerms) throws IOException {
		// Create the String for the rule body
		StringBuilder bodyBuilder = new StringBuilder();
		int countBodyLiterals = 0;
		for (LiteralQueryTemplate template : bodyTemplates) {
			bodyBuilder.append(" ");
			bodyBuilder.append(template.getAspifValue(answerTerms));
			countBodyLiterals++;
		}

		// As aspif supports only disjunctive rules, we have to write one rule for each head literal
		for (LiteralQueryTemplate headLiteralTemplate : headTemplates) {
			writer.write("1 0 1 " + headLiteralTemplate.getAspifValue(answerTerms)
				+ " 0 " + countBodyLiterals + bodyBuilder.toString()
			);
			writer.newLine();
		}
	}
}
