package org.semanticweb.vlog4j.core.model;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.RuleImpl;

public class RuleImplTest {

	@Test
	public void testGetters() {
		Variable x = Expressions.makeVariable("X");
		Variable y = Expressions.makeVariable("Y");
		Variable z = Expressions.makeVariable("Z");
		Constant c = Expressions.makeConstant("c");
		Constant d = Expressions.makeConstant("d");
		Atom atom1 = Expressions.makeAtom("p", x, c);
		Atom atom2 = Expressions.makeAtom("p", x, z);
		Atom atom3 = Expressions.makeAtom("q", x, y);
		Atom atom4 = Expressions.makeAtom("r", x, d);
		Conjunction body = Expressions.makeConjunction(atom1, atom2);
		Conjunction head = Expressions.makeConjunction(atom3, atom4);
		Rule rule = Expressions.makeRule(head, body);

		assertEquals(body, rule.getBody());
		assertEquals(head, rule.getHead());
		assertEquals(Collections.singleton(y), rule.getExistentiallyQuantifiedVariables());
		assertEquals(Sets.newSet(x, z), rule.getUniversallyQuantifiedVariables());
	}

	@Test
	public void testEquals() {
		Variable x = Expressions.makeVariable("X");
		Variable y = Expressions.makeVariable("Y");
		Variable z = Expressions.makeVariable("Z");
		Constant c = Expressions.makeConstant("c");
		Atom atom1 = Expressions.makeAtom("p", x, c);
		Atom atom2 = Expressions.makeAtom("p", x, z);
		Atom atom3 = Expressions.makeAtom("q", x, y);
		Conjunction body = Expressions.makeConjunction(atom1, atom2);
		Conjunction head = Expressions.makeConjunction(atom3);
		Rule rule1 = new RuleImpl(head, body);
		Rule rule2 = Expressions.makeRule(atom3, atom1, atom2);
		Rule rule3 = new RuleImpl(head, head);
		Rule rule4 = new RuleImpl(body, body);
		Rule rule5 = new RuleImpl(body, head);

		assertEquals(rule1, rule1);
		assertEquals(rule2, rule1);
		assertEquals(rule2.hashCode(), rule1.hashCode());
		assertNotEquals(rule3, rule1);
		assertNotEquals(rule3.hashCode(), rule1.hashCode());
		assertNotEquals(rule4, rule1);
		assertNotEquals(rule4.hashCode(), rule1.hashCode());
		assertNotEquals(rule5, rule1);
		assertFalse(rule1.equals(null));
		assertFalse(rule1.equals(c));
	}

	@Test(expected = IllegalArgumentException.class)
	public void bodyNonEmpty() {
		Expressions.makeRule(Expressions.makeAtom("p", Expressions.makeVariable("X")));
	}

	@Test(expected = NullPointerException.class)
	public void bodyNotNull() {
		Conjunction head = Expressions.makeConjunction(Expressions.makeAtom("p", Expressions.makeVariable("X")));
		Expressions.makeRule(head, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void headNonEmpty() {
		Conjunction body = Expressions.makeConjunction(Expressions.makeAtom("p", Expressions.makeVariable("X")));
		Expressions.makeRule(Expressions.makeConjunction(), body);
	}

	@Test(expected = NullPointerException.class)
	public void headNotNull() {
		Conjunction body = Expressions.makeConjunction(Expressions.makeAtom("p", Expressions.makeVariable("X")));
		Expressions.makeRule(null, body);
	}

}
