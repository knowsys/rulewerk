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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.PredicateImpl;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.ReasonerDataSource;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NonExistingPredicateException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;

public class VLogDataFromCsvFileTest {

	private static final String unzippedUnaryPredicateName1 = "p";
	private static final String unzippedUnaryPredicateName2 = "q";
	private static final String zippedUnaryPredicateName1 = "p_z";
	private static final String zippedUnaryPredicateName2 = "q_z";
	private static final String emptyUnaryPredicateName = "empty";

	private static final List<List<Term>> expectedUnaryQueryResult = Arrays.asList(
			Arrays.asList(VLogExpressions.makeConstant("c1")), Arrays.asList(VLogExpressions.makeConstant("c2")));

	private static List<List<Term>> getUnaryQueryResults(final VLog vLog, final String predicateName)
			throws NotStartedException, NonExistingPredicateException {
		final TermQueryResultIterator queryResultsPIterator = vLog
				.query(new Atom(predicateName, VLogExpressions.makeVariable("x")));
		final List<List<Term>> queryResults = new ArrayList<>(
				VLogQueryResultUtils.collectResults(queryResultsPIterator));
		return queryResults;
	}

	@Test
	public void testLoadDataFomCsvString() throws AlreadyStartedException, EDBConfigurationException, IOException,
			NotStartedException, NonExistingPredicateException {

		final String vLogDataSourcesConfigurationString = this.generateVLogDataSourceConfig();

		final VLog vLog = new VLog();
		vLog.start(vLogDataSourcesConfigurationString, false);

		final List<List<Term>> queryResult1 = getUnaryQueryResults(vLog, unzippedUnaryPredicateName1 + "-1");
		final List<List<Term>> queryResultZipped1 = getUnaryQueryResults(vLog, zippedUnaryPredicateName1 + "-1");
		assertEquals(expectedUnaryQueryResult, queryResult1);
		assertEquals(queryResult1, queryResultZipped1);

		final List<List<Term>> queryResult2 = getUnaryQueryResults(vLog, unzippedUnaryPredicateName2 + "-1");
		final List<List<Term>> queryResultZipped2 = getUnaryQueryResults(vLog, zippedUnaryPredicateName2 + "-1");
		assertEquals(expectedUnaryQueryResult, queryResult2);
		assertEquals(queryResult2, queryResultZipped2);

		vLog.stop();
	}

	private String generateVLogDataSourceConfig() throws IOException {
		final ReasonerDataSource unzippedCSV = new CsvFileDataSource(new File(FileDataSourceTestUtils.INPUT_FOLDER,
				FileDataSourceTestUtils.unzippedUnaryCsvFileRoot + ".csv").getPath());
		final DataSourceDeclaration unaryUnzippedCSV1 = new DataSourceDeclarationImpl(
				new PredicateImpl(unzippedUnaryPredicateName1, 1), unzippedCSV);
		final DataSourceDeclaration unaryUnzippedCSV2 = new DataSourceDeclarationImpl(
				new PredicateImpl(unzippedUnaryPredicateName2, 1), unzippedCSV);

		final ReasonerDataSource zippedCSV = new CsvFileDataSource(new File(FileDataSourceTestUtils.INPUT_FOLDER,
				FileDataSourceTestUtils.zippedUnaryCsvFileRoot + ".csv.gz").getPath());
		final DataSourceDeclaration unaryZippedCSV1 = new DataSourceDeclarationImpl(
				new PredicateImpl(zippedUnaryPredicateName1, 1), zippedCSV);
		final DataSourceDeclaration unaryZippedCSV2 = new DataSourceDeclarationImpl(
				new PredicateImpl(zippedUnaryPredicateName2, 1), zippedCSV);

		final KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatements(unaryUnzippedCSV1, unaryUnzippedCSV2, unaryZippedCSV1, unaryZippedCSV2);
		final VLogKnowledgeBase vLogKnowledgeBase = new VLogKnowledgeBase(knowledgeBase);

		final String vLogDataSourcesConfigurationString = vLogKnowledgeBase.getVLogDataSourcesConfigurationString();
		return vLogDataSourcesConfigurationString;
	}

	@Test(expected = NonExistingPredicateException.class)
	public void testLoadDataFomCsvStringNonExistingPredicate() throws AlreadyStartedException,
			EDBConfigurationException, IOException, NotStartedException, NonExistingPredicateException {

		final String vLogDataSourcesConfigurationString = this.generateVLogDataSourceConfig();

		final VLog vLog = new VLog();
		try {
			vLog.start(vLogDataSourcesConfigurationString, false);
			getUnaryQueryResults(vLog, emptyUnaryPredicateName);
		} finally {
			vLog.stop();
		}
	}

}
