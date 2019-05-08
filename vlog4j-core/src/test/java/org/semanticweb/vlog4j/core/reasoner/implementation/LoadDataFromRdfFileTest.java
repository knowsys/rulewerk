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

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

import karmaresearch.vlog.EDBConfigurationException;

public class LoadDataFromRdfFileTest {

	private static final Predicate ternaryPredicate = Expressions.makePredicate("triple", 3);
	private static final PositiveLiteral queryAtom = Expressions.makePositiveLiteral(ternaryPredicate, makeVariable("s"), makeVariable("p"),
			makeVariable("o"));

	@SuppressWarnings("unchecked")
	private static final Set<List<Term>> expectedTernaryQueryResult = Sets.newSet(
			Arrays.asList(makeConstant("<c1>"), makeConstant("<q>"), makeConstant("<c2>")),
			Arrays.asList(makeConstant("<c1>"), makeConstant("<p>"), makeConstant("<c2>")));

	@Test
	public void testLoadEmptyRdfFile()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		FileDataSourceTestUtils.testLoadEmptyFile(ternaryPredicate, queryAtom,
				new RdfFileDataSource(new File(FileDataSourceTestUtils.INPUT_FOLDER + "empty.nt")));
		FileDataSourceTestUtils.testLoadEmptyFile(ternaryPredicate, queryAtom,
				new RdfFileDataSource(new File(FileDataSourceTestUtils.INPUT_FOLDER + "empty.nt.gz")));
	}

	@Test
	public void testLoadTernaryFactsFromRdfFile() throws ReasonerStateException, EdbIdbSeparationException,
	EDBConfigurationException, IOException, IncompatiblePredicateArityException {
		testLoadTernaryFactsFromSingleRdfDataSource(new RdfFileDataSource(
				new File(FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.unzippedNtFileRoot + ".nt")));
		testLoadTernaryFactsFromSingleRdfDataSource(new RdfFileDataSource(
				new File(FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.zippedNtFileRoot + ".nt.gz")));
	}

	public void testLoadTernaryFactsFromSingleRdfDataSource(final FileDataSource fileDataSource)
			throws ReasonerStateException, EdbIdbSeparationException, EDBConfigurationException, IOException,
			IncompatiblePredicateArityException {
		final KnowledgeBase kb = new KnowledgeBaseImpl();

		try (final Reasoner reasoner = Reasoner.getInstance(kb)) {
			reasoner.addFactsFromDataSource(ternaryPredicate, fileDataSource);
			reasoner.load();

			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true);
			final Set<List<Term>> queryResult = QueryResultsUtils.collectQueryResults(queryResultIterator);

			assertEquals(expectedTernaryQueryResult, queryResult);
		}
	}

	@Test(expected = IOException.class)
	public void testLoadNonexistingRdfFile()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final File nonexistingFile = new File("nonexistingFile.nt");
		assertFalse(nonexistingFile.exists());
		final FileDataSource fileDataSource = new RdfFileDataSource(nonexistingFile);
		final KnowledgeBase kb = new KnowledgeBaseImpl();

		try (final Reasoner reasoner = Reasoner.getInstance(kb)) {
			reasoner.addFactsFromDataSource(ternaryPredicate, fileDataSource);
			reasoner.load();
		}
	}

	@Test
	public void testLoadRdfInvalidFormat()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final FileDataSource fileDataSource = new RdfFileDataSource(
				new File(FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.invalidFormatNtFileNameRoot + ".nt"));
		final KnowledgeBase kb = new KnowledgeBaseImpl();

		try (final Reasoner reasoner = Reasoner.getInstance(kb)) {
			reasoner.addFactsFromDataSource(ternaryPredicate, fileDataSource);
			reasoner.load();
			FileDataSourceTestUtils.testNoFactsOverPredicate(reasoner, queryAtom);
		}
	}

}
