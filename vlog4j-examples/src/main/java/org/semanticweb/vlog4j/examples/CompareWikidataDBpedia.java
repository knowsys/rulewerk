package org.semanticweb.vlog4j.examples;

import java.io.IOException;

import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

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

	public static void main(String[] args) throws ParsingException, IOException {
		ExamplesUtils.configureLogging();

		// Wikidata pattern: P69 is "educated at"; Q154804 is "University of Leipzig"
		String wikidataSparql = "?result wdt:P69 wd:Q154804 . " + sparqlGetWikiIriWikidata;
		// DBpedia pattern:
		String dbpediaSparql = "?result <http://dbpedia.org/ontology/almaMater> <http://dbpedia.org/resource/Leipzig_University> . "
				+ sparqlGetWikiIriDBpedia;

		// Configure the SPARQL data sources and some rules to analyse results:
		String rules = "" //
				+ "@prefix wdqs: <https://query.wikidata.org/> ." //
				+ "@prefix dbp: <https://dbpedia.org/> ." //
				+ "@source dbpResult(2) : sparql(dbp:sparql, \"result,enwikipage\", '''" + dbpediaSparql + "''') ." //
				+ "@source wdResult(2) : sparql(wdqs:sparql, \"result,enwikipage\", '''" + wikidataSparql + "''') ." //
				+ "% Rules:\n" //
				+ "inWd(?Wikipage) :- wdResult(?WdId,?Wikipage)." //
				+ "inDbp(?Wikipage) :- dbpResult(?DbpId,?Wikipage)." //
				+ "result(?Wikipage) :- inWd(?Wikipage)." //
				+ "result(?Wikipage) :- inDbp(?Wikipage)." //
				+ "match(?WdId,?DbpId) :- dbpResult(?DbpId,?Wikipage), wdResult(?WdId,?Wikipage)."
				+ "dbpOnly(?Wikipage) :- inDbp(?Wikipage), ~inWd(?Wikipage)."
				+ "wdpOnly(?WdId,?Wikipage) :- wdResult(?WdId,?Wikipage), ~inDbp(?Wikipage)." + ""; //

		final KnowledgeBase kb = RuleParser.parse(rules);

		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();

			int resultCount = ExamplesUtils.getQueryAnswerCount("result(?X)", reasoner);
			int wdCount = ExamplesUtils.getQueryAnswerCount("inWd(?X)", reasoner);
			int dbpCount = ExamplesUtils.getQueryAnswerCount("inDbp(?X)", reasoner);

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
