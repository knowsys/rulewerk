package org.semanticweb.vlog4j.examples.core;

/*-
 * #%L
 * VLog4j Examples
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

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

	public static void main(final String[] args) throws EdbIdbSeparationException, IOException, ReasonerStateException,
			IncompatiblePredicateArityException, ParsingException {
		ExamplesUtils.configureLogging();

		/* 1. Prepare rules and create some related vocabulary objects used later. */

		final String rules = "" // first define some namespaces and abbreviations:
				+ "@prefix ex: <https://example.org/> ."
				+ "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."
				// specify data sources:
				+ "@source triplesEDB(3) : load-rdf(\"" + ExamplesUtils.INPUT_FOLDER + "ternaryBicycleEDB.nt.gz\") ."
				// rule for loading all triples from file:
				+ "triplesIDB(?S, ?P, ?O) :- triplesEDB(?S, ?P, ?O) ."
				// every bicycle has some part that is a wheel:
				+ "triplesIDB(?S, ex:hasPart, !X), triplesIDB(!X, rdf:type, ex:wheel) :- triplesIDB(?S, rdf:type, ex:bicycle) ."
				// every wheel is part of some bicycle:
				+ "triplesIDB(?S, ex:isPartOf, !X) :- triplesIDB(?S, rdf:type, ex:wheel) ."
				// hasPart and isPartOf are mutually inverse relations:
				+ "triplesIDB(?S, ex:isPartOf, ?O) :- triplesIDB(?O, ex:hasPart, ?S) ."
				+ "triplesIDB(?S, ex:hasPart, ?O) :- triplesIDB(?O, ex:isPartOf, ?S) .";

		final KnowledgeBase kb = RuleParser.parse(rules);

		/*
		 * 2. Loading, reasoning, querying and exporting, while using try-with-resources
		 * to close the reasoner automatically.
		 */
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			System.out.println("Before materialisation:");

			ExamplesUtils.printOutQueryAnswers("triplesEDB(?X, <https://example.org/hasPart>, ?Y)", reasoner);

			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();
			System.out.println("After materialisation:");
			final PositiveLiteral hasPartIDB = RuleParser
					.parsePositiveLiteral("triplesIDB(?X, <https://example.org/hasPart>, ?Y)");
			ExamplesUtils.printOutQueryAnswers(hasPartIDB, reasoner);

			/* Exporting query answers to {@code .csv} files. */
			reasoner.exportQueryAnswersToCsv(hasPartIDB,
					ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithBlanks.csv", true);
			reasoner.exportQueryAnswersToCsv(hasPartIDB,
					ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithoutBlanks.csv", false);

			final PositiveLiteral existsHasPartRedBike = RuleParser.parsePositiveLiteral(
					"triplesIDB(<https://example.org/redBike>, <https://example.org/hasPart>, ?X)");
			reasoner.exportQueryAnswersToCsv(existsHasPartRedBike,
					ExamplesUtils.OUTPUT_FOLDER + "existsHasPartIDBRedBikeWithBlanks.csv", true);
		}
	}

}
