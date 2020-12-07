package org.semanticweb.rulewerk.reliances;

/*-
 * #%L
 * Rulewerk Reliances
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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

// TODO unify this with SelfRestraint
public class Instantiator {

	/**
	 * Given an instance of PositiveLiteral, return an instantiation of it. The
	 * instantiation will create constants with the variable names.
	 * 
	 * @note there is a possible conflict here if some constant names in the rule
	 *       are equal to some variable names.
	 * @param positiveliteral to be transformed
	 * @return a ground instantiation of the literal.
	 */
	static public Literal instantiateFact(Literal literal) {
		assert !literal.isNegated();
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(Expressions.makeAbstractConstant(term.getName()));
		}
		return Expressions.makePositiveLiteral(literal.getPredicate(), newTerms);
	}

	/**
	 * Given an instance of PositiveLiteral, return a new PositiveLiteral s.t. its
	 * universally quantified variables have been replaced with constants using the
	 * same variable name.
	 * 
	 * @note there is a possible conflict here if some constant names in the rule
	 *       are equal to some universal variable names.
	 * @param positiveliteral to be transformed
	 * @return a transformed positiveLiteral
	 */
	static public Literal instantiateQuery(Literal literal) {
		assert !literal.isNegated();
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			if (term.getType() == TermType.EXISTENTIAL_VARIABLE) {
				newTerms.add(term);
			} else {
				newTerms.add(Expressions.makeAbstractConstant(term.getName()));
			}

		}
		return Expressions.makePositiveLiteral(literal.getPredicate(), newTerms);
	}

}
