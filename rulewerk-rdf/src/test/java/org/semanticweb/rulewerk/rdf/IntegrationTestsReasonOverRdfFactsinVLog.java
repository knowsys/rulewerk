package org.semanticweb.rulewerk.rdf;

/*-
 * #%L
 * Rulewerk RDF Support
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class IntegrationTestsReasonOverRdfFactsinVLog {
	
	final RdfModelConverter rdfModelConverter = new RdfModelConverter();

	private final Constant carlBenz = Expressions.makeAbstractConstant("https://example.org/Carl-Benz");
	private final Constant invention = Expressions.makeAbstractConstant("https://example.org/invention");
	private final Constant labelEn = Expressions.makeLanguageStringConstant("car", "en");
	private final Constant labelZh = Expressions.makeLanguageStringConstant("\u81EA\u52A8\u8F66", "zh-hans");

	private final Set<List<Term>> expectedQueryResultsInvention = new HashSet<>(
			Arrays.asList(Arrays.asList(carlBenz, invention, labelEn), Arrays.asList(carlBenz, invention, labelZh)));

	private static final Variable subject = Expressions.makeUniversalVariable("s");
	private static final Variable predicate = Expressions.makeUniversalVariable("p");
	private static final Variable object = Expressions.makeUniversalVariable("o");

	@Test
	public void testCanLoadRdfFactsIntoReasoner() throws RDFParseException, RDFHandlerException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "exampleFacts.ttl"),
				RDFFormat.TURTLE);
		final Set<Fact> facts = rdfModelConverter.rdfModelToFacts(model);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(facts);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();

			final PositiveLiteral universalQuery = Expressions.makePositiveLiteral(
					RdfModelConverter.RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(subject, predicate, object));
			final Set<List<Term>> queryResults = this.getQueryResults(reasoner, universalQuery);
			assertTrue(!queryResults.isEmpty());
		}
	}

	@Test
	public void testQueryAnsweringOverRdfFacts() throws RDFParseException, RDFHandlerException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "exampleFacts.ttl"),
				RDFFormat.TURTLE);
		final Set<Fact> facts = rdfModelConverter.rdfModelToFacts(model);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(facts);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();

			final PositiveLiteral inventionQuery = Expressions
					.makePositiveLiteral(RdfModelConverter.RDF_TRIPLE_PREDICATE_NAME, carlBenz, invention, object);
			assertEquals(expectedQueryResultsInvention, this.getQueryResults(reasoner, inventionQuery));
		}
	}

	private Set<List<Term>> getQueryResults(final Reasoner reasoner, final PositiveLiteral query) {
		final QueryResultIterator queryResultIterator = reasoner.answerQuery(query, true);

		final Set<List<Term>> queryResults = new HashSet<>();
		queryResultIterator.forEachRemaining(queryResult -> queryResults.add(queryResult.getTerms()));
		queryResultIterator.close();
		return queryResults;
	}

}
