package org.semanticweb.rulewerk.reasoner.vlog;

/*
 * #%L
 * Rulewerk VLog Reasoner Support
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;

public class VLogRegressionTest {
	@Test
	public void test_issue_166() throws IOException {
		final KnowledgeBase knowledgeBase = new KnowledgeBase();

		final Predicate A = Expressions.makePredicate("A", 1);
		final Predicate B = Expressions.makePredicate("B", 1);
		final Predicate C = Expressions.makePredicate("C", 1);
		final Predicate R = Expressions.makePredicate("Rel", 1);

		final AbstractConstant star = Expressions.makeAbstractConstant("star");
		final AbstractConstant cy = Expressions.makeAbstractConstant("cy");
		final AbstractConstant r0 = Expressions.makeAbstractConstant("r0");
		final UniversalVariable x0 = Expressions.makeUniversalVariable("x0");
		final UniversalVariable x2 = Expressions.makeUniversalVariable("x2");

		knowledgeBase.addStatement(Expressions.makeRule(Expressions.makePositiveLiteral(B, x2),
				Expressions.makePositiveLiteral(A, x2)));
		knowledgeBase.addStatement(Expressions.makeFact(B, star));
		knowledgeBase.addStatement(Expressions.makeRule(Expressions.makePositiveLiteral(R, r0),
														Expressions.makePositiveLiteral(C, cy),
														Expressions.makePositiveLiteral(B, x0)));
		knowledgeBase.addStatement(Expressions.makeFact(C, cy));

		try (final Reasoner reasoner = new VLogReasoner(knowledgeBase)) {
			reasoner.reason();
			final QueryResultIterator result = reasoner.answerQuery(Expressions.makePositiveLiteral(R, x0), false);
			assertTrue(result.hasNext());
			final QueryResult terms = result.next();
			assertFalse(result.hasNext());
			final List<Term> expectedTerms = new ArrayList<Term>();
			expectedTerms.add(r0);
			assertEquals(expectedTerms, terms.getTerms());
		}
	}

	@Test
	public void test_vlog_issue_44() throws IOException {
		final KnowledgeBase knowledgeBase = new KnowledgeBase();

		final Predicate P = Expressions.makePredicate("P", 1);
		final Predicate Q = Expressions.makePredicate("Q", 1);
		final Predicate R = Expressions.makePredicate("R", 1);

		final AbstractConstant c = Expressions.makeAbstractConstant("c");
		final AbstractConstant d = Expressions.makeAbstractConstant("d");
		final UniversalVariable x = Expressions.makeUniversalVariable("x");

		knowledgeBase.addStatement(Expressions.makeFact(P, c));
		knowledgeBase.addStatement(Expressions.makeFact(Q, d));
		knowledgeBase.addStatement(Expressions.makeRule(Expressions.makePositiveLiteral(R, x),
														Expressions.makePositiveLiteral(P, x),
														Expressions.makeNegativeLiteral(Q, x)));

		try (final Reasoner reasoner = new VLogReasoner(knowledgeBase)) {
			reasoner.reason();
			final QueryResultIterator result = reasoner.answerQuery(Expressions.makePositiveLiteral(R, x), false);
			assertTrue(result.hasNext());
			final QueryResult terms = result.next();
			assertFalse(result.hasNext());
			final List<Term> expectedTerms = new ArrayList<Term>();
			expectedTerms.add(c);
			assertEquals(expectedTerms, terms.getTerms());
			assertFalse(result.hasNext());
		}
	}
}
