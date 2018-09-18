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
import org.semanticweb.vlog4j.core.reasoner.FileDataSourceUtils;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

import karmaresearch.vlog.EDBConfigurationException;

public class LoadDataFromCsvTest {

	private static final String unaryPredicateNameP = "p";
	private static final String unaryPredicateNameQ = "q";

	@SuppressWarnings("unchecked")
	private static final Set<List<Term>> expectedUnaryQueryResult = Sets
			.newSet(Arrays.asList(Expressions.makeConstant("c1")), Arrays.asList(Expressions.makeConstant("c2")));

	@Test
	public void testLoadEmptyCsv()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final Predicate predicateP = Expressions.makePredicate("p", 2);
		final Atom queryAtom = Expressions.makeAtom(predicateP, Expressions.makeVariable("x"),
				Expressions.makeVariable("y"));

		for (final String csvFileName : Arrays.asList("empty.csv", "empty.csv.gz")) {
			final File emptyCsvFile = new File(FileDataSourceUtils.INPUT_FOLDER + csvFileName);
			final CsvFileDataSource emptyCsvDataSource = new CsvFileDataSource(emptyCsvFile);

			try (final VLogReasoner reasoner = new VLogReasoner()) {
				reasoner.addFactsFromDataSource(predicateP, emptyCsvDataSource);
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
	}

	@Test
	public void testLoadUnaryFactsFromCsv() throws ReasonerStateException, EdbIdbSeparationException,
	EDBConfigurationException, IOException, IncompatiblePredicateArityException {
		final Predicate predicateP = Expressions.makePredicate(unaryPredicateNameP, 1);
		final Predicate predicateQ = Expressions.makePredicate(unaryPredicateNameQ, 1);

		for (final String csvFileName : Arrays.asList(FileDataSourceUtils.unzippedUnaryCsvFileRoot + ".csv",
				FileDataSourceUtils.zippedUnaryCsvFileRoot + ".csv.gz")) {
			final File csvFile = new File(FileDataSourceUtils.INPUT_FOLDER + csvFileName);
			final CsvFileDataSource csvFileDataSource = new CsvFileDataSource(csvFile);

			try (final VLogReasoner reasoner = new VLogReasoner()) {
				reasoner.addFactsFromDataSource(predicateP, csvFileDataSource);
				reasoner.addFactsFromDataSource(predicateQ, csvFileDataSource);
				reasoner.load();

				final QueryResultIterator pQueryResultIterator = reasoner
						.answerQuery(Expressions.makeAtom(predicateP, Expressions.makeVariable("x")), true);
				final Set<List<Term>> pQueryResult = QueryResultsUtils.collectQueryResults(pQueryResultIterator);
				final QueryResultIterator qQueryResultIterator = reasoner
						.answerQuery(Expressions.makeAtom(predicateQ, Expressions.makeVariable("x")), true);
				final Set<List<Term>> qQueryResult = QueryResultsUtils.collectQueryResults(qQueryResultIterator);

				assertEquals(expectedUnaryQueryResult, pQueryResult);
				assertEquals(pQueryResult, qQueryResult);
			}
		}
	}

}
