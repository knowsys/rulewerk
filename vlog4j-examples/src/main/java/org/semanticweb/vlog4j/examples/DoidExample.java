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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.vlog4j.core.exceptions.VLog4jException;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogKnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

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
public class DoidExample {

	public static void main(final String[] args) throws IOException {
		ExamplesUtils.configureLogging();

		final VLogKnowledgeBase kb = new VLogKnowledgeBase();

		try (Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogFile(ExamplesUtils.OUTPUT_FOLDER + "vlog.log");
			reasoner.setLogLevel(LogLevel.DEBUG);

			/* Configure rules */
			RuleParser ruleParser = new RuleParser();
			try {
				ruleParser.parse(new FileInputStream(ExamplesUtils.INPUT_FOLDER + "/doid.rls"));
			} catch (ParsingException e) {
				System.out.println("Failed to parse rules: " + e.getMessage());
				return;
			}

			for (DataSourceDeclaration dataSourceDeclaration : ruleParser.getDataSourceDeclartions()) {
				kb.addFactsFromDataSource(dataSourceDeclaration.getPredicate(), dataSourceDeclaration.getDataSource());
			}
			kb.addRules(ruleParser.getRules());
			System.out.println("Rules used in this example:");
			kb.getRules().forEach(System.out::println);
			System.out.println("");

			/* Initialise reasoner and compute inferences */
			System.out.print("Initialising rules and data sources ... ");
			reasoner.load();
			System.out.println("completed.");

			System.out.print("Reasoning (including SPARQL query answering) ... ");
			reasoner.reason();
			System.out.println("completed.");

			/* Execute some queries */
			List<String> queries = Arrays.asList("humansWhoDiedOfCancer(?X)", "humansWhoDiedOfNoncancer(?X)");
			QueryResultIterator answers;
			System.out.println("\nNumber of inferred tuples for selected query atoms:");
			for (String queryString : queries) {
				try {
					PositiveLiteral query = ruleParser.parsePositiveLiteral(queryString);
					answers = reasoner.answerQuery(query, true);
					System.out.println("  " + query.toString() + ": " + ExamplesUtils.iteratorSize(answers));
				} catch (ParsingException e) {
					System.out.println("Failed to parse query: " + e.getMessage());
				}
			}

			System.out.println("\nDone.");
		} catch (VLog4jException e) {
			System.out.println("The reasoner encountered a problem:" + e.getMessage());

		}
	}

}
