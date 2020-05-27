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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.implementation.ConjunctionImpl;
import org.semanticweb.rulewerk.core.model.implementation.ExistentialVariableImpl;
import org.semanticweb.rulewerk.core.model.implementation.NegativeLiteralImpl;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.rulewerk.core.model.implementation.RuleImpl;
import org.semanticweb.rulewerk.core.model.implementation.UniversalVariableImpl;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;

public class VariableRenamer {

	static private Term renameVariables(Term term, int idx1) {
		if (term.getType() == TermType.UNIVERSAL_VARIABLE) {
			return new UniversalVariableImpl(term.getName() + "000" + idx1);
		} else if (term.getType() == TermType.EXISTENTIAL_VARIABLE) {
			return new ExistentialVariableImpl(term.getName() + "000" + idx1 );
		} else {
			return term;
		}
	}

	static private Literal renameVariables(Literal literal, int idx1) {
		Literal newLiteral;
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(renameVariables(term, idx1));
		}
		if (literal.isNegated()) {
			newLiteral = new NegativeLiteralImpl(literal.getPredicate(), newTerms);
		} else {
			newLiteral = new PositiveLiteralImpl(literal.getPredicate(), newTerms);
		}
		return newLiteral;
	}

	static public Rule renameVariables(Rule rule, int idx) {
		List<Literal> newBody = new ArrayList<>();
		rule.getBody().forEach(literal -> newBody.add(renameVariables(literal, idx)));

		List<PositiveLiteral> newHead = new ArrayList<>();
		rule.getHead()
				.forEach(literal -> newHead.add((PositiveLiteral) renameVariables(literal, idx)));

		return new RuleImpl(new ConjunctionImpl<>(newHead), new ConjunctionImpl<>(newBody));
	}

	static private Term renameVariables(Term term, LiteralSetUnifier lsu) {
		if (term.getType() == TermType.UNIVERSAL_VARIABLE && lsu.unifier.containsKey(term.getName())) {
			return new UniversalVariableImpl(lsu.unifier.get(term.getName()));
		} else if (term.getType() == TermType.EXISTENTIAL_VARIABLE && lsu.unifier.containsKey(term.getName())) {
			return new ExistentialVariableImpl(lsu.unifier.get(term.getName()));
		} else
			return term;
	}

	static private Literal renameVariables(Literal literal, LiteralSetUnifier lsu) {
		Literal newLiteral;
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(renameVariables(term, lsu));
		}
		if (literal.isNegated()) {
			newLiteral = new NegativeLiteralImpl(literal.getPredicate(), newTerms);
		} else {
			newLiteral = new PositiveLiteralImpl(literal.getPredicate(), newTerms);
		}
		return newLiteral;
	}

	static public Set<Literal> renameVariables(Set<Literal> set, LiteralSetUnifier lsu) {
		Set<Literal> result = new HashSet<>();
		set.forEach(literal -> result.add(renameVariables(literal, lsu)));
		return result;
	}

	static public Rule renameVariables(Rule rule, LiteralSetUnifier lsu) {
		List<Literal> newBody = new ArrayList<>();
		rule.getBody().forEach(literal -> newBody.add(renameVariables(literal, lsu)));

		List<PositiveLiteral> newHead = new ArrayList<>();
		rule.getHead().forEach(literal -> newHead.add((PositiveLiteral) renameVariables(literal, lsu)));

		return new RuleImpl(new ConjunctionImpl<>(newHead), new ConjunctionImpl<>(newBody));
	}
}
