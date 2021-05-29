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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * https://github.com/karmaresearch/vlog/issues/66
 * 
 * @author Irina Dragoste
 *
 */
public class VLogIssue66IT extends VLogIssue {

	@Test
	public void ruleset_succeeds() throws IOException, ParsingException {
		try (final Reasoner reasoner = this.getReasonerWithKbFromResource("vlog/66.rls")) {

			reasoner.reason();

			this.testCorrectness(reasoner);
		}
	}



	private void testCorrectness(final Reasoner reasoner) throws ParsingException {
		final PositiveLiteral query = RuleParser.parsePositiveLiteral(" R(a)");
		final QueryResultIterator answerQuery = reasoner.answerQuery(query, true);
		assertEquals(Correctness.SOUND_AND_COMPLETE, answerQuery.getCorrectness());
		assertTrue(answerQuery.hasNext());
		final QueryResult result = answerQuery.next();
		assertEquals(Arrays.asList(new AbstractConstantImpl("a")), result.getTerms());

		assertFalse(answerQuery.hasNext());
	}
}
