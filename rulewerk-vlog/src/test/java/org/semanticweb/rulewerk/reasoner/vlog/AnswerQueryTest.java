package org.semanticweb.rulewerk.reasoner.vlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/*-
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.Algorithm;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.RuleRewriteStrategy;

public class AnswerQueryTest {

	@Test
	public void testEDBQuerySameConstantSubstitutesSameVariableName() throws IOException {
		final String predicate = "p";
		final Constant constantC = Expressions.makeAbstractConstant("c");
		final Constant constantD = Expressions.makeAbstractConstant("d");
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Variable z = Expressions.makeUniversalVariable("Z");
		final Fact fact = Expressions.makeFact(predicate, Arrays.asList(constantC, constantC, constantD));

		final boolean includeBlanks = false;
		final Set<List<Constant>> factCCD = Collections.singleton(Arrays.asList(constantC, constantC, constantD));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(fact);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			final PositiveLiteral queryAtomXYZ = Expressions.makePositiveLiteral(predicate, x, y, z);
			try (final QueryResultIterator queryResultIteratorXYZ = reasoner.answerQuery(queryAtomXYZ, includeBlanks)) {
				final Set<List<Term>> queryResultsXYZ = QueryResultsUtils.collectQueryResults(queryResultIteratorXYZ);
				assertEquals(factCCD, queryResultsXYZ);
			}

			final PositiveLiteral queryAtomXXZ = Expressions.makePositiveLiteral(predicate, x, x, z);
			try (final QueryResultIterator queryResultIteratorXXZ = reasoner.answerQuery(queryAtomXXZ, includeBlanks)) {
				final Set<List<Term>> queryResultsXXZ = QueryResultsUtils.collectQueryResults(queryResultIteratorXXZ);
				assertEquals(factCCD, queryResultsXXZ);
			}

			final PositiveLiteral queryAtomXXX = Expressions.makePositiveLiteral(predicate, x, x, x);
			try (final QueryResultIterator queryResultIteratorXXX = reasoner.answerQuery(queryAtomXXX, includeBlanks)) {
				assertFalse(queryResultIteratorXXX.hasNext());
			}

			final PositiveLiteral queryAtomXYX = Expressions.makePositiveLiteral(predicate, x, y, x);
			try (final QueryResultIterator queryResultIteratorXYX = reasoner.answerQuery(queryAtomXYX, includeBlanks)) {

				assertFalse(queryResultIteratorXYX.hasNext());
			}
		}
	}

	@Test
	public void testIDBQuerySameBlankSubstitutesSameVariableName() throws IOException {
		final String predicate = "p";
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeExistentialVariable("Y");
		final Variable z = Expressions.makeExistentialVariable("Z");
		final PositiveLiteral pYY = Expressions.makePositiveLiteral(predicate, y, y);
		final PositiveLiteral pYZ = Expressions.makePositiveLiteral(predicate, y, z);
		final Rule pX__pYY_pYZ = Expressions.makeRule(Expressions.makePositiveConjunction(pYY, pYZ),
				Expressions.makeConjunction(Expressions.makePositiveLiteral(predicate, x)));
		assertEquals(Sets.newSet(y, z), pX__pYY_pYZ.getExistentialVariables().collect(Collectors.toSet()));

		final KnowledgeBase kb = new KnowledgeBase();

		kb.addStatements(pX__pYY_pYZ);
		kb.addStatement(Expressions.makeFact(predicate, Arrays.asList(Expressions.makeAbstractConstant("c"))));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			reasoner.setRuleRewriteStrategy(RuleRewriteStrategy.SPLIT_HEAD_PIECES);
			reasoner.load();
			reasoner.reason();

			// expected p(_:b1, _:b1), p(_:b1, _:b2)
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(pYZ, true)) {
				final Set<List<Term>> queryResults = QueryResultsUtils.collectQueryResults(queryResultIterator);
				assertTrue(queryResults.size() == 2);
				final ArrayList<List<Term>> queryResultsArray = new ArrayList<>(queryResults);
				assertEquals(queryResultsArray.get(0).get(0), queryResultsArray.get(1).get(0)); // y
				assertNotEquals(queryResultsArray.get(0).get(1), queryResultsArray.get(1).get(1)); // y, z
			}

			// expected p(_:b1, _:b1)
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(pYY, true)) {
				final Set<List<Term>> queryResults = QueryResultsUtils.collectQueryResults(queryResultIterator);
				assertTrue(queryResults.size() == 1);
				final ArrayList<List<Term>> queryResultsArray = new ArrayList<>(queryResults);
				assertEquals(queryResultsArray.get(0).get(0), queryResultsArray.get(0).get(1)); // y
			}
		}
	}

	@Test
	public void testIDBQuerySameIndividualSubstitutesSameVariableName() throws IOException {
		final String predicate = "p";
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Variable z = Expressions.makeExistentialVariable("Z");
		final Variable t = Expressions.makeExistentialVariable("T");
		final PositiveLiteral pXYYZZT = Expressions.makePositiveLiteral(predicate, x, y, y, z, z, t);
		final Rule pXY__pXYYZZT = Expressions.makeRule(pXYYZZT, Expressions.makePositiveLiteral(predicate, x, y));
		assertEquals(Sets.newSet(z, t), pXY__pXYYZZT.getExistentialVariables().collect(Collectors.toSet()));
		final Constant constantC = Expressions.makeAbstractConstant("c");
		final Constant constantD = Expressions.makeAbstractConstant("d");

		final Fact factPcd = Expressions.makeFact(predicate, Arrays.asList(constantC, constantD));

		final KnowledgeBase kb = new KnowledgeBase();

		kb.addStatements(pXY__pXYYZZT, factPcd);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();

			final PositiveLiteral queryAtomXYYZZT = pXYYZZT;
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXYYZZT, true)) {
				assertTrue(queryResultIterator.hasNext());
				final List<Term> queryResultTerms = queryResultIterator.next().getTerms();
				assertEquals(6, queryResultTerms.size());

				assertEquals(constantC, queryResultTerms.get(0)); // x
				assertEquals(constantD, queryResultTerms.get(1)); // y
				assertEquals(constantD, queryResultTerms.get(2)); // y

				final Term blankForZ = queryResultTerms.get(3); // z
				assertEquals(TermType.NAMED_NULL, blankForZ.getType());
				assertEquals(blankForZ, queryResultTerms.get(4)); // z

				final Term blankForT = queryResultTerms.get(5); // t
				assertEquals(TermType.NAMED_NULL, blankForT.getType());

				assertNotEquals(queryResultTerms.get(4), blankForT); // z, t

				assertFalse(queryResultIterator.hasNext());
			}

			// x and y do not have the same constant substitution
			final PositiveLiteral queryAtomXXYZZT = Expressions.makePositiveLiteral(predicate, x, x, y, z, z, t);
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXXYZZT, true)) {
				assertFalse(queryResultIterator.hasNext());
			}
			// z and t do not have the same blank substitution
			final PositiveLiteral queryAtomXYYZZZ = Expressions.makePositiveLiteral(predicate, x, y, y, z, z, z);
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXYYZZZ, true)) {
				assertFalse(queryResultIterator.hasNext());
			}
			// universal and existential variables do not have the same substitution
			// y and z do not have the same constant substitution
			final PositiveLiteral queryAtomXYYYZT = Expressions.makePositiveLiteral(predicate, x, y, y, y, z, t);
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXYYYZT, true)) {
				assertFalse(queryResultIterator.hasNext());
			}

			// y and t do not have the same constant substitution
			final PositiveLiteral queryAtomXYYZZY = Expressions.makePositiveLiteral(predicate, x, y, y, z, z, y);
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXYYZZY, true)) {
				assertFalse(queryResultIterator.hasNext());
			}

		}
	}

	@Test
	public void queryResultWithBlanks() throws IOException {
		final Variable vx = Expressions.makeUniversalVariable("x");
		final Variable vy = Expressions.makeExistentialVariable("y");
		// P(x) -> Q(y)
		final Rule existentialRule = Expressions.makeRule(Expressions.makePositiveLiteral("q", vy),
				Expressions.makePositiveLiteral("p", vx));
		assertEquals(Sets.newSet(vy), existentialRule.getExistentialVariables().collect(Collectors.toSet()));
		final Constant constantC = Expressions.makeAbstractConstant("c");
		final Fact fact = Expressions.makeFact("p", Arrays.asList(constantC));
		final PositiveLiteral queryAtom = Expressions.makePositiveLiteral("q", Expressions.makeUniversalVariable("?x"));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(existentialRule, fact);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();

			try (final QueryResultIterator queryResultIteratorIncludeBlanks = reasoner.answerQuery(queryAtom, true)) {
				assertTrue(queryResultIteratorIncludeBlanks.hasNext());
				final QueryResult queryResult = queryResultIteratorIncludeBlanks.next();
				assertTrue(queryResult.getTerms().size() == 1);
				final Term queryResultTerm = queryResult.getTerms().get(0);
				assertEquals(TermType.NAMED_NULL, queryResultTerm.getType());
				assertFalse(queryResultIteratorIncludeBlanks.hasNext());
			}

			try (final QueryResultIterator queryResultIteratorExcludeBlanks = reasoner.answerQuery(queryAtom, false)) {
				assertFalse(queryResultIteratorExcludeBlanks.hasNext());
			}
		}
	}

	@Test
	public void queryEmptyKnowledgeBaseBeforeReasoning() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			final PositiveLiteral queryAtom = Expressions.makePositiveLiteral("P",
					Expressions.makeUniversalVariable("?x"));
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true)) {
				final Set<List<Term>> queryResults = QueryResultsUtils.collectQueryResults(queryResultIterator);
				assertEquals(Collections.EMPTY_SET, queryResults);
			}
		}
	}

	@Test
	public void queryEmptyKnowledgeBaseAfterReasoning() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			reasoner.reason();

			final PositiveLiteral queryAtom = Expressions.makePositiveLiteral("P",
					Expressions.makeUniversalVariable("?x"));
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true)) {
				final Set<List<Term>> queryResults = QueryResultsUtils.collectQueryResults(queryResultIterator);
				assertEquals(Collections.EMPTY_SET, queryResults);
			}
		}
	}

	@Test
	public void queryEmptyRules() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		final Fact fact = Expressions.makeFact("P", Arrays.asList(Expressions.makeAbstractConstant("c")));
		kb.addStatement(fact);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			final PositiveLiteral queryAtom = Expressions.makePositiveLiteral("P",
					Expressions.makeUniversalVariable("?x"));

			reasoner.reason();

			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true)) {
				final Set<List<Term>> queryResults = QueryResultsUtils.collectQueryResults(queryResultIterator);
				final Set<List<Term>> expectedQueryResults = Collections
						.singleton(Arrays.asList(Expressions.makeAbstractConstant("c")));
				assertEquals(expectedQueryResults, queryResults);
			}
		}
	}

	@Test
	public void queryEmptyFacts() throws IOException {
		final Variable vx = Expressions.makeUniversalVariable("x");
		final Rule rule = Expressions.makeRule(Expressions.makePositiveLiteral("q", vx),
				Expressions.makePositiveLiteral("p", vx));

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(rule);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			final PositiveLiteral queryAtom1 = Expressions.makePositiveLiteral("p",
					Expressions.makeUniversalVariable("?x"));
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom1, true)) {
				Assert.assertFalse(queryResultIterator.hasNext());
				queryResultIterator.close();
			}

			final PositiveLiteral queryAtom2 = Expressions.makePositiveLiteral("q",
					Expressions.makeUniversalVariable("?x"));
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom2, true)) {
				Assert.assertFalse(queryResultIterator.hasNext());
				queryResultIterator.close();
			}

			reasoner.reason();

			try (final QueryResultIterator queryResultIteratorAfterReason = reasoner.answerQuery(queryAtom1, true)) {
				assertFalse(queryResultIteratorAfterReason.hasNext());
			}

			try (final QueryResultIterator queryResultIteratorAfterReason = reasoner.answerQuery(queryAtom2, true)) {
				assertFalse(queryResultIteratorAfterReason.hasNext());
			}
		}
	}

}
