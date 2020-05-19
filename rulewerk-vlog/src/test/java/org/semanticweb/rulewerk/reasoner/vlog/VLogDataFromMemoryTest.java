package org.semanticweb.rulewerk.reasoner.vlog;

/*
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NonExistingPredicateException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Rule;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

/**
 * Tests VLog functionality when data (facts) is loaded exclusively from memory.
 * 
 * @author Irina.Dragoste
 *
 */
public class VLogDataFromMemoryTest {

	@Test
	public void testVLogSimpleInference() throws AlreadyStartedException, EDBConfigurationException, IOException,
			NotStartedException, NonExistingPredicateException {

		final String[][] argsAMatrix = { { "a" }, { "b" } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("x");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx);
		// tuples: [[a], [b]]
		final Set<List<Term>> tuples = new HashSet<>();
		tuples.add(Arrays.asList(VLogExpressions.makeConstant("a")));
		tuples.add(Arrays.asList(VLogExpressions.makeConstant("b")));

		// Start VLog
		final VLog vLog = new VLog();
		vLog.addData("A", argsAMatrix); // Assert A(a), A(b)
		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);

		// Querying A(?X) before materialize
		final TermQueryResultIterator queryResultIteratorAx1 = vLog.query(atomAx);
		final Set<List<Term>> queryAxResults1 = VLogQueryResultUtils.collectResults(queryResultIteratorAx1);
		assertEquals(tuples, queryAxResults1);

		// Querying B(?X) before materialize
		final TermQueryResultIterator queryResultIteratorBx1 = vLog.query(atomBx);
		assertFalse(queryResultIteratorBx1.hasNext());
		queryResultIteratorBx1.close();

		vLog.materialize(true);

		// Querying B(?X) after materialize
		final TermQueryResultIterator queryResultIteratorBx2 = vLog.query(atomBx);
		final Set<List<Term>> queryResultsBx = VLogQueryResultUtils.collectResults(queryResultIteratorBx2);
		assertEquals(tuples, queryResultsBx);

		final TermQueryResultIterator queryResultIteratorAx2 = vLog.query(atomAx);
		final Set<List<Term>> queryAxResults2 = VLogQueryResultUtils.collectResults(queryResultIteratorAx2);
		assertEquals(tuples, queryAxResults2);

