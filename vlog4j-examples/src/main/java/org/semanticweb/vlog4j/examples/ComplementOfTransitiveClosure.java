package org.semanticweb.vlog4j.examples;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.semanticweb.vlog4j.core.model.api.NegativeLiteral;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.graal.GraalToVLog4JModelConverter;

import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * This example computes the complement of a transitive closure of a graph.
 *
 * @author Markus Kroetzsch
 * @author Larry Gonzalez
 */
public class ComplementOfTransitiveClosure {

	public static void main(final String[] args)
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {

		ExamplesUtils.configureLogging();

		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		try (final Reasoner reasoner = Reasoner.getInstance()) {

			reasoner.setLogFile(ExamplesUtils.OUTPUT_FOLDER + "log.txt");
			reasoner.setLogLevel(LogLevel.DEBUG);

			/* Configure SPARQL data sources */
			final String neighbourCountries = "?country1 wdt:P31 wd:Q6256 . ?country2 wdt:P31 wd:Q6256 ."
					+ "?country1 wdt:P47 ?country2";
			final DataSource neighbourDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					"country1,country2", neighbourCountries);
			final Predicate neighbourPredicate = Expressions.makePredicate("neighbour", 2);
			reasoner.addFactsFromDataSource(neighbourPredicate, neighbourDataSource);

			/* Load rules from DLGP file */
			try (final DlgpParser parser = new DlgpParser(
					new File(ExamplesUtils.INPUT_FOLDER + "/graal", "neighbour-countries-example.dlgp"))) {
				while (parser.hasNext()) {
					final Object object = parser.next();
					if (object instanceof fr.lirmm.graphik.graal.api.core.Rule) {
						reasoner.addRules(
								GraalToVLog4JModelConverter.convertRule((fr.lirmm.graphik.graal.api.core.Rule) object));
					}
				}

				/* Create additional rules with negated literals */
				final Variable x = makeVariable("X");
				final Variable y = makeVariable("Y");

				// complementOfTransitiveNeighbour(X,Y):-
				// country(X),country(Y),~transitiveNeighbour(X,Y)
				final PositiveLiteral country1 = Expressions.makePositiveLiteral("country", x);
				final PositiveLiteral country2 = Expressions.makePositiveLiteral("country", y);
				final NegativeLiteral notTransitiveNeighbour = Expressions.makeNegativeLiteral("transitiveNeighbour", x,
						y);
				final PositiveLiteral complementOfTransitiveNeighbour = Expressions
						.makePositiveLiteral("complementOfTransitiveNeighbour", x, y);
				// we need to bind the variables in negative literals
				reasoner.addRules(
						Expressions.makeRule(Expressions.makePositiveConjunction(complementOfTransitiveNeighbour),
								Expressions.makeConjunction(country1, country2, notTransitiveNeighbour)));

				System.out.println("Rules configured:\n--");
				reasoner.getRules().forEach(System.out::println);
				System.out.println("--");
				reasoner.load();
				System.out.println("Loading completed.");
				reasoner.reason();
				System.out.println("... reasoning completed.");

				System.out.println(
						"Number of countries: " + ExamplesUtils.iteratorSize(reasoner.answerQuery(country1, true)));

				final QueryResultIterator answersComplementOfTransitiveNeighbour = reasoner
						.answerQuery(complementOfTransitiveNeighbour, true);
				System.out.println("Pair of countries s.t. are not transitively neighbours: "
						+ ExamplesUtils.iteratorSize(answersComplementOfTransitiveNeighbour));

				System.out.println("Done.");

			}
		}

	}

}
