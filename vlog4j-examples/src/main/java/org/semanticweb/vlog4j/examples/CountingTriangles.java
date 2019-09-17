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

import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
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

		KnowledgeBase kb;
		/* Configure rules */
		try {
			kb = RuleParser.parse(new FileInputStream(ExamplesUtils.INPUT_FOLDER + "counting-triangles.rls"));
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}
		System.out.println("Rules used in this example:");
		kb.getRules().forEach(System.out::println);
		System.out.println("");

		try (VLogReasoner reasoner = new VLogReasoner(kb)) {

			System.out.println("Note: Materialisation includes SPARQL query answering.");

			/* Initialise reasoner and compute inferences */
			reasoner.reason();

			System.out.print("Found " + ExamplesUtils.getQueryAnswerCount("country(?X)", reasoner)
					+ " countries in Wikidata");
			// Due to symmetry, each joint border is found twice, hence we divide by 2:
			System.out.println(", with " + (ExamplesUtils.getQueryAnswerCount("shareBorder(?X,?Y)", reasoner) / 2)
					+ " pairs of them sharing a border.");
			// Due to symmetry, each triangle is found six times, hence we divide by 6:
			System.out.println("The number of triangles of countries that mutually border each other was "
					+ (ExamplesUtils.getQueryAnswerCount("triangle(?X,?Y,?Z)", reasoner) / 6) + ".");
		}

	}
}
