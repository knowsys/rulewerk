package org.semanticweb.vlog4j.core.reasoner.implementation;

/*-
 * #%L
 * VLog4j Core Components
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.QueryResultIterator;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class VLogReasonerNegation {

	private final Variable x = Expressions.makeVariable("x");
	private final Variable y = Expressions.makeVariable("y");

	private final Constant c = Expressions.makeConstant("c");
	private final Constant d = Expressions.makeConstant("d");
	private final Constant e = Expressions.makeConstant("e");
	private final Constant f = Expressions.makeConstant("f");

	private final Literal pXY = Expressions.makePositiveLiteral("P", x, y);
	private final Literal notQXY = Expressions.makeNegativeLiteral("Q", x, y);

	private final Literal notRXY = Expressions.makeNegativeLiteral("R", x, y);
	private final PositiveLiteral sXY = Expressions.makePositiveLiteral("S", x, y);

	private final Fact pCD = Expressions.makeFact("P", Arrays.asList(c, d));
	private final Fact pEF = Expressions.makeFact("P", Arrays.asList(e, f));
	private final Fact qCD = Expressions.makeFact("Q", Arrays.asList(c, d));

	@Test(expected = RuntimeException.class)
	public void testNotStratifiable() throws IOException {

		final PositiveLiteral qXY = Expressions.makePositiveLiteral("Q", x, y);

		final Rule rule = Expressions.makeRule(qXY, pXY, notQXY);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(rule);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
		}
	}

	@Test
	public void testStratifiable() throws IOException {

		final Rule rule = Expressions.makeRule(sXY, pXY, notQXY, notRXY);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(rule, pCD, pEF, qCD);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();

			try (QueryResultIterator result = reasoner.answerQuery(sXY, true)) {
				assertTrue(result.hasNext());
				final QueryResult answer = result.next();
				assertEquals(answer.getTerms(), Arrays.asList(e, f));
				assertFalse(result.hasNext());
			}
		}
	}

	@Test
	public void testInputNegation() throws IOException {

		final Rule rule = Expressions.makeRule(sXY, pXY, notQXY);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(rule, pCD, pEF, qCD);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();

			try (QueryResultIterator result = reasoner.answerQuery(sXY, true)) {
				assertTrue(result.hasNext());
				final QueryResult answer = result.next();
				assertEquals(answer.getTerms(), Arrays.asList(e, f));
				assertFalse(result.hasNext());
			}
		}
	}

}
