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

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.File;
import java.io.IOException;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.examples.ExamplesUtils;

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

	public static void main(final String[] args) throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {

		/* 1. Instantiating entities and rules. */
		final Predicate bicycleIDB = makePredicate("BicycleIDB", 1);
		final Predicate bicycleEDB = makePredicate("BicycleEDB", 1);
		final Predicate wheelIDB = makePredicate("WheelIDB", 1);
		final Predicate wheelEDB = makePredicate("WheelEDB", 1);
		final Predicate hasPartIDB = makePredicate("HasPartIDB", 2);
		final Predicate hasPartEDB = makePredicate("HasPartEDB", 2);
		final Predicate isPartOfIDB = makePredicate("IsPartOfIDB", 2);
		final Predicate isPartOfEDB = makePredicate("IsPartOfEDB", 2);
		final Variable x = makeVariable("x");
		final Variable y = makeVariable("y");

		/*
		 * BicycleIDB(?x) :- BicycleEDB(?x) .
		 */
		final Atom bicycleIDBX = makeAtom(bicycleIDB, x);
		final Atom bicycleEDBX = makeAtom(bicycleEDB, x);
		final Rule rule1 = makeRule(bicycleIDBX, bicycleEDBX);

		/*
		 * WheelIDB(?x) :- WheelEDB(?x) .
		 */
		final Atom wheelIDBX = makeAtom(wheelIDB, x);
		final Atom wheelEDBX = makeAtom(wheelEDB, x);
		final Rule rule2 = makeRule(wheelIDBX, wheelEDBX);

		/*
		 * hasPartIDB(?x, ?y) :- hasPartEDB(?x, ?y) .
		 */
		final Atom hasPartIDBXY = makeAtom(hasPartIDB, x, y);
		final Atom hasPartEDBXY = makeAtom(hasPartEDB, x, y);
		final Rule rule3 = makeRule(hasPartIDBXY, hasPartEDBXY);

		/*
		 * isPartOfIDB(?x, ?y) :- isPartOfEDB(?x, ?y) .
		 */
		final Atom isPartOfIDBXY = makeAtom(isPartOfIDB, x, y);
		final Atom isPartOfEDBXY = makeAtom(isPartOfEDB, x, y);
		final Rule rule4 = makeRule(isPartOfIDBXY, isPartOfEDBXY);

		/*
		 * exists y. HasPartIDB(?x, !y), WheelIDB(!y) :- BicycleIDB(?x) .
		 */
		final Atom wheelIDBY = makeAtom(wheelIDB, y);
		final Rule rule5 = makeRule(makeConjunction(hasPartIDBXY, wheelIDBY), makeConjunction(bicycleIDBX));

		/*
		 * exists y. IsPartOfIDB(?x, !y) :- WheelIDB(?x) .
		 */
		final Rule rule6 = makeRule(makeConjunction(isPartOfIDBXY), makeConjunction(wheelIDBX));

		/* IsPartOfIDB(?x, ?y) :- HasPartIDB(?y, ?x) . */
		final Atom hasPartIDBYX = makeAtom(hasPartIDB, y, x);
		final Rule rule7 = makeRule(isPartOfIDBXY, hasPartIDBYX);

		/*
		 * HasPartIDB(?x, ?y) :- IsPartOfIDB(?y, ?x) .
		 */
		final Atom isPartOfIDBYX = makeAtom(isPartOfIDB, y, x);
		final Rule rule8 = makeRule(hasPartIDBXY, isPartOfIDBYX);

		/* 2. Loading, reasoning, and querying while using try-with-resources to close the reasoner automatically. */
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8);

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
			ExamplesUtils.printOutQueryAnswers(hasPartEDBXY, reasoner);

			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();
			System.out.println("After materialisation:");
			ExamplesUtils.printOutQueryAnswers(hasPartIDBXY, reasoner);

			/* 3. Exporting query answers to {@code .csv} files. */
			reasoner.exportQueryAnswersToCsv(hasPartIDBXY, ExamplesUtils.OUTPUT_FOLDER + "hasPartIDBXYWithBlanks.csv", true);
			reasoner.exportQueryAnswersToCsv(hasPartIDBXY, ExamplesUtils.OUTPUT_FOLDER + "hasPartIDBXYWithoutBlanks.csv",
					false);

			final Constant redBike = makeConstant("redBike");
			final Atom hasPartIDBRedBikeY = makeAtom(hasPartIDB, redBike, y);
			reasoner.exportQueryAnswersToCsv(hasPartIDBRedBikeY,
					ExamplesUtils.OUTPUT_FOLDER + "hasPartIDBRedBikeYWithBlanks.csv", true);
		}
	}

}
