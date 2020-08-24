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

import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;

public class VariableRenamer {

	static private Term rename(Term term, int idx1) {
		if (term.getType() == TermType.UNIVERSAL_VARIABLE) {
			return Expressions.makeUniversalVariable(term.getName() + "000" + idx1);
		} else if (term.getType() == TermType.EXISTENTIAL_VARIABLE) {
			return Expressions.makeExistentialVariable(term.getName() + "000" + idx1);
		} else {
			return term;
		}
	}

	static private Literal rename(Literal literal, int idx1) {
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(rename(term, idx1));
		}
		if (literal.isNegated()) {
			return Expressions.makeNegativeLiteral(literal.getPredicate(), newTerms);
		} else {
			return Expressions.makePositiveLiteral(literal.getPredicate(), newTerms);
		}
	}

	static public Rule rename(Rule rule, int idx) {
		List<Literal> newBody = new ArrayList<>();
		rule.getBody().forEach(literal -> newBody.add(rename(literal, idx)));

		List<PositiveLiteral> newHead = new ArrayList<>();
		rule.getHead().forEach(literal -> newHead.add((PositiveLiteral) rename(literal, idx)));

		return Expressions.makeRule(Expressions.makeConjunction(newHead), Expressions.makeConjunction(newBody));
	}

	// this is wrong, I need to close it over
	static private Term rename(Term term, Unifier unifier) {
		if (unifier.unifier.containsKey(term)) {
			Term value = unifier.unifier.get(term);
			if (unifier.unifier.containsKey(value)) {
				return rename(value, unifier);
			} else {
				return value;
			}
		} else {
			return term;
		}
	}

	static public Literal rename(Literal literal, Unifier unifier) {
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(rename(term, unifier));
		}
		if (literal.isNegated()) {
			return Expressions.makeNegativeLiteral(literal.getPredicate(), newTerms);
		} else {
			return Expressions.makePositiveLiteral(literal.getPredicate(), newTerms);
		}
	}

	static public Rule rename(Rule rule, Unifier unifier) throws Exception {
		if (!unifier.success) {
			throw new Exception("unifier did not success");
		}
		List<Literal> newBody = new ArrayList<>();
		rule.getBody().forEach(literal -> newBody.add(rename(literal, unifier)));

		List<PositiveLiteral> newHead = new ArrayList<>();
		rule.getHead().forEach(literal -> newHead.add((PositiveLiteral) rename(literal, unifier)));

		return Expressions.makeRule(Expressions.makeConjunction(newHead), Expressions.makeConjunction(newBody));
	}

	static public List<Literal> rename(List<Literal> literals, Unifier unifier) {
		List<Literal> result = new ArrayList<>();
		literals.forEach(literal -> result.add(rename(literal, unifier)));
		return result;
	}
}