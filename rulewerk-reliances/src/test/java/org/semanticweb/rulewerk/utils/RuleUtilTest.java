package org.semanticweb.rulewerk.utils;

/*-
 * #%L
 * Rulewerk Reliances
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

//TODO add more tests
public class RuleUtilTest {

	@Test
	public void isRuleApplicable_001() throws ParsingException {
		Rule qy_px = RuleParser.parseRule("q(!Y) :- p(?X) .");

		assertTrue(RuleUtil.isRuleApplicable(qy_px));
	}

	@Test
	public void isRuleApplicable_002() throws ParsingException {
		Rule qc_qc = RuleParser.parseRule("q(c) :- q(c) .");

		assertFalse(RuleUtil.isRuleApplicable(qc_qc));
	}

	@Test
	public void isRuleApplicable_003() throws ParsingException {
		Rule qy_qc = RuleParser.parseRule("q(!Y) :- q(c) .");

		assertFalse(RuleUtil.isRuleApplicable(qy_qc));
	}

	@Test
	public void isRuleApplicable_004() throws ParsingException {
		Rule qc_qx = RuleParser.parseRule("q(c) :- q(?X) .");

		assertTrue(RuleUtil.isRuleApplicable(qc_qx));
	}

	@Test
	public void isRuleApplicable_005() throws ParsingException {
		Rule qy_qx = RuleParser.parseRule("q(!Y) :- q(?X) .");

		assertFalse(RuleUtil.isRuleApplicable(qy_qx));
	}

	@Test
	public void isRuleApplicable_006() throws ParsingException {
		Rule qx_qx = RuleParser.parseRule("q(?X) :- q(?X) .");

		assertFalse(RuleUtil.isRuleApplicable(qx_qx));
	}
}
