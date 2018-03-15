package org.semanticweb.vlog4j.core.model.validation;

import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;

public class RuleValidator {

	public static void bodyCheck(final List<Atom> body) {
		// TODO Auto-generated method stub

	}

	public static void headCheck(final List<Atom> head) {
		// TODO Auto-generated method stub

	}

	public static void ruleCheck(final List<Atom> body, final List<Atom> head) throws RuleValidationException {
		if (body == null) {
			throw new RuleValidationException("Null rule body");
		}
		if (body.isEmpty()) {
			throw new RuleValidationException("Empty rule body");
		}
		for (final Atom bodyAtom : body) {
			if (bodyAtom == null) {
				throw new RuleValidationException("Rule body contains null atoms");
			}
		}

		if (head == null) {
			throw new RuleValidationException("Null rule head");
		}
		if (head.isEmpty()) {
			throw new RuleValidationException("Empty rule head");
		}
		for (final Atom headAtom : head) {
			if (headAtom == null) {
				throw new RuleValidationException("Rule head contains null atoms");
			}
		}
	}

}
