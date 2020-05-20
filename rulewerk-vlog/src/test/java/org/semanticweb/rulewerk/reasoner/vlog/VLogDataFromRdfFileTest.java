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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NonExistingPredicateException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;

public class VLogDataFromRdfFileTest {

	private static final String unzippedTernaryPredicateName = "triple";
	private static final String zippedTernaryPredicateName = "triple_z";
	private static final String emptyTernaryPredicateName = "empty";

	private static final List<List<Term>> expectedTernaryQueryResult = Arrays.asList(
			Arrays.asList(VLogExpressions.makeConstant("<http://example.org/c1>"),
					VLogExpressions.makeConstant("<http://example.org/p>"),
					VLogExpressions.makeConstant("<http://example.org/c2>")),
			Arrays.asList(VLogExpressions.makeConstant("<http://example.org/c1>"),
					VLogExpressions.makeConstant("<http://example.org/q>"),
					VLogExpressions.makeConstant("\"test string\"^^<http://www.w3.org/2001/XMLSchema#string>")));

	private static List<List<Term>> getTernaryQueryResults(final VLog vLog, final String predicateName)
			throws NotStartedException, NonExistingPredicateException {
		final TermQueryResultIterator queryResultsPIterator = vLog
				.query(new Atom(predicateName, VLogExpressions.makeVariable("s"), VLogExpressions.makeVariable("p"),
						VLogExpressions.makeVariable("o")));
		final List<List<Term>> queryResults = new ArrayList<>(
				VLogQueryResultUtils.collectResults(queryResultsPIterator));
		return queryResults;
	}

	@Test
	public void testLoadDataFromRdfString() throws AlreadyStartedException, EDBConfigurationException, IOException,
			NotStartedException, NonExistingPredicateException {
		final String ternaryPredicateEDBConfig = "EDB0_predname=" + unzippedTernaryPredicateName + "\n"
				+ "EDB0_type=INMEMORY" + "\n" + "EDB0_param0=" + FileDataSourceTestUtils.INPUT_FOLDER + "\n"
				+ "EDB0_param1=" + FileDataSourceTestUtils.unzippedNtFileRoot + "\n" + "EDB1_predname="
				+ zippedTernaryPredicateName + "\n" + "EDB1_type=INMEMORY" + "\n" + "EDB1_param0="
				+ FileDataSourceTestUtils.INPUT_FOLDER + "\n" + "EDB1_param1="
				+ FileDataSourceTestUtils.zippedNtFileRoot;

		final VLog vLog = new VLog();
		vLog.start(ternaryPredicateEDBConfig, false);

		final List<List<Term>> queryResult = getTernaryQueryResults(vLog, unzippedTernaryPredicateName);
		final List<List<Term>> queryResultZipped = getTernaryQueryResults(vLog, zippedTernaryPredicateName);
		assertEquals(expectedTernaryQueryResult, queryResult);
		assertEquals(queryResult, queryResultZipped);

		vLog.stop();
	}

	@Test(expected = NonExistingPredicateException.class)
	public void testLoadDataFromRdfStringNonExistingPredicate() throws AlreadyStartedException,
			EDBConfigurationException, IOException, NotStartedException, NonExistingPredicateException {
		final String ternaryPredicateEDBConfig = "EDB0_predname=" + unzippedTernaryPredicateName + "\n"
				+ "EDB0_type=INMEMORY" + "\n" + "EDB0_param0=" + FileDataSourceTestUtils.INPUT_FOLDER + "\n"
				+ "EDB0_param1=" + FileDataSourceTestUtils.unzippedNtFileRoot + "\n" + "EDB1_predname="
				+ zippedTernaryPredicateName + "\n" + "EDB1_type=INMEMORY" + "\n" + "EDB1_param0="
				+ FileDataSourceTestUtils.INPUT_FOLDER + "\n" + "EDB1_param1="
				+ FileDataSourceTestUtils.zippedNtFileRoot;

		final VLog vLog = new VLog();
		try {
			vLog.start(ternaryPredicateEDBConfig, false);
			getTernaryQueryResults(vLog, emptyTernaryPredicateName);
		} finally {
			vLog.stop();
		}
	}

}
