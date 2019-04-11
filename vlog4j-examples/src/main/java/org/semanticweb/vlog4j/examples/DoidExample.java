package org.semanticweb.vlog4j.examples;

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
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.graal.GraalToVLog4JModelConverter;

import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * This example reasons about human diseases, based on information from the
 * Disease Ontology (DOID) and Wikidata. It illustrates how to load data from
 * different sources (RDF file, SPARQL), and reason about these input using
 * rules that are loaded from a file. The rules used here employ existential
 * quantifiers and stratified negation.
 * 
 * @author Markus Kroetzsch
 */
public class DoidExample {

	public static void main(String[] args)
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {

		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		try (final Reasoner reasoner = Reasoner.getInstance()) {

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

			/* Load rules from DLGP file */
			List<Rule> vlogRules = new ArrayList<>();
			try (final DlgpParser parser = new DlgpParser(
					new File(ExamplesUtils.INPUT_FOLDER + "/graal", "doid-example.dlgp"))) {
				while (parser.hasNext()) {
					final Object object = parser.next();
					if (object instanceof fr.lirmm.graphik.graal.api.core.Rule) {
						vlogRules.add(
								GraalToVLog4JModelConverter.convertRule((fr.lirmm.graphik.graal.api.core.Rule) object));
					}
				}
			}
			reasoner.addRules(vlogRules);

			// Adding a rule with a negated literal from java.
			// % humansWhoDiedOfNoncancer(X) :- deathCause(X,Y), diseaseId(Y,Z),
			// neg_cancerDisease(Z).
			// final Variable x = makeVariable("x");
			// final Variable y = makeVariable("y");
			// final Variable z = makeVariable("z");
			// final Atom humansWhoDiedOfNoncancerAtom = Expressions
			// .makeAtom(Expressions.makePredicate("humansWhoDiedOfNoncancer", 1), x);
			// final Atom deathCauseAtom =
			// Expressions.makeAtom(Expressions.makePredicate("deathCause", 2), x, y);
			// final Atom diseaseIdAtom =
			// Expressions.makeAtom(Expressions.makePredicate("diseaseId", 2), y, z);
			// final Atom notCancerDiseaseAtom =
			// Expressions.makeAtom(Expressions.makePredicate("neg_cancerDisease", 1),
			// z);
			//
			// reasoner.addRules(makeRule(makeConjunction(humansWhoDiedOfNoncancerAtom),
			// makeConjunction(deathCauseAtom, diseaseIdAtom, notCancerDiseaseAtom)));

			System.out.println("Rules configured:\n--");
			vlogRules.forEach(System.out::println);
			System.out.println("--");
			reasoner.load();
			System.out.println("Loading completed.");
			System.out.println("Starting reasoning (including SPARQL query answering) ...");
			reasoner.reason();
			System.out.println("... reasoning completed.");

			saveData(reasoner, "humansWhoDiedOfCancer", 1);
			saveData(reasoner, "humansWhoDiedOfNoncancer", 1);
			saveData(reasoner, "deathCause", 2);
			saveData(reasoner, "diseaseHierarchy", 2);
			saveData(reasoner, "cancerDisease", 1);
			saveData(reasoner, "recentDeaths", 1);

			// System.out.println("After materialisation:");
			// for (final GraalConjunctiveQueryToRule graalConjunctiveQueryToRule :
			// convertedConjunctiveQueries) {
			// ExamplesUtils.printOutQueryAnswers(graalConjunctiveQueryToRule.getQueryAtom(),
			// reasoner);
			// }

		}

	}

	public static void saveData(Reasoner reasoner, String predicateName, int numberOfVariables)
			throws ReasonerStateException, IOException {
		final List<Term> vars = new ArrayList<>();
		for (int i = 0; i < numberOfVariables; i++)
			vars.add(makeVariable("x" + i));
		final Predicate predicate = Expressions.makePredicate(predicateName, numberOfVariables);
		final Atom atom = Expressions.makeAtom(predicate, vars);
		String path = ExamplesUtils.OUTPUT_FOLDER + predicateName + ".csv";
		reasoner.exportQueryAnswersToCsv(atom, path, true);
	}
}
