package org.semanticweb.rulewerk.asp.model;

import java.util.Iterator;

public interface AnswerSetIterator extends Iterator<AnswerSet> {

	/**
	 * Get the reasoning state of the computation that leads to this answer sets.
	 *
	 * @return the reasoning state
	 */
	AspReasoningState getReasoningState();
}
