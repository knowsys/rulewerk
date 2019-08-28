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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;

public class GeneratedAnonymousIndividualsTest {

	private static final String includeBlanksFilePath = FileDataSourceTestUtils.OUTPUT_FOLDER + "include_blanks.csv";
	private static final String excludeBlanksFilePath = FileDataSourceTestUtils.OUTPUT_FOLDER + "exclude_blanks.csv";

	private static final Variable vx = Expressions.makeVariable("x");
	private static final Variable vy = Expressions.makeVariable("y");
	private static final Variable vz = Expressions.makeVariable("z");
	private static final String p = "p";

	// rule: P(?x) -> P(?x,!y), P(?x,!z)
	private static final Rule existentialRule = Expressions.makeRule(
			Expressions.makePositiveConjunction(Expressions.makePositiveLiteral(p, vx, vy),
					Expressions.makePositiveLiteral(p, vx, vz)),
			Expressions.makeConjunction(Expressions.makePositiveLiteral(p, vx)));

	private static KnowledgeBase kb = new KnowledgeBase();
	// fact: P(c)
	private static final Constant constantC = Expressions.makeConstant("c");
	private static final Fact fact = Expressions.makeFact(p, Arrays.asList(constantC));

	// query: P(?x,?y) ?
	final PositiveLiteral queryAtom = Expressions.makePositiveLiteral(p, Expressions.makeVariable("?x"),
			Expressions.makeVariable("?y"));

	static {
		// y,z existential variables that can introduce blanks (anonymous individuals)
		assertEquals(Sets.newSet(vy, vz), existentialRule.getExistentiallyQuantifiedVariables());

		kb.addStatements(existentialRule, fact);
	}

	@Test
	public void testBlanksSkolemChaseNoRuleRewrite() throws IOException {

		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
			assertEquals(RuleRewriteStrategy.NONE, reasoner.getRuleRewriteStrategy());

			reasoner.reason();
			reasoner.exportQueryAnswersToCsv(this.queryAtom, includeBlanksFilePath, true);

			checkTwoDistinctBlanksGenerated(reasoner);

		}
	}

	@Test
	public void testBlanksSkolemChaseSplitHeadPieces() throws IOException {

		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
			// the rule {P(?x) -> P(?x,!y), P(?x,!z)} after split becomes:
			// { {P(?x) -> P(?x,!y,!z)}, {P(?x,?y,?z) ->, P(?x,?y)}, {P(?x,?y,?z) ->,
			// P(?x,?z)} }
			reasoner.setRuleRewriteStrategy(RuleRewriteStrategy.SPLIT_HEAD_PIECES);

			reasoner.reason();
			reasoner.exportQueryAnswersToCsv(this.queryAtom, includeBlanksFilePath, true);

			checkTwoDistinctBlanksGenerated(reasoner);

		}
	}

	@Test
	public void testBlanksRestrictedChaseNoRuleRewrite() throws IOException {

		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			assertEquals(RuleRewriteStrategy.NONE, reasoner.getRuleRewriteStrategy());

			reasoner.reason();
			reasoner.exportQueryAnswersToCsv(this.queryAtom, includeBlanksFilePath, true);

			checkTwoDistinctBlanksGenerated(reasoner);

		}
	}

	@Test
	public void testBlanksRestrictedChaseSplitHeadPieces() throws IOException {

		try (final Reasoner reasoner = new VLogReasoner(kb)) {

			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			// the rule {P(?x) -> P(?x,!y), P(?x,!z)} after split becomes:
			// { {P(?x) -> P(?x,!y,!z)}, {P(?x,?y,?z) ->, P(?x,?y)}, {P(?x,?y,?z) ->,
			// P(?x,?z)} }
			reasoner.setRuleRewriteStrategy(RuleRewriteStrategy.SPLIT_HEAD_PIECES);

			reasoner.reason();
			reasoner.exportQueryAnswersToCsv(this.queryAtom, includeBlanksFilePath, true);

			checkTwoDistinctBlanksGenerated(reasoner);
		}
	}

	private void checkTwoDistinctBlanksGenerated(final Reasoner reasoner) throws IOException {
		// expected facts: P(c, _:b1), P(c, _:b2)
		final List<List<String>> csvContentIncludeBlanks = FileDataSourceTestUtils.getCSVContent(includeBlanksFilePath);
		assertTrue(csvContentIncludeBlanks.size() == 2);
		for (final List<String> queryResult : csvContentIncludeBlanks) {
			assertTrue(queryResult.size() == 2);
			assertEquals(queryResult.get(0), "c");
		}
		final String blank1 = csvContentIncludeBlanks.get(0).get(1);
		final String blank2 = csvContentIncludeBlanks.get(1).get(1);
		assertNotEquals(blank1, blank2);
		assertNotEquals("c", blank1);
		assertNotEquals("c", blank2);

		reasoner.exportQueryAnswersToCsv(this.queryAtom, excludeBlanksFilePath, false);
		final List<List<String>> csvContentExcludeBlanks = FileDataSourceTestUtils.getCSVContent(excludeBlanksFilePath);
		assertTrue(csvContentExcludeBlanks.isEmpty());
	}

}
