package org.semanticweb.vlog4j.examples.core;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;

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

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.File;
import java.io.IOException;

import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
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
 *
 * @author Christian Lewe
 *
 */
public class AddDataFromRdfFile {

	public static void main(final String[] args)
			throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		ExamplesUtils.configureLogging();

		/* 1. Prepare rules and create some related vocabulary objects used later. */
		final Predicate triplesEDB = makePredicate("triplesEDB", 3); // predicate to load RDF
		final Predicate triplesIDB = makePredicate("triplesIDB", 3); // predicate for inferred triples
		final Constant hasPartPredicate = makeConstant("https://example.org/hasPart"); // RDF property used in query

		final String rules = "%%%% We specify the rules syntactically for convenience %%%\n"
				+ "@prefix ex: <https://example.org/> ."
				+ "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."
				// load all triples from file:
				+ "triplesIDB(?S, ?P, ?O) :- triplesEDB(?S, ?P, ?O) ."
				// every bicycle has some part that is a wheel:
				+ "triplesIDB(?S, ex:hasPart, !X), triplesIDB(!X, rdf:type, ex:wheel) :- triplesIDB(?S, rdf:type, ex:bicycle) ."
				// every wheel is part of some bicycle:
				+ "triplesIDB(?S, ex:isPartOf, !X) :- triplesIDB(?S, rdf:type, ex:wheel) ."
				// hasPart and isPartOf are mutually inverse relations:
				+ "triplesIDB(?S, ex:isPartOf, ?O) :- triplesIDB(?O, ex:hasPart, ?S) ."
				+ "triplesIDB(?S, ex:hasPart, ?O) :- triplesIDB(?O, ex:isPartOf, ?S) .";

		RuleParser ruleParser = new RuleParser();
		try {
			ruleParser.parse(rules);
		} catch (ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}

		/*
		 * 2. Loading, reasoning, querying and exporting, while using try-with-resources
		 * to close the reasoner automatically.
		 */
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(ruleParser.getRules());

			/* Importing {@code .nt.gz} file as data source. */
			final DataSource triplesEDBDataSource = new RdfFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "ternaryBicycleEDB.nt.gz"));
			reasoner.addFactsFromDataSource(triplesEDB, triplesEDBDataSource);

			reasoner.load();
			System.out.println("Before materialisation:");
			final Variable x = makeVariable("X");
			final Variable y = makeVariable("Y");
			final PositiveLiteral hasPartEDB = makePositiveLiteral(triplesEDB, x, hasPartPredicate, y);
			ExamplesUtils.printOutQueryAnswers(hasPartEDB, reasoner);

			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();
			System.out.println("After materialisation:");
			final PositiveLiteral hasPartIDB = makePositiveLiteral(triplesIDB, x, hasPartPredicate, y);
			ExamplesUtils.printOutQueryAnswers(hasPartIDB, reasoner);

			/* Exporting query answers to {@code .csv} files. */
			reasoner.exportQueryAnswersToCsv(hasPartIDB,
					ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithBlanks.csv", true);
			reasoner.exportQueryAnswersToCsv(hasPartIDB,
					ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithoutBlanks.csv", false);

			final Constant redBikeSubject = makeConstant("https://example.org/redBike");
			final PositiveLiteral existsHasPartRedBike = makePositiveLiteral(triplesIDB, redBikeSubject,
					hasPartPredicate, x);
			reasoner.exportQueryAnswersToCsv(existsHasPartRedBike,
					ExamplesUtils.OUTPUT_FOLDER + "existsHasPartIDBRedBikeWithBlanks.csv", true);
		}
	}

}
