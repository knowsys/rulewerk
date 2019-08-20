package org.semanticweb.vlog4j.examples;

import java.io.FileInputStream;

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

import org.apache.commons.lang3.tuple.Pair;
import org.semanticweb.vlog4j.core.exceptions.VLog4jException;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
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
		ExamplesUtils.configureLogging();

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			/* Configure rules */
			RuleParser ruleParser = new RuleParser();
			try {
				ruleParser.parse(new FileInputStream(ExamplesUtils.INPUT_FOLDER + "counting-triangles.rls"));
			} catch (ParsingException e) {
				System.out.println("Failed to parse rules: " + e.getMessage());
				return;
			}
			for (Pair<Predicate, DataSource> pair : ruleParser.getDataSources()) {
				reasoner.addFactsFromDataSource(pair.getLeft(), pair.getRight());
			}
			reasoner.addRules(ruleParser.getRules());
			System.out.println("Rules used in this example:");
			reasoner.getRules().forEach(System.out::println);
			System.out.println("");

			/* Initialise reasoner and compute inferences */
			System.out.print("Initialising rules and data sources ... ");
			reasoner.load();
			System.out.println("completed.");

			System.out.print("Reasoning (including SPARQL query answering) ... ");
			reasoner.reason();
			System.out.println("completed.");

			/* Execute queries */
			try {
				PositiveLiteral query;

				query = ruleParser.parsePositiveLiteral("country(?X)");
				System.out.print("Found " + ExamplesUtils.iteratorSize(reasoner.answerQuery(query, true))
						+ " countries in Wikidata");
				// Due to symmetry, each joint border is found twice, hence we divide by 2:
				query = ruleParser.parsePositiveLiteral("shareBorder(?X,?Y)");
				System.out.println(", with " + ExamplesUtils.iteratorSize(reasoner.answerQuery(query, true)) / 2
						+ " pairs of them sharing a border.");
				// Due to symmetry, each triangle is found six times, hence we divide by 6:
				query = ruleParser.parsePositiveLiteral("triangle(?X,?Y,?Z)");
				System.out.println("The number of triangles of countries that mutually border each other was "
						+ ExamplesUtils.iteratorSize(reasoner.answerQuery(query, true)) / 6 + ".");
			} catch (ParsingException e) {
				System.out.println("Failed to parse query: " + e.getMessage());
			}

			System.out.println("Done.");
		} catch (VLog4jException e) {
			System.out.println("The reasoner encountered a problem: " + e.getMessage());
		}

	}
}
