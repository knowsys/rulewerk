package org.semanticweb.vlog4j.core.reasoner.vlog;

/*-
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.vlog4j.core.reasoner.FileDataSourceUtils;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;

public class VLogDataFromCsvTest {

	private static final String unzippedUnaryPredicateNameP = "p";
	private static final String unzippedUnaryPredicateNameQ = "q";
	private static final String zippedUnaryPredicateNameP = "pz";
	private static final String zippedUnaryPredicateNameQ = "qz";

	private final List<List<Term>> expectedUnaryQueryResult = Arrays.asList(
			Arrays.asList(VLogExpressions.makeConstant("c1")), Arrays.asList(VLogExpressions.makeConstant("c2")));

	private static List<List<Term>> getUnaryQueryResults(final VLog vLog, final String predicateName)
			throws NotStartedException {
		final TermQueryResultIterator queryResultsPIterator = vLog
				.query(new Atom(predicateName, VLogExpressions.makeVariable("x")));
		final List<List<Term>> queryResults = new ArrayList<>(
				VLogQueryResultUtils.collectResults(queryResultsPIterator));
		return queryResults;
	}

	@Test
	public void testLoadDataFomCsvString()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		final String unaryPredicatesEDBConfig = "EDB0_predname=" + unzippedUnaryPredicateNameP + "\n"
				+ "EDB0_type=INMEMORY" + "\n" + "EDB0_param0=" + FileDataSourceUtils.INPUT_FOLDER + "\n"
				+ "EDB0_param1=" + FileDataSourceUtils.unzippedUnaryCsvFileRoot + "\n" + "EDB1_predname="
				+ unzippedUnaryPredicateNameQ + "\n" + "EDB1_type=INMEMORY" + "\n" + "EDB1_param0="
				+ FileDataSourceUtils.INPUT_FOLDER + "\n" + "EDB1_param1="
				+ FileDataSourceUtils.unzippedUnaryCsvFileRoot + "\n" + "EDB2_predname=" + zippedUnaryPredicateNameP
				+ "\n" + "EDB2_type=INMEMORY" + "\n" + "EDB2_param0=" + FileDataSourceUtils.INPUT_FOLDER + "\n"
				+ "EDB2_param1=" + FileDataSourceUtils.zippedUnaryCsvFileRoot + "\n" + "EDB3_predname="
				+ zippedUnaryPredicateNameQ + "\n" + "EDB3_type=INMEMORY" + "\n" + "EDB3_param0="
				+ FileDataSourceUtils.INPUT_FOLDER + "\n" + "EDB3_param1=" + FileDataSourceUtils.zippedUnaryCsvFileRoot;

		final VLog vLog = new VLog();
		vLog.start(unaryPredicatesEDBConfig, false);

		final List<List<Term>> queryResultP = getUnaryQueryResults(vLog, unzippedUnaryPredicateNameP);
		final List<List<Term>> queryResultZippedP = getUnaryQueryResults(vLog, zippedUnaryPredicateNameP);
		assertEquals(this.expectedUnaryQueryResult, queryResultP);
		assertEquals(queryResultP, queryResultZippedP);

		final List<List<Term>> queryResultQ = getUnaryQueryResults(vLog, unzippedUnaryPredicateNameQ);
		final List<List<Term>> queryResultZippedQ = getUnaryQueryResults(vLog, zippedUnaryPredicateNameQ);
		assertEquals(this.expectedUnaryQueryResult, queryResultQ);
		assertEquals(queryResultQ, queryResultZippedQ);

		final TermQueryResultIterator queryResultsRIterator = vLog
				.query(new Atom("t", VLogExpressions.makeVariable("x")));
		assertFalse(queryResultsRIterator.hasNext());

		queryResultsRIterator.close();
		vLog.stop();
	}
}
