package org.semanticweb.rulewerk.examples;

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
import java.util.Collections;
import java.util.List;

import org.semanticweb.rulewerk.asp.implementation.AspReasonerImpl;
import org.semanticweb.rulewerk.asp.model.AnswerSetIterator;
import org.semanticweb.rulewerk.asp.model.AspReasoner;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * This example illustrates the use of non-stratified negation to solve the 3-colourability problem.
 * The example uses the Petersen graph as example graph.
 *
 * @author Philipp Hanisch
 */
public class AspExample {

	public static void main(final String[] args) throws IOException, ParsingException {
		ExamplesUtils.configureLogging();

		/* Configure rules */
		KnowledgeBase kb;
		try {
			kb = RuleParser.parse(new FileInputStream(ExamplesUtils.INPUT_FOLDER + "/asp/3col.rls"));
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}
		System.out.println("Rules used in this example:");
		kb.getRules().forEach(System.out::println);
		System.out.println("");

		try (AspReasoner reasoner = new AspReasonerImpl(kb)) {
			System.out.println("Over-approximating rules:");
			reasoner.getDatalogKnowledgeBase().getRules().forEach(System.out::println);
			System.out.println();

			reasoner.setLogFile(ExamplesUtils.OUTPUT_FOLDER + "vlog.log");
			reasoner.setLogLevel(LogLevel.DEBUG);

			/* Initialise reasoner and compute cautious inferences */
			reasoner.reason();

			/* Execute some queries */
			final List<String> queries = Collections.singletonList("coloured(?V,?C)");
			// System.out.println("\nNumber of inferred tuples for selected query atoms:");
			for (final String queryString : queries) {
				PositiveLiteral queryLiteral = RuleParser.parsePositiveLiteral(queryString);

				/* Cautious reasoning should return only the single coloured node <1,2>, which we have fixed */
				System.out.println("Cautious reasoning...");
				QueryResultIterator resultIterator = reasoner.answerQuery(queryLiteral, true);
				while (resultIterator.hasNext()) {
					System.out.println("result: " + resultIterator.next());
				}
				System.out.println();

				/* Compute a complete colouring */
				AnswerSetIterator answerSetIterator = reasoner.getAnswerSets(1);
				while (answerSetIterator.hasNext()) {
					System.out.println("Answer: ");
					answerSetIterator.next().getLiterals(queryLiteral.getPredicate()).forEach(System.out::println);
					System.out.println();
				}
			}
		}
	}
}
