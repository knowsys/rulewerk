package org.semanticweb.vlog4j.core.reasoner.exceptions;

import org.semanticweb.vlog4j.core.reasoner.ReasonerState;

public class ReasonerStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5720169752588784690L;

	private static final String messagePrefix = "Invalid operation for current reasoner state";

	public ReasonerStateException(ReasonerState state, String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
