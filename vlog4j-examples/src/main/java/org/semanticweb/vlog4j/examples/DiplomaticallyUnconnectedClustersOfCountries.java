package org.semanticweb.vlog4j.examples;

import java.io.FileInputStream;
import java.io.IOException;

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

import org.semanticweb.vlog4j.core.exceptions.VLog4jException;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

/**
 * This example computes the complement of a transitive closure of a graph.
 *
 * @author Markus Kroetzsch
 * @author Larry Gonzalez
 */
public class DiplomaticallyUnconnectedClustersOfCountries {

	public static void main(final String[] args) throws IOException {
		ExamplesUtils.configureLogging();

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			RuleParser ruleParser = new RuleParser();
			try {
				ruleParser.parse(new FileInputStream(
						ExamplesUtils.INPUT_FOLDER + "diplomatically-unconnected-clusters-of-countries.rls"));
			} catch (ParsingException e) {
				System.out.println("Failed to parse rules: " + e.getMessage());
				return;
			}

			for (DataSourceDeclaration dataSourceDeclaration : ruleParser.getDataSourceDeclartions()) {
				reasoner.addFactsFromDataSource(dataSourceDeclaration.getPredicate(),
						dataSourceDeclaration.getDataSource());
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
				System.out.println("Found " + ExamplesUtils.iteratorSize(reasoner.answerQuery(query, true))
						+ " countries in Wikidata having at least one diplomatic relationship with another country.");
				// Due to symmetry, each pair is found twice, hence we divide by 2:
				query = ruleParser.parsePositiveLiteral("complementOfEquivalenceClosure(?X,?Y)");
				System.out.println("There are " + ExamplesUtils.iteratorSize(reasoner.answerQuery(query, true)) / 2
						+ " pairs of countries that are not reachable through a transitive diplomatic relation.");
				// Due to symmetry, each triangle is found three, hence we divide by 6:
				query = ruleParser.parsePositiveLiteral("triangle(?X,?Y,?Z)");
				System.out.println("There are " + ExamplesUtils.iteratorSize(reasoner.answerQuery(query, true)) / 6
						+ " triangles of countries without a diplomatic relation.");
				query = ruleParser.parsePositiveLiteral("clique4(?X,?Y,?Z,?W)");
				System.out.println("There are " + ExamplesUtils.iteratorSize(reasoner.answerQuery(query, true)) / 24
						+ " 4-cliques of countries without a diplomatic relation.");
				System.out.println("Therefore, there are three diplomatically unconnected clusters of countries.");

			} catch (ParsingException e) {
				System.out.println("Failed to parse query: " + e.getMessage());
			}

			System.out.println("Done.");
		} catch (VLog4jException e) {
			System.out.println("The reasoner encountered a problem: " + e.getMessage());
		}

	}

}
