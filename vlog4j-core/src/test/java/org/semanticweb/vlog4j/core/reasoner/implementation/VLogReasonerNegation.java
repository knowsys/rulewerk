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
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeNegativeLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeFact;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;

public class VLogReasonerNegation {

	@Test(expected = RuntimeException.class)
	public void testNotStratifiable()
			throws EdbIdbSeparationException, IncompatiblePredicateArityException, ReasonerStateException, IOException {

		final Variable x = makeVariable("x");
		final Variable y = makeVariable("y");

		final Literal pXY = makePositiveLiteral("P", x, y);
		final Literal notQXY = makeNegativeLiteral("Q", x, y);
		final PositiveLiteral qXY = makePositiveLiteral("Q", x, y);

		final Rule rule = makeRule(qXY, pXY, notQXY);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(rule);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
		}
	}

	@Test
	public void testStratifiable()
			throws EdbIdbSeparationException, IncompatiblePredicateArityException, ReasonerStateException, IOException {

		final Variable x = makeVariable("x");
		final Variable y = makeVariable("y");

		final Literal pXY = makePositiveLiteral("P", x, y);
		final Literal notQXY = makeNegativeLiteral("Q", x, y);
		final Literal notRXY = makeNegativeLiteral("R", x, y);
		final PositiveLiteral sXY = makePositiveLiteral("S", x, y);

		final Rule rule = makeRule(sXY, pXY, notQXY, notRXY);
		final Fact pCD = makeFact("P", Arrays.asList(makeConstant("c"), makeConstant("d")));
		final Constant e = makeConstant("e");
		final Constant f = makeConstant("f");
		final Fact pEF = makeFact("P", Arrays.asList(e, f));

		final Fact qCD = makeFact("Q", Arrays.asList(makeConstant("c"), makeConstant("d")));

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
	public void testInputNegation()
			throws EdbIdbSeparationException, IncompatiblePredicateArityException, ReasonerStateException, IOException {

		final Variable x = makeVariable("x");
		final Variable y = makeVariable("y");

		final Literal pXY = makePositiveLiteral("P", x, y);
		final Literal notQXY = makeNegativeLiteral("Q", x, y);
		final PositiveLiteral sXY = makePositiveLiteral("S", x, y);

		final Rule rule = makeRule(sXY, pXY, notQXY);
		final Fact pCD = makeFact("P", Arrays.asList(makeConstant("c"), makeConstant("d")));
		final Constant e = makeConstant("e");
		final Constant f = makeConstant("f");
		final Fact pEF = makeFact("P", Arrays.asList(e, f));

		final Fact qCD = makeFact("Q", Arrays.asList(makeConstant("c"), makeConstant("d")));

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
