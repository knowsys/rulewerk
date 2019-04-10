package org.semanticweb.vlog4j.examples.doid;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.semanticweb.vlog4j.core.model.api.Predicate;
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
		final String sparqlRecentDeaths = "?human wdt:P31 wd:Q5; wdt:P570 ?dateofdeath . \n"
				+ "FILTER (?dateofdeath > \"2018-01-01\"^^xsd:dateTime && ?dateofdeath < \"2019-01-01\"^^xsd:dateTime)";
		final String sparqlHumansWithDisease = "?human wdt:P31 wd:Q5; wdt:P1050 ?disease .\n"
				+ "?disease wdt:P31 wd:Q12136; wdt:P2888 ?diseaselinks .";
		final String sparqlRecentDeathsLabel = sparqlRecentDeaths + "?human rdfs:label ?humanLabel .\n"
				+ "FILTER (lang(?humanLabel) = \"en\")\n";
		final String sparqlRecentDeathsCause = sparqlRecentDeaths + "?human wdt:P509 ?deadCause . ";
		final String sparqlRecentDeathsDoid = sparqlRecentDeathsCause + "?deadCause wdt:P699 ?doid . ";

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
				humanHumanLavelQueryVariables, sparqlRecentDeathsLabel);

		final DataSource humanDeadCauseDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
				humanDeadCauseQueryVariables, sparqlRecentDeathsCause);

		final DataSource deadCauseDoidDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
				deadCauseDoidQueryVariables, sparqlRecentDeathsDoid);

		final Predicate triplesEDB = makePredicate("triplesEDB", 3);
		final Predicate triplesIDB = makePredicate("triplesIDB", 3);
		final Predicate subClassOfPredicate = makePredicate("subClass", 2);

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

		try (final Reasoner reasoner = Reasoner.getInstance()) {

			// TO DO: import rules from Graal
			// reasoner.addRules(rule1, rule6);

			/* Importing {@code .nt.gz} file as data source. */
			final DataSource triplesEDBDataSource = new RdfFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "doid.nt.gz"));

			/* Importing from Sparql */

			final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
					Arrays.asList(humanVar, diseaseVar, diseaseDoidVar));

			// TO DO: link other data sources
			final DataSource sparqlQueryResultDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					queryVariables, sparqlHumansWithDisease);

			reasoner.addFactsFromDataSource(triplesEDB, triplesEDBDataSource);
			// reasoner.addFactsFromDataSource(livingHumansWithDiseases,
			// sparqlQueryResultDataSource);

			reasoner.load();

			System.out.println("Before materialisation:");
			/* triplesEDB(?s, <~/hasPart>, ?o) */

			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();

			System.out.println("After materialisation:");

			// reasoner.exportQueryAnswersToCsv(subClassXY, ExamplesUtils.OUTPUT_FOLDER +
			// "closureSubClass.csv", false);
			// reasoner.exportQueryAnswersToCsv(humanAtom, ExamplesUtils.OUTPUT_FOLDER +
			// "humanWithDiseases.csv", false);
		}
	}
}