		vLog.stop();
	}

	@Test
	public void testBooleanQueryTrueIncludeConstantsFalse() throws AlreadyStartedException, EDBConfigurationException,
			IOException, NotStartedException, NonExistingPredicateException {
		// Creating rules and facts
		final String[][] argsAMatrix = { { "a", "a" } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("X");
		final karmaresearch.vlog.Term varY = VLogExpressions.makeVariable("Y");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX, varY);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX, varY);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx);

		// Start VLog
		final VLog vLog = new VLog();
		vLog.addData("A", argsAMatrix);
		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		vLog.materialize(true);

		// Querying B(a)
		final karmaresearch.vlog.Term constantA = VLogExpressions.makeConstant("a");
		final karmaresearch.vlog.Atom booleanQueryAtomBa = new karmaresearch.vlog.Atom("B", constantA, constantA);

		final TermQueryResultIterator defaultIteratorWithConstantsAndBlanks = vLog.query(booleanQueryAtomBa);
		assertTrue(defaultIteratorWithConstantsAndBlanks.hasNext());
		final Term[] actualQueryResult = defaultIteratorWithConstantsAndBlanks.next();
		final Term[] expectedQueryResult = { constantA, constantA };
		Assert.assertArrayEquals(expectedQueryResult, actualQueryResult);
		assertFalse(defaultIteratorWithConstantsAndBlanks.hasNext());
		defaultIteratorWithConstantsAndBlanks.close();

		final TermQueryResultIterator iteratorNoConstantsNoBlanks = vLog.query(booleanQueryAtomBa, false, false);
		assertTrue(iteratorNoConstantsNoBlanks.hasNext());
		assertTrue(iteratorNoConstantsNoBlanks.next().length == 0);
		iteratorNoConstantsNoBlanks.close();

		final TermQueryResultIterator iteratorNoConstantsWithBlanks = vLog.query(booleanQueryAtomBa, false, true);
		assertTrue(iteratorNoConstantsWithBlanks.hasNext());
		Assert.assertTrue(iteratorNoConstantsWithBlanks.next().length == 0);
		assertFalse(iteratorNoConstantsWithBlanks.hasNext());
		iteratorNoConstantsWithBlanks.close();

		vLog.stop();
	}

	@Test
	public void testBooleanQueryTrueIncludeConstantsTrue() throws AlreadyStartedException, EDBConfigurationException,
			IOException, NotStartedException, NonExistingPredicateException {
		// Creating rules and facts
		final String[][] argsAMatrix = { { "a", "a" } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("X");
		final karmaresearch.vlog.Term varY = VLogExpressions.makeVariable("Y");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX, varY);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX, varY);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx); // A(x,x) -> B(x,x)

		// Start VLog
		final VLog vLog = new VLog();
		vLog.addData("A", argsAMatrix); // assert A(a,a)
		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		vLog.materialize(true);

		// Querying B(a)
		final karmaresearch.vlog.Term constantA = VLogExpressions.makeConstant("a");
		final karmaresearch.vlog.Atom booleanQueryAtomBa = new karmaresearch.vlog.Atom("B", constantA, constantA);

		final Term[] expectedQueryResult = { constantA, constantA };

		final TermQueryResultIterator defaultIteratorWithConstantsAndBlanks = vLog.query(booleanQueryAtomBa);
		assertTrue(defaultIteratorWithConstantsAndBlanks.hasNext());
		final Term[] actualQueryResult = defaultIteratorWithConstantsAndBlanks.next();
		Assert.assertArrayEquals(expectedQueryResult, actualQueryResult);
		assertFalse(defaultIteratorWithConstantsAndBlanks.hasNext());
		defaultIteratorWithConstantsAndBlanks.close();

		final TermQueryResultIterator iteratorWithConstantsAndBlanks = vLog.query(booleanQueryAtomBa, true, false);
		assertTrue(iteratorWithConstantsAndBlanks.hasNext());
		final Term[] actualQueryResult3 = iteratorWithConstantsAndBlanks.next();
		Assert.assertArrayEquals(expectedQueryResult, actualQueryResult3);
		assertFalse(iteratorWithConstantsAndBlanks.hasNext());
		iteratorWithConstantsAndBlanks.close();

		final TermQueryResultIterator iteratorWithConstantsNoBlanks = vLog.query(booleanQueryAtomBa, true, true);
		assertTrue(iteratorWithConstantsNoBlanks.hasNext());
		final Term[] actualQueryResult2 = iteratorWithConstantsNoBlanks.next();
		Assert.assertArrayEquals(expectedQueryResult, actualQueryResult2);
		assertFalse(iteratorWithConstantsNoBlanks.hasNext());
		iteratorWithConstantsNoBlanks.close();

		vLog.stop();
	}

	@Test
	public void testBooleanQueryFalse() throws AlreadyStartedException, EDBConfigurationException, IOException,
			NotStartedException, NonExistingPredicateException {
		final String[][] argsAMatrix = { { "a" } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("X");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx);

		// Start VLog
		final VLog vLog = new VLog();
		vLog.addData("A", argsAMatrix);
		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		vLog.materialize(true);

		// Querying B(a)
		final karmaresearch.vlog.Term constantB = VLogExpressions.makeConstant("b");
		final karmaresearch.vlog.Atom booleanQueryAtomBb = new karmaresearch.vlog.Atom("B", constantB);

		final TermQueryResultIterator queryResultEnnumeration = vLog.query(booleanQueryAtomBb);
		assertFalse(queryResultEnnumeration.hasNext());

		queryResultEnnumeration.close();
		vLog.stop();
	}

	@Test(expected = NonExistingPredicateException.class)
	public void queryEmptyKnowledgeBaseBeforeReasoning() throws NotStartedException, AlreadyStartedException,
			EDBConfigurationException, IOException, NonExistingPredicateException {
		// Start VLog
		final VLog vLog = new VLog();
		try {
			vLog.start(StringUtils.EMPTY, false);

			final karmaresearch.vlog.Atom queryAtom = new karmaresearch.vlog.Atom("P",
					VLogExpressions.makeVariable("?x"));

			vLog.query(queryAtom);
		} finally {
			vLog.stop();
		}
	}

	@Test(expected = NonExistingPredicateException.class)
	public void queryEmptyKnowledgeBaseAfterReasoning() throws NotStartedException, AlreadyStartedException,
			EDBConfigurationException, IOException, NonExistingPredicateException {
		// Start VLog
		final VLog vLog = new VLog();
		try {
			vLog.start(StringUtils.EMPTY, false);
			vLog.materialize(true);

			final karmaresearch.vlog.Atom queryAtom = new karmaresearch.vlog.Atom("P",
					VLogExpressions.makeVariable("?x"));

			vLog.query(queryAtom);
		} finally {
			vLog.stop();
		}
	}

}
