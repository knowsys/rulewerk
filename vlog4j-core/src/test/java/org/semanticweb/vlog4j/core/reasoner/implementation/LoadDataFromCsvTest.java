package org.semanticweb.vlog4j.core.reasoner.implementation;

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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.CsvFileUtils;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

import karmaresearch.vlog.EDBConfigurationException;

public class LoadDataFromCsvTest {

	private static final File UNARY_FACTS_CSV_FILE = new File(CsvFileUtils.CSV_INPORT_FOLDER + "unaryFacts.csv");

	@Test
	public void testGenerateDataSourcesConfigEmpty() throws ReasonerStateException, IOException {
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			final String dataSourcesConfig = reasoner.generateDataSourcesConfig();
			assertTrue(dataSourcesConfig.isEmpty());
		}
	}

	@Test
	public void testLoadEmptyCsv()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final File emptyCsv = new File(CsvFileUtils.CSV_INPORT_FOLDER + "empty.csv");
		final CsvFileDataSource emptyDataSource = new CsvFileDataSource(emptyCsv);

		final Predicate predicateP = Expressions.makePredicate("p", 2);
		final Atom queryAtom = Expressions.makeAtom(predicateP, Expressions.makeVariable("x"),
				Expressions.makeVariable("y"));
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addFactsFromDataSource(predicateP, emptyDataSource);
			reasoner.load();
			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			reasoner.reason();
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(queryAtom, true)) {
				assertFalse(answerQuery.hasNext());
			}
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(queryAtom, false)) {
				assertFalse(answerQuery.hasNext());
			}
			reasoner.resetReasoner();
			reasoner.load();
			reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
			reasoner.reason();
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(queryAtom, true)) {
				assertFalse(answerQuery.hasNext());
			}
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(queryAtom, false)) {
				assertFalse(answerQuery.hasNext());
			}
		}
	}

	@Test
	public void testLoadUnaryFactsFromCsv() throws ReasonerStateException, EdbIdbSeparationException,
			EDBConfigurationException, IOException, IncompatiblePredicateArityException {
		final Predicate predicateP = Expressions.makePredicate("p", 1);
		final Predicate predicateQ = Expressions.makePredicate("q", 1);
		final DataSource dataSource = new CsvFileDataSource(UNARY_FACTS_CSV_FILE);
		@SuppressWarnings("unchecked")
		final Set<List<Term>> expectedPQueryResults = Sets.newSet(Arrays.asList(Expressions.makeConstant("c1")),
				Arrays.asList(Expressions.makeConstant("c2")));
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addFactsFromDataSource(predicateP, dataSource);
			reasoner.addFactsFromDataSource(predicateQ, dataSource);
			reasoner.load();
			final QueryResultIterator pQueryResultIterator = reasoner
					.answerQuery(Expressions.makeAtom(predicateP, Expressions.makeVariable("x")), true);
			final Set<List<Term>> pQueryResults = QueryResultsUtils.collectQueryResults(pQueryResultIterator);
			assertEquals(expectedPQueryResults, pQueryResults);
		}
	}

}
