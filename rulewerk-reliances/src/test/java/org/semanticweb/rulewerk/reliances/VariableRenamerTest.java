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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

public class VariableRenamerTest {

//	final Rule rule2 = RuleParser.parseRule("r(?X) :- q(?X) .");
//	final Rule rule3 = RuleParser.parseRule("p(?Y,!Z) :- p(?X,?Y) .");

	@Test
	public void renameVariablesDatalogRule() throws ParsingException {
		Rule rule1 = RuleParser.parseRule("q(?X) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("q(?X0001) :- p(?X0001) .");

		assertEquals(rule2, VariableRenamer.rename(rule1, 1));
	}

	@Test
	public void renameVariablesExistentialRule() throws ParsingException {
		Rule rule1 = RuleParser.parseRule("q(?X,!Y) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("q(?X0002,!Y0002) :- p(?X0002) .");

		assertEquals(rule2, VariableRenamer.rename(rule1, 2));
	}

	@Test
	public void renameVariablesExistentialRuleWiThConstant() throws ParsingException {
		Rule rule1 = RuleParser.parseRule("q(?X,!Y),r(a) :- p(?X) .");
		Rule rule2 = RuleParser.parseRule("q(?X0003,!Y0003),r(a) :- p(?X0003) .");

		assertEquals(rule2, VariableRenamer.rename(rule1, 3));
	}

	@Test
	public void renameVariablesExistentialRuleWithNegation() throws ParsingException {
		Rule rule1 = RuleParser.parseRule("r(?X,!Y) :- p(?X),~q(?X) .");
		Rule rule2 = RuleParser.parseRule("r(?X0004,!Y0004) :- p(?X0004),~q(?X0004) .");

		assertEquals(rule2, VariableRenamer.rename(rule1, 4));
	}

}
