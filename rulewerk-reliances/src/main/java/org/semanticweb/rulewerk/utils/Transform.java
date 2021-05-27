package org.semanticweb.rulewerk.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.ConjunctionImpl;
import org.semanticweb.rulewerk.core.model.implementation.ExistentialVariableImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.model.implementation.NegativeLiteralImpl;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.rulewerk.core.model.implementation.UniversalVariableImpl;

public class Transform {

	/*
	 * Return an named null when the term is an existentially quantified variable
	 */
	static public Term exi2null(Term term) {
		return term.isExistentialVariable() ? new NamedNullImpl(term.getName()) : term;
	}

	/*
	 * Replace existentially quantified variables with named nulls having the same
	 * name in a literal
	 */
	static public Literal exi2null(Literal literal) {
		Predicate p = literal.getPredicate();
		List<Term> terms = literal.getArguments().stream().map(t -> exi2null(t)).collect(Collectors.toList());
		return literal.isNegated() ? new NegativeLiteralImpl(p, terms) : new PositiveLiteralImpl(p, terms);
	}

	/*
	 * Replace existentially quantified variables with named nulls having the same
	 * name in a list of literal
	 */
	static public List<? extends Literal> exi2null(List<? extends Literal> literals) {
		return literals.stream().map(l -> exi2null(l)).collect(Collectors.toList());
	}

	/*
	 * Replace existentially quantified variables with named nulls having the same
	 * name as the existentially quantified variable in a rule
	 */
	static public Rule exi2null(Rule rule) {
		return Expressions.makeRule(
				new ConjunctionImpl<>(Transform.intoPositiveLiterals(Transform.exi2null(rule.getHead().getLiterals()))),
				new ConjunctionImpl<>(Transform.exi2null(rule.getBody().getLiterals())));
	}

	/*
	 * If {@code term} is a named null, then it returns an existentially quantified
	 * variable having the same name. It returns {@code term} in the contrary case
	 */
	static public Term null2exi(Term term) {
		return term.isNull() ? new ExistentialVariableImpl(term.getName()) : term;
	}

	/*
	 * Replace named nulls with existentially quantified variables having the same
	 * name in a literal
	 */
	static public Literal null2exi(Literal literal) {
		Predicate p = literal.getPredicate();
		List<Term> terms = literal.getArguments().stream().map(t -> null2exi(t)).collect(Collectors.toList());
		return literal.isNegated() ? new NegativeLiteralImpl(p, terms) : new PositiveLiteralImpl(p, terms);
	}

	/*
	 * Replace named nulls with existentially quantified variables having the same
	 * name in a list of literals
	 */
	static public List<? extends Literal> null2exi(List<? extends Literal> literals) {
		return literals.stream().map(l -> null2exi(l)).collect(Collectors.toList());
	}

	/*
	 * If {@code term} is a named null, then it returns aconstant having the same
	 * name. It returns {@code term} in the contrary case
	 */
	static public Term null2cons(Term term) {
		return term.isNull() ? Expressions.makeAbstractConstant(term.getName()) : term;
	}

	/*
	 * Replace named nulls with constants having the same name in a literal
	 */
	static public Literal null2cons(Literal literal) {
		Predicate p = literal.getPredicate();
		List<Term> terms = literal.getArguments().stream().map(t -> null2cons(t)).collect(Collectors.toList());
		return literal.isNegated() ? new NegativeLiteralImpl(p, terms) : new PositiveLiteralImpl(p, terms);
	}

	/*
	 * Replace named nulls with constants having the same name in a list of literals
	 */
	static public List<? extends Literal> null2cons(List<? extends Literal> literals) {
		return literals.stream().map(l -> null2cons(l)).collect(Collectors.toList());
	}

	/*
	 * Return an universally quantified variables when the term is a constant
	 */
	static public Term uni2cons(Term term) {
		return term.isUniversalVariable() ? Expressions.makeAbstractConstant(term.getName()) : term;
	}

	/*
	 * Replace universally quantified variables with constants having the same name
	 * in a literal
	 */
	static public Literal uni2cons(Literal literal) {
		Predicate p = literal.getPredicate();
		List<Term> terms = literal.getArguments().stream().map(t -> uni2cons(t)).collect(Collectors.toList());
		return literal.isNegated() ? new NegativeLiteralImpl(p, terms) : new PositiveLiteralImpl(p, terms);
	}

	/*
	 * Replace universally quantified variables with constants having the same name
	 * in a list of literals
	 */
	static public List<? extends Literal> uni2cons(List<? extends Literal> literals) {
		return literals.stream().map(l -> uni2cons(l)).collect(Collectors.toList());
	}

	/*
	 * If {@code term} is a named null, then it returns an universally quantified
	 * variable having the same name. It returns {@code term} in the contrart case
	 */
	static public Term null2uni(Term term) {
		return term.isNull() ? new UniversalVariableImpl(term.getName()) : term;
	}

	/*
	 * Replace named nulls with universally quantified variables having the same
	 * name in a literal
	 */
	static public Literal null2uni(Literal literal) {
		Predicate p = literal.getPredicate();
		List<Term> terms = literal.getArguments().stream().map(t -> null2uni(t)).collect(Collectors.toList());
		return literal.isNegated() ? new NegativeLiteralImpl(p, terms) : new PositiveLiteralImpl(p, terms);
	}

	/*
	 * Replace named nulls with universally quantified variables having the same
	 * name in a list of literals
	 */
	static public List<? extends Literal> null2uni(List<? extends Literal> literals) {
		return literals.stream().map(l -> null2uni(l)).collect(Collectors.toList());
	}

	/*
	 * If {@code term} is an existentially quantified variable, then returns an
	 * universally quantified variable having the same name. It returns {@code term}
	 * in the contrary case
	 */
	static public Term exi2uni(Term term) {
		return term.isExistentialVariable() ? new UniversalVariableImpl(term.getName()) : term;
	}

	/*
	 * Replace existentially quantified variables with universally quantified
	 * variables having the same name in a literal
	 */
	static public Literal exi2uni(Literal literal) {
		Predicate p = literal.getPredicate();
		List<Term> terms = literal.getArguments().stream().map(t -> exi2uni(t)).collect(Collectors.toList());
		return literal.isNegated() ? new NegativeLiteralImpl(p, terms) : new PositiveLiteralImpl(p, terms);
	}

	/*
	 * Replace existentially quantified variables with universally quantified
	 * variables having the same name in a list of literals
	 */
	static public List<? extends Literal> exi2uni(List<? extends Literal> literals) {
		return literals.stream().map(l -> exi2uni(l)).collect(Collectors.toList());
	}

	/*
	 * @param literals list of (positive) literals that don't contains existentially
	 * quantified variables
	 */
	static public Fact intoFact(Literal literal) {
		Validate.isTrue(!literal.isNegated());
		return Expressions.makeFact(literal.getPredicate(),
				literal.getArguments().stream().map(t -> uni2cons(t)).collect(Collectors.toList()));
	}

	/*
	 * Transform a list of PositiveLiterals into a list of Facts
	 */
	static public List<Fact> intoFacts(List<? extends Literal> literals) {
		return literals.stream().map(l -> intoFact(l)).collect(Collectors.toList());
	}

	static public List<PositiveLiteral> intoPositiveLiterals(List<? extends Literal> literals) {
		return literals.stream().map(l -> (PositiveLiteral) l).collect(Collectors.toList());
	}

}
