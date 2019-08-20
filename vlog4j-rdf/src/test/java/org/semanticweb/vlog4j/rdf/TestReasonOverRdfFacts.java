package org.semanticweb.vlog4j.rdf;

import static org.junit.Assert.assertEquals;

/*-
 * #%L
 * VLog4j RDF Support
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

import static org.junit.Assert.assertTrue;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;
import static org.semanticweb.vlog4j.rdf.RdfModelConverter.RDF_TRIPLE_PREDICATE_NAME;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;

public class TestReasonOverRdfFacts {

	private static final Set<List<Term>> expectedQueryResultsInvention = new HashSet<>(Arrays.asList(
			Arrays.asList(makeConstant("https://example.org/Carl-Benz"), makeConstant("https://example.org/invention"),
					makeConstant("\"car\"@en")),
			Arrays.asList(makeConstant("https://example.org/Carl-Benz"), makeConstant("https://example.org/invention"),
					makeConstant("\"\\u81EA\\u52A8\\u8F66\"@zh-hans"))));

	private static final Variable subject = makeVariable("s");
	private static final Variable predicate = makeVariable("p");
	private static final Variable object = makeVariable("o");

	@Test
	public void testCanLoadRdfFactsIntoReasoner() throws RDFParseException, RDFHandlerException, IOException,
			ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "exampleFacts.ttl"),
				RDFFormat.TURTLE);
		final Set<Fact> facts = RdfModelConverter.rdfModelToFacts(model);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(facts);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			final PositiveLiteral universalQuery = makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(subject, predicate, object));
			final Set<List<Term>> queryResults = this.getQueryResults(reasoner, universalQuery);
			assertTrue(!queryResults.isEmpty());
		}
	}

	@Test
	public void testQueryAnsweringOverRdfFacts() throws RDFParseException, RDFHandlerException, IOException,
			ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "exampleFacts.ttl"),
				RDFFormat.TURTLE);
		final Set<Fact> facts = RdfModelConverter.rdfModelToFacts(model);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(facts);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			final Constant inventionPredicate = makeConstant("https://example.org/invention");
			final Constant carlBenzSubject = makeConstant("https://example.org/Carl-Benz");

			final PositiveLiteral inventionQuery = makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, carlBenzSubject,
					inventionPredicate, object);
			assertEquals(expectedQueryResultsInvention, this.getQueryResults(reasoner, inventionQuery));
		}
	}

	private Set<List<Term>> getQueryResults(final Reasoner reasoner, final PositiveLiteral query)
			throws ReasonerStateException {
		final QueryResultIterator queryResultIterator = reasoner.answerQuery(query, true);

		final Set<List<Term>> queryResults = new HashSet<>();
		queryResultIterator.forEachRemaining(queryResult -> queryResults.add(queryResult.getTerms()));
		queryResultIterator.close();
		return queryResults;
	}

}
