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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

public class VLogIssue69IT extends VLogIssue {

	@Test
	public void ruleset_succeeds() throws ParsingException, IOException {
		try (final Reasoner reasoner = getReasonerWithKbFromResource("vlog/69.rls")) {
			reasoner.reason();

			PositiveLiteral query1 = RuleParser.parsePositiveLiteral("prec(?X,?Y)");
			assertEquals(2, reasoner.countQueryAnswers(query1, true).getCount());

			PositiveLiteral query2 = RuleParser.parsePositiveLiteral("conc(?X,?Y)");
			assertEquals(4, reasoner.countQueryAnswers(query2, true).getCount());
		}
	}
}
