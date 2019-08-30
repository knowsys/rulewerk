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

import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
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

		/* Configure rules */
		KnowledgeBase kb;
		try {
			kb = RuleParser.parse(new FileInputStream(
					ExamplesUtils.INPUT_FOLDER + "diplomatically-unconnected-clusters-of-countries.rls"));
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}

		System.out.println("Rules used in this example:");
		kb.getRules().forEach(System.out::println);
		System.out.println("");

		try (Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogFile(ExamplesUtils.OUTPUT_FOLDER + "vlog.log");
			reasoner.setLogLevel(LogLevel.DEBUG);

			System.out.print("Reasoning (including SPARQL query answering) ... ");
			/* Initialise reasoner and compute inferences */
			reasoner.reason();
			System.out.println("completed.");

			System.out.println("Found " + ExamplesUtils.getQueryAnswerCount("country(?X)", reasoner)
					+ " countries in Wikidata having at least one diplomatic relationship with another country.");
			// Due to symmetry, each pair is found twice, hence we divide by 2:
			System.out.println("There are "
					+ ExamplesUtils.getQueryAnswerCount("complementOfEquivalenceClosure(?X,?Y)", reasoner) / 2
					+ " pairs of countries that are not reachable through a transitive diplomatic relation.");
			// Due to symmetry, each triangle is found three, hence we divide by 6:
			System.out.println("There are " + ExamplesUtils.getQueryAnswerCount("triangle(?X,?Y,?Z)", reasoner) / 6
					+ " triangles of countries without a diplomatic relation.");
			System.out.println("There are " + ExamplesUtils.getQueryAnswerCount("clique4(?X,?Y,?Z,?W)", reasoner) / 24
					+ " 4-cliques of countries without a diplomatic relation.");
			System.out.println(
					"Therefore, there are three diplomatically unconnected clusters of countries. Seen on 2019-08-30. It might have changed.");

			System.out.println("Done.");
		}
	}
}
