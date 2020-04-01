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
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NonExistingPredicateException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.VLog;

public class ExportQueryResultToCsvFileTest {

	@Test
	public void testExportUnaryPredicateFacts() throws EDBConfigurationException, NotStartedException, NonExistingPredicateException, IOException {
		final String[][] argsAMatrix = { { "c1" }, { "c2" } };
		final List<List<String>> expectedQueryResult = Arrays.asList(Arrays.asList("c1"), Arrays.asList("c2"));
		final VLog vLog = new VLog();
		vLog.addData("p", argsAMatrix);
		final String csvFilePath = FileDataSourceTestUtils.OUTPUT_FOLDER + "unaryFacts.csv";
		vLog.writeQueryResultsToCsv(new Atom("p", VLogExpressions.makeVariable("x")), csvFilePath);

		final List<List<String>> queryResult = FileDataSourceTestUtils.getCSVContent(csvFilePath);
		assertEquals(expectedQueryResult, queryResult);
	}

	@Test
	public void testExportBinaryPredicateFacts() throws EDBConfigurationException, NotStartedException, NonExistingPredicateException, IOException {
		final String[][] argsAMatrix = { { "c1", "c2" }, { "c3", "c4" } };
		final List<List<String>> expectedQueryResult = Arrays.asList(Arrays.asList("c1", "c2"),
				Arrays.asList("c3", "c4"));
		final VLog vLog = new VLog();
		vLog.addData("p", argsAMatrix);
		final String csvFilePath = FileDataSourceTestUtils.OUTPUT_FOLDER + "binaryFacts.csv";
		vLog.writeQueryResultsToCsv(new Atom("p", VLogExpressions.makeVariable("x"), VLogExpressions.makeVariable("y")),
				csvFilePath);

		final List<List<String>> queryResult = FileDataSourceTestUtils.getCSVContent(csvFilePath);
		assertEquals(expectedQueryResult, queryResult);
	}

}
