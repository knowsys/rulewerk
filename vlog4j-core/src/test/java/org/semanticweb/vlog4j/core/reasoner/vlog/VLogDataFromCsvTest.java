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
