package org.semanticweb.rulewerk.examples;

import java.io.FileInputStream;

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

import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * In this example we count the number of triangles in the reflexive
 * sharingBorderWith relation from Wikidata.
 * 
 * @author Markus Kroetzsch
 * @author Larry Gonzalez
 *
 */
public class CountingTriangles {

	public static void main(final String[] args) throws IOException, ParsingException {
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

			final double countries = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("country(?X)"))
					.getCount();
			final double shareBorder = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("shareBorder(?X,?Y)"))
					.getCount();
			final double triangles = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("triangle(?X,?Y,?Z)"))
					.getCount();

			System.out.print("Found " + countries + " countries in Wikidata");
			// Due to symmetry, each joint border is found twice, hence we divide by 2:
			System.out.println(", with " + (shareBorder / 2) + " pairs of them sharing a border.");
			// Due to symmetry, each triangle is found six times, hence we divide by 6:
			System.out.println("The number of triangles of countries that mutually border each other was "
					+ (triangles / 6) + ".");
		}

	}

}
