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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.Correctness;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.QueryResultIterator;

public class QueryAnsweringCorrectnessTest {

	private static final Predicate predP = Expressions.makePredicate("predP", 1);
	private static final Predicate predQ = Expressions.makePredicate("predQ", 1);
	private static final Variable x = Expressions.makeVariable("x");
	private static final Constant c = Expressions.makeConstant("c");
	private static final Constant d = Expressions.makeConstant("d");
	private static final Constant g = Expressions.makeConstant("g");
	private static final Constant h = Expressions.makeConstant("h");
	private static final Constant e = Expressions.makeConstant("e");
	private static final Constant f = Expressions.makeConstant("f");

	private static final PositiveLiteral ruleHeadQx = Expressions.makePositiveLiteral(predQ, x);
	private static final PositiveLiteral ruleBodyPx = Expressions.makePositiveLiteral(predP, x);

	private static final Rule ruleQxPx = Expressions.makeRule(ruleHeadQx, ruleBodyPx);

	private static final Fact factPc = Expressions.makeFact(predP, c);
	private static final Fact factPd = Expressions.makeFact(predP, d);

	private static final Fact factQg = Expressions.makeFact(predQ, g);
	private static final Fact factQh = Expressions.makeFact(predQ, h);

	private static final InMemoryDataSource datasource = new InMemoryDataSource(1, 2);

	{
		datasource.addTuple("e");
		datasource.addTuple("f");
	}

	@Test
	public void testCorrectnessKBChanges() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {

			reasoner.reason();
			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				assertFalse(resultIterator.hasNext());
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(ruleQxPx);

			// there are no facts for Q-1 predicate
			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				assertFalse(resultIterator.hasNext());
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}
			reasoner.reason();

			// there are no facts for Q-1 predicate
			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				assertFalse(resultIterator.hasNext());
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(factQg);
			reasoner.reason();

