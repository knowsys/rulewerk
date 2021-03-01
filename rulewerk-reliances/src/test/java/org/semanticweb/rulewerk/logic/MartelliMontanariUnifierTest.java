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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.math.mapping.PartialMapping;
import org.semanticweb.rulewerk.parser.RuleParser;

public class MartelliMontanariUnifierTest {

	PartialMapping pm = new PartialMapping(new int[] { 0 }, 1);
	List<Literal> l1 = new ArrayList<>();
	List<Literal> l2 = new ArrayList<>();

	@Test
	public void test01() throws Exception {
		l1.clear();
		l2.clear();
		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(!X2,!X2)"));

		MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(l1, l2, pm);

		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test02() throws Exception {
		l1.clear();
		l2.clear();
		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(?X2,c)"));

		MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(l1, l2, pm);

		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test03() throws Exception {
		l1.clear();
		l2.clear();
		l1.add(RuleParser.parseLiteral("r(?X10001, !Y10001, !Z10001)"));
		l2.add(RuleParser.parseLiteral("r(c, ?X20002, ?Y20002)"));

		MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(l1, l2, pm);

		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test04() throws Exception {
		l1.clear();
		l2.clear();
		l1.add(RuleParser.parseLiteral("p(?X)"));
		l2.add(RuleParser.parseLiteral("q(?X)"));

		MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(l1, l2, pm);

		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test05() throws Exception {
		l1.clear();
		l2.clear();
		l1.add(RuleParser.parseLiteral("p(?Y,?X)"));
		l2.add(RuleParser.parseLiteral("p(?X,?Y)"));

		MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(l1, l2, pm);

		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test06() throws Exception {
		l1.clear();
		l2.clear();
		l1.add(RuleParser.parseLiteral("p(!Y,!X)"));
		l2.add(RuleParser.parseLiteral("p(!X,!Y)"));

		MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(l1, l2, pm);

		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test07() throws Exception {
		l1.clear();
		l2.clear();
		l1.add(RuleParser.parseLiteral("p(?x1,?x1,?x1)"));
		l2.add(RuleParser.parseLiteral("p(?x2,c1,c2)"));

		MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(l1, l2, pm);

		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test08() throws Exception {
		l1.clear();
		l2.clear();
		l1.add(RuleParser.parseLiteral("p(c)"));
		l2.add(RuleParser.parseLiteral("p(c)"));

		MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(l1, l2, pm);

		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test09() throws Exception {
		l1.clear();
		l2.clear();
		l1.add(RuleParser.parseLiteral("p(c)"));
		l2.add(RuleParser.parseLiteral("p(d)"));

		MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(l1, l2, pm);

		assertFalse(unifier.getSuccess());
	}
}
