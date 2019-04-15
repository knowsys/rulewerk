package org.semanticweb.vlog4j.examples;

import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

public final class ExamplesUtils {

	public static final String OUTPUT_FOLDER = "src/main/data/output/";
	public static final String INPUT_FOLDER = "src/main/data/input/";

	/*
	 * This is a utility class. Therefore, it is best practice to do the following: (1) Make the class final, (2) make its constructor private, (3) make all its
	 * fields and methods static. This prevents the classes instantiation and inheritance.
	 */
	private ExamplesUtils() {

	}

	/**
	 * Prints out the {@code reasoner} answer's to given query ({@code queryAtom}).
	 *
	 * @param queryAtom
	 *            query to be answered
	 * @param reasoner
	 *            reasoner to query on
	 * @throws ReasonerStateException
	 *             in case the reasoner has not yet been loaded.
	 */
	public static void printOutQueryAnswers(final PositiveLiteral queryAtom, final Reasoner reasoner) throws ReasonerStateException {
		System.out.println("Answers to query " + queryAtom + " :");
		try (final QueryResultIterator answers = reasoner.answerQuery(queryAtom, true)) {
			answers.forEachRemaining(answer -> System.out.println(" - " + answer));
			System.out.println();
		}
	}
}
