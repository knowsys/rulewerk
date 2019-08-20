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
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;

public class StratifiedNegationTest {

	@Test(expected = EdbIdbSeparationException.class)
	public void testNotStratifiableEdbIdbSeparation()
			throws EdbIdbSeparationException, IncompatiblePredicateArityException, ReasonerStateException, IOException {

		final Variable x = makeVariable("x");
		final Variable y = makeVariable("y");

		final Literal pXY = makePositiveLiteral("P", x, y);
		final Literal notQXY = makeNegativeLiteral("Q", x, y);
		final PositiveLiteral qXY = makePositiveLiteral("Q", x, y);

		final Rule rule = makeRule(qXY, pXY, notQXY);
		final PositiveLiteral fact = makePositiveLiteral("Q", makeConstant("c"), makeConstant("d"));

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(rule);
			reasoner.addFacts(fact);

			reasoner.load();
		}
	}
	
	@Test(expected = RuntimeException.class)
	public void testNotStratifiable()
			throws EdbIdbSeparationException, IncompatiblePredicateArityException, ReasonerStateException, IOException {

		final Variable x = makeVariable("x");
		final Variable y = makeVariable("y");

		final Literal pXY = makePositiveLiteral("P", x, y);
		final Literal notQXY = makeNegativeLiteral("Q", x, y);
		final PositiveLiteral qXY = makePositiveLiteral("Q", x, y);

		final Rule rule = makeRule(qXY, pXY, notQXY);
		final PositiveLiteral fact = makePositiveLiteral("P", makeConstant("c"), makeConstant("d"));

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(rule);
			reasoner.addFacts(fact);

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
		final PositiveLiteral pCD = makePositiveLiteral("P", makeConstant("c"), makeConstant("d"));
		final Constant e = makeConstant("e");
		final Constant f = makeConstant("f");
		final PositiveLiteral pEF = makePositiveLiteral("P", e, f);
		
		final PositiveLiteral qCD = makePositiveLiteral("Q", makeConstant("c"), makeConstant("d"));

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(rule);
			reasoner.addFacts(pCD, pEF, qCD);

			reasoner.load();
			reasoner.reason();
			
			try(QueryResultIterator result=reasoner.answerQuery(sXY, true)){
				assertTrue(result.hasNext());
				final QueryResult answer = result.next();
				assertEquals(answer.getTerms(),Arrays.asList(e, f));
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
		final PositiveLiteral pCD = makePositiveLiteral("P", makeConstant("c"), makeConstant("d"));
		final Constant e = makeConstant("e");
		final Constant f = makeConstant("f");
		final PositiveLiteral pEF = makePositiveLiteral("P", e, f);
		
		final PositiveLiteral qCD = makePositiveLiteral("Q", makeConstant("c"), makeConstant("d"));

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(rule);
			reasoner.addFacts(pCD, pEF, qCD);

			reasoner.load();
			reasoner.reason();
			
			try(QueryResultIterator result=reasoner.answerQuery(sXY, true)){
				assertTrue(result.hasNext());
				final QueryResult answer = result.next();
				assertEquals(answer.getTerms(),Arrays.asList(e, f));
				assertFalse(result.hasNext());
			}
		}
	}

}
