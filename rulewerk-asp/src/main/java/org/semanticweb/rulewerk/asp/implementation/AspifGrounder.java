package org.semanticweb.rulewerk.asp.implementation;

/*
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

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.asp.model.Grounder;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * An ASP grounder whose output is the aspif format, which is used by Gringo.
 *
 * @author Philipp Hanisch
 */
public class AspifGrounder implements Grounder {

	private final Map<Integer, AspifIdentifier> integerAspifIdentifierMap;
	private final Map<AspifIdentifier, Integer> aspifIdentifierIntegerMap;
	private int aspifCounter;

	private final KnowledgeBase knowledgeBase;
	private final Reasoner reasoner;
	private final BufferedWriter writer;

	/**
	 * Constructor.
	 *
	 * @param knowledgeBase the knowledge base to ground
	 * @param reasoner      the reasoner for inferring an over-approximation
	 * @param writer		the writer to write the grounding
	 */
	public AspifGrounder(KnowledgeBase knowledgeBase, Reasoner reasoner, BufferedWriter writer) {
		Validate.notNull(knowledgeBase);
		Validate.notNull(reasoner);
		Validate.notNull(writer);

		this.knowledgeBase = knowledgeBase;
		this.reasoner = reasoner;
		this.writer = writer;
		this.integerAspifIdentifierMap = new Int2ObjectOpenHashMap<>();
		this.aspifIdentifierIntegerMap = new Object2IntOpenHashMap<>();
		this.aspifCounter = 1;
	}

	/**
	 * Auxiliary class which is used as light-weight collection of elements that uniquely identifies (grounded) literals for
	 * aspif groundings. Based on these identifiers, the class provides statically the functionality to get an integer
	 * that is on-the-fly uniquely connected with a certain aspif identifier.
	 */
	static class AspifIdentifier {

		final private String predicateName;
		final private String[] termNames;

		final private List<Term> terms;
		final private Predicate predicate;

		/**
		 * Constructor. Create an aspif identifier for the given literal and the list of terms as its arguments.
		 *
		 * @param literal     the literal
		 * @param answerTerms the arguments
		 */
		public AspifIdentifier(Literal literal, List<Term> answerTerms) {
			predicate = literal.getPredicate();
			predicateName = predicate.getName();

			terms = answerTerms;
			termNames = new String[answerTerms.size()];
			int i = 0;
			for (Term term : answerTerms) {
				termNames[i++] = term.getName();
			}
		}

		public String[] getTermNames() {
			return termNames;
		}

		public String getPredicateName() {
			return predicateName;
		}

		public List<Term> getTerms() {
			return terms;
		}

		public Predicate getPredicate() {
			return predicate;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int hashcode = 23;
			hashcode = hashcode * prime + this.predicateName.hashCode();
			hashcode = hashcode * prime + Arrays.hashCode(termNames);
			return hashcode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof AspifIdentifier)) {
				return false;
			}
			final AspifIdentifier other = (AspifIdentifier) obj;
			return this.predicateName.equals(other.getPredicateName())
				&& Arrays.equals(this.termNames, other.getTermNames());
		}
	}

	@Override
	public boolean ground() throws IOException {
		this.reasoner.reason();
		writer.write("asp 1 0 0");
		writer.newLine();

		for (Statement statement : knowledgeBase.getStatements()) {
			boolean successful = statement.accept(this);
			if (!successful) {
				return false;
			}
		}

		for (Integer aspifValue : integerAspifIdentifierMap.keySet()) {
			// We encode a literal in the answer set by its aspif integer, and we transform it back with the help of the
			// integer-to-literal map later.
			writer.write("4 "
				+ aspifValue.toString().length() + " " + aspifValue.toString()
				+ " 1 " + aspifValue.toString());
			writer.newLine();
		}

		writer.write("0");
		writer.newLine();
		return true;
	}

	@Override
	public Map<Integer, Literal> getIntegerLiteralMap() {
		Map<Integer, Literal> map = new Int2ObjectOpenHashMap<>();
		for (Integer integer : integerAspifIdentifierMap.keySet()) {
			AspifIdentifier aspifIdentifier = integerAspifIdentifierMap.get(integer);
			map.put(integer, Expressions.makePositiveLiteral(aspifIdentifier.getPredicate(), aspifIdentifier.getTerms()));
		}
		return map;
	}

	@Override
	public Boolean visit(Fact statement) {
		try {
			writer.write("1 0 1 " + getAspifValue(statement, statement.getArguments()) + " 0 0");
			writer.newLine();
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Boolean visit(Rule statement) {
		PositiveLiteral query = statement.getBodyVariablesLiteral();
		QueryResultIterator answers = reasoner.answerQuery(query, false);
		Map<Variable, Term> answerMap = new HashMap<>();
		try {
			while (answers.hasNext()) {
				List<Term> answerTerms = answers.next().getTerms();
				for (int i=0; i<query.getArguments().size(); i++) {
					Term queryTerm = query.getArguments().get(i);
					if (queryTerm.isVariable()) {
						answerMap.put((Variable) queryTerm, answerTerms.get(i));
					}
				}

				// Create the String for the rule body
				StringBuilder bodyBuilder = new StringBuilder();
				int countBodyLiterals = 0;
				for (Literal literal : statement.getBody()) {
					bodyBuilder.append(" ");
					bodyBuilder.append(getAspifValue(literal, getGroundTerms(literal, answerMap)));
					countBodyLiterals++;
				}

				// As aspif supports only disjunctive rules, we have to write one rule for each head literal
				for (Literal literal : statement.getHead()) {
					writer.write("1 0 1 " + getAspifValue(literal, getGroundTerms(literal, answerMap))
						+ " 0 " + countBodyLiterals + bodyBuilder.toString()
					);
					writer.newLine();
				}

				answerMap.clear();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Boolean visit(DataSourceDeclaration statement) {
		return null;
	}

	/**
	 * Get and possibly negate the aspif integer for a literal w.r.t. an answer.
	 *
	 * @param literal	  the literal
	 * @param answerTerms a map representing the answer
	 * @return			  the aspif value
	 * @throws IOException an IO exception
	 */
	private int getAspifValue(Literal literal, List<Term> answerTerms) throws IOException {
		AspifIdentifier aspifIdentifier = new AspifIdentifier(literal, answerTerms);
		Integer aspifValue = aspifIdentifierIntegerMap.getOrDefault(aspifIdentifier, 0);
		if (aspifValue == 0) {
			aspifValue = aspifCounter++;
			aspifIdentifierIntegerMap.put(aspifIdentifier, aspifValue);
			integerAspifIdentifierMap.put(aspifValue, aspifIdentifier);
		}

		return literal.isNegated() ? -aspifValue : aspifValue;
	}

	/**
	 * Get a one-time only aspif integer that can be used to abbreviate constructs.
	 *
	 * @return an aspif integer
	 */
	private int getAspifValue() {
		return aspifCounter++;
	}

	/**
	 * Get the ground terms of a literal with respect to a specific answer.
	 *
	 * @param literal   the literal to ground
	 * @param answerMap a map representing the answer
	 * @return			list of ground terms
	 */
	private List<Term> getGroundTerms(Literal literal, Map<Variable, Term> answerMap) {
		List<Term> groundTerms = new ArrayList<>();
		for (int i = 0; i < literal.getArguments().size(); i++) {
			Term term = literal.getArguments().get(i);
			groundTerms.add(term.isVariable() ? answerMap.get(term) : term);
		}
		return groundTerms;
	}
}
