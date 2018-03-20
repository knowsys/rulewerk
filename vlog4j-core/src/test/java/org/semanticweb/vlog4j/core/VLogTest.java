package org.semanticweb.vlog4j.core;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.StringQueryResultEnumeration;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

public class VLogTest extends TestCase {

	public void testVLogSimpleInference() throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {

		// Creating rules and facts
		final String constantNameA = "a";
		final String constantNameB = "b";
		final String[][] argsAMatrix = { { constantNameA }, { constantNameB } };
		final karmaresearch.vlog.Term[] argX = { new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "X") };
		final karmaresearch.vlog.Atom atomBx = new karmaresearch.vlog.Atom("B", argX);
		final karmaresearch.vlog.Atom atomAx = new karmaresearch.vlog.Atom("A", argX);
		final karmaresearch.vlog.Atom[] headAtoms = { atomBx };
		final karmaresearch.vlog.Atom[] bodyAtoms = { atomAx };
		final karmaresearch.vlog.Rule[] rules = { new karmaresearch.vlog.Rule(headAtoms, bodyAtoms) };

		// Start VLog
		final VLog vlog = new VLog();
		vlog.start("", false);
		vlog.addData("A", argsAMatrix);
		vlog.setRules(rules, RuleRewriteStrategy.NONE);
		vlog.materialize(true);

		final List<List<String>> expectedQueryResults = new ArrayList<>();
		final List<String> answer1 = new ArrayList<>();
		answer1.add(constantNameA);
		expectedQueryResults.add(answer1);
		final List<String> answer2 = new ArrayList<>();
		answer2.add(constantNameB);
		expectedQueryResults.add(answer2);

		// Querying
		final StringQueryResultEnumeration answers = vlog.query(atomBx);

		final List<List<String>> actualQueryResults = getAnswers(answers);
		assertEquals(expectedQueryResults, actualQueryResults);

		vlog.stop();
	}

	public static List<List<String>> getAnswers(final StringQueryResultEnumeration queryResult) {
		final List<List<String>> answers = new ArrayList<>();
		while (queryResult.hasMoreElements()) {
			answers.add(Arrays.asList(queryResult.nextElement()));
		}

		queryResult.cleanup();

		return answers;
	}
}
