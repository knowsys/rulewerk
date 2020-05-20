package org.semanticweb.rulewerk.examples.graal;

/*-
 * #%L
 * Rulewerk Examples
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
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
import java.net.URL;

import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.NegativeLiteral;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.examples.DoidExample;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.graal.GraalToRulewerkModelConverter;

import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * This example is a variant of {@link DoidExample} using Graal. It reasons
 * about human diseases, based on information from the Disease Ontology (DOID)
 * and Wikidata. It illustrates how to load data from different sources (RDF
 * file, SPARQL), and reason about these inputs using rules that are loaded from
 * a file in DLGP syntax. Since DLGP does not support negation, an additional
 * rule with stratified negation is added through custom Java code.
 *
 * @author Markus Kroetzsch
 * @author Larry Gonzalez
 */
public class DoidExampleGraal {

	public static void main(final String[] args) throws IOException {

		ExamplesUtils.configureLogging();

		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		final KnowledgeBase kb = new KnowledgeBase();

		try (final Reasoner reasoner = new VLogReasoner(kb)) {

			/* Configure RDF data source */
			final Predicate doidTriplePredicate = Expressions.makePredicate("doidTriple", 3);
			final DataSource doidDataSource = new RdfFileDataSource(ExamplesUtils.INPUT_FOLDER + "doid.nt.gz");
			kb.addStatement(new DataSourceDeclarationImpl(doidTriplePredicate, doidDataSource));

			/* Configure SPARQL data sources */
			final String sparqlHumansWithDisease = "?disease wdt:P699 ?doid .";
			// (wdt:P669 = "Disease Ontology ID")
			final DataSource diseasesDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					"disease,doid", sparqlHumansWithDisease);
			final Predicate diseaseIdPredicate = Expressions.makePredicate("diseaseId", 2);
			kb.addStatement(new DataSourceDeclarationImpl(diseaseIdPredicate, diseasesDataSource));

			final String sparqlRecentDeaths = "?human wdt:P31 wd:Q5; wdt:P570 ?deathDate . FILTER (YEAR(?deathDate) = 2018)";
			// (wdt:P31 = "instance of"; wd:Q5 = "human", wdt:570 = "date of death")
			final DataSource recentDeathsDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint, "human",
					sparqlRecentDeaths);
			final Predicate recentDeathsPredicate = Expressions.makePredicate("recentDeaths", 1);
			kb.addStatement(new DataSourceDeclarationImpl(recentDeathsPredicate, recentDeathsDataSource));

			final String sparqlRecentDeathsCause = sparqlRecentDeaths + "?human wdt:P509 ?causeOfDeath . ";
			// (wdt:P509 = "cause of death")
			final DataSource recentDeathsCauseDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					"human,causeOfDeath", sparqlRecentDeathsCause);
			final Predicate recentDeathsCausePredicate = Expressions.makePredicate("recentDeathsCause", 2);
			kb.addStatement(new DataSourceDeclarationImpl(recentDeathsCausePredicate, recentDeathsCauseDataSource));

			/* Load rules from DLGP file */
			try (final DlgpParser parser = new DlgpParser(
					new File(ExamplesUtils.INPUT_FOLDER + "/graal", "doid-example.dlgp"))) {
				while (parser.hasNext()) {
					final Object object = parser.next();
					if (object instanceof fr.lirmm.graphik.graal.api.core.Rule) {
						kb.addStatement(GraalToRulewerkModelConverter
								.convertRule((fr.lirmm.graphik.graal.api.core.Rule) object));
					}
				}
			}

			/* Create additional rules with negated literals */
			final Variable x = Expressions.makeUniversalVariable("X");
			final Variable y = Expressions.makeUniversalVariable("Y");
			final Variable z = Expressions.makeUniversalVariable("Z");
			// humansWhoDiedOfNoncancer(X):-deathCause(X,Y),diseaseId(Y,Z),~cancerDisease(Z)
			final NegativeLiteral notCancerDisease = Expressions.makeNegativeLiteral("cancerDisease", z);
			final PositiveLiteral diseaseId = Expressions.makePositiveLiteral("diseaseId", y, z);
			final PositiveLiteral deathCause = Expressions.makePositiveLiteral("deathCause", x, y);
			final PositiveLiteral humansWhoDiedOfNoncancer = Expressions.makePositiveLiteral("humansWhoDiedOfNoncancer",
					x);
			kb.addStatement(Expressions.makeRule(Expressions.makePositiveConjunction(humansWhoDiedOfNoncancer),
					Expressions.makeConjunction(deathCause, diseaseId, notCancerDisease)));
			// humansWhoDiedOfNoncancer(X) :- deathCause(X,Y), ~hasDoid(Y)
			final NegativeLiteral hasNotDoid = Expressions.makeNegativeLiteral("hasDoid", y);
			kb.addStatement(Expressions.makeRule(Expressions.makePositiveConjunction(humansWhoDiedOfNoncancer),
					Expressions.makeConjunction(deathCause, hasNotDoid)));

			System.out.println("Rules configured:\n--");
			kb.getRules().forEach(System.out::println);
			System.out.println("--");
			System.out.println("Starting reasoning (including SPARQL query answering) ...");
			reasoner.reason();
			System.out.println("... reasoning completed.");

			final PositiveLiteral humansWhoDiedOfCancer = Expressions.makePositiveLiteral("humansWhoDiedOfCancer", x);
			System.out.println("Humans in Wikidata who died in 2018 due to cancer: "
					+ reasoner.countQueryAnswers(humansWhoDiedOfCancer).getCount());

			System.out.println("Humans in Wikidata who died in 2018 due to some other cause: "
					+ reasoner.countQueryAnswers(humansWhoDiedOfNoncancer).getCount());
			System.out.println("Done.");
		}

	}

}
