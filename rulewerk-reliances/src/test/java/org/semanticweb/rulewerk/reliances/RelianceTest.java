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
	public void testtest01() throws Exception {
		Rule rule1 = RuleParser.parseRule("q(?X,?Y) :- p(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("r(?X,!Z) :- q(?X,?Y), q(?Y,?X) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertTrue(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void testtest02() throws Exception {
		Rule rule1 = RuleParser.parseRule(
				"cancerDisease(?Xdoid) :- diseaseHierarchy(?X, ?Y), doid(?Y, \"DOID:162\"), doid(?X, ?Xdoid) .");
		Rule rule2 = RuleParser.parseRule(
				"humansWhoDiedOfNoncancer(?X) :- deathCause(?X, ?Y), diseaseId(?Y, ?Z), ~cancerDisease(?Z) .");

		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
		assertFalse(Reliance.positively(rule2, rule1));
		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void testtest03() throws Exception {
		Rule rule1 = RuleParser.parseRule("S(?Y,?X) :- R(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("R(?Y,?X) :- S(?X,?Y) .");

//		assertFalse(Reliance.positively(rule1, rule1));
		assertFalse(Reliance.positively(rule1, rule2));
//		assertFalse(Reliance.positively(rule2, rule1));
//		assertFalse(Reliance.positively(rule2, rule2));
	}

	@Test
	public void testtest04() throws Exception {
		Rule rule1 = RuleParser.parseRule("S(?Y,?X), P(?X) :- R(?X,?Y) .");
		Rule rule2 = RuleParser.parseRule("R(?X,?Y) :- S(?Y,?X) ."); //there is something wrong here

		assertFalse(Reliance.positively(rule1, rule1));
		//assertFalse(Reliance.positively(rule1, rule2)); // one of these should be true
		//assertFalse(Reliance.positively(rule2, rule1)); // one of these should be true
		assertFalse(Reliance.positively(rule2, rule2));
	}

	
}
