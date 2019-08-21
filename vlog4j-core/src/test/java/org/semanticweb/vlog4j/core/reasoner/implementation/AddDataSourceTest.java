package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.assertEquals;

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
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;

import karmaresearch.vlog.EDBConfigurationException;

public class AddDataSourceTest {

	private static final String CSV_FILE_PATH = FileDataSourceTestUtils.INPUT_FOLDER + "unaryFacts.csv";

	@Test
	public void testAddDataSourceExistentDataForDifferentPredicates() throws ReasonerStateException,
			EdbIdbSeparationException, EDBConfigurationException, IOException, IncompatiblePredicateArityException {
		final Predicate predicateParity1 = Expressions.makePredicate("p", 1);
		final Constant constantA = Expressions.makeConstant("a");
		final Fact factPredicatePArity2 = Expressions.makeFact("p", Arrays.asList(constantA, constantA));
		final Fact factPredicateQArity1 = Expressions.makeFact("q", Arrays.asList(constantA));
		final Predicate predicateLArity1 = Expressions.makePredicate("l", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_PATH));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(factPredicatePArity2);
		kb.addStatement(factPredicateQArity1);
		kb.addStatement(new DataSourceDeclarationImpl(predicateLArity1, dataSource));
		kb.addStatement(new DataSourceDeclarationImpl(predicateParity1, dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
			final QueryResultIterator queryResultIteratorL1 = reasoner.answerQuery(
					Expressions.makePositiveLiteral(predicateLArity1, Expressions.makeVariable("x")), false);
			final Set<List<Term>> queryResultsL1 = QueryResultsUtils.collectQueryResults(queryResultIteratorL1);

			final QueryResultIterator queryResultIteratorP1 = reasoner.answerQuery(
					Expressions.makePositiveLiteral(predicateParity1, Expressions.makeVariable("x")), false);
			final Set<List<Term>> queryResultsP1 = QueryResultsUtils.collectQueryResults(queryResultIteratorP1);
			assertEquals(queryResultsL1, queryResultsP1);

		}
	}

	@Test
	public void testAddDataSourceBeforeLoading() throws ReasonerStateException, EdbIdbSeparationException,
			EDBConfigurationException, IOException, IncompatiblePredicateArityException {
		final Predicate predicateP = Expressions.makePredicate("p", 1);
		final Predicate predicateQ = Expressions.makePredicate("q", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_PATH));

		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.addStatement(new DataSourceDeclarationImpl(predicateP, dataSource));
			kb.addStatement(new DataSourceDeclarationImpl(predicateQ, dataSource));
			reasoner.load();
		}
	}

	// TODO rewrite test
	@Ignore
	@Test(expected = ReasonerStateException.class)
	public void testAddDataSourceAfterLoading() throws ReasonerStateException, EdbIdbSeparationException,
			EDBConfigurationException, IOException, IncompatiblePredicateArityException {
		final Predicate predicateP = Expressions.makePredicate("p", 1);
		final Predicate predicateQ = Expressions.makePredicate("q", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_PATH));

		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.addStatement(new DataSourceDeclarationImpl(predicateP, dataSource));
			reasoner.load();
			kb.addStatement(new DataSourceDeclarationImpl(predicateQ, dataSource));
		}
	}

	// TODO rewrite test
	@Ignore
	@Test(expected = ReasonerStateException.class)
	public void testAddDataSourceAfterReasoning() throws ReasonerStateException, EdbIdbSeparationException,
			EDBConfigurationException, IOException, IncompatiblePredicateArityException {
		final Predicate predicateP = Expressions.makePredicate("p", 1);
		final Predicate predicateQ = Expressions.makePredicate("q", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_PATH));

		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.addStatement(new DataSourceDeclarationImpl(predicateP, dataSource));
			reasoner.load();
			reasoner.reason();
			kb.addStatement(new DataSourceDeclarationImpl(predicateQ, dataSource));
		}
	}

	//FIXME decide how to handle datasources with multiple predicates
	@Ignore
	// TODO move to a test class for KnowledgeBase
	@Test(expected = IllegalArgumentException.class)
	public void testAddDataSourceNoMultipleDataSourcesForPredicate() throws ReasonerStateException, IOException {
		final Predicate predicate = Expressions.makePredicate("p", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_PATH));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(predicate, dataSource));
		kb.addStatement(new DataSourceDeclarationImpl(predicate, dataSource));
	}
	
	//FIXME decide how to handle datasources with multiple predicates
	@Ignore
	// TODO move to a test class for KnowledgeBase
	@Test(expected = IllegalArgumentException.class)
	public void testAddDataSourceNoFactsForPredicate() throws ReasonerStateException, IOException {
		final Predicate predicate = Expressions.makePredicate("p", 1);
		final DataSource dataSource = new CsvFileDataSource(new File(CSV_FILE_PATH));
		final Fact fact = Expressions.makeFact(Expressions.makePredicate("p", 1),
				Arrays.asList(Expressions.makeConstant("a")));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(fact);
		kb.addStatement(new DataSourceDeclarationImpl(predicate, dataSource));
	}

}
