package org.semanticweb.vlog4j.examples;

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

import java.io.File;
import java.io.IOException;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;

public class SkolemChaseExecutionFromToFile {

	public static void main(String[] args)
			throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {

		// 1. Instantiating entities and rules.
		final Predicate bicycleIDB = Expressions.makePredicate("BicycleIDB", 1);
		final Predicate bicycleEDB = Expressions.makePredicate("BicycleEDB", 1);
		final Predicate wheelIDB = Expressions.makePredicate("WheelIDB", 1);
		final Predicate wheelEDB = Expressions.makePredicate("WheelEDB", 1);
		final Predicate hasPartIDB = Expressions.makePredicate("HasPartIDB", 2);
		final Predicate hasPartEDB = Expressions.makePredicate("HasPartEDB", 2);
		final Predicate isPartOfIDB = Expressions.makePredicate("IsPartOfIDB", 2);
		final Predicate isPartOfEDB = Expressions.makePredicate("IsPartOfEDB", 2);
		final Variable x = Expressions.makeVariable("x");
		final Variable y = Expressions.makeVariable("y");

		// BicycleIDB(?x) :- BicycleEDB(?x) .
		final Atom bicycleIDBX = Expressions.makeAtom(bicycleIDB, x);
		final Atom bicycleEDBX = Expressions.makeAtom(bicycleEDB, x);
		final Rule rule1 = Expressions.makeRule(bicycleIDBX, bicycleEDBX);

		// WheelIDB(?x) :- WheelEDB(?x) .
		final Atom wheelIDBX = Expressions.makeAtom(wheelIDB, x);
		final Atom wheelEDBX = Expressions.makeAtom(wheelEDB, x);
		final Rule rule2 = Expressions.makeRule(wheelIDBX, wheelEDBX);

		// hasPartIDB(?x, ?y) :- hasPartEDB(?x, ?y) .
		final Atom hasPartIDBXY = Expressions.makeAtom(hasPartIDB, x, y);
		final Atom hasPartEDBXY = Expressions.makeAtom(hasPartEDB, x, y);
		final Rule rule3 = Expressions.makeRule(hasPartIDBXY, hasPartEDBXY);

		// isPartOfIDB(?x, ?y) :- isPartOfEDB(?x, ?y) .
		final Atom isPartOfIDBXY = Expressions.makeAtom(isPartOfIDB, x, y);
		final Atom isPartOfEDBXY = Expressions.makeAtom(isPartOfEDB, x, y);
		final Rule rule4 = Expressions.makeRule(isPartOfIDBXY, isPartOfEDBXY);

		// HasPartIDB(?x, !y), WheelIDB(!y) :- BicycleIDB(?x) .
		final Atom wheelIDBY = Expressions.makeAtom(wheelIDB, y);
		final Rule rule5 = Expressions.makeRule(Expressions.makeConjunction(hasPartIDBXY, wheelIDBY),
				Expressions.makeConjunction(bicycleIDBX));

		// IsPartOfIDB(?x, !y) :- WheelIDB(?x) .
		// Atom bycicleIDBY = Expressions.makeAtom(bicycleIDB, y);
		final Rule rule6 = Expressions.makeRule(Expressions.makeConjunction(isPartOfIDBXY),
				Expressions.makeConjunction(wheelIDBX));

		// IsPartOfIDB(?x, ?y) :- HasPartIDB(?y, ?x) .
		final Atom hasPartIDBYX = Expressions.makeAtom(hasPartIDB, y, x);
		final Rule rule7 = Expressions.makeRule(isPartOfIDBXY, hasPartIDBYX);

		// HasPartIDB(?x, ?y) :- IsPartOfIDB(?y, ?x) .
		final Atom isPartOfIDBYX = Expressions.makeAtom(isPartOfIDB, y, x);
		final Rule rule8 = Expressions.makeRule(hasPartIDBXY, isPartOfIDBYX);

		// 2. Loading, reasoning, and querying.
		final Reasoner reasoner = Reasoner.getInstance();
		reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);

		reasoner.addRules(rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8);

		final String filesDirPath = "src" + File.separator + "main" + File.separator + "data";
		final DataSource bicycleEDBPath = new CsvFileDataSource(
				new File(filesDirPath + File.separator + "bycicleEDB.csv"));
		reasoner.addFactsFromDataSource(bicycleEDB, bicycleEDBPath);
		final DataSource hasPartPath = new CsvFileDataSource(
				new File(filesDirPath + File.separator + "hasPartEDB.csv"));
		reasoner.addFactsFromDataSource(hasPartEDB, hasPartPath);
		final DataSource wheelPath = new CsvFileDataSource(new File(filesDirPath + File.separator + "wheelEDB.csv"));
		reasoner.addFactsFromDataSource(wheelEDB, wheelPath);

		reasoner.load();

		ExamplesUtil.printOutQueryAnswers(hasPartEDBXY, reasoner);

		reasoner.reason();

		ExamplesUtil.printOutQueryAnswers(hasPartIDBXY, reasoner);

		// 3. Exporting
		reasoner.exportQueryAnswersToCsv(hasPartIDBXY, filesDirPath + File.separator + "hasPartIDBXYWithBlanks.csv",
				true);

		reasoner.exportQueryAnswersToCsv(hasPartIDBXY, filesDirPath + File.separator + "hasPartIDBXYWithoutBlanks.csv",
				false);

		final Constant redBike = Expressions.makeConstant("redBike");
		final Atom hasPartIDBRedBikeY = Expressions.makeAtom(hasPartIDB, redBike, y);
		reasoner.exportQueryAnswersToCsv(hasPartIDBRedBikeY,
				filesDirPath + File.separator + "hasPartIDBRedBikeYWithBlanks.csv", true);

		// 4. Closing
		// Use try-with resources, or remember to call close() to free the reasoner
		// resources.
		reasoner.reason();

	}

}
