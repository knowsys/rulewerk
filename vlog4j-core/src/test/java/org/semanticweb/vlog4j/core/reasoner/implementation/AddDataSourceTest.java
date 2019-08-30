package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Correctness;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.QueryResultIterator;

public class AddDataSourceTest {

	private static final String CSV_FILE_c1_c2_PATH = FileDataSourceTestUtils.INPUT_FOLDER + "unaryFacts.csv";

	private static final String CSV_FILE_c_d_PATH = FileDataSourceTestUtils.INPUT_FOLDER + "unaryFactsCD.csv";

	private final Set<List<Term>> csvFile_c1_c2_Content = new HashSet<>(Arrays
			.asList(Arrays.asList(Expressions.makeConstant("c1")), Arrays.asList(Expressions.makeConstant("c2"))));

	private final Set<List<Term>> csvFile_c_d_Content = new HashSet<>(
			Arrays.asList(Arrays.asList(Expressions.makeConstant("c")), Arrays.asList(Expressions.makeConstant("d"))));;

	@Test
	public void testAddDataSourceExistentDataForDifferentPredicates() throws IOException {
		final Predicate predicateParity1 = Expressions.makePredicate("p", 1);
		final Constant constantA = Expressions.makeConstant("a");
		final Fact factPredicatePArity2 = Expressions.makeFact("p", Arrays.asList(constantA, constantA));
		final Fact factPredicateQArity1 = Expressions.makeFact("q", Arrays.asList(constantA));
		final Predicate predicateLArity1 = Expressions.makePredicate("l", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_c1_c2_PATH));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(factPredicatePArity2);
		kb.addStatement(factPredicateQArity1);
		kb.addStatement(new DataSourceDeclarationImpl(predicateLArity1, dataSource));
		kb.addStatement(new DataSourceDeclarationImpl(predicateParity1, dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
			try (final QueryResultIterator queryResult = reasoner.answerQuery(
					Expressions.makePositiveLiteral(predicateLArity1, Expressions.makeVariable("x")), false)) {
				assertEquals(csvFile_c1_c2_Content, QueryResultsUtils.collectQueryResults(queryResult));
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());
			}
			try (final QueryResultIterator queryResult = reasoner.answerQuery(
					Expressions.makePositiveLiteral(predicateParity1, Expressions.makeVariable("x")), false)) {
				assertEquals(csvFile_c1_c2_Content, QueryResultsUtils.collectQueryResults(queryResult));
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());
			}

		}
	}

	@Test
	public void testAddDataSourceBeforeLoading() throws IOException {
		final Predicate predicateP = Expressions.makePredicate("p", 1);
		final Predicate predicateQ = Expressions.makePredicate("q", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_c1_c2_PATH));

		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.addStatement(new DataSourceDeclarationImpl(predicateP, dataSource));
			kb.addStatement(new DataSourceDeclarationImpl(predicateQ, dataSource));
			reasoner.load();
			try (final QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicateP, Expressions.makeVariable("x")), true)) {
				assertEquals(csvFile_c1_c2_Content, QueryResultsUtils.collectQueryResults(queryResult));
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());
			}
			try (final QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicateQ, Expressions.makeVariable("x")), true)) {
				assertEquals(csvFile_c1_c2_Content, QueryResultsUtils.collectQueryResults(queryResult));
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());
			}

		}
	}

	@Test
	public void testAddDataSourceAfterLoading() throws IOException {
		final Predicate predicateP = Expressions.makePredicate("p", 1);
		final Predicate predicateQ = Expressions.makePredicate("q", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_c1_c2_PATH));

		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			
			kb.addStatement(new DataSourceDeclarationImpl(predicateP, dataSource));
			
			reasoner.load();

			kb.addStatement(new DataSourceDeclarationImpl(predicateQ, dataSource));

			try (final QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicateP, Expressions.makeVariable("x")), true)) {
				assertEquals(csvFile_c1_c2_Content, QueryResultsUtils.collectQueryResults(queryResult));
				assertEquals(Correctness.SOUND_BUT_INCOMPLETE, queryResult.getCorrectness());
			}
			
			// there is no fact for predicate Q loaded in the reasoner
			try (final QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicateQ, Expressions.makeVariable("x")), true)) {
				assertFalse(queryResult.hasNext());
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());
			}
		}
	}

	@Test
	public void testAddDataSourceAfterReasoning() throws IOException {
		final Predicate predicateP = Expressions.makePredicate("p", 1);
		final Predicate predicateQ = Expressions.makePredicate("q", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_c1_c2_PATH));

		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			
			kb.addStatement(new DataSourceDeclarationImpl(predicateP, dataSource));
			
			reasoner.reason();

			kb.addStatement(new DataSourceDeclarationImpl(predicateQ, dataSource));

			try (final QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicateP, Expressions.makeVariable("x")), true)) {
				assertEquals(csvFile_c1_c2_Content, QueryResultsUtils.collectQueryResults(queryResult));
				assertEquals(Correctness.SOUND_BUT_INCOMPLETE, queryResult.getCorrectness());
			}
