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

/**
 * TODO add documentation
 * 
 * @author Larry Gonzalez
 *
 */
public class UnifierBasedVariableRenamer {

	MartelliMontanariUnifier unifier;
	boolean renameExistentials;

	UnifierBasedVariableRenamer(MartelliMontanariUnifier unifier, boolean renameExistentials) {
		assert unifier.success;
		this.unifier = unifier;
		this.renameExistentials = renameExistentials;
	}

	private Term rename(Term term) {
		if (term.getType() == TermType.UNIVERSAL_VARIABLE) {
			return unifier.getUnifiedTerm(term);
		} else if (term.getType() == TermType.EXISTENTIAL_VARIABLE) {
			if (renameExistentials) {
				return unifier.getUnifiedTerm(term);
			} else {
				return term;
			}
		} else {
			return term;
		}
	}

	public Literal rename(Literal literal) {
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(rename(term));
		}
		if (literal.isNegated()) {
			return Expressions.makeNegativeLiteral(literal.getPredicate(), newTerms);
		} else {
			return Expressions.makePositiveLiteral(literal.getPredicate(), newTerms);
		}
	}

	public Rule rename(Rule rule) {
		assert unifier.success;
		List<Literal> newBody = new ArrayList<>();
		rule.getBody().forEach(literal -> newBody.add(rename(literal)));

		List<PositiveLiteral> newHead = new ArrayList<>();
		rule.getHead().forEach(literal -> newHead.add((PositiveLiteral) rename(literal)));

		return Expressions.makeRule(Expressions.makeConjunction(newHead), Expressions.makeConjunction(newBody));
	}

	public List<Literal> rename(List<Literal> literals) {
		List<Literal> result = new ArrayList<>();
		literals.forEach(literal -> result.add(rename(literal)));
		return result;
	}
}