			final Set<List<Term>> expectedAnswers_g = new HashSet<>(Arrays.asList(Collections.singletonList(g)));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_g, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(factQh);

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_g, queryAnswers);
				assertEquals(Correctness.INCORRECT, resultIterator.getCorrectness());
			}

			reasoner.reason();

			final Set<List<Term>> expectedAnswers_g_h = new HashSet<>(
					Arrays.asList(Collections.singletonList(g), Collections.singletonList(h)));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_g_h, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}
			
			kb.addStatements(factPc, factPd);

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_g_h, queryAnswers);
				assertEquals(Correctness.INCORRECT, resultIterator.getCorrectness());
			}

			reasoner.reason();

			final Set<List<Term>> expectedAnswers_g_h_c_d = new HashSet<>(Arrays.asList(Collections.singletonList(g),
					Collections.singletonList(h), Collections.singletonList(c), Collections.singletonList(d)));
			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_g_h_c_d, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			final Set<List<Term>> expectedAnswers_c_d = new HashSet<>(
					Arrays.asList(Collections.singletonList(c), Collections.singletonList(d)));
			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(new DataSourceDeclarationImpl(predP, datasource));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_g_h_c_d, queryAnswers);
				assertEquals(Correctness.INCORRECT, resultIterator.getCorrectness());
			}

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d, queryAnswers);
				assertEquals(Correctness.INCORRECT, resultIterator.getCorrectness());
			}

			reasoner.reason();

			final Set<List<Term>> expectedAnswers_g_h_c_d_e_f = new HashSet<>(Arrays.asList(
					Collections.singletonList(g), Collections.singletonList(h), Collections.singletonList(c),
					Collections.singletonList(d), Collections.singletonList(e), Collections.singletonList(f)));
			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_g_h_c_d_e_f, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			final Set<List<Term>> expectedAnswers_c_d_e_f = new HashSet<>(Arrays.asList(Collections.singletonList(c),
					Collections.singletonList(d), Collections.singletonList(e), Collections.singletonList(f)));
			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d_e_f, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(new DataSourceDeclarationImpl(predQ, datasource));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_g_h_c_d_e_f, queryAnswers);
				assertEquals(Correctness.INCORRECT, resultIterator.getCorrectness());
			}

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d_e_f, queryAnswers);
				assertEquals(Correctness.INCORRECT, resultIterator.getCorrectness());
			}

			reasoner.reason();

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_g_h_c_d_e_f, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d_e_f, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(ruleQxPx);

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d_e_f, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}
		}
	}

	@Test
	public void testCorrectnessKBChangesNoRules() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {

			reasoner.reason();

			// there are no facts for P-1 predicate
			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				assertFalse(resultIterator.hasNext());
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(factPc);
			reasoner.reason();

			final Set<List<Term>> expectedAnswers_c = new HashSet<>(Arrays.asList(Collections.singletonList(c)));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(factPd);

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c, queryAnswers);
				assertEquals(Correctness.SOUND_BUT_INCOMPLETE, resultIterator.getCorrectness());
			}

			reasoner.reason();

			final Set<List<Term>> expectedAnswers_c_d = new HashSet<>(
					Arrays.asList(Collections.singletonList(c), Collections.singletonList(d)));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatements(factPc, factPd);
			kb.addStatement(factPc);
			kb.addStatement(factPd);
			kb.addStatements(factPc, factPd, factPc, factPd);

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(new DataSourceDeclarationImpl(predP, datasource));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d, queryAnswers);
				assertEquals(Correctness.SOUND_BUT_INCOMPLETE, resultIterator.getCorrectness());
			}

			reasoner.reason();

			final Set<List<Term>> expectedAnswers_c_d_e_f = new HashSet<>(Arrays.asList(Collections.singletonList(c),
					Collections.singletonList(d), Collections.singletonList(e), Collections.singletonList(f)));
			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d_e_f, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(new DataSourceDeclarationImpl(predP, datasource));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d_e_f, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(ruleQxPx);

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c_d_e_f, queryAnswers);
				assertEquals(Correctness.INCORRECT, resultIterator.getCorrectness());
			}
		}

	}

	@Test
	public void testCorrectnessKBChangesReset() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {

			reasoner.reason();

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				assertFalse(resultIterator.hasNext());
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(factPc);
			reasoner.reason();

			final Set<List<Term>> expectedAnswers_c = new HashSet<>(Arrays.asList(Collections.singletonList(c)));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				assertFalse(resultIterator.hasNext());
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatement(ruleQxPx);
			reasoner.reason();

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			reasoner.resetReasoner();
			reasoner.reason();

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleHeadQx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

		}
	}

	@Test
	public void testCorrectnessNoKBChanges() throws IOException {

		final KnowledgeBase kb = new KnowledgeBase();
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {

			kb.addStatement(factPc);
			reasoner.reason();

			final Set<List<Term>> expectedAnswers_c = new HashSet<>(Arrays.asList(Collections.singletonList(c)));

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

			kb.addStatements(factPc, factPc);
			kb.addStatement(factPc);

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBodyPx, true)) {
				final Set<List<Term>> queryAnswers = QueryResultsUtils.collectQueryResults(resultIterator);
				assertEquals(expectedAnswers_c, queryAnswers);
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}
		}
	}

	@Test
	public void testMaterialisationIncomplete() throws IOException {
		final Variable y = Expressions.makeVariable("y");
		final Variable z = Expressions.makeVariable("z");

		final Predicate predR = Expressions.makePredicate("predR", 2);

		final PositiveLiteral ruleBody_R_x_y = Expressions.makePositiveLiteral(predR, x, y);
		final PositiveLiteral ruleHead_R_y_z = Expressions.makePositiveLiteral(predR, y, z);
		// R(?x, ?y) -> R(?y, !z)
		final Rule rule = Expressions.makeRule(ruleHead_R_y_z, ruleBody_R_x_y);

		final Fact fact_R_c_d = Expressions.makeFact(predR, c, d);
		final Fact fact_R_d_e = Expressions.makeFact(predR, d, e);
		final Fact fact_R_e_c = Expressions.makeFact(predR, e, c);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factPc);
		kb.addStatements(fact_R_c_d, fact_R_d_e, fact_R_e_c);
		kb.addStatements(rule);
		
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
			reasoner.setReasoningTimeout(1);
			assertFalse(reasoner.reason());

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBody_R_x_y, true)) {
				assertEquals(Correctness.SOUND_BUT_INCOMPLETE, resultIterator.getCorrectness());
			}
			
			reasoner.setReasoningTimeout(2);
			assertFalse(reasoner.reason());

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBody_R_x_y, true)) {
				assertEquals(Correctness.SOUND_BUT_INCOMPLETE, resultIterator.getCorrectness());
			}
			
			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			reasoner.setReasoningTimeout(null);
			assertTrue(reasoner.reason());

			try (final QueryResultIterator resultIterator = reasoner.answerQuery(ruleBody_R_x_y, true)) {
				assertEquals(Correctness.SOUND_AND_COMPLETE, resultIterator.getCorrectness());
			}

		}
	}

}
