package org.semanticweb.vlog4j.core.reasoner;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;

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

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

/**
 * Test case ensuring {@link Reasoner#setReasoningTimeout(Integer)} works as expected and terminates reasoning after the given {@link #timeout}.
 * Results are accepted within one second to account for setup and tear down of reasoning resources.
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
	private static List<PositiveLiteral> facts = new ArrayList<>();
	/**
	 * A list of rules to be used in multiple test runs.
	 */
	private static List<Rule> rules = new ArrayList<>();
	
	private Reasoner reasoner;
	
	/**
	 * The timeout after which reasoning should be completed. One second is added to account for setup and tear down of reasoning resources.
	 */
	@org.junit.Rule
	public Timeout globalTimeout = Timeout.seconds(timeout + 1);

	/**
	 * This method provides the {@link #facts} and {@link #rules} to be used in all test runs.
	 * To test if the timeout works as expected, a small set of facts and rules is used that results in an infinite chase.
	 * Facts:
	 * 	infinite_EDB(A, B)
	 * Rules:
	 * 	infinite_IDB(?x, ?y) :- infinite_EDB(?x, ?y) 
	 * 	infinite_IDB(?y, ?z) :- infinite_IDB(?x, ?y)
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		final Predicate infinite_EDB = makePredicate("infinite_EDB", 2);
		final Predicate infinite_IDB = makePredicate("infinite_IDB", 2);
		
		facts.add(makePositiveLiteral(infinite_EDB, makeConstant("A"), makeConstant("B")));
		
		final Variable x = makeVariable("x");
		final Variable y = makeVariable("y");

		final PositiveLiteral infinite_IDB_xy = makePositiveLiteral(infinite_IDB, x, y);
		final PositiveLiteral infinite_EDB_xy = makePositiveLiteral(infinite_EDB, x, y);
		
		final Rule import_rule = makeRule(infinite_IDB_xy, infinite_EDB_xy);
		rules.add(import_rule);
		
		final Variable z = makeVariable("z");
		
		final PositiveLiteral infinite_IDB_yz = makePositiveLiteral(infinite_IDB, y, z);
		final Rule infinite_rule = makeRule(infinite_IDB_yz, infinite_IDB_xy);
		rules.add(infinite_rule);
	}

	@Before
	public void setUp() throws ReasonerStateException {
		reasoner = Reasoner.getInstance();
		
		reasoner.addFacts(facts);
		reasoner.addRules(rules);
	}

	@Test
	public void skolem() throws EdbIdbSeparationException, IncompatiblePredicateArityException, IOException, ReasonerStateException {
		reasoner.setReasoningTimeout(timeout);
		reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
		
		reasoner.load();
		
		reasoner.reason();
	}
	
	@Test
	public void restricted() throws EdbIdbSeparationException, IncompatiblePredicateArityException, IOException, ReasonerStateException {
		reasoner.setReasoningTimeout(timeout);
		reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
		
		reasoner.load();
		
		reasoner.reason();
	}
	
	@Test
	public void skolemAfterLoad() throws EdbIdbSeparationException, IncompatiblePredicateArityException, IOException, ReasonerStateException {
		reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
		
		reasoner.load();
		
		reasoner.setReasoningTimeout(timeout);
		
		reasoner.reason();
	}
	
	@Test
	public void restrictedAfterLoad() throws EdbIdbSeparationException, IncompatiblePredicateArityException, IOException, ReasonerStateException {
		reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
		
		reasoner.load();
		
		reasoner.setReasoningTimeout(timeout);
		
		reasoner.reason();
	}
	
	@After
	public void tearDown() {
		reasoner.close();
	}
}
