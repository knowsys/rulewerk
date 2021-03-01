package org.semanticweb.rulewerk.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class Substitution {

	static public Term apply(Unifier unifier, Term term) {
		Validate.isTrue(unifier.getSuccess());
		return unifier.getValue(term);
	}

	static public Literal apply(Unifier unifier, Literal literal) {
		Validate.isTrue(unifier.getSuccess());

		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(apply(unifier, term));
		}
		if (literal.isNegated()) {
			return Expressions.makeNegativeLiteral(literal.getPredicate(), newTerms);
		} else {
			return Expressions.makePositiveLiteral(literal.getPredicate(), newTerms);
		}
	}

	static public Rule apply(Unifier unifier, Rule rule) {
		Validate.isTrue(unifier.getSuccess());

		List<Literal> newBody = new ArrayList<>();
		rule.getBody().forEach(literal -> newBody.add(apply(unifier, literal)));

		List<PositiveLiteral> newHead = new ArrayList<>();
		rule.getHead().forEach(literal -> newHead.add((PositiveLiteral) apply(unifier, literal)));

		return Expressions.makeRule(Expressions.makeConjunction(newHead), Expressions.makeConjunction(newBody));
	}

}
