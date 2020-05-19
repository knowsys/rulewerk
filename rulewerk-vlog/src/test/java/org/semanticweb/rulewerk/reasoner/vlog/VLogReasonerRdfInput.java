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
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.implementation.FileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.RdfFileDataSource;

public class VLogReasonerRdfInput {

	private static final Predicate ternaryPredicate = Expressions.makePredicate("triple", 3);
	private static final PositiveLiteral queryAtom = Expressions.makePositiveLiteral(ternaryPredicate,
			Expressions.makeUniversalVariable("s"), Expressions.makeUniversalVariable("p"),
			Expressions.makeUniversalVariable("o"));

	@SuppressWarnings("unchecked")
	private static final Set<List<Term>> expectedTernaryQueryResult = Sets.newSet(
			Arrays.asList(Expressions.makeAbstractConstant("http://example.org/c1"),
					Expressions.makeAbstractConstant("http://example.org/p"),
					Expressions.makeAbstractConstant("http://example.org/c2")),
			Arrays.asList(Expressions.makeAbstractConstant("http://example.org/c1"),
					Expressions.makeAbstractConstant("http://example.org/q"),
					Expressions.makeDatatypeConstant("test string", "http://www.w3.org/2001/XMLSchema#string")));

	@Ignore
	// TODO test fails for now, because of a VLog bug. Remove the @Ignore annotation
	// after VLog bug is fixed.
	@Test
	public void testLoadEmptyRdfFile() throws IOException {
		FileDataSourceTestUtils.testLoadEmptyFile(ternaryPredicate, queryAtom,
				new RdfFileDataSource(FileDataSourceTestUtils.INPUT_FOLDER + "empty.nt"));
	}

	@Ignore
	// TODO test fails for now, because of a VLog bug. Remove the @Ignore annotation
	// after VLog bug is fixed.
	@Test
	public void testLoadEmptyRdfFileGz() throws IOException {
		FileDataSourceTestUtils.testLoadEmptyFile(ternaryPredicate, queryAtom,
				new RdfFileDataSource(FileDataSourceTestUtils.INPUT_FOLDER + "empty.nt.gz"));
	}

	@Test
	public void testLoadTernaryFactsFromRdfFile() throws IOException {
		testLoadTernaryFactsFromSingleRdfDataSource(new RdfFileDataSource(
				FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.unzippedNtFileRoot + ".nt"));
	}

	@Test
	public void testLoadTernaryFactsFromRdfFileGz() throws IOException {
		testLoadTernaryFactsFromSingleRdfDataSource(new RdfFileDataSource(
				FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.zippedNtFileRoot + ".nt.gz"));
	}

	public void testLoadTernaryFactsFromSingleRdfDataSource(final FileDataSource fileDataSource) throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(ternaryPredicate, fileDataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true);
			final Set<List<Term>> queryResult = QueryResultsUtils.collectQueryResults(queryResultIterator);

			assertEquals(expectedTernaryQueryResult, queryResult);
		}
	}

	@Test(expected = IOException.class)
	public void testLoadNonexistingRdfFile() throws IOException {
		final File nonexistingFile = new File("nonexistingFile.nt");
		assertFalse(nonexistingFile.exists());
		final FileDataSource fileDataSource = new RdfFileDataSource(nonexistingFile.getName());
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(ternaryPredicate, fileDataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
		}
	}

	@Test
	public void testLoadRdfInvalidFormat() throws IOException {
		final FileDataSource fileDataSource = new RdfFileDataSource(
				FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.invalidFormatNtFileNameRoot + ".nt");
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(ternaryPredicate, fileDataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			FileDataSourceTestUtils.testNoFactsOverPredicate(reasoner, queryAtom);
		}
	}

}
