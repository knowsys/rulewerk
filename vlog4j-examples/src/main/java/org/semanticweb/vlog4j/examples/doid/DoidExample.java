package org.semanticweb.vlog4j.examples.doid;

/*-
 * #%L
 * VLog4j Examples
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.graal.GraalToVLog4JModelConverter;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

public class DoidExample {
	public static void main(String[] args)
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {

		/* Configure data sources */

		/* SPARQL queries */
		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		final String sparqlHumansWithDisease = "?disease wdt:P699 ?doid .";
		final DataSource diseasesDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint, "disease,doid",
				sparqlHumansWithDisease);

		final String sparqlRecentDeaths = "?human wdt:P31 wd:Q5; wdt:P570 ?dateofdeath . \n"
				+ "FILTER (?dateofdeath > \"2018-01-01\"^^xsd:dateTime && ?dateofdeath < \"2019-01-01\"^^xsd:dateTime)";

		final String sparqlRecentDeathsLabel = sparqlRecentDeaths + "?human rdfs:label ?humanLabel .\n"
				+ "FILTER (lang(?humanLabel) = \"en\")\n";
		final DataSource recentDeathsLabelDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
				"human,humanLabel", sparqlRecentDeathsLabel);

		final String sparqlRecentDeathsCause = sparqlRecentDeaths + "?human wdt:P509 ?causeOfDeath . ";
		final DataSource recentDeathsCauseDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
				"human,causeOfDeath", sparqlRecentDeathsCause);

//		final String sparqlRecentDeathsDoid = sparqlRecentDeathsCause + "?causeOfDeath wdt:P699 ?doid . ";
//		final DataSource recentDeathsDoidDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
//				"human,causeOfDeath,doid", sparqlRecentDeathsDoid);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			// final Predicate predicateTE = Expressions.makePredicate("te", 3);
			// reasoner.addFactsFromDataSource(predicateTE, rdfFileDataSource);

			final Predicate diseaseIdPredicate = Expressions.makePredicate("diseaseId", 2);
			reasoner.addFactsFromDataSource(diseaseIdPredicate, diseasesDataSource);
			final Predicate recentDeathsLabelPredicate = Expressions.makePredicate("recentDeathsLabel", 2);
			reasoner.addFactsFromDataSource(recentDeathsLabelPredicate, recentDeathsLabelDataSource);
			final Predicate recentDeathsCausePredicate = Expressions.makePredicate("recentDeathsCause", 2);
			reasoner.addFactsFromDataSource(recentDeathsCausePredicate, recentDeathsCauseDataSource);
//			final Predicate recentDeathsDoidPredicate = Expressions.makePredicate("recentDeathsDoid", 3);
//			reasoner.addFactsFromDataSource(recentDeathsDoidPredicate, recentDeathsDoidDataSource);

			final Predicate doidTriplePredicate = makePredicate("doidTriple", 3);
			final DataSource doidDataSource = new RdfFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "doid.nt.gz"));
			reasoner.addFactsFromDataSource(doidTriplePredicate, doidDataSource);

			final List<Rule> graalRules = new ArrayList<>();
			// final List<ConjunctiveQuery> graalConjunctiveQueries = new ArrayList<>();

			try (final DlgpParser parser = new DlgpParser(
					new File(ExamplesUtils.INPUT_FOLDER + "/graal", "doid-example.dlgp"))) {
				while (parser.hasNext()) {
					final Object object = parser.next();
					if (object instanceof Rule) {
						graalRules.add((Rule) object);
					} // else if (object instanceof ConjunctiveQuery) {
						// graalConjunctiveQueries.add((ConjunctiveQuery) object);
						// }
				}
			}

			/* to query the materialization */
//			final List<GraalConjunctiveQueryToRule> convertedConjunctiveQueries = new ArrayList<>();
//			for (final ConjunctiveQuery conjunctiveQuery : graalConjunctiveQueries) {
//				final String queryUniqueId = "query" + convertedConjunctiveQueries.size();
//				convertedConjunctiveQueries
//						.add(GraalToVLog4JModelConverter.convertQuery(queryUniqueId, conjunctiveQuery));
//			}

			final Set<Atom> atoms = new HashSet<>();
			List<org.semanticweb.vlog4j.core.model.api.Rule> vlogRules = GraalToVLog4JModelConverter
					.convertRules(graalRules);

			for (org.semanticweb.vlog4j.core.model.api.Rule rule : vlogRules) {
				atoms.addAll(rule.getHead().getAtoms());
				atoms.addAll(rule.getBody().getAtoms());
			}

			reasoner.addRules(vlogRules);

			reasoner.load();
			System.out.println("Load completed");
			System.out.println(vlogRules);
			reasoner.reason();
			System.out.println("Reasoning completed");

//			System.out.println("After materialisation:");
//			for (final GraalConjunctiveQueryToRule graalConjunctiveQueryToRule : convertedConjunctiveQueries) {
//				ExamplesUtils.printOutQueryAnswers(graalConjunctiveQueryToRule.getQueryAtom(), reasoner);
//			}

			for (Atom atom : atoms) {
				String filepath = ExamplesUtils.OUTPUT_FOLDER + atom.getPredicate().getName() + ".csv";
				System.out.println(filepath);
				reasoner.exportQueryAnswersToCsv(atom, filepath, true);

			}
			// TODO query
		}

	}
}
