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
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.QueryResultIterator;

public class VLogReasonerCsvInput {

	private static final Predicate unaryPredicate1 = Expressions.makePredicate("p", 1);
	private static final Predicate unaryPredicate2 = Expressions.makePredicate("q", 1);

	@SuppressWarnings("unchecked")
	private static final Set<List<Term>> expectedUnaryQueryResult = Sets.newSet(Arrays.asList(makeConstant("c1")),
			Arrays.asList(makeConstant("c2")));

	@Ignore
	// FIXME: test ignored because of a bug in VLog. Remore the @Ignore annotation
	// after bug is fixed.
	@Test
	public void testLoadEmptyCsvFile() throws IOException {
		final PositiveLiteral queryAtom = Expressions.makePositiveLiteral(unaryPredicate1, makeVariable("x"));

		FileDataSourceTestUtils.testLoadEmptyFile(unaryPredicate1, queryAtom,
				new CsvFileDataSource(new File(FileDataSourceTestUtils.INPUT_FOLDER + "empty.csv")));
		FileDataSourceTestUtils.testLoadEmptyFile(unaryPredicate1, queryAtom,
				new CsvFileDataSource(new File(FileDataSourceTestUtils.INPUT_FOLDER + "empty.csv.gz")));
	}

	@Test
	public void testLoadUnaryFactsFromCsvFile() throws IOException {
		testLoadUnaryFactsFromSingleCsvDataSource(new CsvFileDataSource(new File(
				FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.unzippedUnaryCsvFileRoot + ".csv")));
		testLoadUnaryFactsFromSingleCsvDataSource(new CsvFileDataSource(new File(
				FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.zippedUnaryCsvFileRoot + ".csv.gz")));
	}

	private void testLoadUnaryFactsFromSingleCsvDataSource(final FileDataSource fileDataSource) throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(unaryPredicate1, fileDataSource));
		kb.addStatement(new DataSourceDeclarationImpl(unaryPredicate2, fileDataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			final QueryResultIterator queryResultIterator1 = reasoner
					.answerQuery(Expressions.makePositiveLiteral(unaryPredicate1, makeVariable("x")), true);
			final Set<List<Term>> queryResult1 = QueryResultsUtils.collectQueryResults(queryResultIterator1);
			final QueryResultIterator queryResultIterator2 = reasoner
					.answerQuery(Expressions.makePositiveLiteral(unaryPredicate2, makeVariable("x")), true);
			final Set<List<Term>> queryResult2 = QueryResultsUtils.collectQueryResults(queryResultIterator2);

			assertEquals(expectedUnaryQueryResult, queryResult1);
			assertEquals(expectedUnaryQueryResult, queryResult2);
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
	public void testLoadNonexistingCsvFile() throws IOException {
		final File nonexistingFile = new File("nonexistingFile.csv");
		assertFalse(nonexistingFile.exists());
		final FileDataSource fileDataSource = new CsvFileDataSource(nonexistingFile);
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(unaryPredicate1, fileDataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
		}
	}

	@Test(expected = IncompatiblePredicateArityException.class)
	public void testLoadCsvFileWrongArity() throws IOException {
		final FileDataSource fileDataSource = new CsvFileDataSource(new File(
				FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.binaryCsvFileNameRoot + ".csv"));
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(unaryPredicate1, fileDataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
		}
	}

}
