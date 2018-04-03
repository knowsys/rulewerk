package org.semanticweb.vlog4j.core.reasoner.implementation;

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

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

public class LoadDataFromSparqlQueryTest {

	@Test
	public void testSimpleSparqlQuery() throws ReasonerStateException, EdbIdbSeparationException, IOException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, "b,a", "?a p:P22 ?b");
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

	@Test(expected = RuntimeException.class)
	public void testConjunctiveQueryNewLineCharacterInQueryBody()
			throws ReasonerStateException, EdbIdbSeparationException, IOException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, "a,c",
				"?a p:P22 ?b .\n" + "?c p:P25 ?b");
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

	@Test
	public void testConjunctiveQuery() throws ReasonerStateException, EdbIdbSeparationException, IOException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, "a,c",
				"?a p:P22 ?b ." + "?c p:P25 ?b");
		final Predicate haveChildrenTogether = Expressions.makePredicate("haveChildrenTogether", 2);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(haveChildrenTogether, dataSource);
			reasoner.load();
			reasoner.answerQuery(Expressions.makeAtom(haveChildrenTogether, Expressions.makeVariable("x"),
					Expressions.makeVariable("y")), true);
			// TODO find another example of a conjunctive query that has answers
		}
	}

	@Ignore
	@Test
	public void testRepeatVariableNameQuery() throws ReasonerStateException, EdbIdbSeparationException, IOException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");

		// ibc++abi.dylib: terminating with uncaught exception of type
		// nlohmann::detail::parse_error: [json.exception.parse_error.101] parse error
		// at 1: syntax error - invalid literal; last read: 'S'
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, "b,b", "?a p:P22 ?b");
		final Predicate hasParents = Expressions.makePredicate("hasParents", 2);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(hasParents, dataSource);
			reasoner.load();
			System.out.println("shit");
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(
					Expressions.makeAtom(hasParents, Expressions.makeVariable("x"), Expressions.makeVariable("y")),
					false)) {

				assertTrue(answerQuery.hasNext());
			}
		}
	}

	@Ignore
	@Test
	public void testSparqlQuerySpaceBetweenVariableNames()
			throws ReasonerStateException, EdbIdbSeparationException, IOException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		// libc++abi.dylib: terminating with uncaught exception of type
		// nlohmann::detail::parse_error: [json.exception.parse_error.101] parse error
		// at 1: syntax error - invalid literal; last read: 'S'
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, "b, a", "?a p:P22 ?b");

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			final Predicate fatherOfPredicate = Expressions.makePredicate("FatherOf", 2);
			reasoner.addFactsFromDataSource(fatherOfPredicate, dataSource);
			reasoner.load();
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(Expressions.makeAtom(fatherOfPredicate,
					Expressions.makeVariable("x"), Expressions.makeVariable("y")), false)) {

				assertTrue(answerQuery.hasNext());
			}
		}
	}

	@Ignore
	@Test
	public void testDataSourcePredicateDoesNotMatchSparqlQueryTerms()
			throws ReasonerStateException, EdbIdbSeparationException, IOException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, "b,a",
				"?a p:P22 ?b . ?c p:P25 ?b .");

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			// TODO must validate predicate arity sonner
			reasoner.addFactsFromDataSource(Expressions.makePredicate("FatherOf", 3), dataSource);
			reasoner.load();
		}

	}

	@Ignore
	@Test
	public void queryPredicateDoesNotDataSourcePredicate()
			throws ReasonerStateException, EdbIdbSeparationException, IOException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, "b,a",
				"?a p:P22 ?b . ?c p:P25 ?b .");

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			// TODO must validate
			final Predicate ternaryPredicate = Expressions.makePredicate("ternary", 3);
			reasoner.addFactsFromDataSource(ternaryPredicate, dataSource);
			reasoner.load();
			// [0xe849e93b4b3692e0 2018-04-04 00:08:57] ERROR Wrong tuple size in query
			// libc++abi.dylib: terminating with uncaught exception of type int
			// TODO validate sooner
			reasoner.answerQuery(Expressions.makeAtom(ternaryPredicate, Expressions.makeVariable("x"),
					Expressions.makeVariable("y"), Expressions.makeVariable("z")), false);
		}

	}

}
