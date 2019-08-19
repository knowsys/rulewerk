package org.semanticweb.vlog4j.examples;

/*-
 * #%L
 * VLog4j Examples
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import org.semanticweb.vlog4j.core.exceptions.VLog4jException;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

/**
 * In this example we count the number of triangles in the reflexive
 * sharingBorderWith relation from Wikidata.
 * 
 * @author Markus Kroetzsch
 * @author Larry Gonzalez
 *
 */
public class CountingTriangles {

	public static void main(final String[] args) throws IOException {

		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		ExamplesUtils.configureLogging(); // use simple logger for the example

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.setLogFile(ExamplesUtils.OUTPUT_FOLDER + "vlog.log");
			reasoner.setLogLevel(LogLevel.DEBUG);

			// (wdt:P47 = "Sharing border with")
			// list of sharing border countries
			final String sparqlCountriesSharingBorders = "?country1 wdt:P31 wd:Q6256 . ?country2 wdt:P31 wd:Q6256 . ?country1 wdt:P47 ?country2 .";

			final DataSource sharingBordersDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					"country1,country2", sparqlCountriesSharingBorders);
			final Predicate sharingBordersPredicate = Expressions.makePredicate("sharingBorders", 2);
			reasoner.addFactsFromDataSource(sharingBordersPredicate, sharingBordersDataSource);

			// We compute the reflexive relation from "sharingBorders", and then we count
			// the number of triangles
			String rules = "" //
					+ "reflexiveBorder(?X,?Y) :- sharingBorders(?X,?Y) .\n"
					+ "reflexiveBorder(?Y,?X) :- reflexiveBorder(?X,?Y) .\n"
					+ "triangle(?X,?Y,?Z) :- reflexiveBorder(?X,?Y), reflexiveBorder(?Y,?Z), reflexiveBorder(?Z,?X) . \n";

			RuleParser ruleParser = new RuleParser();
			try {
				ruleParser.parse(rules);
			} catch (ParsingException e) {
				System.out.println("Failed to parse rules: " + e.getMessage());
				return;
			}

			reasoner.addRules(ruleParser.getRules());

			System.out.println("Rules configured:\n--");
			reasoner.getRules().forEach(System.out::println);
			System.out.println("--");

			reasoner.load();

			System.out.println("Loading completed.");
			System.out.println("Starting reasoning ...");
			reasoner.reason();
			System.out.println("... reasoning completed.\n--");

			/* Execute a query */
			try {
				PositiveLiteral query = ruleParser.parsePositiveLiteral("triangle(?X,?Y,?Z)");
				QueryResultIterator answers = reasoner.answerQuery(query, true);
				// Note that we divide it by 6
				System.out.println("The number of triangles in the sharesBorderWith relation (from Wikidata) is: "
						+ ": " + ExamplesUtils.iteratorSize(answers) / 6);
			} catch (ParsingException e) {
				System.out.println("Failed to parse query: " + e.getMessage());
			}

			System.out.println("Done.");
		} catch (VLog4jException e) {
			System.out.println("The reasoner encountered a problem:" + e.getMessage());
		}

	}
}
