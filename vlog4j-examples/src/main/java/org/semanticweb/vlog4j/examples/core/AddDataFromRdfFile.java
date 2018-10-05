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
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.examples.ExamplesUtils;

/**
 * This example is an adaptation of {@link AddDataFromCsvFile}. It shows how
 * facts can be imported from {@code .nt.gz} files, and also how query answers
 * can be exported to {@code .csv} files. <i>Note that you can also import from
 * {@code .nt} files.</i>
 *
 * For importing, an {@link RdfFileDataSource} that contains a path to the
 * corresponding {@code .nt.gz} file must be created. An {@code .nt} file
 * contains facts over one or more predicates in the N-Triples format, while an
 * {@code .nt.gz} file is the gzipped version of such an {@code .nt} file. For
 * exporting, a path to the output {@code .csv} file must be specified.
 *
 * @author Christian Lewe
 *
 */
public class AddDataFromRdfFile {

	public static void main(final String[] args)
			throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {

		/* 1. Instantiating entities and rules. */
		final Predicate triplesEDB = Expressions.makePredicate("triplesEDB", 3);
		final Predicate triplesIDB = Expressions.makePredicate("triplesIDB", 3);

		final Constant bicyclePredicate = Expressions.makeConstant("<http://an.example/bicycle>");
		final Constant wheelPredicate = Expressions.makeConstant("<http://an.example/wheel>");
		final Constant hasPartPredicate = Expressions.makeConstant("<http://an.example/hasPart>");
		final Constant isPartOfPredicate = Expressions.makeConstant("<http://an.example/isPartOf>");
		final Constant emptyObject = Expressions.makeConstant("<http://an.example/empty>");

		final Variable x = Expressions.makeVariable("x");
		final Variable s = Expressions.makeVariable("s");
		final Variable o = Expressions.makeVariable("o");

		/*
		 * We will write '<~/someName>' instead of <http://an.example/someName>.
		 *
		 * triplesIDB(?s, <~/bicycle>, ?o) :- triplesEDB(?s, <~/bicycle>, ?o) .
		 */
		final Atom bicycleIDB = Expressions.makeAtom(triplesIDB, s, bicyclePredicate, o);
		final Atom bicycleEDB = Expressions.makeAtom(triplesEDB, s, bicyclePredicate, o);
		final Rule rule1 = Expressions.makeRule(bicycleIDB, bicycleEDB);

		/*
		 * triplesIDB(?s, <~/wheel>, ?o) :- triplesEDB(?s, <~/wheel>, ?o) .
		 */
		final Atom wheelIDB = Expressions.makeAtom(triplesIDB, s, wheelPredicate, o);
		final Atom wheelEDB = Expressions.makeAtom(triplesEDB, s, wheelPredicate, o);
		final Rule rule2 = Expressions.makeRule(wheelIDB, wheelEDB);

		/*
		 * triplesIDB(?s, <~/hasPart>, ?o) :- triplesEDB(?s, <~/hasPart>, ?o) .
		 */
		final Atom hasPartIDB = Expressions.makeAtom(triplesIDB, s, hasPartPredicate, o);
		final Atom hasPartEDB = Expressions.makeAtom(triplesEDB, s, hasPartPredicate, o);
		final Rule rule3 = Expressions.makeRule(hasPartIDB, hasPartEDB);

		/*
		 * triplesIDB(?s, <~/isPartOf>, ?o) :- triplesEDB(?s, <~/isPartOf>, ?o) .
		 */
		final Atom isPartOfIDB = Expressions.makeAtom(triplesIDB, s, isPartOfPredicate, o);
		final Atom isPartOfEDB = Expressions.makeAtom(triplesEDB, s, isPartOfPredicate, o);
		final Rule rule4 = Expressions.makeRule(isPartOfIDB, isPartOfEDB);

		/*
		 * exists x. triplesIDB(?s, <~/hasPartIDB>, !x), triplesIDB(!x, <~/wheelIDB>,
		 * <~/empty>) :- triplesIDB(?s, <~/bicycleIDB>, ?o) .
		 */
		final Atom existsHasPartIDB = Expressions.makeAtom(triplesIDB, s, hasPartPredicate, x);
		final Atom existsWheelIDB = Expressions.makeAtom(triplesIDB, x, wheelPredicate, emptyObject);
		final Rule rule5 = Expressions.makeRule(Expressions.makeConjunction(existsHasPartIDB, existsWheelIDB),
				Expressions.makeConjunction(bicycleIDB));

		/*
		 * exists x. triplesIDB(?s, <~/isPartOfIDB>, !x) :- triplesIDB(?s, <~/wheelIDB>,
		 * ?o) .
		 */
		final Atom existsIsPartOfIDB = Expressions.makeAtom(triplesIDB, s, isPartOfPredicate, x);
		final Rule rule6 = Expressions.makeRule(Expressions.makeConjunction(existsIsPartOfIDB),
				Expressions.makeConjunction(wheelIDB));

		/*
		 * triplesIDB(?s, <~/isPartOfIDB>, ?o) :- triplesIDB(?o, <~/hasPartIDB>, ?s) .
		 */
		final Atom hasPartIDBReversed = Expressions.makeAtom(triplesIDB, o, hasPartPredicate, s);
		final Rule rule7 = Expressions.makeRule(isPartOfIDB, hasPartIDBReversed);

		/*
		 * triplesIDB(?s, <~/hasPartIDB>, ?o) :- triplesIDB(?o, <~/isPartOfIDB>, ?s) .
		 */
		final Atom isPartOfIDBReversed = Expressions.makeAtom(triplesIDB, o, isPartOfPredicate, s);
		final Rule rule8 = Expressions.makeRule(hasPartIDB, isPartOfIDBReversed);

		/* 2. Loading, reasoning, and querying. */
		final Reasoner reasoner = Reasoner.getInstance();
		reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
		reasoner.addRules(rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8);

		/* Importing {@code .nt} file as data source. */
		final DataSource triplesEDBPath = new RdfFileDataSource(
				new File(ExamplesUtils.INPUT_FOLDER + "ternaryBicycleEDB.nt"));
		reasoner.addFactsFromDataSource(triplesEDB, triplesEDBPath);

		reasoner.load();
		System.out.println("Before materialisation:");
		ExamplesUtils.printOutQueryAnswers(hasPartEDB, reasoner);

		reasoner.reason();
		System.out.println("After materialisation:");
		ExamplesUtils.printOutQueryAnswers(hasPartIDB, reasoner);

		/* 3. Exporting query answers to {@code .csv} files. */
		reasoner.exportQueryAnswersToCsv(hasPartIDB, ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithBlanks.csv",
				true);
		reasoner.exportQueryAnswersToCsv(hasPartIDB, ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithoutBlanks.csv",
				false);

		final Constant redBikeSubject = Expressions.makeConstant("<http://an.example/redBike>");
		final Atom existsHasPartRedBike = Expressions.makeAtom(triplesIDB, redBikeSubject, hasPartPredicate, x);
		reasoner.exportQueryAnswersToCsv(existsHasPartRedBike,
				ExamplesUtils.OUTPUT_FOLDER + "existsHasPartIDBRedBikeWithBlanks.csv", true);

		/*
		 * 4. Closing. Use try-with resources, or remember to call {@code close()} to
		 * free the reasoner resources.
		 */
		reasoner.close();
	}

}
