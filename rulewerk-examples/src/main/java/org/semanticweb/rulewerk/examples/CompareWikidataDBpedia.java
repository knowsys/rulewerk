package org.semanticweb.rulewerk.examples;

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

import java.io.IOException;

import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * This example shows how to integrate and compare the contents of two SPARQL
 * endpoints, in this case for Wikidata and DBpedia. We are asking both sources
 * for the same information (each using their terms to express it), and query
 * for related English Wikipedia article URLs as a key to integrate the data
 * over. For a fair comparison, we restrict to Wikidata entities that have a
 * related English Wikipedia page (others cannot be in English DBpedia in the
 * first place).
 *
 * The example query used asks for alumni of the University of Leipzig (one of
 * the oldest European universities).
 *
 * @author Markus Kroetzsch
 *
 */
public class CompareWikidataDBpedia {

	/**
	 * SPARQL pattern snippet to find an English Wikipedia page URL from a Wikidata
	 * entity ?result.
	 */
	static String sparqlGetWikiIriWikidata = "?enwikipage schema:about ?result ; "
			+ "schema:isPartOf <https://en.wikipedia.org/> . ";
	/**
	 * SPARQL pattern snippet to find an English Wikipedia page URL from a DBpedia
	 * entity ?result. Some string magic is needed to replace the outdated http
	 * protocol used in DBpedia's Wikidata page names by the current https.
	 */
	static String sparqlGetWikiIriDBpedia = "?result <http://xmlns.com/foaf/0.1/isPrimaryTopicOf> ?enwikipageHttp . "
			+ "BIND( IRI(CONCAT(\"https\",SUBSTR(str(?enwikipageHttp), 5))) AS ?enwikipage)";

	public static void main(final String[] args) throws ParsingException, IOException {
		ExamplesUtils.configureLogging();

		// Wikidata pattern: P69 is "educated at"; Q154804 is "University of Leipzig"
		final String wikidataSparql = "?result wdt:P69 wd:Q154804 . " + sparqlGetWikiIriWikidata;
		// DBpedia pattern:
		final String dbpediaSparql = "?result <http://dbpedia.org/ontology/almaMater> <http://dbpedia.org/resource/Leipzig_University> . "
				+ sparqlGetWikiIriDBpedia;

		// Configure the SPARQL data sources and some rules to analyse results:
		final String rules = "" //
				+ "@prefix wdqs: <https://query.wikidata.org/> .\n" //
				+ "@prefix dbp: <https://dbpedia.org/> .\n" //
				+ "@source dbpResult[2] : sparql(dbp:sparql, \"result,enwikipage\", '''" + dbpediaSparql + "''') .\n" //
				+ "@source wdResult[2] : sparql(wdqs:sparql, \"result,enwikipage\", '''" + wikidataSparql + "''') .\n" //
				+ "% Rules:\n" //
				+ "inWd(?Wikipage) :- wdResult(?WdId,?Wikipage).\n" //
				+ "inDbp(?Wikipage) :- dbpResult(?DbpId,?Wikipage).\n" //
				+ "result(?Wikipage) :- inWd(?Wikipage).\n" //
				+ "result(?Wikipage) :- inDbp(?Wikipage).\n" //
				+ "match(?WdId,?DbpId) :- dbpResult(?DbpId,?Wikipage), wdResult(?WdId,?Wikipage).\n"
				+ "dbpOnly(?Wikipage) :- inDbp(?Wikipage), ~inWd(?Wikipage).\n"
				+ "wdpOnly(?WdId,?Wikipage) :- wdResult(?WdId,?Wikipage), ~inDbp(?Wikipage).\n"; //

		System.out.println("Knowledge base used in this example:\n\n" + rules);

		final KnowledgeBase kb = RuleParser.parse(rules);

		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();

			final long resultCount = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("result(?X)"))
					.getCount();
			final long wdCount = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("inWd(?X)")).getCount();
			final long dbpCount = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("inDbp(?X)")).getCount();

			System.out.println("Found " + resultCount + " matching entities overall, of which " + wdCount
					+ " were in Wikidata and " + dbpCount + " were in DBPedia");

			System.out.println("We focus on results found in DBpedia only (usually the smaller set).");
			ExamplesUtils.printOutQueryAnswers("dbpOnly(?X)", reasoner);

			System.out.println("Note: some of these results might still be in Wikidata, due to:\n"
					+ "* recent Wikipedia article renamings that are not updated in DBpedia\n"
					+ "* failure to match Wikipedia URLs due to small differences in character encoding\n");
		}
	}

}
