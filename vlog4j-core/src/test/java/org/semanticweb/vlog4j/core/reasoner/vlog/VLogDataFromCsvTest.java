package org.semanticweb.vlog4j.core.reasoner.vlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;

public class VLogDataFromCsvTest {
	private static final String CSV_INPUT_FOLDER = "src/test/data/input/";

	private static final String unaryPredicateNameP = "p";
	private static final String unaryPredicateNameQ = "q";

	private final List<List<Term>> expectedQueryResultUnary = Arrays.asList(
			Arrays.asList(VLogExpressions.makeConstant("c1")), Arrays.asList(VLogExpressions.makeConstant("c2")));

	@Test
	public void testLoadDataFomCsvString()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		final String unaryPredicatesEDBConfig = "EDB0_predname=" + unaryPredicateNameQ + "\n" + "EDB0_type=INMEMORY\n"
				+ "EDB0_param0=" + CSV_INPUT_FOLDER + "\n" + "EDB0_param1=unaryFacts\n" + "EDB1_predname="
				+ unaryPredicateNameP + "\n" + "EDB1_type=INMEMORY\n" + "EDB1_param0=" + CSV_INPUT_FOLDER + "\n"
				+ "EDB1_param1=unaryFacts";
		final VLog vLog = new VLog();
		vLog.start(unaryPredicatesEDBConfig, false);
		final TermQueryResultIterator queryResultsPIterator = vLog
				.query(new Atom(unaryPredicateNameP, VLogExpressions.makeVariable("x")));
		final List<List<Term>> queryResultsP = new ArrayList<>(
				VLogQueryResultUtils.collectResults(queryResultsPIterator));
		assertEquals(expectedQueryResultUnary, queryResultsP);

		final TermQueryResultIterator queryResultsQIterator = vLog
				.query(new Atom(unaryPredicateNameQ, VLogExpressions.makeVariable("x")));
		final List<List<Term>> queryResultsQ = new ArrayList<>(
				VLogQueryResultUtils.collectResults(queryResultsQIterator));
		assertEquals(expectedQueryResultUnary, queryResultsQ);

		final TermQueryResultIterator queryResultsRIterator = vLog
				.query(new Atom("t", VLogExpressions.makeVariable("x")));
		assertFalse(queryResultsRIterator.hasNext());
		queryResultsRIterator.close();
		vLog.stop();
	}

}
