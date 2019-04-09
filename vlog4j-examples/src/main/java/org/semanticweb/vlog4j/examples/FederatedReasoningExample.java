package org.semanticweb.vlog4j.examples;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.FileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.graal.GraalToVLog4JModelConverter;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

public class FederatedReasoningExample {

	public static void main(final String[] args) throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {

		@NonNull
		final File rdfFile = new File(ExamplesUtils.INPUT_FOLDER, "iswc-2017-complete.csv.gz");
		final FileDataSource rdfFileDataSource = new CsvFileDataSource(rdfFile);

		// Gets a country from an affiliation. Note that the parameter "a" should be bound.
		@NonNull
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		@NonNull
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(Arrays.asList(Expressions.makeVariable("a"), Expressions.makeVariable("bLabel")));
		@NonNull
		final String queryBody = "SERVICE wikibase:mwapi {"

				+ "  bd:serviceParam wikibase:api \"EntitySearch\" ."

				+ "  bd:serviceParam wikibase:endpoint \"www.wikidata.org\" ."

				+ "  bd:serviceParam mwapi:search ?a ."

				+ "  bd:serviceParam mwapi:language \"en\" ."

				+ "  ?c wikibase:apiOutputItem mwapi:item ."

				+ " }" + " ?c wdt:P31/wdt:P279* wd:Q43229 ."

				+ " ?c wdt:P17 ?b ." + " SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\". }";

		final SparqlQueryResultDataSource sparqlQueryResultDataSource = new SparqlQueryResultDataSource(endpoint, queryVariables, queryBody);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			@NonNull
			final Predicate predicateTE = Expressions.makePredicate("te", 3);
			reasoner.addFactsFromDataSource(predicateTE, rdfFileDataSource);

			@NonNull
			final Predicate predicateInCountry = Expressions.makePredicate("inCountry", 2);
			reasoner.addFactsFromDataSource(predicateInCountry, sparqlQueryResultDataSource);

			final List<Rule> graalRules = new ArrayList<>();
			try (final DlgpParser parser = new DlgpParser(new File(ExamplesUtils.INPUT_FOLDER, "rules.dlgp"))) {
				while (parser.hasNext()) {
					final Object object = parser.next();
					if (object instanceof Rule) {
						graalRules.add((Rule) object);
					}
				}
			}
			reasoner.addRules(GraalToVLog4JModelConverter.convertRules(graalRules));

			reasoner.load();
			reasoner.reason();

			// TODO query
			// TODO modify CSV file to contain lowercase inCountry predicate
		}

	}

}
