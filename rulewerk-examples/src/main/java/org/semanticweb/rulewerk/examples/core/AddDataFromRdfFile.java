package org.semanticweb.rulewerk.examples.core;

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

import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * This example shows how facts can be imported from files in the RDF N-Triples
 * format. Specifically, it imports from a {@code .nt.gz} file, but you can also
 * import from {@code .nt} files. Moreover, it shows how query answers that
 * result from reasoning over these facts can be exported to {@code .csv} files.
 * <p>
 * This example is an adaptation of {@link AddDataFromCsvFile}, where the rules
 * have been modified to work with the ternary predicates that N-Triples
 * enforces.
 * <p>
 * For importing, an {@link RdfFileDataSource} that contains a path to the
 * corresponding {@code .nt.gz} file must be created. An {@code .nt} file
 * contains facts in the RDF N-Triples format, which can be associated with a
 * ternary {@link Predicate}. A {@code .nt.gz} file is the gzipped version of
 * such an {@code .nt} file.
 * <p>
 * For exporting, a path to the output {@code .csv} file must be specified.
 * <p>
 * Exception handling is omitted for simplicity.
 *
 * @author Christian Lewe
 * @author Markus Kroetzsch
 *
 */
public class AddDataFromRdfFile {

	public static void main(final String[] args) throws IOException, ParsingException {
		ExamplesUtils.configureLogging();

		/* 1. Prepare rules and create some related vocabulary objects used later. */

		final String rules = "" // first define some namespaces and abbreviations:
				+ "@prefix ex: <https://example.org/> ."
				+ "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."
				// specify data sources:
				+ "@source triple[3] : load-rdf(\"" + ExamplesUtils.INPUT_FOLDER + "ternaryBicycleEDB.nt.gz\") ."
				// every bicycle has some part that is a wheel:
				+ "triple(?S, ex:hasPart, !X), triple(!X, rdf:type, ex:wheel) :- triple(?S, rdf:type, ex:bicycle) ."
				// every wheel is part of some bicycle:
				+ "triple(?S, ex:isPartOf, !X) :- triple(?S, rdf:type, ex:wheel) ."
				// hasPart and isPartOf are mutually inverse relations:
				+ "triple(?S, ex:isPartOf, ?O) :- triple(?O, ex:hasPart, ?S) ."
				+ "triple(?S, ex:hasPart, ?O) :- triple(?O, ex:isPartOf, ?S) .";

		final KnowledgeBase kb = RuleParser.parse(rules);

		/*
		 * 2. reasoning, querying and exporting, while using try-with-resources to close
		 * the reasoner automatically.
		 */

		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();
			System.out.println("After materialisation:");
			final PositiveLiteral hasPartIDB = RuleParser
					.parsePositiveLiteral("triple(?X, <https://example.org/hasPart>, ?Y)");
			ExamplesUtils.printOutQueryAnswers(hasPartIDB, reasoner);

			/* Exporting query answers to {@code .csv} files. */
			reasoner.exportQueryAnswersToCsv(hasPartIDB, ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartWithBlanks.csv",
					true);
			reasoner.exportQueryAnswersToCsv(hasPartIDB,
					ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartWithoutBlanks.csv", false);

			final PositiveLiteral existsHasPartRedBike = RuleParser
					.parsePositiveLiteral("triple(<https://example.org/redBike>, <https://example.org/hasPart>, ?X)");
			reasoner.exportQueryAnswersToCsv(existsHasPartRedBike,
					ExamplesUtils.OUTPUT_FOLDER + "existsHasPartRedBikeWithBlanks.csv", true);
		}
	}

}
