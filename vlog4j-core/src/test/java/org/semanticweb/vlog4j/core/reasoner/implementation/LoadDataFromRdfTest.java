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

public class LoadDataFromRdfTest {

	private static final String ternaryPredicateName = "triple";

	@SuppressWarnings("unchecked")
	private static final Set<List<Term>> expectedTernaryQueryResult = Sets.newSet(
			Arrays.asList(Expressions.makeConstant("<c1>"), Expressions.makeConstant("<q>"),
					Expressions.makeConstant("<c2>")),
			Arrays.asList(Expressions.makeConstant("<c1>"), Expressions.makeConstant("<p>"),
					Expressions.makeConstant("<c2>")));

	@Test
	public void testLoadEmptyRdfFile()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final Predicate predicate = Expressions.makePredicate(ternaryPredicateName, 3);
		final Atom queryAtom = Expressions.makeAtom(predicate, Expressions.makeVariable("s"),
				Expressions.makeVariable("p"), Expressions.makeVariable("o"));

		for (final String rdfFileName : Arrays.asList("empty.nt", "empty.nt.gz")) {
			final File emptyRdfFile = new File(FileDataSourceUtils.INPUT_FOLDER + rdfFileName);
			final FileDataSource emptyDataSource = new RdfFileDataSource(emptyRdfFile);

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
	public void testLoadTernaryFactsFromRdfFile() throws ReasonerStateException, EdbIdbSeparationException,
	EDBConfigurationException, IOException, IncompatiblePredicateArityException {
		final Predicate predicate = Expressions.makePredicate(ternaryPredicateName, 3);

		for (final String rdfFileName : Arrays.asList(FileDataSourceUtils.unzippedNtFileRoot + ".nt",
				FileDataSourceUtils.zippedNtFileRoot + ".nt.gz")) {
			final File rdfFile = new File(FileDataSourceUtils.INPUT_FOLDER + rdfFileName);
			final FileDataSource fileDataSource = new RdfFileDataSource(rdfFile);

			try (final Reasoner reasoner = Reasoner.getInstance()) {
				reasoner.addFactsFromDataSource(predicate, fileDataSource);
				reasoner.load();

				final QueryResultIterator queryResultIterator = reasoner.answerQuery(Expressions.makeAtom(predicate,
						Expressions.makeVariable("s"), Expressions.makeVariable("p"), Expressions.makeVariable("o")),
						true);
				final Set<List<Term>> queryResult = QueryResultsUtils.collectQueryResults(queryResultIterator);

				assertEquals(expectedTernaryQueryResult, queryResult);
			}
		}
	}

	@Test(expected = IOException.class)
	public void testRdfFileNotOnDisk()
			throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final File unexistingFile = new File("unexistingFile.nt");
		assertFalse(unexistingFile.exists());
		final FileDataSource fileDataSource = new RdfFileDataSource(unexistingFile);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(Expressions.makePredicate(ternaryPredicateName, 3), fileDataSource);
			reasoner.load();
		}
	}

}
