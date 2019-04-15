package org.semanticweb.vlog4j.core.reasoner.vlog;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.semanticweb.vlog4j.core.reasoner.vlog.VLogExpressions.makeAtom;
import static org.semanticweb.vlog4j.core.reasoner.vlog.VLogExpressions.makeConstant;
import static org.semanticweb.vlog4j.core.reasoner.vlog.VLogExpressions.makeNegatedAtom;
import static org.semanticweb.vlog4j.core.reasoner.vlog.VLogExpressions.makeRule;
import static org.semanticweb.vlog4j.core.reasoner.vlog.VLogExpressions.makeVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.MaterializationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Rule;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.LogLevel;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

public class TestStratifiedNegation {

	/**
	 * P(x,y), Not(Q(x,y)) -> Q(x,y)
	 * 
	 * @throws EDBConfigurationException
	 * @throws NotStartedException
	 * @throws IOException
	 * @throws AlreadyStartedException
	 */
	@Test
	public void testSimpleInputNegationArity2()
			throws EDBConfigurationException, NotStartedException, AlreadyStartedException, IOException {
		final Term varX = makeVariable("X");
		final Term varY = makeVariable("Y");

		final Atom ruleHead = makeAtom("Q", varX, varY);

		final Rule rule = makeRule(ruleHead, VLogExpressions.makeAtom("P", varX, varY));

		final VLog vLog = new VLog();
		vLog.setLogLevel(LogLevel.DEBUG);

		final String[][] factTermsForP = { { "c", "d" } };
		vLog.addData("P", factTermsForP);

		final String[][] factTermsForQ = { { "e", "f" } };
		vLog.addData("Q", factTermsForQ);

		System.out.println(rule);
		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);

		try (final TermQueryResultIterator queryResult = vLog.query(makeAtom("P", varX, varY), true, false);) {
			assertTrue(queryResult.hasNext());
			final Term[] next = queryResult.next();
			assertArrayEquals(new Term[] { makeConstant("c"), makeConstant("d") }, next);
		}

		vLog.materialize(false);

		try (final TermQueryResultIterator queryResult = vLog.query(ruleHead, true, false);) {

			final List<List<Term>> result = new ArrayList<>(VLogQueryResultUtils.collectResults(queryResult));

			final List<List<Term>> expectedResult = Arrays.asList(Arrays.asList(makeConstant("c")),
					Arrays.asList(makeConstant("d")), Arrays.asList(makeConstant("e")),
					Arrays.asList(makeConstant("f")));
			
			assertEquals(expectedResult, result);

		}
	}

	/**
	 * P(x), Not(Q(x)) -> R(x) Q - EDB.
	 * 
	 * @throws EDBConfigurationException
	 * @throws NotStartedException
	 */
	@Test
	public void testSimpleInputNegation() throws EDBConfigurationException, NotStartedException {
		final Term varX = makeVariable("x");

		// P(x), Not(Q(x)) -> R(x) .
		final Atom isR = makeAtom("R", varX);

		final Atom isP = makeAtom("P", varX);
		final Atom isNotQ = makeNegatedAtom("Q", varX);
		final Rule rule = makeRule(isR, isP, isNotQ);

		final VLog vLog = new VLog();
		vLog.setLogLevel(LogLevel.DEBUG);

		// P(c) .
		final String[][] factTermsForP = { { "c" } };
		vLog.addData("P", factTermsForP);

		// Q(d) . => Q is an EDB predicate.
		final String[][] factTermsForQ = { { "d" } };
		vLog.addData("Q", factTermsForQ);

		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		System.out.println(rule);

		try (final TermQueryResultIterator queryResult = vLog.query(isP, true, false);) {
			assertTrue(queryResult.hasNext());
			final Term[] next = queryResult.next();
			assertArrayEquals(new Term[] { VLogExpressions.makeConstant("c") }, next);
		}

		final Atom isQ = VLogExpressions.makeAtom("Q", varX);
		try (final TermQueryResultIterator queryResult = vLog.query(isQ, true, false);) {
			assertTrue(queryResult.hasNext());
			final Term[] next = queryResult.next();
			assertArrayEquals(new Term[] { VLogExpressions.makeConstant("d") }, next);
		}

		try (final TermQueryResultIterator queryResult = vLog.query(isR, true, false);) {
			assertFalse(queryResult.hasNext());
		}

		vLog.materialize(false);

		try (final TermQueryResultIterator queryResult = vLog.query(isR, true, false);) {
			assertTrue(queryResult.hasNext());
			final Term[] next = queryResult.next();
			assertArrayEquals(new Term[] { VLogExpressions.makeConstant("c") }, next);
			assertFalse(queryResult.hasNext());
		}
	}

	/**
	 * P(x), Not(Q(x)) -> R(x) <br>
	 * R-IDB.
	 * 
	 * @throws EDBConfigurationException
	 * @throws NotStartedException
	 */
	@Test
	public void testStratifiedNegationOnIDB() throws EDBConfigurationException, NotStartedException {
		final Term varX = VLogExpressions.makeVariable("x");

		final Atom isP = VLogExpressions.makeAtom("P", varX);
		final Atom isNotQ = VLogExpressions.makeNegatedAtom("Q", varX);
		final Atom isR = VLogExpressions.makeAtom("R", varX);

		// P(x), Not(Q(x)) -> R(x) .
		final Rule rule = VLogExpressions.makeRule(isR, isP, isNotQ);
		System.out.println(rule);

		final VLog vLog = new VLog();
		final String[][] factTerms = { { "c" } };

		// P(c) .
		vLog.addData("P", factTerms);
		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);

		try (final TermQueryResultIterator queryResult = vLog.query(isR, true, false);) {
			assertFalse(queryResult.hasNext());
		}

		try (final TermQueryResultIterator queryResult = vLog.query(VLogExpressions.makeAtom("Q", varX), true,
				false);) {
			assertFalse(queryResult.hasNext());
		}

		try (final TermQueryResultIterator queryResult = vLog.query(isP, true, false);) {
			assertTrue(queryResult.hasNext());
		}
		vLog.materialize(true);

		try (final TermQueryResultIterator queryResult = vLog.query(isR, true, false);) {
			assertTrue(queryResult.hasNext());
			final Term[] next = queryResult.next();
			assertArrayEquals(new Term[] { VLogExpressions.makeConstant("c") }, next);
			assertFalse(queryResult.hasNext());
		}
	}

	/**
	 * P(x), Not(Q(x)) -> Q(x) <br>
	 * Q - IDB.
	 * 
	 * @throws EDBConfigurationException
	 * @throws NotStartedException
	 */
	@Test(expected = MaterializationException.class)
	public void testNegationOnIDBUnstratifiable() throws EDBConfigurationException, NotStartedException {
		final Term varX = VLogExpressions.makeVariable("x");
		final String predP = "P";
		final String predQ = "Q";

		final Atom isQ = VLogExpressions.makeAtom(predQ, varX);
		final Atom isP = VLogExpressions.makeAtom(predP, varX);
		final Atom isNotQ = VLogExpressions.makeNegatedAtom(predQ, varX);

		// P(x), Not(Q(x)) -> Q(x) .
		final Rule rule = VLogExpressions.makeRule(isQ, isP, isNotQ);

		final VLog vLog = new VLog();
		final String[][] factTerms = { { "c" } };

		// P(c) .
		vLog.addData(predP, factTerms);
		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);

		try (final TermQueryResultIterator queryResult = vLog.query(isQ, true, false);) {
			assertFalse(queryResult.hasNext());
		}
		vLog.materialize(true);
	}

}
