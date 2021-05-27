package org.semanticweb.rulewerk.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.NegativeLiteral;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class Substitute {

	static public Term term(Substitution subst, Term term) {
		return subst.contains(term) ? subst.getValue(term) : term;
	}

	static public PositiveLiteral positiveLiteral(Substitution subst, PositiveLiteral literal) {
		return Expressions.makePositiveLiteral(literal.getPredicate(), terms(subst, literal.getArguments()));
	}

	static public NegativeLiteral negativeLiteral(Substitution subst, NegativeLiteral literal) {
		return Expressions.makeNegativeLiteral(literal.getPredicate(), terms(subst, literal.getArguments()));
	}

	static public Literal literal(Substitution subst, Literal literal) {
		return literal.isNegated() ? negativeLiteral(subst, (NegativeLiteral) literal)
				: positiveLiteral(subst, (PositiveLiteral) literal);
	}

	static public List<Term> terms(Substitution subst, List<Term> terms) {
		return terms.stream().map(term -> term(subst, term)).collect(Collectors.toList());
	}

	static public List<PositiveLiteral> positiveLiterals(Substitution subst, List<PositiveLiteral> literals) {
		return literals.stream().map(literal -> positiveLiteral(subst, literal)).collect(Collectors.toList());
	}

	static public List<NegativeLiteral> negativeLiterals(Substitution subst, List<NegativeLiteral> literals) {
		return literals.stream().map(literal -> negativeLiteral(subst, literal)).collect(Collectors.toList());
	}

	static public List<Literal> literals(Substitution subst, List<Literal> literals) {
		return literals.stream().map(literal -> literal(subst, literal)).collect(Collectors.toList());
	}

	static public Rule rule(Substitution subst, Rule rule) {
		List<Literal> newBody = literals(subst, rule.getBody().getLiterals());
		List<PositiveLiteral> newHead = positiveLiterals(subst, rule.getHead().getLiterals());
		return Expressions.makeRule(Expressions.makeConjunction(newHead), Expressions.makeConjunction(newBody));
	}
}
