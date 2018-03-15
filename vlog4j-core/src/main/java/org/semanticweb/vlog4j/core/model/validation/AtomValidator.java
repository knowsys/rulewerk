package org.semanticweb.vlog4j.core.model.validation;

import java.util.List;

import org.semanticweb.vlog4j.core.model.Term;

public class AtomValidator {
	public static void validArguments(final List<Term> arguments) throws AtomValidationException {
		if (arguments == null) {
			throw new AtomValidationException("Null Atom argument list");
		}
		if (arguments.isEmpty()) {
			throw new AtomValidationException("Empty Atom argument list");
		}
		for (final Term term : arguments) {
			if (term == null) {
				throw new AtomValidationException("Null argument in the Atom argument list");
			}
		}
	}

}
