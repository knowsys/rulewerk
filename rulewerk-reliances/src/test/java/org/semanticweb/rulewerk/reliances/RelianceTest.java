package org.semanticweb.rulewerk.reliances;

/*-
 * #%L
 * Rulewerk Reliances
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

import org.junit.Test;

import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.parser.RuleParser;

public class RelianceTest {

	@Test
	public void simpleDatalogRuleTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("r(?X) :- q(?X) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertTrue(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void cyclicDependency() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("p(?X) :- q(?X) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void verySimpleExistentialRuleTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(!Y) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("r(!Z) :- q(c) .");
		assertFalse(Reliance.positively(rule1, rule1));
		//assertFalse(Reliance.positively(rule1, rule2)); // THIS IS A BUG. IT SHOULD BE FALSE
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void simpleExistentialRuleTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X,!Y) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("r(?X,?Y) :- q(?X,?Y) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertTrue(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void complexExistentialRuleTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("r(?X1,!Y1,!Z1) :- a(?X1) .");
		Rule rule2 = RuleParser.parseRule("b(?X2,?X3) :- r(c,?X2, ?Y2), r(c,?X3, ?Y3) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertTrue(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void test01() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X,?Y) :- p(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("r(?X,!Z) :- q(?X,?Y), q(?Y,?X) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertTrue(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void test02() throws Exception {
		Rule rule1 = RuleParser.parseRule("cd(?Xdoid) :- dh(?X, ?Y), doid(?Y, \"DOID:162\"), doid(?X, ?Xdoid) .");
		Rule rule2 = RuleParser.parseRule("hwdonc(?X) :- dc(?X, ?Y), id(?Y, ?Z), ~cD(?Z) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void test03() throws Exception {
		Rule rule1 = RuleParser.parseRule("S(?Y,?X) :- R(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("R(?Y,?X) :- S(?X,?Y) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void test04() throws Exception {
		Rule rule1 = RuleParser.parseRule("S(?Y,?X), P(?X) :- R(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("R(?X,?Y) :- S(?Y,?X) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertTrue(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void recursiveRuleTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("p(?Y,!Z) :- p(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("p(?X,!Z) :- p(?X,?Y) .");

		assertTrue(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void transitiveClosure1Test() throws Exception {
		Rule rule1 = RuleParser.parseRule("Q(?X,?Y) :- P(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("Q(?X,?Z) :- Q(?X,?Y), Q(?Y,?Z) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertTrue(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertTrue(Reliance.positively(rule2, rule2));
	}

	@Test
	public void transitiveClosure2Test() throws Exception {
		Rule rule1 = RuleParser.parseRule("Q(?X,?Y) :- P(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("Q(?X,?Z) :- P(?X,?Y), Q(?Y,?Z) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertTrue(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertTrue(Reliance.positively(rule2, rule2));
	}

	@Test
	public void test05() throws Exception {
		Rule rule1 = RuleParser.parseRule("Q(?X,?Y) :- Q(?X,?Y) .");

		assertFalse(Reliance.positively(rule1, rule1));
	}

	@Test
	public void test06() throws Exception {
		Rule rule1 = RuleParser.parseRule("Q(?X,?Y) :- P(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("P(?X,!Z) :- Q(?X,?Y) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertTrue(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void test07() throws Exception {
		Rule rule1 = RuleParser.parseRule("P(?X,!Z) :- P(?X,?Y) .");

		assertFalse(Reliance.positively(rule1, rule1));
	}

	@Test
	public void test08() throws Exception {
		Rule rule1 = RuleParser.parseRule("P(?X,!U), P(!U,!V) :- P(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("P(?X,!U), P(!U,!V) :- Q(?X,?Y) .");

		assertTrue(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertTrue(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void test09() throws Exception {
		Rule rule1 = RuleParser.parseRule("P(!U,?Y,?Z) :- P(?X,?Y,?Z) .");
		Rule rule2 = RuleParser.parseRule("P(?X,!U,?Z) :- P(?X,?Y,?Z) .");
		Rule rule3 = RuleParser.parseRule("P(?X,?Y,!U) :- P(?X,?Y,?Z) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule1, rule3));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
		assertFalse(Reliance.positively(rule2, rule3));
		assertFalse(Reliance.positively(rule3, rule1));
		assertFalse(Reliance.positively(rule3, rule2));
		assertFalse(Reliance.positively(rule3, rule3));
	}

	@Test
	public void test10() throws Exception {
		Rule rule1 = RuleParser.parseRule("P(!U,?Z,?Y) :- P(?X,?Y,?Z) .");
		Rule rule2 = RuleParser.parseRule("P(?Z,!U,?X) :- P(?X,?Y,?Z) .");
		Rule rule3 = RuleParser.parseRule("P(?Y,?X,!U) :- P(?X,?Y,?Z) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertTrue(Reliance.positively(rule1, rule2));
		assertTrue(Reliance.positively(rule1, rule3));
		assertTrue(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
		assertTrue(Reliance.positively(rule2, rule3));
		assertTrue(Reliance.positively(rule3, rule1));
		assertTrue(Reliance.positively(rule3, rule2));
		assertFalse(Reliance.positively(rule3, rule3));
	}

	@Test
	public void test11() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X,!U,!V), q(?Y,!V,!U), q(?Z,!V,!W) :- q(?X,?Y,?Z) .");

		assertTrue(Reliance.positively(rule1, rule1));
	}

	@Test
	public void test12() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(!U,!V,!W) :- q(?X,?Y,?Z) .");

		assertFalse(Reliance.positively(rule1, rule1));
	}

	@Test
	public void test13() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(!Y) :- q(?X) .");

		assertFalse(Reliance.positively(rule1, rule1));
	}

	@Test
	public void test14() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("r(!Y) :- q(c) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertTrue(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}
}
