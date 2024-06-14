package org.semanticweb.rulewerk.integrationtests.vlogissues;

/*-
 * #%L
 * Rulewerk Integration Tests
 * %%
 * Copyright (C) 2018 - 2022 Rulewerk Developers
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * https://github.com/karmaresearch/vlog/issues/55
 * 
 * https://github.com/karmaresearch/vlog/issues/73
 * 
 * RDF literals (constants) "foo"^^<http://www.w3.org/2001/XMLSchema#string> and
 * "foo" should be interpreted the same in VLog.
 * 
 * @author Irina Dragoste
 *
 */
public class VLogIssue73IT extends VLogIssue {

	// TODO join data from RDF with data from Rulewerk, for example
	
	// TODO add unit test for SPARQL data source
	
	@Test
	public void rule_rulewerk_succeeds() throws ParsingException, IOException {
		try (final Reasoner reasoner = this.getReasonerWithKbFromResource("vlog/73/73.rls")) {
			final Statement fact1 = RuleParser.parseFact("long(\"foo\"^^<http://www.w3.org/2001/XMLSchema#string>) .");
			final Statement fact2 = RuleParser.parseFact("short(\"foo\") .");
			reasoner.getKnowledgeBase().addStatement(fact1);
			reasoner.getKnowledgeBase().addStatement(fact2);
			reasoner.reason();

			testJoin(reasoner);
		}
	}

	private void testJoin(final Reasoner reasoner) throws ParsingException {
		try (final QueryResultIterator answerQuery = reasoner.answerQuery(RuleParser.parsePositiveLiteral("join(?x)"),
				true)) {
			assertEquals(Correctness.SOUND_AND_COMPLETE, answerQuery.getCorrectness());
			assertTrue(answerQuery.hasNext());
		}
	}

	@Test
	public void rule_csv_succeeds() throws ParsingException, IOException {
		try (final Reasoner reasoner = this.getReasonerWithKbFromResource("vlog/73/73.rls")) {
			final DataSourceDeclaration declaration1 = RuleParser.parseDataSourceDeclaration(
					"@source long[1]: load-csv(\"src/test/resources/vlogissues/vlog/73/long.csv\") .");
			final DataSourceDeclaration declaration2 = RuleParser.parseDataSourceDeclaration(
					"@source short[1]: load-csv(\"src/test/resources/vlogissues/vlog/73/short.csv\") .");

			reasoner.getKnowledgeBase().addStatement(declaration1);
			reasoner.getKnowledgeBase().addStatement(declaration2);
			reasoner.reason();

			testJoin(reasoner);
		}
	}

	@Test
	public void rule_nt_succeeds() throws ParsingException, IOException {
		try (final Reasoner reasoner = this.getReasonerWithKbFromResource("vlog/73/73-nt.rls")) {
			final DataSourceDeclaration declaration = RuleParser.parseDataSourceDeclaration(
					"@source triple[3]: load-rdf(\"src/test/resources/vlogissues/vlog/73/73.nt\") .");
			reasoner.getKnowledgeBase().addStatement(declaration);
			reasoner.reason();
		
			testJoin(reasoner);
		}
	}

}
