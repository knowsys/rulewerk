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
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

public class PositiveDependencyTest {

	@Test
	public void simpleDatalogRuleTest() throws ParsingException {
		Rule rule1 = RuleParser.parseRule("q(?X) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("r(?X) :- q(?X) .");

		assertFalse(PositiveDependency.reliesPositivelyOn(rule1, rule1));
		assertTrue(PositiveDependency.reliesPositivelyOn(rule1, rule2));
		assertFalse(PositiveDependency.reliesPositivelyOn(rule2, rule1));
		assertFalse(PositiveDependency.reliesPositivelyOn(rule2, rule2));
	}

	@Test
	public void simpleExistentialRuleTest() throws ParsingException {
		Rule rule1 = RuleParser.parseRule("q(?X,!Y) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("r(?X,?Y) :- q(?X,?Y) .");

		assertFalse(PositiveDependency.reliesPositivelyOn(rule1, rule1));
		assertTrue(PositiveDependency.reliesPositivelyOn(rule1, rule2));
		assertFalse(PositiveDependency.reliesPositivelyOn(rule2, rule1));
		assertFalse(PositiveDependency.reliesPositivelyOn(rule2, rule2));
	}

	@Test
	public void complexExistentialRuleTest() throws ParsingException {
		Rule rule1 = RuleParser.parseRule("r(?X,!Y,!Z) :- a(?X) .");
		Rule rule2 = RuleParser.parseRule("b(?X1,?X2) :- r(c,?X1, ?Y1), r(c,?X2, ?Y2) .");

		assertFalse(PositiveDependency.reliesPositivelyOn(rule1, rule1));
		assertFalse(PositiveDependency.reliesPositivelyOn(rule1, rule2));
		assertFalse(PositiveDependency.reliesPositivelyOn(rule2, rule1));
		assertFalse(PositiveDependency.reliesPositivelyOn(rule2, rule2));
	}

}
