package org.semanticweb.rulewerk.examples.reliances;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * This example reasons about human diseases, based on information from the
 * Disease Ontology (DOID) and Wikidata. It illustrates how to load data from
 * different sources (RDF file, SPARQL), and reason about these inputs using
 * rules that are loaded from a file. The rules used here employ existential
 * quantifiers and stratified negation.
 * 
 * @author Markus Kroetzsch
 * @author Larry Gonzalez
 */
public class DoidExampleMeasuringTime {

	static private void meterialize(String ruleFile, String kind, int i) throws IOException, ParsingException {
//		ExamplesUtils.configureLogging();

		/* Configure rules */
		KnowledgeBase kb;
		try {
			kb = RuleParser.parse(new FileInputStream(ExamplesUtils.INPUT_FOLDER + ruleFile)); // THIS SHOULD BE
																								// CHANGED
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}

		try (Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogFile(ExamplesUtils.OUTPUT_FOLDER + "vlog-" + kind + "-" + i + ".log");
			reasoner.setLogLevel(LogLevel.DEBUG);

			/* Initialise reasoner and compute inferences */
			reasoner.reason();

			/* Execute some queries */
			final List<String> queries = Arrays.asList("humansWhoDiedOfCancer(?X)", "humansWhoDiedOfNoncancer(?X)",
					"deathCause(?X,?Y)", "hasDoid(?X)", "doid(?X, ?Y)", "diseaseHierarchy(?X, ?Y)",
					"cancerDisease(?X)");
			System.out.println("\nNumber of inferred tuples for selected query atoms:");
			for (final String queryString : queries) {
				double answersCount = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral(queryString))
						.getCount();
				System.out.println("  " + queryString + ": " + answersCount);
			}
		}

	}

	static private void print(long[] array) {
		String content = "[";
		for (int i = 0; i < array.length; i++) {
			content += array[i] + ", ";
		}
		content += "]";
		System.out.println(content);
	}

	public static void main(final String[] args) throws IOException, ParsingException {
		// normal
		long startTime, endTime;
		long first[] = new long[10];
		for (int i = 0; i < 10; i++) {
			startTime = System.currentTimeMillis();
			meterialize("/doid.rls", "ori", i);
			endTime = System.currentTimeMillis();
			first[i] = endTime - startTime;
		}

		long second[] = new long[10];
		for (int i = 0; i < 10; i++) {
			startTime = System.currentTimeMillis();
			meterialize("/doid-modified.rls", "mod", i);
			endTime = System.currentTimeMillis();
			second[i] = endTime - startTime;
		}

		print(first);
		print(second);
	}

}
