package org.semanticweb.vlog4j.core.reasoner;

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
import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

public class ReasonerStateTest {

	private static final Variable vx = Expressions.makeVariable("x");
	private static final Atom exampleQueryAtom = Expressions.makeAtom("q", vx);

	// p(?x) -> q(?x)
	private static final Atom ruleHeadQx = Expressions.makeAtom("q", vx);
	private static final Atom ruleBodyPx = Expressions.makeAtom("p", vx);
	private static final Rule rule = Expressions.makeRule(ruleHeadQx, ruleBodyPx);
	private static final Constant constantC = Expressions.makeConstant("c");
	private static final Atom factPc = Expressions.makeAtom("p", constantC);

	@Test
	public void testResetBeforeLoad() {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.reset();
		}
	}

	@Test
	public void testResetDiscardInferences() throws ReasonerStateException, EdbIdbSeparationException, IOException {
		for (final Algorithm algorith : Algorithm.values()) {
			// discard inferences regardless of the inference algorithm
			try (final Reasoner reasoner = Reasoner.getInstance();) {
				reasoner.addFacts(factPc);
				reasoner.addRules(rule);
				reasoner.load();
				reasoner.setAlgorithm(algorith);
				reasoner.reason();
				try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
					assertTrue(queryResultIterator.hasNext());
					assertEquals(Arrays.asList(constantC), queryResultIterator.next().getTerms());
					assertFalse(queryResultIterator.hasNext());
				}

				reasoner.reset();
				reasoner.load();
				try (final QueryResultIterator queryResultIteratorAfterReset = reasoner.answerQuery(ruleHeadQx, true)) {
					assertFalse(queryResultIteratorAfterReset.hasNext());
				}
			}
		}
	}

	@Test
	public void testResetKeepExplicitDatabase() throws ReasonerStateException, EdbIdbSeparationException, IOException {
		try (final Reasoner reasoner = Reasoner.getInstance();) {
			// assert p(c)
			reasoner.addFacts(factPc);
			// assert r(d)
			final Predicate predicateR1 = Expressions.makePredicate("r", 1);
			reasoner.addDataSource(predicateR1,
					new CsvFileDataSource(new File(CsvFileUtils.CSV_INPORT_FOLDER, "constantD.csv")));
			// p(?x) -> q(?x)
			reasoner.addRules(rule);
			reasoner.load();
			checkExplicitFacts(reasoner, predicateR1);

			reasoner.reset();
			reasoner.load();
			checkExplicitFacts(reasoner, predicateR1);

			// check rule exists in knowledge base after reset
			reasoner.reason();
			try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				assertTrue(queryResultIterator.hasNext());
				assertEquals(Arrays.asList(constantC), queryResultIterator.next().getTerms());
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
				.answerQuery(Expressions.makeAtom(predicateR1, vx), true)) {
			assertTrue(queryResultIteratorRx.hasNext());
			assertEquals(Arrays.asList(Expressions.makeConstant("d")), queryResultIteratorRx.next().getTerms());
			assertFalse(queryResultIteratorRx.hasNext());
		}
	}

	@Test
	public void testResetEmptyKnowledgeBase() throws EdbIdbSeparationException, IOException, ReasonerStateException {
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
		reasoner.reset();

		// 2. load again
		reasoner.load();
		try (final QueryResultIterator queryResultIterator = reasoner.answerQuery(exampleQueryAtom, true)) {
			assertFalse(queryResultIterator.hasNext());
		}
		reasoner.reset();

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
	public void testSuccessiveCloseAfterLoad() throws EdbIdbSeparationException, IOException {
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
