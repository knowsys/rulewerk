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
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.PredicateImpl;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.implementation.RdfFileDataSource;

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
					// TODO: see comments of https://github.com/karmaresearch/vlog/issues/73 for
					// expected value
//					VLogExpressions.makeConstant("\"test string\"^^<http://www.w3.org/2001/XMLSchema#string>")
					VLogExpressions.makeConstant("\"test string\"")));

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
	public void testLoadDataFromRdfStringUnzipped() throws AlreadyStartedException, EDBConfigurationException,
			IOException, NotStartedException, NonExistingPredicateException {

		final VLog vLog = new VLog();
		vLog.start(this.generateVLogDataSourceConfig(), false);

		final List<List<Term>> queryResult = getTernaryQueryResults(vLog, unzippedTernaryPredicateName + "-3");
		assertEquals(expectedTernaryQueryResult, queryResult);

		vLog.stop();
	}

	@Test
	public void testLoadDataFromRdfStringZipped() throws AlreadyStartedException, EDBConfigurationException,
			IOException, NotStartedException, NonExistingPredicateException {

		final VLog vLog = new VLog();
		vLog.start(this.generateVLogDataSourceConfig(), false);

		final List<List<Term>> queryResultZipped = getTernaryQueryResults(vLog, zippedTernaryPredicateName + "-3");
		assertEquals(expectedTernaryQueryResult, queryResultZipped);

		vLog.stop();
	}

	@Test(expected = NonExistingPredicateException.class)
	public void testLoadDataFromRdfStringNonExistingPredicate() throws AlreadyStartedException,
			EDBConfigurationException, IOException, NotStartedException, NonExistingPredicateException {

		final VLog vLog = new VLog();
		try {
			vLog.start(this.generateVLogDataSourceConfig(), false);
			getTernaryQueryResults(vLog, emptyTernaryPredicateName);
		} finally {
			vLog.stop();
		}
	}

	private String generateVLogDataSourceConfig() throws IOException {
		final RdfFileDataSource unzippedRDFDataSource = new RdfFileDataSource(
				new File(FileDataSourceTestUtils.INPUT_FOLDER, FileDataSourceTestUtils.unzippedNtFileRoot + ".nt")
						.getPath());
		final DataSourceDeclarationImpl unzippedRDF = new DataSourceDeclarationImpl(
				new PredicateImpl(unzippedTernaryPredicateName, 3), unzippedRDFDataSource);

		final RdfFileDataSource zippedRDFDataSource = new RdfFileDataSource(
				new File(FileDataSourceTestUtils.INPUT_FOLDER, FileDataSourceTestUtils.zippedNtFileRoot + ".nt.gz")
						.getPath());
		final DataSourceDeclarationImpl zippedRDF = new DataSourceDeclarationImpl(
				new PredicateImpl(zippedTernaryPredicateName, 3), zippedRDFDataSource);

		final KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatements(unzippedRDF, zippedRDF);
		final VLogKnowledgeBase vLogKnowledgeBase = new VLogKnowledgeBase(knowledgeBase);
		return vLogKnowledgeBase.getVLogDataSourcesConfigurationString();

	}

}
