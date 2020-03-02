package org.semanticweb.rulewerk.graal;


/*-
 * #%L
 * Rulewerk Graal Import Components
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


import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;


import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;

/**
 * A utility class containing a
 * <a href="http://graphik-team.github.io/graal/">Graal</a>
 * {@link ConjunctiveQuery}. Answering a
 * <a href="http://graphik-team.github.io/graal/">Graal</a>
 * {@link ConjunctiveQuery} over a certain knowledge base is equivalent to
 * adding a {@link Rule} to the knowledge base, <em> prior to reasoning</em>.
 * The rule consists of the query {@link Literal}s as the body and a single
 * {@link PositiveLiteral} with a new predicate containing all the answer
 * variables of the query as the head. After the reasoning process, in which the
 * rule is materialised, is completed, this rule head can then be used as a
 * query to obtain the results of the Graal {@link ConjunctiveQuery}.
 * 
 * @author Adrian Bielefeldt
 */
public class GraalConjunctiveQueryToRule {

	private final Rule rule;

	private final PositiveLiteral query;

	/**
	 * Constructor for a GraalConjunctiveQueryToRule.
	 * 
	 * @param ruleHeadPredicateName the query predicate name. Becomes the name of
	 *                              the rule head Predicate.
	 * @param answerVariables       the query answer variables. They become the
	 *                              terms of the rule head PositiveLiteral.
	 * @param conjunction           the query body. Becomes the rule body.
	 */
	protected GraalConjunctiveQueryToRule(final String ruleHeadPredicateName, final List<Term> answerVariables,
			final Conjunction<PositiveLiteral> conjunction) {
		this.query = Expressions.makePositiveLiteral(ruleHeadPredicateName, answerVariables);
		this.rule = Expressions.makePositiveLiteralsRule(Expressions.makePositiveConjunction(this.query), conjunction);
	}

	/**
	 * A rule that needs to be added to the program to answer the
	 * {@link ConjunctiveQuery Graal ConjunctiveQuery} represented by this object.
	 * It consists of all query literals from the original Graal ConjunctiveQuery as
	 * the body and a single PositiveLiteral containing all the answer variables of
	 * the query as the head.
	 * 
	 * @return The rule equivalent to the Graal ConjunctiveQuery represented by this
	 *         object.
	 */
	public Rule getRule() {
		return this.rule;
	}

	/**
	 * A query {@link PositiveLiteral} that returns the results of the
	 * {@link ConjunctiveQuery Graal ConjunctiveQuery} represented by this object,
	 * provided the corresponding rule ({@link #getRule()}) has been added to the
	 * program. It is equal to the head of the rule returned by {@link #getRule()}.
	 * 
	 * @return The query {@link PositiveLiteral} to obtain the results of the Graal
	 *         ConjunctiveQuery represented by this object.
	 */
	public PositiveLiteral getQuery() {
		return this.query;
	}

	@Override
	public int hashCode() {
		return this.rule.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final GraalConjunctiveQueryToRule other = (GraalConjunctiveQueryToRule) obj;

		if (!this.rule.equals(other.rule)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GraalConjunctiveQueryToRule [rule=" + this.rule + "]";
	}

}
