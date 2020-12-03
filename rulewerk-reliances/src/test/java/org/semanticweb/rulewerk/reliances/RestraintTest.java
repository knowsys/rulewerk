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

public class RestraintTest {

	@Test
	public void falseDueToBlockingTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X,!Y) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("q(?X,!Y) :- r(?X) .");

		assertFalse(Restraint.restraint(rule1, rule1));
		assertFalse(Restraint.restraint(rule1, rule2));
		assertFalse(Restraint.restraint(rule2, rule1));
		assertFalse(Restraint.restraint(rule2, rule2));
	}

	@Test
	public void singleAtomPiece() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X,?Y) :- p(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("q(?X,!Y) :- r(?X) .");

		assertFalse(Restraint.restraint(rule1, rule1));
		assertTrue(Restraint.restraint(rule1, rule2));
		assertFalse(Restraint.restraint(rule2, rule1));
		assertFalse(Restraint.restraint(rule2, rule2));
	}

	@Test
	public void twoVariablesIntoOneTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("b(?X,!Y,!Y) :- a(?X) .");
		Rule rule2 = RuleParser.parseRule("b(?X,!Y,!Z) :- a(?X) .");

		assertFalse(Restraint.restraint(rule1, rule1));
		assertTrue(Restraint.restraint(rule1, rule2));
		assertFalse(Restraint.restraint(rule2, rule1));
		assertFalse(Restraint.restraint(rule2, rule2));
	}

	@Test
	public void singleUnconnectedPieceTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("b(!Y) :- a(?X) .");

		assertFalse(Restraint.restraint(rule1, rule1));
	}

	@Test
	public void unconnectedPieceTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("b(?X),c(!Y) :- a(?X) .");

		assertTrue(Restraint.restraint(rule1, rule1));
	}

	@Test
	public void successorPredecesorTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(!Y,?X), q(?X,!Z) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("q(?X,!Y) :- r(?X) .");

		assertFalse(Restraint.restraint(rule1, rule1));
		assertTrue(Restraint.restraint(rule1, rule2));
		assertFalse(Restraint.restraint(rule2, rule1));
		assertFalse(Restraint.restraint(rule2, rule2));
	}

	@Test
	public void successorPredecesorWithExtraAtomTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(!Y,?X), q(?X,!Z) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("q(?X,!Y), s(!Y) :- r(?X) .");

		assertFalse(Restraint.restraint(rule1, rule1));
		assertFalse(Restraint.restraint(rule1, rule2));
		assertTrue(Restraint.restraint(rule2, rule1));
		assertFalse(Restraint.restraint(rule2, rule2));
	}

	@Test
	public void successorPredecesorWithExtraAtomToUniversalVarTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(!Y,?X), q(?X,?Z) :- p(?X,?Z) .");
		Rule rule2 = RuleParser.parseRule("q(?X,!Y), s(!Y) :- r(?X) .");

		assertTrue(Restraint.restraint(rule1, rule1));
		assertFalse(Restraint.restraint(rule1, rule2));
		assertFalse(Restraint.restraint(rule2, rule1));
		assertFalse(Restraint.restraint(rule2, rule2));
	}

	@Test
	public void unifyTwoAtomsIntoOneTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X,!Y) :- r(?X) .");
		Rule rule2 = RuleParser.parseRule("q(?X,!Z), q(?Y,!Z) :- p(?X,?Y) .");

		assertFalse(Restraint.restraint(rule1, rule1));
		assertFalse(Restraint.restraint(rule1, rule2));
		assertTrue(Restraint.restraint(rule2, rule1));
		assertTrue(Restraint.restraint(rule2, rule2));
	}

	@Test
	public void blockingRestraintTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("b(?X,!Y,!Y),c(!Y,!Z) :- a(?X) .");
		Rule rule2 = RuleParser.parseRule("b(?X,!Y,!Z),c(!Z,!Z) :- a(?X) .");

		assertFalse(Restraint.restraint(rule1, rule1));
		assertFalse(Restraint.restraint(rule1, rule2));
		assertFalse(Restraint.restraint(rule2, rule1));
		assertFalse(Restraint.restraint(rule2, rule2));
	}

	@Test
	public void MarkussExampleTest() throws Exception {
		Rule rule1 = RuleParser.parseRule("r(?X,!V,!W), r(?X,?X,!W), a(!V) :- b(?X) .");

		assertTrue(Restraint.restraint(rule1, rule1));
	}

}
