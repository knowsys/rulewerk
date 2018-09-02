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

import static org.junit.Assert.assertTrue;

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
	private static final String unaryPredicateNameP = "p";
	private static final String unaryPredicateNameQ = "q";

	private final List<List<Term>> expectedQueryResultUnary = Arrays.asList(
			Arrays.asList(VLogExpressions.makeConstant("c1")), Arrays.asList(VLogExpressions.makeConstant("c2")));

	private boolean checkCsvLoad(final String csvFileName)
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		final String unaryPredicatesEDBConfig = "EDB0_predname=" + unaryPredicateNameQ + "\n" + "EDB0_type=INMEMORY\n"
				+ "EDB0_param0=" + FileDataSourceUtils.INPUT_FOLDER + "\n" + "EDB0_param1=" + csvFileName + "\n"
				+ "EDB1_predname=" + unaryPredicateNameP + "\n" + "EDB1_type=INMEMORY\n" + "EDB1_param0="
				+ FileDataSourceUtils.INPUT_FOLDER + "\n" + "EDB1_param1=" + csvFileName;
		final VLog vLog = new VLog();
		vLog.start(unaryPredicatesEDBConfig, false);
		final TermQueryResultIterator queryResultsPIterator = vLog
				.query(new Atom(unaryPredicateNameP, VLogExpressions.makeVariable("x")));
		final List<List<Term>> queryResultsP = new ArrayList<>(
				VLogQueryResultUtils.collectResults(queryResultsPIterator));
		if (!this.expectedQueryResultUnary.equals(queryResultsP)) {
			return false;
		}

		final TermQueryResultIterator queryResultsQIterator = vLog
				.query(new Atom(unaryPredicateNameQ, VLogExpressions.makeVariable("x")));
		final List<List<Term>> queryResultsQ = new ArrayList<>(
				VLogQueryResultUtils.collectResults(queryResultsQIterator));
		if (!this.expectedQueryResultUnary.equals(queryResultsQ)) {
			return false;
		}

		final TermQueryResultIterator queryResultsRIterator = vLog
				.query(new Atom("t", VLogExpressions.makeVariable("x")));
		if (queryResultsRIterator.hasNext()) {
			return false;
		}
		queryResultsRIterator.close();
		vLog.stop();

		return true;
	}

	@Test
	public void testLoadDataFomCsvString()
			throws AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		assertTrue(checkCsvLoad("unaryFacts"));
		assertTrue(checkCsvLoad("unaryFactsZipped"));
	}
}
