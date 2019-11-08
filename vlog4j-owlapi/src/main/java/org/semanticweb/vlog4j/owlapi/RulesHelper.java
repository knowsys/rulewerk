package org.semanticweb.vlog4j.owlapi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.vlog4j.owlapi.AbstractClassToRuleConverter.SimpleConjunction;

public final class RulesHelper {
	
	private RulesHelper() {}
	
	static SimpleConjunction replaceTerm(SimpleConjunction conjunction, Term mainTerm, Term individualTerm) {
		SimpleConjunction newSimpleConjunction = new SimpleConjunction();
		conjunction.getConjuncts().forEach(conjunct -> {
			PositiveLiteral newLiteral = replaceTerm(conjunct, mainTerm, individualTerm);
			newSimpleConjunction.add(newLiteral);
		});
		return newSimpleConjunction;
	}

	static PositiveLiteral replaceTerm(PositiveLiteral positiveLiteral, Term sourceTerm, Term targetTerm) {

		List<Term> arguments = positiveLiteral.getArguments();
		List<Term> modifiableArguments = replaceTerm(sourceTerm, targetTerm, arguments);

		return new PositiveLiteralImpl(positiveLiteral.getPredicate(), modifiableArguments);
	}

	static List<Term> replaceTerm(Term sourceTerm, Term targetTerm, List<Term> terms) {
		List<Term> newTerms = new ArrayList<>(terms);

		UnaryOperator<Term> replaceSourceTerm = term -> term.equals(sourceTerm) ? targetTerm : term;
		newTerms.replaceAll(replaceSourceTerm);
		
		return newTerms;
	}

}
