package org.semanticweb.vlog4j.examples.doid;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.examples.ExamplesUtils;

public class DoidExample {
	public static void main(String[] args)
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {

		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		/* SPARQL queries */

		String auxString = "?human wdt:P31 wd:Q5 . ";
		auxString += "?human wdt:P1050 ?disease . ";
		auxString += "?disease wdt:P31 wd:Q12136 . ";
		auxString += "?disease wdt:P2888 ?diseaselinks . ";
		final String queryBody = auxString;

		// human humanLabel
		auxString = "";
		auxString += "?human wdt:P31 wd:Q5 .";
		auxString += "?human wdt:P570 ?dateofdeath . ";
		auxString += "?human rdfs:label ?humanLabel . ";
		auxString += "FILTER (lang(?humanLabel) = \"en\") . ";
		auxString += "FILTER (?dateofdeath > \"2018-01-01\"^^xsd:dateTime) . ";
		auxString += "FILTER (?dateofdeath < \"2019-01-01\"^^xsd:dateTime) . ";
		final String humanHumanLavelQuery = auxString;

		// human deadCause
		auxString = "";
		auxString += "?human wdt:P31 wd:Q5 . ";
		auxString += "?human wdt:P570 ?dateofdeath . ";
		auxString += "?human wdt:P509 ?deadCause . ";
		auxString += "FILTER (?dateofdeath > \"2018-01-01\"^^xsd:dateTime) . ";
		auxString += "FILTER (?dateofdeath < \"2019-01-01\"^^xsd:dateTime) . ";
		final String humanDeadCauseQuery = auxString;

		// deadCause doid
		auxString = "";
		auxString += "?human wdt:P31 wd:Q5 . ";
		auxString += "?human wdt:P570 ?dateofdeath . ";
		auxString += "?human wdt:P509 ?deadCause . ";
		auxString += "?deadCause wdt:P699 ?doid . ";
		auxString += "FILTER (?dateofdeath > \"2018-01-01\"^^xsd:dateTime) . ";
		auxString += "FILTER (?dateofdeath < \"2019-01-01\"^^xsd:dateTime) . ";
		final String deadCauseDoidQuery = auxString;

		/* Variables */
		final Variable humanVar = Expressions.makeVariable("human");
		final Variable humanLabelVar = Expressions.makeVariable("humanLabel");
		final Variable deadCauseVar = Expressions.makeVariable("deadCause");
		final Variable doidVar = Expressions.makeVariable("doid");

		/* Predicates */
		final Predicate dead = makePredicate("dead", 2); // human humanLabel
		final Predicate deadCause = makePredicate("deadcause", 2); // human deadCause
		final Predicate disease = makePredicate("disease", 2); // disease, doid

		/* query variables */
		final LinkedHashSet<Variable> humanHumanLavelQueryVariables = new LinkedHashSet<>(
				Arrays.asList(humanVar, humanLabelVar));

		final LinkedHashSet<Variable> humanDeadCauseQueryVariables = new LinkedHashSet<>(
				Arrays.asList(humanVar, deadCauseVar));

		final LinkedHashSet<Variable> deadCauseDoidQueryVariables = new LinkedHashSet<>(
				Arrays.asList(deadCauseVar, doidVar));

		/* sparql queries */
		final DataSource humanHumanLabelDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
				humanHumanLavelQueryVariables, humanHumanLavelQuery);

