package org.semanticweb.rulewerk.reliances;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class Instantiate {

	static public Constant term(Term term) {
		return Expressions.makeAbstractConstant(term.getName());
	}

	static public Fact literal(Literal literal) {
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(term(term));
		}
		return Expressions.makeFact(literal.getPredicate(), newTerms);
	}

}
