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

import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.parser.api.RuleParser;
import org.semanticweb.vlog4j.parser.implementation.PrologueException;
import org.semanticweb.vlog4j.parser.implementation.javacc.ParseException;

public class SimpleExampleJavaCC {
	public static void main(final String[] args) throws ParseException, PrologueException, ReasonerStateException,
			EdbIdbSeparationException, IncompatiblePredicateArityException, IOException {

		ExamplesUtils.configureLogging();

		try (final Reasoner reasoner = Reasoner.getInstance()) {

			String rules = "";
			rules += "@base <http://www.example.org/rdf#> . \n";
			rules += "<p>(<a>) .                            \n";
			rules += "<q>(?x) :- <p>(?x) .                  \n";
			rules += "<q>(?y) .                             \n";
			rules += "<r>(?x,?y) :- <q>(?x) .               \n";
			rules += "<r>(?x,?y) .                          \n";

			RuleParser rp = new RuleParser(rules);
			rp.parse();

			reasoner.addFacts(rp.getFacts());
			reasoner.addRules(rp.getRules());

			System.out.println("Rules configured:\n--");
			reasoner.getRules().forEach(System.out::println);
			System.out.println("--");
			reasoner.load();

			System.out.println("Loading completed.");
			System.out.println("Starting reasoning (including SPARQL query answering) ...");
			reasoner.reason();
			System.out.println("... reasoning completed.\n--");

			System.out.println("Number of results in queries:");
			QueryResultIterator answers;
			for (PositiveLiteral l : rp.getQueries()) {
				answers = reasoner.answerQuery(l, true);
				System.out.print(l.toString());
				System.out.println(": " + ExamplesUtils.iteratorSize(answers));
			}
			System.out.println("Done.");
		}
	}
}