		final DataSource humanDeadCauseDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
				humanDeadCauseQueryVariables, humanDeadCauseQuery);

		final DataSource deadCauseDoidDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
				deadCauseDoidQueryVariables, deadCauseDoidQuery);

		final Predicate triplesEDB = makePredicate("triplesEDB", 3);
		final Predicate triplesIDB = makePredicate("triplesIDB", 3);
		final Predicate subClassOfPredicate = makePredicate("subClass", 2);
		final Predicate humanPredicate = makePredicate("human", 1);
		final Predicate livingHumansWithDiseases = makePredicate("livingHumansWithMedicalConditions", 3);
		final Constant hasPartPredicate = makeConstant("<https://example.org/hasPart>");
		final Constant isPartOfPredicate = makeConstant("<https://example.org/isPartOf>");
		final Constant hasTypePredicate = makeConstant("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
		final Constant subClassOf = makeConstant("<http://www.w3.org/2000/01/rdf-schema#subClassOf>");
		final Constant bicycleObject = makeConstant("<https://example.org/bicycle>");
		final Constant wheelObject = makeConstant("<https://example.org/wheel>");

		final Variable x = makeVariable("x");
		final Variable y = makeVariable("y");
		final Variable z = makeVariable("z");
		final Variable s = makeVariable("s");
		final Variable p = makeVariable("p");
		final Variable o = makeVariable("o");

		// final Variable humanVar = Expressions.makeVariable("human");
		final Variable diseaseVar = Expressions.makeVariable("disease");
		final Variable diseaseDoidVar = Expressions.makeVariable("diseaselinks");
		// final Variable deadCause = Expressions.makeVariable("deadCause");

		/*
		 * We will write <~/someName> instead of <https://example.org/someName> and
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

		final Atom SSubClassOfO = makeAtom(triplesIDB, s, subClassOf, o);
		final Atom subClass = makeAtom(subClassOfPredicate, s, o);
		final Rule rule6 = makeRule(subClass, SSubClassOfO);

		final Atom subClassXZ = makeAtom(subClassOfPredicate, x, z);
		final Atom subClassXY = makeAtom(subClassOfPredicate, x, y);
		final Atom subClassYZ = makeAtom(subClassOfPredicate, y, z);
		final Rule rule7 = makeRule(makeConjunction(subClassXZ), makeConjunction(subClassXY, subClassYZ));

		final Atom humansWithDeseasesAtom = makeAtom(livingHumansWithDiseases, humanVar, diseaseVar, diseaseDoidVar);

		final Atom humanAtom = makeAtom(humanPredicate, humanVar);
		final Rule rule8 = makeRule(humanAtom, humansWithDeseasesAtom);
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8);

			/* Importing {@code .nt.gz} file as data source. */
			final DataSource triplesEDBDataSource = new RdfFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "doid.nt.gz"));

			/* Importing from Sparql */

			final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
					Arrays.asList(humanVar, diseaseVar, diseaseDoidVar));

			final DataSource sparqlQueryResultDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					queryVariables, queryBody);

			reasoner.addFactsFromDataSource(triplesEDB, triplesEDBDataSource);
			reasoner.addFactsFromDataSource(livingHumansWithDiseases, sparqlQueryResultDataSource);

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
//			reasoner.exportQueryAnswersToCsv(hasPartIDB,
//					ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithBlanks.csv", true);
//			reasoner.exportQueryAnswersToCsv(hasPartIDB,
//					ExamplesUtils.OUTPUT_FOLDER + "ternaryHasPartIDBWithoutBlanks.csv", false);
//
//			// reasoner.exportQueryAnswersToCsv(factIDB, ExamplesUtils.OUTPUT_FOLDER +
//			// "factIDB.csv", false);
			reasoner.exportQueryAnswersToCsv(subClassXY, ExamplesUtils.OUTPUT_FOLDER + "closureSubClass.csv", false);
			reasoner.exportQueryAnswersToCsv(humanAtom, ExamplesUtils.OUTPUT_FOLDER + "humanWithDiseases.csv", false);
//			final Constant redBikeSubject = makeConstant("<https://example.org/redBike>");
//			final Atom existsHasPartRedBike = makeAtom(triplesIDB, redBikeSubject, hasPartPredicate, x);
//			reasoner.exportQueryAnswersToCsv(existsHasPartRedBike,
//					ExamplesUtils.OUTPUT_FOLDER + "existsHasPartIDBRedBikeWithBlanks.csv", true);
		}
	}
}
