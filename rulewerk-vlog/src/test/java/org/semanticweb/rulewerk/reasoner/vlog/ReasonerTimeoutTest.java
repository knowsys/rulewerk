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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.Algorithm;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

/**
 * Test case ensuring {@link Reasoner#setReasoningTimeout(Integer)} works as
 * expected and terminates reasoning after the given {@link #timeout}. Results
 * are accepted within one second to account for setup and tear down of
 * reasoning resources.
 *
 * @author Adrian Bielefeldt
 *
 */
public class ReasonerTimeoutTest {

	/**
	 * The timeout after which reasoning should be completed in seconds.
	 */
	private static int timeout = 1;

	/**
	 * A list of facts to be used in multiple test runs.
	 */
	private static List<Fact> facts = new ArrayList<>();
	/**
	 * A list of rules to be used in multiple test runs.
	 */
	private static List<Rule> rules = new ArrayList<>();

	private Reasoner reasoner;

	private final static KnowledgeBase kb = new KnowledgeBase();

	/**
	 * The timeout after which reasoning should be completed.
	 */
	@org.junit.Rule
	public Timeout globalTimeout = Timeout.seconds(timeout * 5);

	private final static Predicate infinite_EDB = Expressions.makePredicate("infinite_EDB", 2);
	private final static Predicate infinite_IDB = Expressions.makePredicate("infinite_IDB", 2);
	private final static Variable x = Expressions.makeUniversalVariable("x");
	private final static Variable y = Expressions.makeUniversalVariable("y");
	private final static Variable z = Expressions.makeExistentialVariable("z");

	private final static PositiveLiteral infinite_IDB_xy = Expressions.makePositiveLiteral(infinite_IDB, x, y);
	private final static PositiveLiteral infinite_EDB_xy = Expressions.makePositiveLiteral(infinite_EDB, x, y);
	private final static PositiveLiteral infinite_IDB_yz = Expressions.makePositiveLiteral(infinite_IDB, y, z);

	private final static Rule infinite_rule = Expressions.makeRule(infinite_IDB_yz, infinite_IDB_xy);

	/**
	 * This method provides the {@link #facts} and {@link #rules} to be used in all
	 * test runs. To test if the timeout works as expected, a small set of facts and
	 * rules is used that results in an infinite chase. Facts: infinite_EDB(A, B)
	 * Rules: infinite_IDB(?x, ?y) :- infinite_EDB(?x, ?y) infinite_IDB(?y, ?z) :-
	 * infinite_IDB(?x, ?y)
	 */
	@BeforeClass
	public static void setUpBeforeClass() {

		facts.add(Expressions.makeFact(infinite_EDB,
				Arrays.asList(Expressions.makeAbstractConstant("A"), Expressions.makeAbstractConstant("B"))));

		final Rule import_rule = Expressions.makeRule(infinite_IDB_xy, infinite_EDB_xy);
		rules.add(import_rule);

		rules.add(infinite_rule);

		kb.addStatements(rules);
		kb.addStatements(facts);
	}

	@Before
	public void setUp() {
		this.reasoner = new VLogReasoner(kb);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetReasoningTimeout() {
		try (final Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			reasoner.setReasoningTimeout(-3);
		}
	}

	@Test
	public void skolem() throws IOException {
		this.reasoner.setReasoningTimeout(timeout);
		this.reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);

		assertFalse(this.reasoner.reason());
	}

	@Test
	public void restricted() throws IOException {
		this.reasoner.setReasoningTimeout(timeout);
		this.reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);

		assertFalse(this.reasoner.reason());
	}

	@Test
	public void skolemAfterLoad() throws IOException {
		this.reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);

		this.reasoner.setReasoningTimeout(timeout);

		assertFalse(this.reasoner.reason());
	}

	@Test
	public void restrictedAfterLoad() throws IOException {
		this.reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);

		this.reasoner.setReasoningTimeout(timeout);

		assertFalse(this.reasoner.reason());
	}

	@Test
	public void resetReasoningTimeoutToNull() throws IOException {
		this.reasoner.setReasoningTimeout(timeout);

		this.reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
		assertFalse(this.reasoner.reason());

		this.reasoner.resetReasoner();

		final PositiveLiteral blocking_IDB_yx = Expressions.makePositiveLiteral(infinite_IDB, y, x);
		final Rule blockingRule = Expressions.makeRule(blocking_IDB_yx, infinite_IDB_xy);
		kb.addStatement(blockingRule);

		this.reasoner.setReasoningTimeout(null);
		assertTrue(this.reasoner.reason());
	}

	@After
	public void tearDown() {
		this.reasoner.close();
	}
}
