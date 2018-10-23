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
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.examples.ExamplesUtils;

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

		/* 1. Instantiating entities and rules. */
		final Predicate triplesEDB = makePredicate("triplesEDB", 3);
		final Predicate triplesIDB = makePredicate("triplesIDB", 3);

		final Constant hasPartPredicate = makeConstant("<http://an.example/hasPart>");
		final Constant isPartOfPredicate = makeConstant("<http://an.example/isPartOf>");
		final Constant hasTypePredicate = makeConstant("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
		final Constant bicycleObject = makeConstant("<http://an.example/bicycle>");
		final Constant wheelObject = makeConstant("<http://an.example/wheel>");

		final Variable x = makeVariable("x");
		final Variable s = makeVariable("s");
		final Variable p = makeVariable("p");
		final Variable o = makeVariable("o");

		/*
		 * We will write <~/someName> instead of <http://an.example/someName> and
		 * <~#someName> instead of <www.w3.org/1999/02/22-rdf-syntax-ns#someName>.
		 *
		 * triplesIDB(?s, ?p, ?o) :- triplesEDB(?s, ?p, ?o) .
		 */
		final Atom factIDB = makeAtom(triplesIDB, s, p, o);
		final Atom factEDB = makeAtom(triplesEDB, s, p, o);
		final Rule rule1 = makeRule(factIDB, factEDB);

		/*
		 * exists x. triplesIDB(?s, <~/hasPart>, !x), triplesIDB(!x, <~#type>,
		 * <~/wheel>) :- triplesIDB(?s, <~#type>, <~/bicycle>) .
		 */
		final Atom existsHasPartIDB = makeAtom(triplesIDB, s, hasPartPredicate, x);
		final Atom existsWheelIDB = makeAtom(triplesIDB, x, hasTypePredicate, wheelObject);
		final Atom bicycleIDB = makeAtom(triplesIDB, s, hasTypePredicate, bicycleObject);
		final Rule rule2 = makeRule(makeConjunction(existsHasPartIDB, existsWheelIDB), makeConjunction(bicycleIDB));

		/*
		 * exists x. triplesIDB(?s, <~/isPartOf>, !x) :- triplesIDB(?s, <~#type>,
		 * <~/wheel>) .
		 */
		final Atom existsIsPartOfIDB = makeAtom(triplesIDB, s, isPartOfPredicate, x);
		final Atom wheelIDB = makeAtom(triplesIDB, s, hasTypePredicate, wheelObject);
		final Rule rule3 = makeRule(makeConjunction(existsIsPartOfIDB), makeConjunction(wheelIDB));

		/*
		 * triplesIDB(?s, <~/isPartOf>, ?o) :- triplesIDB(?o, <~/hasPart>, ?s) .
		 */
		final Atom isPartOfIDB = makeAtom(triplesIDB, s, isPartOfPredicate, o);
		final Atom hasPartIDBReversed = makeAtom(triplesIDB, o, hasPartPredicate, s);
		final Rule rule4 = makeRule(isPartOfIDB, hasPartIDBReversed);

		/*
		 * triplesIDB(?s, <~/hasPart>, ?o) :- triplesIDB(?o, <~/isPartOf>, ?s) .
		 */
		final Atom hasPartIDB = makeAtom(triplesIDB, s, hasPartPredicate, o);
		final Atom isPartOfIDBReversed = makeAtom(triplesIDB, o, isPartOfPredicate, s);
		final Rule rule5 = makeRule(hasPartIDB, isPartOfIDBReversed);

		/*
		 * 2. Loading, reasoning, querying and exporting, while using try-with-resources
		 * to close the reasoner automatically.
		 */
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(rule1, rule2, rule3, rule4, rule5);

			/* Importing {@code .nt.gz} file as data source. */
			final DataSource triplesEDBDataSource = new RdfFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "ternaryBicycleEDB.nt.gz"));
			reasoner.addFactsFromDataSource(triplesEDB, triplesEDBDataSource);

			reasoner.load();
			System.out.println("Before materialisation:");
			/* triplesEDB(?s, <~/hasPart>, ?o) */
			final Atom hasPartEDB = makeAtom(triplesEDB, s, hasPartPredicate, o);
			ExamplesUtils.printOutQueryAnswers(hasPartEDB, reasoner);

			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();
			System.out.println("After materialisation:");
			ExamplesUtils.printOutQueryAnswers(hasPartIDB, reasoner);

			/* Exporting query answers to {@code .csv} files. */
			reasoner.exportQueryAnswersToCsv(hasPartIDB,
					ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithBlanks.csv", true);
			reasoner.exportQueryAnswersToCsv(hasPartIDB,
					ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithoutBlanks.csv", false);

			final Constant redBikeSubject = makeConstant("<http://an.example/redBike>");
			final Atom existsHasPartRedBike = makeAtom(triplesIDB, redBikeSubject, hasPartPredicate, x);
			reasoner.exportQueryAnswersToCsv(existsHasPartRedBike,
					ExamplesUtils.OUTPUT_FOLDER + "existsHasPartIDBRedBikeWithBlanks.csv", true);
		}
	}

}
