package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

public class LoadDataFromSparqlQueryTest {

	/**
	 * Tests the query "SELECT ?b ?a WHERE {?a p:P22 ?b}"
	 * 
	 * @throws ReasonerStateException
	 * @throws EdbIdbSeparationException
	 * @throws IOException
	 * @throws IncompatiblePredicateArityException
	 */
	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test
	public void testSimpleSparqlQuery()
			throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeVariable("b"), Expressions.makeVariable("a")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// a has father b
				"?a p:P22 ?b");
		final Predicate fatherOfPredicate = Expressions.makePredicate("FatherOf", 2);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(fatherOfPredicate, dataSource);
			reasoner.load();
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(Expressions.makeAtom(fatherOfPredicate,
					Expressions.makeVariable("x"), Expressions.makeVariable("y")), false)) {

				assertTrue(answerQuery.hasNext());
				final QueryResult firstAnswer = answerQuery.next();
				assertNotNull(firstAnswer);
			}
		}
	}

	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test
	public void testSimpleSparqlQueryHttps()
			throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeVariable("b"), Expressions.makeVariable("a")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// a has father b
				"?a p:P22 ?b");
		final Predicate fatherOfPredicate = Expressions.makePredicate("FatherOf", 2);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(fatherOfPredicate, dataSource);
			reasoner.load();
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(Expressions.makeAtom(fatherOfPredicate,
					Expressions.makeVariable("x"), Expressions.makeVariable("y")), false)) {

				assertTrue(answerQuery.hasNext());
				final QueryResult firstAnswer = answerQuery.next();
				assertNotNull(firstAnswer);
			}
		}
	}

	/**
	 * Tests the query "SELECT ?b ?a WHERE {?a p:P22 ?b .}"
	 * 
	 * @throws ReasonerStateException
	 * @throws EdbIdbSeparationException
	 * @throws IOException
	 * @throws IncompatiblePredicateArityException
	 */
	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test
	public void testSimpleSparqlQuery2()
			throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeVariable("b"), Expressions.makeVariable("a")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// a has father b
				"?a p:P22 ?b .");
		final Predicate fatherOfPredicate = Expressions.makePredicate("FatherOf", 2);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(fatherOfPredicate, dataSource);
			reasoner.load();
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(Expressions.makeAtom(fatherOfPredicate,
					Expressions.makeVariable("x"), Expressions.makeVariable("y")), false)) {

				assertTrue(answerQuery.hasNext());
			}
		}
	}

	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test(expected = RuntimeException.class)
	public void testConjunctiveQueryNewLineCharacterInQueryBody()
			throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeVariable("a"), Expressions.makeVariable("c")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// b has father a and b has mother c
				"?b p:P22 ?a .\n" + "?b p:P25 ?c");
		final Predicate haveChildrenTogether = Expressions.makePredicate("haveChildrenTogether", 2);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(haveChildrenTogether, dataSource);
			reasoner.load();
			reasoner.answerQuery(Expressions.makeAtom(haveChildrenTogether, Expressions.makeVariable("x"),
					Expressions.makeVariable("y")), false);
		}
	}

	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test
	public void testConjunctiveQuery()
			throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeVariable("a"), Expressions.makeVariable("c")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// b has father a and b has mother c
				"?b p:P22 ?a ." + "?b p:P25 ?c");
		final Predicate haveChildrenTogether = Expressions.makePredicate("haveChildrenTogether", 2);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(haveChildrenTogether, dataSource);
			reasoner.load();
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(Expressions.makeAtom(haveChildrenTogether,
					Expressions.makeVariable("x"), Expressions.makeVariable("y")), false)) {

				assertTrue(answerQuery.hasNext());
			}
		}
	}

	@Test(expected = IncompatiblePredicateArityException.class)
	public void testDataSourcePredicateDoesNotMatchSparqlQueryTerms()
			throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeVariable("b"), Expressions.makeVariable("a")));

		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// b has father a and b has mother c
				"?b p:P22 ?a ." + "?b p:P25 ?c");

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			// TODO must validate predicate arity sonner
			reasoner.addFactsFromDataSource(Expressions.makePredicate("ternary", 3), dataSource);
			reasoner.load();
		}
	}

}
