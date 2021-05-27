package org.semanticweb.rulewerk.logic;

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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.parser.RuleParser;

public class MartelliMontanariUnifierTest {

	Map<Literal, Literal> m = new HashMap<>();
	MartelliMontanariUnifier unifier;

	@Test(expected = IllegalArgumentException.class)
	public void test01() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("q(?X1,?X1)"), RuleParser.parseLiteral("q(!X2,!X2)"));

		unifier = new MartelliMontanariUnifier(m);
		assertTrue(unifier.isSuccessful());
	}

	@Test
	public void test02() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("q(?X1,?X1)"), RuleParser.parseLiteral("q(?X2,c)"));

		unifier = new MartelliMontanariUnifier(m);
		assertTrue(unifier.isSuccessful());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test03() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("r(?X10001, !Y10001, !Z10001)"),
				RuleParser.parseLiteral("r(c, ?X20002, ?Y20002)"));

		unifier = new MartelliMontanariUnifier(m);
		assertTrue(unifier.isSuccessful());
	}

	@Test
	public void test04() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("p(?X)"), RuleParser.parseLiteral("q(?X)"));

		unifier = new MartelliMontanariUnifier(m);
		assertFalse(unifier.isSuccessful());
	}

	@Test
	public void test05() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("p(?Y,?X)"), RuleParser.parseLiteral("p(?X,?Y)"));

		unifier = new MartelliMontanariUnifier(m);
		assertTrue(unifier.isSuccessful());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test06() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("p(!Y,!X)"), RuleParser.parseLiteral("p(!X,!Y)"));

		unifier = new MartelliMontanariUnifier(m);
		assertTrue(unifier.isSuccessful());
	}

	@Test
	public void test07() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("p(?x1,?x1,?x1)"), RuleParser.parseLiteral("p(?x2,c1,c2)"));

		unifier = new MartelliMontanariUnifier(m);
		assertFalse(unifier.isSuccessful());
	}

	@Test
	public void test08() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("p(c)"), RuleParser.parseLiteral("p(c)"));

		unifier = new MartelliMontanariUnifier(m);
		assertTrue(unifier.isSuccessful());
	}

	@Test
	public void test09() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("p(c)"), RuleParser.parseLiteral("p(d)"));

		unifier = new MartelliMontanariUnifier(m);
		assertFalse(unifier.isSuccessful());
	}
	
	@Test
	public void test10() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("p(?Y,?X)"), RuleParser.parseLiteral("p(?X,?Y)"));

		unifier = new MartelliMontanariUnifier(m);
		assertTrue(unifier.isSuccessful());
	}
	
	@Test
	public void test11() throws Exception {
		m.clear();
		m.put(RuleParser.parseLiteral("r(?X1, ?Y1, ?Z1)"),
				RuleParser.parseLiteral("r(c, ?X2, ?Y2)"));

		unifier = new MartelliMontanariUnifier(m);
		assertTrue(unifier.isSuccessful());
	}
}
