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

import java.io.IOException;
import java.net.URL;

import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;

public class PoliticianExample {
	// occupation (P106) politician (Q82955): 575705 results in 3587 ms
	// #wdt:P106 = occupation
	// #wd:Q82955 = politician
	//
	// SELECT ?item ?itemLabel
	// WHERE
	// {
	// ?item wdt:P106 wd:Q82955.
	// SERVICE wikibase:label { bd:serviceParam wikibase:language
	// "[AUTO_LANGUAGE],en". }
	// }
	//

	// "position held" (Property:P39)

	// "field of work" (Property:P101)

	// spouse (P26)
	// the subject has the object as their spouse (husband, wife, partner, etc.).
	// Use "partner" (P451) for non-married companions

	// #wdt:P106 = occupation
	// #wd:Q82955 = politician
	//
	// SELECT ?politician ?spouse_of_politician
	// WHERE {
	// ?politician wdt:P106 wd:Q82955.
	// ?politician wdt:P26 ?spouse_of_politician.
	//
	// }
	// 14130 results in 4606 ms

	// partner (P451)
	// Jump to navigationJump to search
	// Someone in a relationship without being married. Use "spouse" for married
	// couples.

	//
	// #wdt:P106 = occupation
	// #wd:Q82955 = politician
	// #wdt:P451 = partner
	//
	// SELECT ?politician ?partner_of_politician
	// WHERE {
	// ?politician wdt:P106 wd:Q82955.
	// ?politician wdt:P451 ?partner_of_politician.
	// }
	// 558

	// 1744 answers (direct relative)
	// #wdt:P106 = occupation
	// #wd:Q82955 = politician
	// #wd:P1038 = relative
	//
	// SELECT ?politician ?relative_of_politician
	// WHERE {
	// ?politician wdt:P106 wd:Q82955.
	// ?politician wdt:P1038 ?relative_of_politician.
	// ?relative_of_politician wdt:P106 wd:Q82955.
	// }

	//
	// #wd:Q484876 = CEO
	// #wdt:P106 = OCCUPATION
	// #wd:Q82955 = politician
	//
	// SELECT ?politician ?CEO_relative_of_politician
	// WHERE {
	// ?politician wdt:P106 wd:Q82955.
	// ?politician wdt:P1038 ?CEO_relative_of_politician.
	// # ?CEO_relative_of_politician wdt:P106 wd:Q82955.
	// ?CEO_relative_of_politician wdt:P106 wd:Q484876.
	// }

	// 1744 answers (direct relative)
	// #wdt:P106 = occupation
	// #wd:Q82955 = politician
	// #wd:P1038 = relative
	//
	// SELECT ?politician ?relative_of_politician
	// WHERE {
	// ?politician wdt:P106 wd:Q82955.
	// ?politician wdt:P1038 ?relative_of_politician.
	// ?relative_of_politician wdt:P106 wd:Q82955.
	// }

	public static void main(final String[] args) throws IncompatiblePredicateArityException, IOException {

		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		final String politician = "politician";
		final String relative = "relative_of_politician";
		final DataSource ds = new SparqlQueryResultDataSource(wikidataSparqlEndpoint, relative + "," + politician,
				"	SELECT ?politician ?relative_of_politician \n" + "	WHERE 	{\n"
						+ "      ?politician wdt:P106 wd:Q82955.\n"
						+ "      ?politician wdt:P1038 ?relative_of_politician.\n" + "	}");
		final KnowledgeBase kb = new KnowledgeBase();
		final Predicate relativeOfPolitician = Expressions.makePredicate("relativeOfPolitician", 2);
		kb.addStatement(new DataSourceDeclarationImpl(relativeOfPolitician, ds));

		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			final PositiveLiteral relativeOfPoliticianPL = Expressions.makePositiveLiteral(relativeOfPolitician,
					Expressions.makeVariable(relative), Expressions.makeVariable(politician));
			final QueryResultIterator answerQuery = reasoner.answerQuery(relativeOfPoliticianPL, false);
			answerQuery.forEachRemaining(System.out::println);

		}
	}

}
