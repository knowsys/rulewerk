package org.semanticweb.rulewerk.reasoner.vlog;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.rulewerk.core.exceptions.ReasonerStateException;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.Algorithm;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.ReasonerState;
import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;

public class VLogReasonerStateTest {

	private static final Predicate p = Expressions.makePredicate("p", 1);
	private static final Predicate q = Expressions.makePredicate("q", 1);
	private static final Variable x = Expressions.makeUniversalVariable("x");
	private static final Constant c = Expressions.makeAbstractConstant("c");
	private static final Constant d = Expressions.makeAbstractConstant("d");
	private static final PositiveLiteral exampleQueryAtom = Expressions.makePositiveLiteral("q", x);

	private static final PositiveLiteral ruleHeadQx = Expressions.makePositiveLiteral(q, x);
	private static final PositiveLiteral ruleBodyPx = Expressions.makePositiveLiteral(p, x);
	private static final Rule ruleQxPx = Expressions.makeRule(ruleHeadQx, ruleBodyPx);
	private static final Fact factPc = Expressions.makeFact(p, c);
	private static final Fact factPd = Expressions.makeFact(p, d);

	@Test(expected = ReasonerStateException.class)
	public void testFailAnswerQueryBeforeLoad() {
		try (final Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			reasoner.answerQuery(exampleQueryAtom, true);
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailExportQueryAnswersBeforeLoad() throws IOException {
		try (final Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			reasoner.exportQueryAnswersToCsv(exampleQueryAtom, "", true);
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailAnswerQueryAfterReset() throws IOException {
		try (final Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			reasoner.reason();
			reasoner.resetReasoner();
			reasoner.answerQuery(exampleQueryAtom, true);
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailExportQueryAnswersAfterReset() throws IOException {
		try (final Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			reasoner.reason();
			reasoner.resetReasoner();
			reasoner.exportQueryAnswersToCsv(exampleQueryAtom, "", true);
		}
	}

	@Test
	public void testAddFactsAndQuery() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {

			kb.addStatement(factPc);
			reasoner.load();

			final PositiveLiteral query = Expressions.makePositiveLiteral(p, x);
			final Set<List<Term>> expectedAnswersC = new HashSet<>(Arrays.asList(Collections.singletonList(c)));

			try (final QueryResultIterator queryResult = reasoner.answerQuery(query, true)) {
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());
				final Set<List<Term>> queryAnswersC = QueryResultsUtils.collectQueryResults(queryResult);

				assertEquals(expectedAnswersC, queryAnswersC);
			}

			reasoner.getKnowledgeBase().addStatement(factPd);

			try (final QueryResultIterator queryResult = reasoner.answerQuery(query, true)) {
				assertEquals(Correctness.INCORRECT, queryResult.getCorrectness());
				assertEquals(expectedAnswersC, QueryResultsUtils.collectQueryResults(queryResult));
			}

			reasoner.load();

			try (final QueryResultIterator queryResult = reasoner.answerQuery(query, true)) {
				assertEquals(Correctness.SOUND_AND_COMPLETE, queryResult.getCorrectness());

				final Set<List<Term>> queryAnswersD = QueryResultsUtils.collectQueryResults(queryResult);

				final Set<List<Term>> expectedAnswersCD = new HashSet<>(
						Arrays.asList(Collections.singletonList(c), Collections.singletonList(d)));
				assertEquals(expectedAnswersCD, queryAnswersD);
			}
		}
	}

	@Test
	public void testAddRules2() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(ruleQxPx);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.resetReasoner();
		}
	}

	@Test(expected = NullPointerException.class)
	public void testAddRules3() {
		final KnowledgeBase kb = new KnowledgeBase();
		final List<Rule> rules = new ArrayList<>();
		rules.add(ruleQxPx);
		rules.add(null);
		kb.addStatements(rules);
	}

	@Test(expected = NullPointerException.class)
	public void testAddFacts2() throws IOException {

		final KnowledgeBase kb = new KnowledgeBase();
		final List<Fact> facts = new ArrayList<>();
		facts.add(factPc);
		facts.add(null);
		kb.addStatements(facts);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
		}
	}

	@Test
	public void testResetBeforeLoad() {
		try (final Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			reasoner.resetReasoner();
		}
	}

	@Test
	public void testResetDiscardInferences() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc);

