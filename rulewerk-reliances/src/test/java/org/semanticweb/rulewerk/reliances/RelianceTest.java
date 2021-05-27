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

package org.semanticweb.rulewerk.reliances;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.parser.RuleParser;

public class RelianceTest {
	
	@Test
	public void noMatchingAtomsTest() throws Exception {
		Rule px_qx = RuleParser.parseRule("q(?X) :- p(?X) .");

		assertFalse(Reliance.positively(px_qx, px_qx));
	}

	@Test
	public void chainUni2UniTest() throws Exception {
		Rule px_qx = RuleParser.parseRule("q(?X) :- p(?X) .");
		Rule qx_rx = RuleParser.parseRule("r(?X) :- q(?X) .");

		assertTrue(Reliance.positively(px_qx, qx_rx));
	}

	@Test
	public void chainUni2ExiTest() throws Exception {
		Rule px_qy = RuleParser.parseRule("q(!Y) :- p(?X) .");
		Rule qx_rx = RuleParser.parseRule("r(?X) :- q(?X) .");

		assertTrue(Reliance.positively(px_qy, qx_rx));
	}

	@Test
	public void chainUni2ConTest() throws Exception {
		Rule px_qc = RuleParser.parseRule("q(c) :- p(?X) .");
		Rule qx_rx = RuleParser.parseRule("r(?X) :- q(?X) .");

		assertTrue(Reliance.positively(px_qc, qx_rx));
	}

	@Test
	public void chainCon2UniTest() throws Exception {
		Rule px_qx = RuleParser.parseRule("q(?X) :- p(?X) .");
		Rule qc_rx = RuleParser.parseRule("r(!X) :- q(c) .");

		assertTrue(Reliance.positively(px_qx, qc_rx));
	}

	@Test
	public void chainCon2ExiTest() throws Exception {
		Rule px_qy = RuleParser.parseRule("q(!Y) :- p(?X) .");
		Rule qc_rx = RuleParser.parseRule("r(!X) :- q(c) .");

		assertFalse(Reliance.positively(px_qy, qc_rx));
	}

	@Test
	public void chainCon2ConTest() throws Exception {
		Rule px_qc = RuleParser.parseRule("q(c) :- p(?X) .");
		Rule qc_rx = RuleParser.parseRule("r(!X) :- q(c) .");
		Rule qd_rx = RuleParser.parseRule("r(!X) :- q(d) .");

		assertTrue(Reliance.positively(px_qc, qc_rx));
		assertFalse(Reliance.positively(px_qc, qd_rx));
	}

	@Test
	public void cyclicUni2UniTest() throws Exception {
		Rule px_qx = RuleParser.parseRule("q(?X) :- p(?X) .");
		Rule qx_px = RuleParser.parseRule("p(?X) :- q(?X) .");

		assertFalse(Reliance.positively(px_qx, qx_px));
	}

	@Test
	public void cyclicUni2ExiTest() throws Exception {
		Rule px_qy = RuleParser.parseRule("q(!Y) :- p(?X) .");
		Rule qx_px = RuleParser.parseRule("p(?X) :- q(?X) .");

		assertFalse(Reliance.positively(px_qy, qx_px));
		assertFalse(Reliance.positively(qx_px, px_qy));
	}

	@Test
	public void cyclicUni2ConTest() throws Exception {
		Rule px_qc = RuleParser.parseRule("q(c) :- p(?X) .");
		Rule qx_px = RuleParser.parseRule("p(?X) :- q(?X) .");

		assertTrue(Reliance.positively(px_qc, qx_px));
		assertTrue(Reliance.positively(qx_px, px_qc));
	}

	@Test
	public void cyclicCon2UniTest() throws Exception {
		Rule px_qx = RuleParser.parseRule("q(?X) :- p(?X) .");
		Rule qc_px = RuleParser.parseRule("p(!X) :- q(c) .");

		assertFalse(Reliance.positively(px_qx, qc_px));
	}

	@Test
	public void cyclicCon2ExiTest() throws Exception {
		Rule px_qy = RuleParser.parseRule("q(!Y) :- p(?X) .");
		Rule qc_px = RuleParser.parseRule("p(!X) :- q(c) .");

		assertFalse(Reliance.positively(px_qy, qc_px));
	}

	@Test
	public void cyclicCon2ConTest() throws Exception {
		Rule px_qc = RuleParser.parseRule("q(c) :- p(?X) .");
		Rule qc_px = RuleParser.parseRule("p(!X) :- q(c) .");
		Rule qd_px = RuleParser.parseRule("p(!X) :- q(d) .");

		assertFalse(Reliance.positively(px_qc, qc_px));
		assertFalse(Reliance.positively(px_qc, qd_px));
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

		assertFalse(Reliance.positively(rule1, rule1));
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

	@Test(expected = IllegalArgumentException.class)
	public void test05a() throws Exception {
		Rule rule1 = RuleParser.parseRule("Q(?X,?Y) :- Q(?X,?Y) .");

		Reliance.positively(rule1, rule1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test05b() throws Exception {
		Rule rule1 = RuleParser.parseRule("Q(?X,?Y) :- Q(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("Q(?Y,?Y) :- Q(?X,?Y) .");

		Reliance.positively(rule2, rule1);
	}

	@Test
	public void test06() throws Exception {
		Rule rule1 = RuleParser.parseRule("Q(?X,?Y) :- P(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("P(?X,!Z) :- Q(?X,?Y) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
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

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
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
		Rule rule1 = RuleParser.parseRule("P(?Y,?X) :- P(?X,?Y) .");

		assertFalse(Reliance.positively(rule1, rule1));
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
	
	@Test
	public void test15() throws Exception {
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
	public void rulesNotApplicable() throws Exception {
		Rule rule1 = RuleParser.parseRule("P(!U,?Y) :- P(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("P(?Y,?Y) :- P(?X,?Y) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}
	
}

