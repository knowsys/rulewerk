package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

import karmaresearch.vlog.EDBConfigurationException;

public class AnswerQueryTest {

	@Test
	public void testEDBQuerySameConstantSubstitutesSameVariableName()
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final String predicate = "p";
		final Constant constantC = Expressions.makeConstant("c");
		final Constant constantD = Expressions.makeConstant("d");
		final Variable x = Expressions.makeVariable("X");
		final Variable y = Expressions.makeVariable("Y");
		final Variable z = Expressions.makeVariable("Z");
		final Atom fact = Expressions.makeAtom(predicate, constantC, constantC, constantD);

		final boolean includeBlanks = false;
		@SuppressWarnings("unchecked")
		final Set<List<Constant>> factCCD = Sets.newSet(Arrays.asList(constantC, constantC, constantD));

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFacts(fact);
			reasoner.load();

			final Atom queryAtomXYZ = Expressions.makeAtom(predicate, x, y, z);
			try (final QueryResultIterator queryResultIteratorXYZ = reasoner.answerQuery(queryAtomXYZ, includeBlanks)) {
				final Set<List<Term>> queryResultsXYZ = QueryResultsUtils.collectQueryResults(queryResultIteratorXYZ);
				assertEquals(factCCD, queryResultsXYZ);
			}

			final Atom queryAtomXXZ = Expressions.makeAtom(predicate, x, x, z);
			try (final QueryResultIterator queryResultIteratorXXZ = reasoner.answerQuery(queryAtomXXZ, includeBlanks)) {
				final Set<List<Term>> queryResultsXXZ = QueryResultsUtils.collectQueryResults(queryResultIteratorXXZ);
				assertEquals(factCCD, queryResultsXXZ);
			}

			final Atom queryAtomXXX = Expressions.makeAtom(predicate, x, x, x);
			try (final QueryResultIterator queryResultIteratorXXX = reasoner.answerQuery(queryAtomXXX, includeBlanks)) {
				assertFalse(queryResultIteratorXXX.hasNext());
			}

