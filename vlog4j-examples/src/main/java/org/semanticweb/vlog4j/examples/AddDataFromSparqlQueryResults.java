package org.semanticweb.vlog4j.examples;

/*-
 * #%L
 * VLog4j Examples
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
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;

/**
 * This is a simple example of adding data from the result of a SPARQL query on
 * a remote database endpoint. In this example, we will query WikiData for
 * titles of publications that have authors who have children together.
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

	public static void main(String[] args)
			throws ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException, IOException {

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

		final Variable titleVariable = Expressions.makeVariable("title");
		final Variable motherVariable = Expressions.makeVariable("mother");
		final Variable fatherVariable = Expressions.makeVariable("father");

		/*
		 * The query variables are the variables from the query body which will appear
		 * in the query result, in the given order. Fact resulting from this query will
		 * have as terms the title of the publication, the mother publication author and
		 * the father publication author.
		 */
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(titleVariable, motherVariable, fatherVariable));

		/*
		 * We query WikiData with the SPARQL query composed of the query variables and
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
		final Predicate titleOfPublicationThatHasAuthorsWhoParentTheSameChild = Expressions
				.makePredicate("havePublicationsTogether", 3);

		try (Reasoner reasoner = Reasoner.getInstance()) {

			/*
			 * The SPARQL query results will be added to the reasoner knowledge base, as
			 * facts associated to the predicate
			 * titleOfPublicationThatHasAuthorsWhoParentTheSameChild.
			 */
			reasoner.addFactsFromDataSource(titleOfPublicationThatHasAuthorsWhoParentTheSameChild,
					sparqlQueryResultDataSource);

			reasoner.load();

			/*
			 * We construct a query atom for the predicated associated to the SPARQL query
			 * result.
			 */
			Atom queryAtom = Expressions.makeAtom(titleOfPublicationThatHasAuthorsWhoParentTheSameChild,
					Expressions.makeVariable("x"), Expressions.makeVariable("y"), Expressions.makeVariable("z"));

			/* We query the reasoner for facts of the SPARQL query result predicate. */
			System.out.println("Publications that have authors who parent the same child:");
			try (QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, false)) {
				queryResultIterator.forEachRemaining(queryResult -> {
					List<Term> queryResultTerms = queryResult.getTerms();

					System.out.println("- title: " + queryResultTerms.get(0) + ", mother author: "
							+ queryResultTerms.get(1) + ", father author: " + queryResultTerms.get(2));
				});
			}

			Atom haveChildrenTogether = Expressions.makeAtom("haveChildrenTogether", Expressions.makeVariable("y"),
					Expressions.makeVariable("z"));
			Atom isMother = Expressions.makeAtom("isMother", Expressions.makeVariable("y"));
			Atom isFather = Expressions.makeAtom("isFather", Expressions.makeVariable("z"));
			Conjunction ruleHeadConjunction = Expressions.makeConjunction(haveChildrenTogether, isMother, isFather);
			/*
			 * haveChildrenTogetherRuleHeadAtom(y,z), isMother(y), isFather(z) :-
			 * titleOfPublicationThatHasAuthorsWhoParentTheSameChild(x,y,z)
			 */
			Rule rule = Expressions.makeRule(ruleHeadConjunction, Expressions.makeConjunction(queryAtom));

			/*
			 * We reset the reasoner in order to add the created rule, and reason on the
			 * data added from the WikiData SPARQL query result.
			 */
			reasoner.resetReasoner();
			reasoner.addRules(rule);
			reasoner.load();
			reasoner.reason();

			/* We query the reasoner for facts of the haveChildrenTogether predicate. */
			System.out.println("Pairs of authors who have children together and wrote publications together:");
			try (QueryResultIterator queryResultIterator = reasoner.answerQuery(haveChildrenTogether, false)) {
				queryResultIterator.forEachRemaining(queryResult -> {
					List<Term> queryResultTerms = queryResult.getTerms();

					System.out
							.println("- author1: " + queryResultTerms.get(0) + ", author2: " + queryResultTerms.get(1));
				});
			}
			
			/* We query the reasoner for facts of the isMother predicate. */
			System.out.println("Mothers:");
			try (QueryResultIterator queryResultIterator = reasoner.answerQuery(isMother, false)) {
				queryResultIterator.forEachRemaining(queryResult -> {
					List<Term> queryResultTerms = queryResult.getTerms();

					System.out
							.println("- mother: " + queryResultTerms.get(0));
				});
			}
			
			/* We query the reasoner for facts of the isFather predicate. */
			System.out.println("Fathers:");
			try (QueryResultIterator queryResultIterator = reasoner.answerQuery(isFather, false)) {
				queryResultIterator.forEachRemaining(queryResult -> {
					List<Term> queryResultTerms = queryResult.getTerms();

					System.out
							.println("- father: " + queryResultTerms.get(0));
				});
			}
			

		}
	}

}
