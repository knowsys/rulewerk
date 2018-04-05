package org.semanticweb.vlog4j.core.reasoner.implementation;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.CsvFileUtils;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

public class ReasonerStateTest {

	private static final Predicate p = Expressions.makePredicate("p", 1);
	private static final Predicate q = Expressions.makePredicate("q", 1);
	private static final Variable x = Expressions.makeVariable("x");
	private static final Constant c = Expressions.makeConstant("c");
	// private static final Constant d = Expressions.makeConstant("d");
	private static final Atom exampleQueryAtom = Expressions.makeAtom("q", x);

	private static final Atom ruleHeadQx = Expressions.makeAtom(q, x);
	private static final Atom ruleBodyPx = Expressions.makeAtom(p, x);
	private static final Rule ruleQxPx = Expressions.makeRule(ruleHeadQx, ruleBodyPx);
	private static final Atom factPc = Expressions.makeAtom(p, c);
	// private static final Atom factPd = Expressions.makeAtom(q, d);

	@Test(expected = NullPointerException.class)
	public void testSetAlgorithm() {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			reasoner.setAlgorithm(null);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetReasoningTimeout() {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			reasoner.setReasoningTimeout(-3);
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testAddRules1() throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			reasoner.load();
			reasoner.addRules(ruleQxPx);
		}
	}

	@Test
	public void testAddRules2() throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			reasoner.load();
			reasoner.resetReasoner();
			reasoner.addRules(ruleQxPx);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddRules3() throws EdbIdbSeparationException, IOException, ReasonerStateException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			final List<Rule> rules = new ArrayList<>();
			rules.add(ruleQxPx);
			rules.add(null);
			reasoner.addRules(rules);
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testAddFacts1() throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			reasoner.load();
			reasoner.addFacts(factPc);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddFacts2() throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			final List<Atom> facts = new ArrayList<>();
			facts.add(factPc);
			facts.add(null);
			reasoner.addFacts(facts);
			reasoner.load();
		}
	}

	@Test
	public void testResetBeforeLoad() {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.resetReasoner();
		}
	}

	@Test(expected = NullPointerException.class)
	public void setRuleRewriteStrategy1() throws ReasonerStateException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			reasoner.setRuleRewriteStrategy(null);
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void setRuleRewriteStrategy2() throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			reasoner.load();
			reasoner.setRuleRewriteStrategy(RuleRewriteStrategy.NONE);
		}
	}

	@Test
	public void setRuleRewriteStrategy3() throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			reasoner.load();
			reasoner.resetReasoner();
			reasoner.setRuleRewriteStrategy(RuleRewriteStrategy.NONE);
		}
	}

	@Test
	public void testResetDiscardInferences() throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		for (final Algorithm algorithm : Algorithm.values()) {
			// discard inferences regardless of the inference algorithm
			try (final Reasoner reasoner = Reasoner.getInstance();) {
				reasoner.addFacts(factPc);
				reasoner.addRules(ruleQxPx);
				reasoner.setAlgorithm(algorithm);

				reasoner.load();
				reasoner.reason();
				try (final QueryResultIterator queryQxIterator = reasoner.answerQuery(ruleHeadQx, true)) {
					final Set<List<Term>> queryQxResults = QueryResultsUtils.collectQueryResults(queryQxIterator);
					final Set<List<Term>> queryQxExpectedResults = new HashSet<List<Term>>();
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
					final Set<List<Term>> queryPxExpectedResults = new HashSet<List<Term>>();
					queryPxExpectedResults.add(Arrays.asList(c));
					assertEquals(queryPxResults, queryPxExpectedResults);
				}
			}
		}
	}

	@Test
	public void testResetKeepExplicitDatabase() throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			// assert p(c)
			reasoner.addFacts(factPc);
			// assert r(d)
			final Predicate predicateR1 = Expressions.makePredicate("r", 1);
			reasoner.addFactsFromDataSource(predicateR1,
					new CsvFileDataSource(new File(CsvFileUtils.CSV_INPORT_FOLDER, "constantD.csv")));
			// p(?x) -> q(?x)
			reasoner.addRules(ruleQxPx);
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

	private void checkExplicitFacts(final Reasoner reasoner, final Predicate predicateR1)
			throws ReasonerStateException {
		try (final QueryResultIterator queryResultIteratorPx = reasoner.answerQuery(ruleBodyPx, true)) {
			assertTrue(queryResultIteratorPx.hasNext());
			assertEquals(factPc.getTerms(), queryResultIteratorPx.next().getTerms());
			assertFalse(queryResultIteratorPx.hasNext());
		}
		try (final QueryResultIterator queryResultIteratorRx = reasoner
				.answerQuery(Expressions.makeAtom(predicateR1, x), true)) {
			assertTrue(queryResultIteratorRx.hasNext());
			assertEquals(Arrays.asList(Expressions.makeConstant("d")), queryResultIteratorRx.next().getTerms());
			assertFalse(queryResultIteratorRx.hasNext());
		}
	}

	@Test
	public void testResetEmptyKnowledgeBase() throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		final Reasoner reasoner = Reasoner.getInstance();
		// 1. load and reason
		reasoner.load();
		try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)) {
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.reason();
		try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)) {
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.resetReasoner();

		// 2. load again
		reasoner.load();
		try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)) {
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.resetReasoner();

		// 3. load and reason again
		reasoner.load();
		try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)) {
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.reason();
		try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)) {
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.close();
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailReasonBeforeLoad() throws ReasonerStateException, IOException {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.reason();
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailAnswerQueryBeforeLoad() throws ReasonerStateException {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.answerQuery(exampleQueryAtom, true);
		}
	}

	@Test(expected = ReasonerStateException.class)
	public void testFailExportQueryAnswerToCsvBeforeLoad() throws ReasonerStateException, IOException {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.exportQueryAnswersToCsv(exampleQueryAtom, CsvFileUtils.CSV_EXPORT_FOLDER + "output.csv", true);
		}
	}

	@Test
	public void testSuccessiveCloseAfterLoad() throws EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.load();
			reasoner.close();
			reasoner.close();
		}
	}

	@Test
	public void testSuccessiveCloseBeforeLoad() {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.close();
			reasoner.close();
		}
	}

}
