package org.semanticweb.vlog4j.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;

import org.semanticweb.vlog4j.parser.api.RuleParser;
import org.semanticweb.vlog4j.parser.implementation.PrologueException;
import org.semanticweb.vlog4j.parser.implementation.javacc.ParseException;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;

/**
 * This example reasons about human diseases, based on information from the
 * Disease Ontology (DOID) and Wikidata. It illustrates how to load data from
 * different sources (RDF file, SPARQL), and reason about these inputs using
 * rules that are loaded from a file. The rules used here employ existential
 * quantifiers and stratified negation.
 * 
 * @author Markus Kroetzsch
 * @author Larry Gonzalez
 */
public class DoidExampleLocalSyntax {

	public static void main(final String[] args) throws ReasonerStateException, IOException, EdbIdbSeparationException,
			IncompatiblePredicateArityException, ParseException, PrologueException {

		ExamplesUtils.configureLogging();

		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.setLogFile(ExamplesUtils.OUTPUT_FOLDER+"vlog.log");
			reasoner.setLogLevel(LogLevel.DEBUG);

			/* Configure RDF data source */
			final Predicate doidTriplePredicate = makePredicate("doidTriple", 3);
			final DataSource doidDataSource = new RdfFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "doid.nt.gz"));
			reasoner.addFactsFromDataSource(doidTriplePredicate, doidDataSource);

			/* Configure SPARQL data sources */
			final String sparqlHumansWithDisease = "?disease wdt:P699 ?doid .";
			// (wdt:P669 = "Disease Ontology ID")
			final DataSource diseasesDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					"disease,doid", sparqlHumansWithDisease);
			final Predicate diseaseIdPredicate = Expressions.makePredicate("diseaseId", 2);
			reasoner.addFactsFromDataSource(diseaseIdPredicate, diseasesDataSource);

			final String sparqlRecentDeaths = "?human wdt:P31 wd:Q5; wdt:P570 ?deathDate . FILTER (YEAR(?deathDate) = 2018)";
			// (wdt:P31 = "instance of"; wd:Q5 = "human", wdt:570 = "date of death")
			final DataSource recentDeathsDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint, "human",
					sparqlRecentDeaths);
			final Predicate recentDeathsPredicate = Expressions.makePredicate("recentDeaths", 1);
			reasoner.addFactsFromDataSource(recentDeathsPredicate, recentDeathsDataSource);

			final String sparqlRecentDeathsCause = sparqlRecentDeaths + "?human wdt:P509 ?causeOfDeath . ";
			// (wdt:P509 = "cause of death")
			final DataSource recentDeathsCauseDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					"human,causeOfDeath", sparqlRecentDeathsCause);
			final Predicate recentDeathsCausePredicate = Expressions.makePredicate("recentDeathsCause", 2);
			reasoner.addFactsFromDataSource(recentDeathsCausePredicate, recentDeathsCauseDataSource);

			RuleParser rp = new RuleParser(
					new FileInputStream(ExamplesUtils.INPUT_FOLDER + "/localSyntax/doid-example.txt"));
			for (Rule rule : rp.getRules())
				System.out.println(rule);
			rp.parse();

			reasoner.addFacts(rp.getFacts());
			reasoner.addRules(rp.getRules());

			System.out.println("Rules configured:\n--");
			reasoner.getRules().forEach(System.out::println);
			System.out.println("--");
			reasoner.load();
			System.out.println("Loading completed.");
			System.out.println("Starting reasoning (including SPARQL query answering) ...");
			reasoner.reason();
			System.out.println("... reasoning completed.\n--");

			System.out.println("Number of results in queries:");
			QueryResultIterator answers;
			for (PositiveLiteral l : rp.getQueries()) {
				answers = reasoner.answerQuery(l, true);
				System.out.print(l.toString());
				System.out.println(": " + ExamplesUtils.iteratorSize(answers));
			}
			System.out.println("Done.");

		}

	}

}