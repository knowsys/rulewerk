package org.semanticweb.rulewerk.logic;

import org.semanticweb.rulewerk.core.model.api.Term;

public interface Unifier {
	/*
	 * Returns the term associated with {@code Term} {@value key}. If {@value key}
	 * is not present in the unifier, then {@value key} is returned.
	 */
	public Term getValue(Term key);

	/*
	 * Getter of Success
	 */
	public boolean getSuccess();

}
