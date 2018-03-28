package org.semanticweb.vlog4j.core.reasoner;

import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

public class ReasonerStateTest {

	private static final Atom exampleQueryAtom = Expressions.makeAtom("p", Expressions.makeVariable("x"));

	@Test
	public void testResetBeforeLoad() {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.reset();
		}
	}

	@Test
	public void testResetEmptyKnowledgeBase() throws EdbIdbSeparationException, IOException, ReasonerStateException {
		final Reasoner reasoner = Reasoner.getInstance();
		// 1. load and reason
		reasoner.load();
		try(final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)){
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.reason();
		try(final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)){
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.reset();

		// 2. load again
		reasoner.load();
		try(final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)){
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.reset();

		// 3. load and reason again
		reasoner.load();
		try(final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)){
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.reason();
		try(final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)){
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.close();
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailReasonBeforeLoad() throws ReasonerStateException, IOException {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.reason();
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailAnswerQueryBeforeLoad() throws ReasonerStateException {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.answerQuery(exampleQueryAtom, true);
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailExportQueryAnswerToCsvBeforeLoad() throws ReasonerStateException, IOException {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.exportQueryAnswersToCsv(exampleQueryAtom, CsvFileUtils.CSV_EXPORT_FOLDER + "output.csv", true);
		}
	}

	@Test
	public void testSuccessiveCloseAfterLoad() throws EdbIdbSeparationException, IOException {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.load();
			reasoner.close();
			reasoner.close();
		}
	}

	@Test
	public void testSuccessiveCloseBeforeLoad() {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.close();
			reasoner.close();
		}
	}

}