		for (final Algorithm algorithm : Algorithm.values()) {
			// discard inferences regardless of the inference algorithm
			try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
				reasoner.setAlgorithm(algorithm);

				reasoner.load();
				reasoner.reason();
				try (final QueryResultIterator queryQxIterator = reasoner.answerQuery(ruleHeadQx, true)) {
					final Set<List<Term>> queryQxResults = QueryResultsUtils.collectQueryResults(queryQxIterator);
					final Set<List<Term>> queryQxExpectedResults = new HashSet<>();
					queryQxExpectedResults.add(Arrays.asList(c));
					assertEquals(queryQxResults, queryQxExpectedResults);
				}

				reasoner.resetReasoner();
				reasoner.load();
				try (final QueryResultIterator queryQxIterator = reasoner.answerQuery(ruleHeadQx, true)) {
					final Set<List<Term>> queryQxResults = QueryResultsUtils.collectQueryResults(queryQxIterator);
					assertTrue(queryQxResults.isEmpty());
				}
				try (final QueryResultIterator queryPxIterator = reasoner.answerQuery(ruleBodyPx, true)) {
					final Set<List<Term>> queryPxResults = QueryResultsUtils.collectQueryResults(queryPxIterator);
					final Set<List<Term>> queryPxExpectedResults = new HashSet<>();
					queryPxExpectedResults.add(Arrays.asList(c));
					assertEquals(queryPxResults, queryPxExpectedResults);
				}
			}
		}
	}

	@Test
	public void testResetKeepExplicitDatabase() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(ruleQxPx);
		// assert p(c)
		kb.addStatement(factPc);
		// assert r(d)
		final Predicate predicateR1 = Expressions.makePredicate("r", 1);
		kb.addStatement(new DataSourceDeclarationImpl(predicateR1,
				new CsvFileDataSource(FileDataSourceTestUtils.INPUT_FOLDER + "constantD.csv")));
		// p(?x) -> q(?x)

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			checkExplicitFacts(reasoner, predicateR1);

			reasoner.resetReasoner();
			reasoner.load();
			checkExplicitFacts(reasoner, predicateR1);

			// check rule exists in knowledge base after reset
			reasoner.reason();
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				assertTrue(queryResultIterator.hasNext());
				assertEquals(Arrays.asList(c), queryResultIterator.next().getTerms());
				assertFalse(queryResultIterator.hasNext());
			}

		}
	}

	private void checkExplicitFacts(final Reasoner reasoner, final Predicate predicateR1) {
		try (final QueryResultIterator queryResultIteratorPx = reasoner.answerQuery(ruleBodyPx, true)) {
			assertTrue(queryResultIteratorPx.hasNext());
			assertEquals(factPc.getArguments(), queryResultIteratorPx.next().getTerms());
			assertFalse(queryResultIteratorPx.hasNext());
		}
		try (final QueryResultIterator queryResultIteratorRx = reasoner
				.answerQuery(Expressions.makePositiveLiteral(predicateR1, x), true)) {
			assertTrue(queryResultIteratorRx.hasNext());
			assertEquals(Arrays.asList(Expressions.makeAbstractConstant("d")), queryResultIteratorRx.next().getTerms());
			assertFalse(queryResultIteratorRx.hasNext());
		}
	}

	@Test
	public void testResetEmptyKnowledgeBase() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			// 1. load and reason
			reasoner.load();
			reasoner.reason();
			reasoner.resetReasoner();

			// 2. load again
			reasoner.load();
			reasoner.resetReasoner();

			// 3. load and reason again
			reasoner.load();
			reasoner.reason();
			reasoner.close();
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailExportQueryAnswerToCsvBeforeLoad() throws IOException {
		try (final Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			reasoner.exportQueryAnswersToCsv(exampleQueryAtom, FileDataSourceTestUtils.OUTPUT_FOLDER + "output.csv",
					true);
		}
	}

	@Test
	public void testSuccessiveCloseAfterLoad() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.close();
			reasoner.close();
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testSuccessiveCloseBeforeLoad() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.close();
			reasoner.close();
			reasoner.load();
		}
	}

	@Test
	public void testCloseRepeatedly() throws IOException {
		try (final VLogReasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			reasoner.close();
		}

		try (final VLogReasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			reasoner.load();
			reasoner.close();
			reasoner.close();
		}
	}

	@Test
	public void testStatementRemovalBeforeLoad() {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.removeStatement(ruleQxPx);
			assertEquals(ReasonerState.KB_NOT_LOADED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsListRemovalBeforeLoad() {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.removeStatements(Arrays.asList(factPc, factPd));
			assertEquals(ReasonerState.KB_NOT_LOADED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsArrayRemovalBeforeLoad() {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.removeStatements(factPc, factPd);
			assertEquals(ReasonerState.KB_NOT_LOADED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementRemovalAfterLoad() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			kb.removeStatement(ruleQxPx);
			assertEquals(ReasonerState.KB_CHANGED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsListRemovalAfterLoad() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			kb.removeStatements(Arrays.asList(ruleQxPx, factPd));
			assertEquals(ReasonerState.KB_CHANGED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsArrayRemovalAfterLoad() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			kb.removeStatements(Arrays.asList(ruleQxPx, factPd));
			assertEquals(ReasonerState.KB_CHANGED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementRemovalBeforeReason() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.removeStatement(ruleQxPx);
			reasoner.reason();
			assertEquals(ReasonerState.MATERIALISED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsListRemovalBeforeReason() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.removeStatements(Arrays.asList(ruleQxPx, factPd));
			reasoner.reason();
			assertEquals(ReasonerState.MATERIALISED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsArrayRemovalBeforeReason() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			kb.removeStatements(ruleQxPx, factPd);
			reasoner.reason();
			assertEquals(ReasonerState.MATERIALISED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementRemovalAfterReason() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			kb.removeStatement(ruleQxPx);
			assertEquals(ReasonerState.KB_CHANGED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsListRemovalAfterReason() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			kb.removeStatements(Arrays.asList(factPc, ruleQxPx));
			assertEquals(ReasonerState.KB_CHANGED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsArrayRemovalAfterReason() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			kb.removeStatements(factPc, ruleQxPx);
			assertEquals(ReasonerState.KB_CHANGED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementNotRemovedAfterReason() throws IOException {
		final Fact newFact = Expressions.makeFact("newPred", c);
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			kb.removeStatement(newFact);
			assertEquals(ReasonerState.MATERIALISED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsListNotRemovedAfterReason() throws IOException {
		final Fact newFact = Expressions.makeFact("newPred", c);
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			kb.removeStatements(Arrays.asList(newFact));
			assertEquals(ReasonerState.MATERIALISED, reasoner.getReasonerState());
		}
	}

	@Test
	public void testStatementsArrayListNotRemovedAfterReason() throws IOException {
		final Fact newFact = Expressions.makeFact("newPred", c);
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(ruleQxPx, factPc, factPd);
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			kb.removeStatements(newFact, newFact);
			assertEquals(ReasonerState.MATERIALISED, reasoner.getReasonerState());
		}
	}

}
