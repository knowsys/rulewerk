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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.StringQueryResultEnumeration;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

@Ignore
public class VLogTest {
	@Test
	public void testVLogSimpleInference()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {

		// Creating rules and facts
		final String constantNameA = "a";
		final String constantNameB = "b";
		final String[][] argsAMatrix = { { constantNameA }, { constantNameB } };
		final karmaresearch.vlog.Term[] varX = {
				new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "X") };
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);
		final karmaresearch.vlog.Atom[] headAtoms = { atomBx };
		final karmaresearch.vlog.Atom[] bodyAtoms = { atomAx };
		final karmaresearch.vlog.Rule[] rules = { new karmaresearch.vlog.Rule(headAtoms, bodyAtoms) };

		// Start VLog
		final VLog vlog = new VLog();
		vlog.start("", false);
		vlog.addData("A", argsAMatrix);
		vlog.setRules(rules, RuleRewriteStrategy.NONE);
		vlog.materialize(true);

		// Querying B(?X)
		final StringQueryResultEnumeration queryResultEnnumeration = vlog.query(atomBx);

		// expected query result: [[a], [b]]
		final List<List<String>> expectedQueryResults = new ArrayList<>();
		final List<String> answer1 = new ArrayList<>();
		answer1.add(constantNameA);
		expectedQueryResults.add(answer1);
		final List<String> answer2 = new ArrayList<>();
		answer2.add(constantNameB);
		expectedQueryResults.add(answer2);

		final Set<List<String>> actualQueryResults = getAnswers(queryResultEnnumeration);
		System.out.println(actualQueryResults);
		assertEquals(expectedQueryResults, actualQueryResults);

		queryResultEnnumeration.cleanup();
		vlog.stop();
	}

	@Test
	public void testBooleanQueryTrue()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		// Creating rules and facts
		final String constantNameA = "a";
		final String[][] argsAMatrix = { { constantNameA, constantNameA } };
		final karmaresearch.vlog.Term varX = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				"X");
		final karmaresearch.vlog.Term varY = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				"Y");
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B",
				new karmaresearch.vlog.Term[] { varX, varY });
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A",
				new karmaresearch.vlog.Term[] { varX, varY });
		final karmaresearch.vlog.Atom[] headAtoms = { atomBx };
		final karmaresearch.vlog.Atom[] bodyAtoms = { atomAx };
		final karmaresearch.vlog.Rule[] rules = { new karmaresearch.vlog.Rule(headAtoms, bodyAtoms) };

		// Start VLog
		final VLog vlog = new VLog();
		vlog.start("", false);
		vlog.addData("A", argsAMatrix);
		vlog.setRules(rules, RuleRewriteStrategy.NONE);
		vlog.materialize(true);

		// Querying B(a)
		final karmaresearch.vlog.Term constantA = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				constantNameA);
		final karmaresearch.vlog.Atom booleanQueryAtomBa = new karmaresearch.vlog.Atom("B",
				new karmaresearch.vlog.Term[] { constantA, constantA });

		final StringQueryResultEnumeration queryResultEnnumeration = vlog.query(booleanQueryAtomBa);
		assertTrue(queryResultEnnumeration.hasMoreElements());
		final String[] actualQueryResult = queryResultEnnumeration.nextElement();
		final String[] expectedQueryResult = { constantNameA, constantNameA };
		Assert.assertArrayEquals(expectedQueryResult, actualQueryResult);
		assertFalse(queryResultEnnumeration.hasMoreElements());

		queryResultEnnumeration.cleanup();
		vlog.stop();
	}

	@Test
	public void testBooleanQueryFalse()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		final String[][] argsAMatrix = { { "a" } };
		final karmaresearch.vlog.Term[] varX = {
				new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "X") };
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);
		final karmaresearch.vlog.Atom[] headAtoms = { atomBx };
		final karmaresearch.vlog.Atom[] bodyAtoms = { atomAx };
		final karmaresearch.vlog.Rule[] rules = { new karmaresearch.vlog.Rule(headAtoms, bodyAtoms) };

		// Start VLog
		final VLog vlog = new VLog();
		vlog.start("", false);
		vlog.addData("A", argsAMatrix);
		vlog.setRules(rules, RuleRewriteStrategy.NONE);
		vlog.materialize(true);

		// Querying B(a)
		final karmaresearch.vlog.Term[] constantB = {
				new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, "b") };
		final karmaresearch.vlog.Atom booleanQueryAtomBb = new karmaresearch.vlog.Atom("B", constantB);

		final StringQueryResultEnumeration queryResultEnnumeration = vlog.query(booleanQueryAtomBb);
		assertFalse(queryResultEnnumeration.hasMoreElements());

		queryResultEnnumeration.cleanup();
		vlog.stop();
	}

	@Test
	public void testVLogQueryBeforeMaterialize()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {

		// Creating rules and facts
		final String constantNameA = "a";
		final String constantNameB = "b";
		final String[][] argsAMatrix = { { constantNameA }, { constantNameB } };
		final karmaresearch.vlog.Term[] varX = {
				new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "X") };
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);

		// Start VLog
		final VLog vlog = new VLog();
		vlog.start("", false);
		vlog.addData("A", argsAMatrix);

		// Querying A(?X)
		final StringQueryResultEnumeration queryResultEnnumeration = vlog.query(atomAx);

		// expected query result: [[a], [b]]
		final List<List<String>> expectedQueryResults = new ArrayList<>();
		final List<String> answer1 = new ArrayList<>();
		answer1.add(constantNameA);
		expectedQueryResults.add(answer1);
		final List<String> answer2 = new ArrayList<>();
		answer2.add(constantNameB);
		expectedQueryResults.add(answer2);

		// but was [a, 0_0_0, 0_0_0], [b, 0_0_0, 0_0_0]]
		final Set<List<String>> actualQueryResults = getAnswers(queryResultEnnumeration);
		System.out.println(actualQueryResults);
		assertEquals(expectedQueryResults, actualQueryResults);

		queryResultEnnumeration.cleanup();
		vlog.stop();
	}

	@Test
	public void testSupportedConstantNames()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		// final String constantNameNumber = "a";
		// final String constantNameStartsWithNumber = "b";

		final String constantNameNumber = "1";
		final String constantNameStartsWithNumber = "12_13_14";

		final String[][] argsAMatrix = { { constantNameNumber }, { constantNameStartsWithNumber } };

		final Set<List<String>> expectedQueryResultsA = new HashSet<>();
		final List<String> answer1 = new ArrayList<>();
		answer1.add(constantNameNumber);
		expectedQueryResultsA.add(answer1);
		final List<String> answer2 = new ArrayList<>();
		answer2.add(constantNameStartsWithNumber);
		expectedQueryResultsA.add(answer2);

		final karmaresearch.vlog.Term[] varX = {
				new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "X") };
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", varX);

		// Start VLog
		final VLog vlog = new VLog();
		vlog.start("", false);
		// add data: A(1), A(12_13_14).
		vlog.addData("A", argsAMatrix);

		// final Query VLog: A(?X)?
		// final StringQueryResultEnumeration queryResultEnnumerationABeforeMat =
		// vlog.query(atomAx);
		// assertTrue(queryResultEnnumerationABeforeMat.hasMoreElements());
		// final List<List<String>> actualQueryResultABeforeMat =
		// getAnswers(queryResultEnnumerationABeforeMat);
		// assertEquals(expectedQueryResultsA, actualQueryResultABeforeMat);

		// add rule A(x) -> B(x)
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", varX);
		final karmaresearch.vlog.Atom[] headAtoms = { atomBx };
		final karmaresearch.vlog.Atom[] bodyAtoms = { atomAx };
		final karmaresearch.vlog.Rule[] rules = { new karmaresearch.vlog.Rule(headAtoms, bodyAtoms) };
		vlog.setRules(rules, RuleRewriteStrategy.NONE);

		// materialize
		vlog.materialize(true);

		// // Query VLog: A(?X)?
		// final StringQueryResultEnumeration queryResultEnnumerationAAfterMat =
		// vlog.query(atomAx);
		// assertTrue(queryResultEnnumerationAAfterMat.hasMoreElements());
		// final List<List<String>> actualQueryResultAAfterMat =
		// getAnswers(queryResultEnnumerationAAfterMat);
		// System.out.println(actualQueryResultAAfterMat);
		// assertEquals(expectedQueryResultsA, actualQueryResultAAfterMat);

		// // Query VLog: B(?X)?
		final StringQueryResultEnumeration queryResultEnnumerationBAfterMat = vlog.query(atomBx);
		assertTrue(queryResultEnnumerationBAfterMat.hasMoreElements());
		final Set<List<String>> actualQueryResultBAfterMat = getAnswers(queryResultEnnumerationBAfterMat);
		assertEquals(expectedQueryResultsA, actualQueryResultBAfterMat);

		queryResultEnnumerationBAfterMat.cleanup();
		vlog.stop();
	}

	private static Set<List<String>> getAnswers(final StringQueryResultEnumeration queryResult) {
		final Set<List<String>> answers = new HashSet<>();
		while (queryResult.hasMoreElements()) {
			answers.add(Arrays.asList(queryResult.nextElement()));
		}

		queryResult.cleanup();

		return answers;
	}
}