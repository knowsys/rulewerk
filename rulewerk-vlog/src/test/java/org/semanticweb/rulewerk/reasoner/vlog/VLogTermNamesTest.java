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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

public class VLogTermNamesTest {

	@Test
	public void testTermCase() throws EDBConfigurationException, NotStartedException, NonExistingPredicateException {
		final String[][] argsAMatrix = { { "A" }, { "a" } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("x");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("b", varX);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx); // A(?x) -> b(?x)
		// tuples: [[A], [a]]
		final Set<List<Term>> tuples = new HashSet<>();
		tuples.add(Arrays.asList(VLogExpressions.makeConstant("A")));
		tuples.add(Arrays.asList(VLogExpressions.makeConstant("a")));

		// Start VLog
		final VLog vLog = new VLog();
		vLog.addData("A", argsAMatrix); // Assert A(A), A(a)
		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);

		// Querying A(?X) before materialize
		final TermQueryResultIterator queryResultIteratorAx1 = vLog.query(atomAx);
		final Set<List<Term>> queryAxResults1 = VLogQueryResultUtils.collectResults(queryResultIteratorAx1);
		assertEquals(tuples, queryAxResults1);

		// Querying b(?X) before materialize
		final TermQueryResultIterator queryResultIteratorBx1 = vLog.query(atomBx);
		assertFalse(queryResultIteratorBx1.hasNext());
		queryResultIteratorBx1.close();

		vLog.materialize(true);

		// Querying b(?X) after materialize
		final TermQueryResultIterator queryResultIteratorBx2 = vLog.query(atomBx);
		final Set<List<Term>> queryResultsBx = VLogQueryResultUtils.collectResults(queryResultIteratorBx2);
		assertEquals(tuples, queryResultsBx);

		// Querying A(?X) after materialize
		final TermQueryResultIterator queryResultIteratorAx12 = vLog.query(atomAx);
		final Set<List<Term>> queryAxResults2 = VLogQueryResultUtils.collectResults(queryResultIteratorAx12);
		assertEquals(tuples, queryAxResults2);

	}

	@Test
	public void testSupportedConstantNames()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException, NonExistingPredicateException {
		final String constantNameNumber = "1";
		final String constantNameStartsWithNumber = "12_13_14";
		final String[][] argsAMatrix = { { constantNameNumber }, { constantNameStartsWithNumber } };
		final karmaresearch.vlog.Term varX = VLogExpressions.makeVariable("X");
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);

		final Set<List<Term>> expectedQueryResultsA = new HashSet<>();
		expectedQueryResultsA.add(Arrays.asList(VLogExpressions.makeConstant(constantNameNumber)));
		expectedQueryResultsA.add(Arrays.asList(VLogExpressions.makeConstant(constantNameStartsWithNumber)));
		// Start VLog
		final VLog vLog = new VLog();
		// Assert: A(1), A(12_13_14).
		vLog.addData("A", argsAMatrix);

		// Query VLog: A(?X)?
		final TermQueryResultIterator queryResultIteratorABeforeMat = vLog.query(atomAx);
		assertTrue(queryResultIteratorABeforeMat.hasNext());
		final Set<List<Term>> actualQueryResultABeforeMat = VLogQueryResultUtils
				.collectResults(queryResultIteratorABeforeMat);
		assertEquals(expectedQueryResultsA, actualQueryResultABeforeMat);

		// add rule A(x) -> B(x)
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX);
		final Rule rule = VLogExpressions.makeRule(atomBx, atomAx);
		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);

		// materialize
		vLog.materialize(true);

		// Query VLog: B(?X)?
		final TermQueryResultIterator queryResultEnnumerationBAfterMat = vLog.query(atomBx);
		assertTrue(queryResultEnnumerationBAfterMat.hasNext());
		final Set<List<Term>> actualQueryResultBAfterMat = VLogQueryResultUtils
				.collectResults(queryResultEnnumerationBAfterMat);
		assertEquals(expectedQueryResultsA, actualQueryResultBAfterMat);

		queryResultEnnumerationBAfterMat.close();
		vLog.stop();
	}

}
