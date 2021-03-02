package org.semanticweb.rulewerk.logic;

/*-
 * #%L
 * Rulewerk Reliances
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"));
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
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.math.mapping.PartialMapping;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

public class RespectingUnifierTest {

	PartialMapping pm = new PartialMapping(new int[] { 0 }, 1);
	List<Literal> l1 = new ArrayList<>();
	List<Literal> l2 = new ArrayList<>();

	@Test
	public void differentPredicateName() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(?X1)"));
		l2.add(RuleParser.parseLiteral("q(?X1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test00() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(c1)"));
		l2.add(RuleParser.parseLiteral("p(c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test01() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(c1)"));
		l2.add(RuleParser.parseLiteral("p(c2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test02() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(c1)"));
		l2.add(RuleParser.parseLiteral("p(!X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test03() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(c1)"));
		l2.add(RuleParser.parseLiteral("p(?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test10() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(!X1)"));
		l2.add(RuleParser.parseLiteral("p(c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test11() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(!X1)"));
		l2.add(RuleParser.parseLiteral("p(!X1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test12() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(!X1)"));
		l2.add(RuleParser.parseLiteral("p(!X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test13() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(!X1)"));
		l2.add(RuleParser.parseLiteral("p(?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test20() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(?X1)"));
		l2.add(RuleParser.parseLiteral("p(c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test21() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(?X1)"));
		l2.add(RuleParser.parseLiteral("p(!X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test22() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(?X1)"));
		l2.add(RuleParser.parseLiteral("p(?X1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test23() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("p(?X1)"));
		l2.add(RuleParser.parseLiteral("p(?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test000() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c11)"));
		l2.add(RuleParser.parseLiteral("q(c11,c11)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test001() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c11)"));
		l2.add(RuleParser.parseLiteral("q(c11,c12)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test002() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c11)"));
		l2.add(RuleParser.parseLiteral("q(c21,c11)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test003() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c11)"));
		l2.add(RuleParser.parseLiteral("q(!X2,!X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test004() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c11)"));
		l2.add(RuleParser.parseLiteral("q(!X2,!X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test005() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c11)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test006() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c11)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test007() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c12)"));
		l2.add(RuleParser.parseLiteral("q(c21,c22)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test008() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c12)"));
		l2.add(RuleParser.parseLiteral("q(c11,c12)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test009() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c12)"));
		l2.add(RuleParser.parseLiteral("q(!X2,!X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test010() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c12)"));
		l2.add(RuleParser.parseLiteral("q(!X2,!X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test011() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c12)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test012() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(c11,c12)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test100() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X1)"));
		l2.add(RuleParser.parseLiteral("q(c1,c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test101() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X1)"));
		l2.add(RuleParser.parseLiteral("q(c1,c2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test102() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X1)"));
		l2.add(RuleParser.parseLiteral("q(!X2,!X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test103() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X1)"));
		l2.add(RuleParser.parseLiteral("q(!X2,!X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test104() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X1)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test105() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X1)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test106() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X2)"));
		l2.add(RuleParser.parseLiteral("q(c1,c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test107() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X2)"));
		l2.add(RuleParser.parseLiteral("q(c1,c2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test108() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X2)"));
		l2.add(RuleParser.parseLiteral("q(!X3,!X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test109() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X2)"));
		l2.add(RuleParser.parseLiteral("q(!X3,!X4)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test110() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X2)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test111() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(!X1,!X2)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test200() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(c1,c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test201() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(c1,c2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test202() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(c1,!X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test203() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(c1,?X1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test204() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(c1,?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test205() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(!X2,c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test206() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(!X2,!X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test207() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(!X2,!X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test208a() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(!X2,?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test208b() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(!X2,?X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test208() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(?X2,c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test209() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(?X2,!X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test210() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test211() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X1)"));
		l2.add(RuleParser.parseLiteral("q(?X2,?X3)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test212() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(c1,c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test213() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(c1,c2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test214() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(c1,!X1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test215() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(c1,?X1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertFalse(unifier.getSuccess());
	}

	@Test
	public void test216() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(!X1,c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test217() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(!X1,!X1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test218() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(!X1,!X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test219() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(!X1,?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test220() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(?X1,c1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test221() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(?X1,!X1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test222() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(?X1,?X1)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

	@Test
	public void test223() throws ParsingException {
		l1.clear();
		l2.clear();

		l1.add(RuleParser.parseLiteral("q(?X1,?X2)"));
		l2.add(RuleParser.parseLiteral("q(?X1,?X2)"));

		RespectingUnifier unifier = new RespectingUnifier(l1, l2, pm);
		assertTrue(unifier.getSuccess());
	}

}
