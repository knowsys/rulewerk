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
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

import karmaresearch.vlog.EDBConfigurationException;

public class LoadDataFromCsvTest {

	private static final String unaryPredicateName1 = "p";
	private static final String unaryPredicateName2 = "q";

	@SuppressWarnings("unchecked")
	private static final Set<List<Term>> expectedUnaryQueryResult = Sets
			.newSet(Arrays.asList(Expressions.makeConstant("c1")), Arrays.asList(Expressions.makeConstant("c2")));

	@Test
	public void testLoadEmptyCsvFile()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final Predicate predicate = Expressions.makePredicate(unaryPredicateName1, 1);
		final Atom queryAtom = Expressions.makeAtom(predicate, Expressions.makeVariable("x"));

		for (final String csvFileName : Arrays.asList("empty.csv", "empty.csv.gz")) {
			final File emptyCsvFile = new File(FileDataSourceUtils.INPUT_FOLDER + csvFileName);
			final FileDataSource emptyDataSource = new CsvFileDataSource(emptyCsvFile);

			try (final Reasoner reasoner = Reasoner.getInstance()) {
				reasoner.addFactsFromDataSource(predicate, emptyDataSource);
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
	public void testLoadUnaryFactsFromCsvFile() throws ReasonerStateException, EdbIdbSeparationException,
	EDBConfigurationException, IOException, IncompatiblePredicateArityException {
		final Predicate predicate1 = Expressions.makePredicate(unaryPredicateName1, 1);
		final Predicate predicate2 = Expressions.makePredicate(unaryPredicateName2, 1);

		for (final String csvFileName : Arrays.asList(FileDataSourceUtils.unzippedUnaryCsvFileRoot + ".csv",
				FileDataSourceUtils.zippedUnaryCsvFileRoot + ".csv.gz")) {
			final File csvFile = new File(FileDataSourceUtils.INPUT_FOLDER + csvFileName);
			final FileDataSource fileDataSource = new CsvFileDataSource(csvFile);

			try (final Reasoner reasoner = Reasoner.getInstance()) {
				reasoner.addFactsFromDataSource(predicate1, fileDataSource);
				reasoner.addFactsFromDataSource(predicate2, fileDataSource);
				reasoner.load();

				final QueryResultIterator queryResultIterator1 = reasoner
						.answerQuery(Expressions.makeAtom(predicate1, Expressions.makeVariable("x")), true);
				final Set<List<Term>> queryResult1 = QueryResultsUtils.collectQueryResults(queryResultIterator1);
				final QueryResultIterator queryResultIterator2 = reasoner
						.answerQuery(Expressions.makeAtom(predicate2, Expressions.makeVariable("x")), true);
				final Set<List<Term>> queryResult2 = QueryResultsUtils.collectQueryResults(queryResultIterator2);

				assertEquals(expectedUnaryQueryResult, queryResult1);
				assertEquals(expectedUnaryQueryResult, queryResult2);
			}
		}
	}

	/**
	 * Tries to add a {@code CsvFileDataSource} from a file that does not exist on
	 * disk.
	 *
	 * @throws IOException
	 * @throws ReasonerStateException
	 * @throws EdbIdbSeparationException
	 * @throws IncompatiblePredicateArityException
	 */
	@Test(expected = IOException.class)
	public void testCsvFileNotOnDisk()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final File unexistingFile = new File("unexistingFile.csv");
		assertFalse(unexistingFile.exists());
		final FileDataSource fileDataSource = new CsvFileDataSource(unexistingFile);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(Expressions.makePredicate(unaryPredicateName1, 1), fileDataSource);
			reasoner.load();
		}
	}

}
