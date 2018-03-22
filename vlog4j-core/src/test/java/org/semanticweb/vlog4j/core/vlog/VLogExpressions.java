package org.semanticweb.vlog4j.core.vlog;

/**
 * Utility class with static methods for creating {@code karmaresearch.vlog}
 * objects.
 * 
 * @author Irina.Dragoste
 *
 */
public final class VLogExpressions {

	private VLogExpressions() {
	}

	public static karmaresearch.vlog.Term makeVariable(final String name) {
		final karmaresearch.vlog.Term variable = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				name);
		return variable;
	}

	public static karmaresearch.vlog.Term makeConstant(final String name) {
		final karmaresearch.vlog.Term constant = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				name);
		return constant;
	}

	public static karmaresearch.vlog.Rule makeRule(final karmaresearch.vlog.Atom headAtom,
			final karmaresearch.vlog.Atom... bodyAtoms) {
		return new karmaresearch.vlog.Rule(new karmaresearch.vlog.Atom[] { headAtom }, bodyAtoms);
	}

}