// there is no fact for predicate Q loaded in the reasoner
			try (final QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicateQ, Expressions.makeVariable("x")), true)) {
				assertFalse(queryResult.hasNext());
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());
			}
		}
	}

	// FIXME decide how to handle datasources with multiple predicates
	@Ignore
	@Test
	public void testAddDataSourceNoMultipleDataSourcesForPredicate() throws IOException {
		final Predicate predicate = Expressions.makePredicate("p", 1);
		final DataSource dataSource1 = new CsvFileDataSource(new File(CSV_FILE_c1_c2_PATH));
		final DataSource dataSource2 = new CsvFileDataSource(new File(CSV_FILE_c_d_PATH));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(predicate, dataSource1));
		kb.addStatement(new DataSourceDeclarationImpl(predicate, dataSource2));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			try (QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicate, Expressions.makeVariable("x")), true)) {
				System.out.println(QueryResultsUtils.collectQueryResults(queryResult));
			}
		}
	}

	// FIXME decide how to handle datasources with multiple predicates
	@Ignore
	@Test
	public void testAddDataSourceNoFactsForPredicate() throws IOException {
		final Predicate predicate = Expressions.makePredicate("p", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_c1_c2_PATH));
		final Fact fact = Expressions.makeFact(Expressions.makePredicate("p", 1),
				Arrays.asList(Expressions.makeConstant("a")));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(fact);
		kb.addStatement(new DataSourceDeclarationImpl(predicate, dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			try (QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicate, Expressions.makeVariable("x")), true)) {
				QueryResultsUtils.collectQueryResults(queryResult);
			}
		}
	}

	@Test
	public void testAddMultipleDataSourcesForPredicateAfterReasoning() throws IOException {
		final Predicate predicate = Expressions.makePredicate("p", 1);
		final DataSource dataSource1 = new CsvFileDataSource(new File(CSV_FILE_c1_c2_PATH));
		final DataSource dataSource2 = new CsvFileDataSource(
				new File(FileDataSourceTestUtils.INPUT_FOLDER + "unaryFactsCD.csv"));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(predicate, dataSource1));
		kb.addStatement(new DataSourceDeclarationImpl(predicate, dataSource2));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			try (QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicate, Expressions.makeVariable("x")), true)) {
				final Set<List<Term>> expectedAnswers = new HashSet<>(csvFile_c1_c2_Content);
				expectedAnswers.addAll(csvFile_c_d_Content);

				assertEquals(expectedAnswers, QueryResultsUtils.collectQueryResults(queryResult));
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());

			}
		}
	}

	@Test
	public void testAddDataSourceAndFactsForPredicateAfterReasoning() throws IOException {
		final Predicate predicate = Expressions.makePredicate("p", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_c1_c2_PATH));
		final Fact fact = Expressions.makeFact(Expressions.makePredicate("p", 1),
				Arrays.asList(Expressions.makeConstant("a")));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(fact);
		kb.addStatement(new DataSourceDeclarationImpl(predicate, dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			try (QueryResultIterator queryResult = reasoner
					.answerQuery(Expressions.makePositiveLiteral(predicate, Expressions.makeVariable("x")), true)) {
				final Set<List<Term>> expectedAnswers = new HashSet<>(csvFile_c1_c2_Content);
				expectedAnswers.add(Arrays.asList(Expressions.makeConstant("a")));

				assertEquals(expectedAnswers, QueryResultsUtils.collectQueryResults(queryResult));
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());
			}
		}
	}

}
