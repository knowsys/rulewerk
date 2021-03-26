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

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Auxiliary class which is used as light-weight collection of elements that uniquely identifies (grounded) literals for
 * aspif groundings. Based on these identifiers, the class provides statically the functionality to get an integer
 * that is on-the-fly uniquely connected with a certain aspif identifier.
 */
public class AspifIdentifier {

	private static final Map<Integer, AspifIdentifier> integerAspifIdentifierMap = new Int2ObjectOpenHashMap<>();
	private static final Map<AspifIdentifier, Integer> aspifIdentifierIntegerMap = new Object2IntOpenHashMap<>();
	private static int aspifCounter = 1;

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

	public PositiveLiteral getPositiveLiteral() {
		return Expressions.makePositiveLiteral(predicate, terms);
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

	public static Map<Integer, AspifIdentifier> getIntegerAspifIdentifierMap() {
		return integerAspifIdentifierMap;
	}

	/**
	 * Get and possibly negate the aspif integer for a literal w.r.t. an answer.
	 *
	 * @param literal	  the literal
	 * @param answerTerms a map representing the answer
	 * @return			  the aspif value
	 */
	public static int getAspifValue(Literal literal, List<Term> answerTerms) {
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
	public static int getAspifValue() {
		return aspifCounter++;
	}

	/**
	 * Reset the static components to their initial states.
	 */
	public static void reset() {
		integerAspifIdentifierMap.clear();
		aspifIdentifierIntegerMap.clear();
		aspifCounter = 1;
	}
}
