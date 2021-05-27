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

import java.io.IOException;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

//TODO add more tests
public class RuleUtilTest {

	@Test
	public void isApplicable_001() throws ParsingException, IOException {
		Rule qy_px = RuleParser.parseRule("q(!Y) :- p(?X) .");
		assertTrue(RuleUtil.isApplicable(qy_px));
	}

	@Test
	public void isApplicable_002() throws ParsingException, IOException {
		Rule qc_qc = RuleParser.parseRule("q(c) :- q(c) .");
		assertFalse(RuleUtil.isApplicable(qc_qc));
	}

	@Test
	public void isApplicable_003() throws ParsingException, IOException {
		Rule qy_qc = RuleParser.parseRule("q(!Y) :- q(c) .");
		assertFalse(RuleUtil.isApplicable(qy_qc));
	}

	@Test
	public void isApplicable_004() throws ParsingException, IOException {
		Rule qc_qx = RuleParser.parseRule("q(c) :- q(?X) .");
		assertTrue(RuleUtil.isApplicable(qc_qx));
	}

	@Test
	public void isApplicable_005() throws ParsingException, IOException {
		Rule qy_qx = RuleParser.parseRule("q(!Y) :- q(?X) .");
		assertFalse(RuleUtil.isApplicable(qy_qx));
	}

	@Test
	public void isApplicable_006() throws ParsingException, IOException {
		Rule qx_qx = RuleParser.parseRule("q(?X) :- q(?X) .");
		assertFalse(RuleUtil.isApplicable(qx_qx));
	}

	@Test
	public void isApplicable_007() throws ParsingException, IOException {
		Rule pxz_pxy = RuleParser.parseRule("p(?X,!Z) :- p(?X,?Y) .");
		assertFalse(RuleUtil.isApplicable(pxz_pxy));
	}

	@Test
	public void isApplicable_008() throws ParsingException, IOException {
		Rule qxy_pxy = RuleParser.parseRule("q(?X,?Y) :- p(?X,?Y) .");
		assertTrue(RuleUtil.isApplicable(qxy_pxy));
	}

	@Test
	public void isApplicable_009() throws ParsingException, IOException {
		Rule pxz_qxy = RuleParser.parseRule("p(?X,!Z) :- q(?X,?Y) .");
		assertTrue(RuleUtil.isApplicable(pxz_qxy));
	}

	@Test
	public void containsRepeatedAtoms_001() throws ParsingException, IOException {
		Rule qy_px = RuleParser.parseRule("q(!Y) :- p(?X) .");
		assertFalse(RuleUtil.containsRepeatedAtoms(qy_px));
	}

	@Test
	public void containsRepeatedAtoms_002() throws ParsingException, IOException {
		Rule qc_qc = RuleParser.parseRule("q(c) :- q(c) .");
		assertTrue(RuleUtil.containsRepeatedAtoms(qc_qc));
	}

	@Test
	public void containsRepeatedAtoms_003() throws ParsingException, IOException {
		Rule qy_qc = RuleParser.parseRule("q(!Y) :- q(c) .");
		assertFalse(RuleUtil.containsRepeatedAtoms(qy_qc));
	}

	@Test
	public void containsRepeatedAtoms_004() throws ParsingException, IOException {
		Rule qc_qx = RuleParser.parseRule("q(c) :- q(?X) .");
		assertFalse(RuleUtil.containsRepeatedAtoms(qc_qx));
	}

	@Test
	public void containsRepeatedAtoms_005() throws ParsingException, IOException {
		Rule qy_qx = RuleParser.parseRule("q(!Y) :- q(?X) .");
		assertFalse(RuleUtil.containsRepeatedAtoms(qy_qx));
	}

	@Test
	public void containsRepeatedAtoms_006() throws ParsingException, IOException {
		Rule qx_qx = RuleParser.parseRule("q(?X) :- q(?X) .");
		assertTrue(RuleUtil.containsRepeatedAtoms(qx_qx));
	}

	@Test
	public void containsRepeatedAtoms_007() throws ParsingException, IOException {
		Rule pxz_pxy = RuleParser.parseRule("p(?X,!Z) :- p(?X,?Y) .");
		assertFalse(RuleUtil.containsRepeatedAtoms(pxz_pxy));
	}

}
