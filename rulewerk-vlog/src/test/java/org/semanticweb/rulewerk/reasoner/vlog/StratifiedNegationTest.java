package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.MaterializationException;
import karmaresearch.vlog.NonExistingPredicateException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Rule;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.LogLevel;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

public class StratifiedNegationTest {

	/**
	 * P(x), Not(Q(x)) -> R(x) Q - EDB.
	 *
	 * @throws EDBConfigurationException
	 * @throws NotStartedException
	 */
	@Test
	public void testSimpleInputNegation()
			throws EDBConfigurationException, NotStartedException, NonExistingPredicateException {
		final Term varX = VLogExpressions.makeVariable("x");

		// P(x), Not(Q(x)) -> R(x) .
		final Atom isR = VLogExpressions.makeAtom("R", varX);

		final Atom isP = VLogExpressions.makeAtom("P", varX);
		final Atom isNotQ = VLogExpressions.makeNegatedAtom("Q", varX);
		final Rule rule = VLogExpressions.makeRule(isR, isP, isNotQ);

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
	public void testStratifiedNegationOnIDB()
			throws EDBConfigurationException, NotStartedException, NonExistingPredicateException {
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
	public void testNegationOnIDBUnstratifiable()
			throws EDBConfigurationException, NotStartedException, NonExistingPredicateException {
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
