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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.rulewerk.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;

public class VLogReasonerSparqlInput {

	/**
	 * Tests the query "SELECT ?b ?a WHERE {?a p:P22 ?b}"
	 *
	 * @throws ReasonerStateException
	 * @throws EdbIdbSeparationException
	 * @throws IOException
	 * @throws IncompatiblePredicateArityException
	 * @throws QueryPredicateNonExistentException
	 */
	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test
	public void testSimpleSparqlQuery() throws IOException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeUniversalVariable("b"), Expressions.makeUniversalVariable("a")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// a has father b
				"?a wdt:P22 ?b");
		final Predicate fatherOfPredicate = Expressions.makePredicate("FatherOf", 2);
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(fatherOfPredicate, dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			try (final QueryResultIterator answerQuery = reasoner
					.answerQuery(Expressions.makePositiveLiteral(fatherOfPredicate,
							Expressions.makeUniversalVariable("x"), Expressions.makeUniversalVariable("y")), false)) {

				assertTrue(answerQuery.hasNext());
				final QueryResult firstAnswer = answerQuery.next();
				assertNotNull(firstAnswer);
			}
		}
	}

	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test
	public void testSimpleSparqlQueryHttps() throws IOException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeUniversalVariable("b"), Expressions.makeUniversalVariable("a")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// a has father b
				"?a wdt:P22 ?b");
		final Predicate fatherOfPredicate = Expressions.makePredicate("FatherOf", 2);
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(fatherOfPredicate, dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			try (final QueryResultIterator answerQuery = reasoner
					.answerQuery(Expressions.makePositiveLiteral(fatherOfPredicate,
							Expressions.makeUniversalVariable("x"), Expressions.makeUniversalVariable("y")), false)) {

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
	 * @throws QueryPredicateNonExistentException
	 */
	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test
	public void testSimpleSparqlQuery2() throws IOException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeUniversalVariable("b"), Expressions.makeUniversalVariable("a")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// a has father b
				"?a wdt:P22 ?b .");
		final Predicate fatherOfPredicate = Expressions.makePredicate("FatherOf", 2);
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(fatherOfPredicate, dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			try (final QueryResultIterator answerQuery = reasoner
					.answerQuery(Expressions.makePositiveLiteral(fatherOfPredicate,
							Expressions.makeUniversalVariable("x"), Expressions.makeUniversalVariable("y")), false)) {

				assertTrue(answerQuery.hasNext());
			}
		}
	}

	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test(expected = RulewerkRuntimeException.class)
	public void testConjunctiveQueryNewLineCharacterInQueryBody() throws IOException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeUniversalVariable("a"), Expressions.makeUniversalVariable("c")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// b has father a and b has mother c
				"?b wdt:P22 ?a .\n" + "?b wdt:P25 ?c");
		final Predicate haveChildrenTogether = Expressions.makePredicate("haveChildrenTogether", 2);
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(haveChildrenTogether, dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.answerQuery(Expressions.makePositiveLiteral(haveChildrenTogether,
					Expressions.makeUniversalVariable("x"), Expressions.makeUniversalVariable("y")), false);
		}
	}

	@Ignore // Ignored during CI because it makes lengthy calls to remote servers
	@Test
	public void testConjunctiveQuery() throws IOException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeUniversalVariable("a"), Expressions.makeUniversalVariable("c")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// b has father a and b has mother c
				"?b wdt:P22 ?a ." + "?b wdt:P25 ?c");
		final Predicate haveChildrenTogether = Expressions.makePredicate("haveChildrenTogether", 2);
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(haveChildrenTogether, dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			try (final QueryResultIterator answerQuery = reasoner
					.answerQuery(
							Expressions.makePositiveLiteral(haveChildrenTogether,
									Expressions.makeUniversalVariable("x"), Expressions.makeUniversalVariable("y")),
							false)) {

				assertTrue(answerQuery.hasNext());
			}
		}
	}

	@Test(expected = IncompatiblePredicateArityException.class)
	public void testDataSourcePredicateDoesNotMatchSparqlQueryTerms() throws IOException {
		final URL endpoint = new URL("https://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeUniversalVariable("b"), Expressions.makeUniversalVariable("a")));

		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				// b has father a and b has mother c
				"?b wdt:P22 ?a ." + "?b wdt:P25 ?c");
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(new DataSourceDeclarationImpl(Expressions.makePredicate("ternary", 3), dataSource));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
		}
	}

}
