package org.semanticweb.rulewerk.examples.core;

/*-
 * #%L
 * Rulewerk Examples
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

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.examples.ExamplesUtils;

/**
 * This is a simple example of adding data from the result of a SPARQL query on
 * a remote database endpoint, using {@link SparqlQueryResultDataSource}. In
 * this example, we will query Wikidata for titles of publications that have
 * authors who have children together.
 *
 * @author Irina Dragoste
 *
 */
public class AddDataFromSparqlQueryResults {

	/**
	 * <a href="https://www.wikidata.org/wiki/Property:P50"> WikiData author
	 * property id.</a>
	 */
	private static final String WIKIDATA_AUTHOR_PROPERTY = "wdt:P50";
	/**
	 * <a href="https://www.wikidata.org/wiki/Property:P1476"> WikiData title
	 * property id.</a> Published title of a work, such as a newspaper article, a
	 * literary work, a website, or a performance work
	 */
	private static final String WIKIDATA_TITLE_PROPERTY = "wdt:P1476";
	/**
	 * <a href="https://www.wikidata.org/wiki/Property:P25"> WikiData mother
	 * property id.</a>
	 */
	private static final String WIKIDATA_MOTHER_PROPERTY = "wdt:P25";
	/**
	 * <a href="https://www.wikidata.org/wiki/Property:P22"> WikiData father
	 * property id.</a>
	 */
	private static final String WIKIDATA_FATHER_PROPERTY = "wdt:P22";

	public static void main(final String[] args) throws IOException {

		ExamplesUtils.configureLogging();

		/*
		 * The WikiData SPARQL query endpoint.
		 */
		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		/*
		 * SPARQL query body that looks for publications where two authors of the
		 * publication are the mother, respectively father of the same child.
		 */
		final String queryBody = " ?publication " + WIKIDATA_TITLE_PROPERTY + " ?title ." + "?publication "
				+ WIKIDATA_AUTHOR_PROPERTY + " ?mother ." + " ?publication " + WIKIDATA_AUTHOR_PROPERTY + " ?father ."
				+ " ?child " + WIKIDATA_MOTHER_PROPERTY + " ?mother ." + " ?child " + WIKIDATA_FATHER_PROPERTY
				+ " ?father .";

		final Variable titleVariable = Expressions.makeUniversalVariable("title");
		final Variable motherVariable = Expressions.makeUniversalVariable("mother");
		final Variable fatherVariable = Expressions.makeUniversalVariable("father");

		/*
		 * The query variables are the variables from the query body which will appear
		 * in the query result, in the given order. Fact resulting from this query will
		 * have as terms the title of the publication, the mother publication author and
		 * the father publication author.
		 */
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(titleVariable, motherVariable, fatherVariable));

		/*
		 * We query Wikidata with the SPARQL query composed of the query variables and
		 * query body. The query result is a DataSource we will associate to a
		 * predicate.
		 */
		final DataSource sparqlQueryResultDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
				queryVariables, queryBody);

		/*
		 * Predicate that will be mapped to the SPARQL query result. It must have the
		 * same arity as the query variables size. In this case, we have 3 query
		 * variables (title, mother and father).
		 */
		final Predicate queryPredicate = Expressions.makePredicate("publicationParents", 3);

		try (Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {

			final KnowledgeBase kb = reasoner.getKnowledgeBase();
			/*
			 * The SPARQL query results will be added to the reasoner knowledge base, as
			 * facts associated to the predicate publicationParents.
			 */

			kb.addStatement(new DataSourceDeclarationImpl(queryPredicate, sparqlQueryResultDataSource));
			reasoner.reason();

			/*
			 * We construct a query PositiveLiteral for the predicated associated to the
			 * SPARQL query result.
			 */
			final PositiveLiteral query = Expressions.makePositiveLiteral(queryPredicate, Expressions.makeUniversalVariable("x"),
					Expressions.makeUniversalVariable("y"), Expressions.makeUniversalVariable("z"));

			/* We query the reasoner for facts of the SPARQL query result predicate. */
			System.out.println("Titles of publications by co-authors who have a child together:");
			try (QueryResultIterator queryResultIterator = reasoner.answerQuery(query, false)) {
				queryResultIterator.forEachRemaining(queryResult -> {
					final List<Term> queryResultTerms = queryResult.getTerms();

					System.out.println("- title: " + queryResultTerms.get(0) + ", mother author: "
							+ queryResultTerms.get(1) + ", father author: " + queryResultTerms.get(2));
				});
			}

			/*
			 * To do some basic reasoning, we would now like to add the following rule that
			 * extracts (unique) mothers, fathers, and pairs from the queried data:
			 * haveChildrenTogether(?y, ?z), isMother(?y), isFather(?z) :-
			 * publicationParents(?x, ?y, ?z) .
			 */
			final PositiveLiteral haveChildrenTogether = Expressions.makePositiveLiteral("haveChildrenTogether",
					Expressions.makeUniversalVariable("y"), Expressions.makeUniversalVariable("z"));
			final PositiveLiteral isMother = Expressions.makePositiveLiteral("isMother", Expressions.makeUniversalVariable("y"));
			final PositiveLiteral isFather = Expressions.makePositiveLiteral("isFather", Expressions.makeUniversalVariable("z"));
			final Conjunction<PositiveLiteral> ruleHeadConjunction = Expressions
					.makePositiveConjunction(haveChildrenTogether, isMother, isFather);
			final Rule rule = Expressions.makeRule(ruleHeadConjunction, Expressions.makeConjunction(query));

			/*
			 * We add the created rule, and reason on the data added from the Wikidata
			 * SPARQL query result.
			 */
			kb.addStatement(rule);
			reasoner.reason();

			/* We query the reasoner for facts of the haveChildrenTogether predicate. */
			System.out.println("Co-authors who have a child:");
			try (QueryResultIterator queryResultIterator = reasoner.answerQuery(haveChildrenTogether, false)) {
				queryResultIterator.forEachRemaining(queryResult -> {
					final List<Term> queryResultTerms = queryResult.getTerms();

					System.out
							.println("- author1: " + queryResultTerms.get(0) + ", author2: " + queryResultTerms.get(1));
				});
			}

			/* We query the reasoner for facts of the isMother predicate. */
			System.out.println("Mothers:");
			try (QueryResultIterator queryResultIterator = reasoner.answerQuery(isMother, false)) {
				queryResultIterator.forEachRemaining(queryResult -> {
					final List<Term> queryResultTerms = queryResult.getTerms();

					System.out.println("- mother: " + queryResultTerms.get(0));
				});
			}

			/* We query the reasoner for facts of the isFather predicate. */
			System.out.println("Fathers:");
			try (QueryResultIterator queryResultIterator = reasoner.answerQuery(isFather, false)) {
				queryResultIterator.forEachRemaining(queryResult -> {
					final List<Term> queryResultTerms = queryResult.getTerms();

					System.out.println("- father: " + queryResultTerms.get(0));
				});
			}

		}
	}

}
