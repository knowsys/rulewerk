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
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

/**
 * This example shows how facts can be imported from files in the CSV format.
 * Specifically, it imports from a {@code .csv.gz} file, but you can also import
 * from {@code .csv} files. Moreover, it shows how query answers that result
 * from reasoning over these facts can be exported to {@code .csv} files.
 * <p>
 * For importing, a {@link CsvFileDataSource} that contains a path to the
 * corresponding {@code .csv.gz} file must be created. A {@code .csv} file
 * contains facts in the CSV format over exactly one predicate. A
 * {@code .csv.gz} file is the gzipped version of such a {@code .csv} file.
 * <p>
 * For exporting, a path to the output {@code .csv} file must be specified.
 *
 * @author Christian Lewe
 * @author Irina Dragoste
 *
 */
public class AddDataFromCsvFile {

	public static void main(final String[] args)
			throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {

		ExamplesUtils.configureLogging();

		/* 1. Prepare rules and create some related vocabulary objects used later. */
		final Predicate bicycleEDB = makePredicate("bicycleEDB", 1);
		final Predicate wheelEDB = makePredicate("wheelEDB", 1);
		final Predicate hasPartIDB = makePredicate("hasPartIDB", 2);
		final Predicate hasPartEDB = makePredicate("hasPartEDB", 2);

		final String rules = "%%%% We specify the rules syntactically for convenience %%%\n"
				// load all data from the file-based ("EDB") predicates:
				+ "bicycleIDB(?X) :- bicycleEDB(?X) ." //
				+ "wheelIDB(?X) :- wheelEDB(?X) ." //
				+ "hasPartIDB(?X, ?Y) :- hasPartEDB(?X, ?Y) ." //
				+ "isPartOfIDB(?X, ?Y) :- isPartOfEDB(?X, ?Y) ."
				// every bicycle has some part that is a wheel:
				+ "hasPartIDB(?X, !Y), wheelIDB(!Y) :- bicycleIDB(?X) ."
				// every wheel is part of some bicycle:
				+ "isPartOfIDB(?X, !Y) :- wheelIDB(?X) ."
				// hasPart and isPartOf are mutually inverse relations:
				+ "hasPartIDB(?X, ?Y) :- isPartOfIDB(?Y, ?X) ." //
				+ "isPartOfIDB(?X, ?Y) :- hasPartIDB(?Y, ?X) .";

		RuleParser ruleParser = new RuleParser();
		try {
			ruleParser.parse(rules);
		} catch (ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}

		/*
		 * 2. Loading, reasoning, and querying while using try-with-resources to close
		 * the reasoner automatically.
		 */
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(ruleParser.getRules());

			/* Importing {@code .csv} files as data sources. */
			final DataSource bicycleEDBDataSource = new CsvFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "bicycleEDB.csv.gz"));
			final DataSource hasPartDataSource = new CsvFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "hasPartEDB.csv.gz"));
			final DataSource wheelDataSource = new CsvFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "wheelEDB.csv.gz"));
			reasoner.addFactsFromDataSource(bicycleEDB, bicycleEDBDataSource);
			reasoner.addFactsFromDataSource(hasPartEDB, hasPartDataSource);
			reasoner.addFactsFromDataSource(wheelEDB, wheelDataSource);

			reasoner.load();
			System.out.println("Before materialisation:");
			final Variable x = makeVariable("X");
			final Variable y = makeVariable("Y");
			final PositiveLiteral hasPartEDBXY = makePositiveLiteral(hasPartEDB, x, y);
			ExamplesUtils.printOutQueryAnswers(hasPartEDBXY, reasoner);

			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();
			System.out.println("After materialisation:");
			final PositiveLiteral hasPartIDBXY = makePositiveLiteral(hasPartIDB, x, y);
			ExamplesUtils.printOutQueryAnswers(hasPartIDBXY, reasoner);

			/* 3. Exporting query answers to {@code .csv} files. */
			reasoner.exportQueryAnswersToCsv(hasPartIDBXY, ExamplesUtils.OUTPUT_FOLDER + "hasPartIDBXYWithBlanks.csv",
					true);
			reasoner.exportQueryAnswersToCsv(hasPartIDBXY,
					ExamplesUtils.OUTPUT_FOLDER + "hasPartIDBXYWithoutBlanks.csv", false);

			final Constant redBike = makeConstant("redBike");
			final PositiveLiteral hasPartIDBRedBikeY = makePositiveLiteral(hasPartIDB, redBike, y);
			reasoner.exportQueryAnswersToCsv(hasPartIDBRedBikeY,
					ExamplesUtils.OUTPUT_FOLDER + "hasPartIDBRedBikeYWithBlanks.csv", true);
		}
	}

}
