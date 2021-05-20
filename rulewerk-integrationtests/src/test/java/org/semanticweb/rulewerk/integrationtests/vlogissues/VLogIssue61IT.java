package org.semanticweb.rulewerk.integrationtests.vlogissues;

/*-
 * #%L
 * Rulewerk Integration Tests
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

public class VLogIssue61IT extends VLogIssue {

	boolean hasCorrectAnswers(QueryResultIterator answers) {
		int numAnswers = 0;
		boolean hasEqualNullsAnswer = false;

		while (answers.hasNext()) {
			++numAnswers;

			List<Term> terms = answers.next().getTerms();
			hasEqualNullsAnswer = hasEqualNullsAnswer || (terms.get(1).equals(terms.get(2)));
		}

		return hasEqualNullsAnswer && numAnswers <= 2;
	}

	@Test
	public void ruleset01_succeeds() throws ParsingException, IOException {
		try (final Reasoner reasoner = getReasonerWithKbFromResource("vlog/61-1.rls")) {
			reasoner.reason();

			PositiveLiteral query = RuleParser.parsePositiveLiteral("q(?X,?Y,?Z)");
			assertTrue(hasCorrectAnswers(reasoner.answerQuery(query, true)));
		}
	}

	@Test
	public void ruleset02_succeeds() throws ParsingException, IOException {
		try (final Reasoner reasoner = getReasonerWithKbFromResource("vlog/61-2.rls")) {
			reasoner.reason();

			PositiveLiteral query = RuleParser.parsePositiveLiteral("q(?X,?Y,?Z)");
			assertTrue(hasCorrectAnswers(reasoner.answerQuery(query, true)));
		}
	}
}