			final Atom queryAtomXYX = Expressions.makeAtom(predicate, x, y, x);
			try (final QueryResultIterator queryResultIteratorXYX = reasoner.answerQuery(queryAtomXYX, includeBlanks)) {

				assertFalse(queryResultIteratorXYX.hasNext());
			}
		}
	}

	@Test
	public void testIDBQuerySameBlankSubstitutesSameVariableName()
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final String predicate = "p";
		final Variable x = Expressions.makeVariable("X");
		final Variable y = Expressions.makeVariable("Y");
		final Variable z = Expressions.makeVariable("Z");
		final Atom pYY = Expressions.makeAtom(predicate, y, y);
		final Atom pYZ = Expressions.makeAtom(predicate, y, z);
		final Rule pX__pYY_pYZ = Expressions.makeRule(Expressions.makeConjunction(pYY, pYZ),
				Expressions.makeConjunction(Expressions.makeAtom(predicate, x)));
		assertEquals(Sets.newSet(y, z), pX__pYY_pYZ.getExistentiallyQuantifiedVariables());

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			reasoner.setRuleRewriteStrategy(RuleRewriteStrategy.SPLIT_HEAD_PIECES);
			reasoner.addFacts(Expressions.makeAtom(predicate, Expressions.makeConstant("c")));
			reasoner.addRules(pX__pYY_pYZ);
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
	public void testIDBQuerySameIndividualSubstitutesSameVariableName()
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final String predicate = "p";
		final Variable x = Expressions.makeVariable("X");
		final Variable y = Expressions.makeVariable("Y");
		final Variable z = Expressions.makeVariable("Z");
		final Variable t = Expressions.makeVariable("T");
		final Atom pXYYZZT = Expressions.makeAtom(predicate, x, y, y, z, z, t);
		final Rule pXY__pXYYZZT = Expressions.makeRule(pXYYZZT, Expressions.makeAtom(predicate, x, y));
		assertEquals(Sets.newSet(z, t), pXY__pXYYZZT.getExistentiallyQuantifiedVariables());
		final Constant constantC = Expressions.makeConstant("c");
		final Constant constantD = Expressions.makeConstant("d");
		final Atom factPcd = Expressions.makeAtom(predicate, constantC, constantD);

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFacts(factPcd);
			reasoner.addRules(pXY__pXYYZZT);
			reasoner.load();
			reasoner.reason();

			final Atom queryAtomXYYZZT = pXYYZZT;
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXYYZZT, true)) {
				assertTrue(queryResultIterator.hasNext());
				final List<Term> queryResultTerms = queryResultIterator.next().getTerms();
				assertEquals(6, queryResultTerms.size());

				assertEquals(constantC, queryResultTerms.get(0)); // x
				assertEquals(constantD, queryResultTerms.get(1)); // y
				assertEquals(constantD, queryResultTerms.get(2)); // y

				final Term blankForZ = queryResultTerms.get(3); // z
				assertEquals(TermType.BLANK, blankForZ.getType());
				assertEquals(blankForZ, queryResultTerms.get(4)); // z

				final Term blankForT = queryResultTerms.get(5); // t
				assertEquals(TermType.BLANK, blankForT.getType());

				assertNotEquals(queryResultTerms.get(4), blankForT); // z, t

				assertFalse(queryResultIterator.hasNext());
			}

			// x and y do not have the same constant substitution
			final Atom queryAtomXXYZZT = Expressions.makeAtom(predicate, x, x, y, z, z, t);
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXXYZZT, true)) {
				assertFalse(queryResultIterator.hasNext());
			}
			// z and t do not have the same blank substitution
			final Atom queryAtomXYYZZZ = Expressions.makeAtom(predicate, x, y, y, z, z, z);
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXYYZZZ, true)) {
				assertFalse(queryResultIterator.hasNext());
			}
			// universal and existential variables do not have the same substitution
			// y and z do not have the same constant substitution
			final Atom queryAtomXYYYZT = Expressions.makeAtom(predicate, x, y, y, y, z, t);
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXYYYZT, true)) {
				assertFalse(queryResultIterator.hasNext());
			}

			// y and t do not have the same constant substitution
			final Atom queryAtomXYYZZY = Expressions.makeAtom(predicate, x, y, y, z, z, y);
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtomXYYZZY, true)) {
				assertFalse(queryResultIterator.hasNext());
			}

		}
	}

	@Test
	public void queryResultWithBlanks() throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final Variable vx = Expressions.makeVariable("x");
		final Variable vy = Expressions.makeVariable("y");
		// P(x) -> Q(y)
		final Rule existentialRule = Expressions.makeRule(Expressions.makeAtom("q", vy), Expressions.makeAtom("p", vx));
		assertEquals(Sets.newSet(vy), existentialRule.getExistentiallyQuantifiedVariables());
		final Constant constantC = Expressions.makeConstant("c");
		final Atom fact = Expressions.makeAtom("p", constantC);
		final Atom queryAtom = Expressions.makeAtom("q", Expressions.makeVariable("?x"));

		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addFacts(fact);
			reasoner.addRules(existentialRule);
			reasoner.load();
			reasoner.reason();

			try (final QueryResultIterator queryResultIteratorIncludeBlanks = reasoner.answerQuery(queryAtom, true)) {
				assertTrue(queryResultIteratorIncludeBlanks.hasNext());
				final QueryResult queryResult = queryResultIteratorIncludeBlanks.next();
				assertTrue(queryResult.getTerms().size() == 1);
				final Term queryResultTerm = queryResult.getTerms().get(0);
				assertEquals(TermType.BLANK, queryResultTerm.getType());
				assertFalse(queryResultIteratorIncludeBlanks.hasNext());
			}

			try (final QueryResultIterator queryResultIteratorExcludeBlanks = reasoner.answerQuery(queryAtom, false)) {
				assertFalse(queryResultIteratorExcludeBlanks.hasNext());
			}
		}
	}

	@Test
	public void queryEmptyKnowledgeBase() throws IOException, EdbIdbSeparationException, ReasonerStateException, IncompatiblePredicateArityException {
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.load();

			final Atom queryAtom = Expressions.makeAtom("P", Expressions.makeVariable("?x"));
			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true);
			Assert.assertFalse(queryResultIterator.hasNext());
			queryResultIterator.close();

			reasoner.reason();

			try (final QueryResultIterator queryResultIteratorAfterReason = reasoner.answerQuery(queryAtom, true)) {
				assertFalse(queryResultIteratorAfterReason.hasNext());
			}
		}
	}

	@Test
	public void queryEmptyRules() throws IOException, EdbIdbSeparationException, ReasonerStateException, IncompatiblePredicateArityException {
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			final Atom fact = Expressions.makeAtom("P", Expressions.makeConstant("c"));
			reasoner.addFacts(fact);
			reasoner.load();

			final Atom queryAtom = Expressions.makeAtom("P", Expressions.makeVariable("?x"));

			reasoner.reason();

			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true)) {
				final Set<List<Term>> queryResults = QueryResultsUtils.collectQueryResults(queryResultIterator);
				@SuppressWarnings("unchecked")
				final Set<List<Term>> expectedQueryResults = Sets.newSet(Arrays.asList(Expressions.makeConstant("c")));
				assertEquals(expectedQueryResults, queryResults);
			}
		}
	}

	@Test
	public void queryEmptyFacts()
			throws EDBConfigurationException, IOException, EdbIdbSeparationException, ReasonerStateException, IncompatiblePredicateArityException {
		final Variable vx = Expressions.makeVariable("x");
		final Rule rule = Expressions.makeRule(Expressions.makeAtom("q", vx), Expressions.makeAtom("p", vx));

		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addRules(rule);
			reasoner.load();

			final Atom queryAtom = Expressions.makeAtom("P", Expressions.makeVariable("?x"));
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true)) {
				Assert.assertFalse(queryResultIterator.hasNext());
				queryResultIterator.close();
			}

			reasoner.reason();

			try (final QueryResultIterator queryResultIteratorAfterReason = reasoner.answerQuery(queryAtom, true)) {
				assertFalse(queryResultIteratorAfterReason.hasNext());
			}
		}
	}

}
