package org.semanticweb.vlog4j.core.vlog;

/*
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Rule;
import karmaresearch.vlog.StringQueryResultIterator;
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
	public void testVLogSimpleInference()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {

		final String[][] argsAMatrix = { { "a" }, { "b" } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("X");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx);
		// tuples: [[a], [b]]
		final Set<List<String>> tuples = new HashSet<>();
		tuples.add(Arrays.asList("a"));
		tuples.add(Arrays.asList("b"));

		// Start VLog
		final VLog vlog = new VLog();
		vlog.addData("A", argsAMatrix); // Assert A(a), A(b)
		vlog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);

		// Querying A(?X) before materialize
		final StringQueryResultIterator queryResultIteratorAx1 = vlog.query(atomAx);
		final Set<List<String>> queryAxResults1 = collectAnswers(queryResultIteratorAx1);
		assertEquals(tuples, queryAxResults1);

		// Querying B(?X) before materialize
		final StringQueryResultIterator queryResultIteratorBx1 = vlog.query(atomBx);
		assertFalse(queryResultIteratorBx1.hasNext());
		queryResultIteratorBx1.close();

		vlog.materialize(true);

		// Querying B(?X) after materialize
		final StringQueryResultIterator queryResultIteratorBx2 = vlog.query(atomBx);
		final Set<List<String>> queryResultsBx = collectAnswers(queryResultIteratorBx2);
		assertEquals(tuples, queryResultsBx);

		final StringQueryResultIterator queryResultIteratorAx2 = vlog.query(atomAx);
		final Set<List<String>> queryAxResults2 = collectAnswers(queryResultIteratorAx2);
		assertEquals(tuples, queryAxResults2);

		vlog.stop();
	}

	@Test
	public void testBooleanQueryTrueIncludeConstantsFalse()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		// Creating rules and facts
		final String[][] argsAMatrix = { { "a", "a" } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("X");
		final karmaresearch.vlog.Term varY = VLogExpressions.makeVariable("Y");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX, varY);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX, varY);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx);

		// Start VLog
		final VLog vlog = new VLog();
		vlog.addData("A", argsAMatrix);
		vlog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		vlog.materialize(true);

		// Querying B(a)
		final karmaresearch.vlog.Term constantA = VLogExpressions.makeConstant("a");
		final karmaresearch.vlog.Atom booleanQueryAtomBa = new karmaresearch.vlog.Atom("B", constantA, constantA);

		final StringQueryResultIterator queryResultIterator = vlog.query(booleanQueryAtomBa);
		assertTrue(queryResultIterator.hasNext());
		final String[] actualQueryResult = queryResultIterator.next();
		final String[] expectedQueryResult = { "a", "a" };
		Assert.assertArrayEquals(expectedQueryResult, actualQueryResult);
		assertFalse(queryResultIterator.hasNext());
		queryResultIterator.close();

		final StringQueryResultIterator queryResultIteratorNoConstants = vlog.query(booleanQueryAtomBa, false, false);
		assertTrue(queryResultIteratorNoConstants.hasNext());
		assertTrue(queryResultIteratorNoConstants.next().length == 0);
		queryResultIteratorNoConstants.close();

		vlog.stop();
	}

	@Test
	public void testBooleanQueryTrueIncludeConstantsTrue()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		// Creating rules and facts
		final String[][] argsAMatrix = { { "a", "a" } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("X");
		final karmaresearch.vlog.Term varY = VLogExpressions.makeVariable("Y");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX, varY);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX, varY);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx);

		// Start VLog
		final VLog vlog = new VLog();
		vlog.addData("A", argsAMatrix);
		vlog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		vlog.materialize(true);

		// Querying B(a)
		final karmaresearch.vlog.Term constantA = VLogExpressions.makeConstant("a");
		final karmaresearch.vlog.Atom booleanQueryAtomBa = new karmaresearch.vlog.Atom("B", constantA, constantA);

		final StringQueryResultIterator queryResultIterator = vlog.query(booleanQueryAtomBa);
		assertTrue(queryResultIterator.hasNext());
		final String[] actualQueryResult = queryResultIterator.next();
		final String[] expectedQueryResult = { "a", "a" };
		Assert.assertArrayEquals(expectedQueryResult, actualQueryResult);
		assertFalse(queryResultIterator.hasNext());
		queryResultIterator.close();

		final StringQueryResultIterator queryResultIteratorWithConstants = vlog.query(booleanQueryAtomBa, true, false);
		assertTrue(queryResultIteratorWithConstants.hasNext());
		final String[] actualQueryResult2 = queryResultIterator.next();
		Assert.assertArrayEquals(expectedQueryResult, actualQueryResult2);
		queryResultIteratorWithConstants.close();

		vlog.stop();
	}

	@Test
	public void testBooleanQueryFalse()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		final String[][] argsAMatrix = { { "a" } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("X");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx);

		// Start VLog
		final VLog vlog = new VLog();
		vlog.addData("A", argsAMatrix);
		vlog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		vlog.materialize(true);

		// Querying B(a)
		final karmaresearch.vlog.Term constantB = VLogExpressions.makeConstant("b");
		final karmaresearch.vlog.Atom booleanQueryAtomBb = new karmaresearch.vlog.Atom("B", constantB);

		final StringQueryResultIterator queryResultEnnumeration = vlog.query(booleanQueryAtomBb);
		assertFalse(queryResultEnnumeration.hasNext());

		queryResultEnnumeration.close();
		vlog.stop();
	}

	@Test
	public void testSupportedConstantNames()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		final String constantNameNumber = "1";
		final String constantNameStartsWithNumber = "12_13_14";
		final String[][] argsAMatrix = { { constantNameNumber }, { constantNameStartsWithNumber } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("X");
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);

		final Set<List<String>> expectedQueryResultsA = new HashSet<>();
		expectedQueryResultsA.add(Arrays.asList(constantNameNumber));
		expectedQueryResultsA.add(Arrays.asList(constantNameStartsWithNumber));
		// Start VLog
		final VLog vlog = new VLog();
		// Assert: A(1), A(12_13_14).
		vlog.addData("A", argsAMatrix);

		// Query VLog: A(?X)?
		final StringQueryResultIterator queryResultIteratorABeforeMat = vlog.query(atomAx);
		assertTrue(queryResultIteratorABeforeMat.hasNext());
		final Set<List<String>> actualQueryResultABeforeMat = collectAnswers(queryResultIteratorABeforeMat);
		assertEquals(expectedQueryResultsA, actualQueryResultABeforeMat);

		// add rule A(x) -> B(x)
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx);
		vlog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);

		// materialize
		vlog.materialize(true);

		// Query VLog: B(?X)?
		final StringQueryResultIterator queryResultEnnumerationBAfterMat = vlog.query(atomBx);
		assertTrue(queryResultEnnumerationBAfterMat.hasNext());
		final Set<List<String>> actualQueryResultBAfterMat = collectAnswers(queryResultEnnumerationBAfterMat);
		assertEquals(expectedQueryResultsA, actualQueryResultBAfterMat);

		queryResultEnnumerationBAfterMat.close();
		vlog.stop();
	}

	@Test
	public void queryEmptyKnowledgeBase()
			throws NotStartedException, AlreadyStartedException, EDBConfigurationException, IOException {
		// Start VLog
		final VLog vlog = new VLog();
		vlog.start(StringUtils.EMPTY, false);

		final karmaresearch.vlog.Atom queryAtom = new karmaresearch.vlog.Atom("P", VLogExpressions.makeVariable("?x"));

		final StringQueryResultIterator stringQueryResultIterator = vlog.query(queryAtom);
		Assert.assertFalse(stringQueryResultIterator.hasNext());
		stringQueryResultIterator.close();

		vlog.materialize(true);

		final StringQueryResultIterator queryResultIteratorAfterReason = vlog.query(queryAtom);
		Assert.assertFalse(queryResultIteratorAfterReason.hasNext());
		queryResultIteratorAfterReason.close();

		vlog.stop();
	}

	@Test
	public void queryEmptyKnowledgeBaseSetRules()
			throws NotStartedException, AlreadyStartedException, EDBConfigurationException, IOException {
		// Start VLog
		final VLog vlog = new VLog();
		vlog.start(StringUtils.EMPTY, false);

		vlog.setRules(new Rule[] {}, VLog.RuleRewriteStrategy.NONE);

		final karmaresearch.vlog.Atom queryAtom = new karmaresearch.vlog.Atom("P", VLogExpressions.makeVariable("?x"));

		final StringQueryResultIterator stringQueryResultIterator = vlog.query(queryAtom);
		Assert.assertFalse(stringQueryResultIterator.hasNext());
		stringQueryResultIterator.close();

		vlog.materialize(true);

		final StringQueryResultIterator queryResultIteratorAfterReason = vlog.query(queryAtom);
		Assert.assertFalse(queryResultIteratorAfterReason.hasNext());
		queryResultIteratorAfterReason.close();

		vlog.stop();
	}

	private static Set<List<String>> collectAnswers(final StringQueryResultIterator queryResultIterator) {
		final Set<List<String>> answers = new HashSet<>();
		while (queryResultIterator.hasNext()) {
			answers.add(Arrays.asList(queryResultIterator.next()));
		}
		queryResultIterator.close();
		return answers;
	}
}