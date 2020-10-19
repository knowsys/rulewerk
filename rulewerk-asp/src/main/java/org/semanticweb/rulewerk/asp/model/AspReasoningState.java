package org.semanticweb.rulewerk.asp.model;

/**
 * Enumeration of different result state for the reasoning about ASP programs.
 *
 * @author Philipp Hanisch
 */
public enum AspReasoningState {

	/**
	 * The corresponding ASP program is satisfiable.
	 */
	SATISFIABLE,

	/**
	 * The corresponding ASP program is unsatisfiable.
	 */
	UNSATISFIABLE,

	/**
	 * The reasoning process was interrupted.
	 */
	INTERRUPTED,

	/**
	 * An error occurred during the reasoning process.
	 */
	ERROR
}


