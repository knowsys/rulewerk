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

public class SelfRestraintTest {

	// TODO add tests with constants
	@Test
	public void datalogRule01() throws Exception {
		Rule rule = RuleParser.parseRule("q(?X) :- p(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void datalogRule02() throws Exception {
		Rule rule = RuleParser.parseRule("q(?X,?X) :- p(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void datalogRule03() throws Exception {
		Rule rule = RuleParser.parseRule("q(?X,?Y) :- p(?X,?Y) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void datalogRule04() throws Exception {
		Rule rule = RuleParser.parseRule("q(?X,?Y), q(?Y,?Z) :- p(?X,?Y,?Z) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void datalogRule05() throws Exception {
		Rule rule = RuleParser.parseRule("r(?X,?Y), r(?Y,?Z) :- p(?X,?Y), q(?Y,?Z) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void datalogRule06() throws Exception {
		Rule rule = RuleParser.parseRule("r(?X,?Y,?Z), s(?X,?Y,?Z) :- p(?X,?Y), q(?Y,?Z) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule01() throws Exception {
		Rule rule = RuleParser.parseRule("q(?X,!Y) :- r(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule02() throws Exception {
		Rule rule = RuleParser.parseRule("b(?X,!Y,!Y) :- a(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule03() throws Exception {
		Rule rule = RuleParser.parseRule("b(?X,!Y,!Z) :- a(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule04() throws Exception {
		Rule rule = RuleParser.parseRule("b(!Y) :- a(?X) .");

		assertTrue(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule05() throws Exception {
		Rule rule = RuleParser.parseRule("b(?X),c(!Y) :- a(?X) .");

		assertTrue(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule06() throws Exception {
		Rule rule = RuleParser.parseRule("q(!Y,?X), q(?X,!Z) :- p(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule07() throws Exception {
		Rule rule = RuleParser.parseRule("q(?X,!Y), s(!Y) :- r(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule08() throws Exception {
		Rule rule = RuleParser.parseRule("q(!Y,?X), q(?X,?Z) :- p(?X,?Z) .");

		assertTrue(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule09() throws Exception {
		Rule rule = RuleParser.parseRule("q(?X,!Z), q(?Y,!Z) :- p(?X,?Y) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule10() throws Exception {
		Rule rule = RuleParser.parseRule("b(?X,!Y,!Y),c(!Y,!Z) :- a(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule11() throws Exception {
		Rule rule = RuleParser.parseRule("b(?X,!Y,!Z),c(!Z,!Z) :- a(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule12() throws Exception {
		Rule rule = RuleParser.parseRule("r(?X,!V,!W), r(?X,?X,!W), a(!V) :- b(?X) .");

		assertTrue(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule13() throws Exception {
		Rule rule = RuleParser.parseRule("b(?X,!Y), c(!Y) :- a(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule14() throws Exception {
		Rule rule = RuleParser.parseRule("r(?X, ?Y, !Z), r(?X, !Z, ?Y) :- b(?X,?Y) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule15() throws Exception {
		Rule rule = RuleParser.parseRule("r(?X, !U, !V), r(?X, !W, !V), s(!U, !V, !W) :- b(?X) .");

		assertFalse(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule16() throws Exception {
		Rule rule = RuleParser.parseRule("q(?X,!U,!V), q(?Y,!V,!U), q(?Z,!V,!W) :- p(?X,?Y,?Z) .");

		assertTrue(SelfRestraint.restraint(rule));
	}

	@Test
	public void existentialRule17() throws Exception {
		Rule rule = RuleParser.parseRule("q(?X,?Y), q(?X,!U), q(?Y,!U) :- p(?X,?Y) .");

		assertTrue(Restraint.restraint(rule, rule));
	}
}
