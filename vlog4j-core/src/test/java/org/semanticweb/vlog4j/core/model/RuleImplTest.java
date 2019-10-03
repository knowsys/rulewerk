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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.RuleImpl;

public class RuleImplTest {

	@Test
	public void testGetters() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeExistentialVariable("Y");
		final Variable z = Expressions.makeUniversalVariable("Z");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		final Literal atom1 = Expressions.makePositiveLiteral("p", x, c);
		final Literal atom2 = Expressions.makePositiveLiteral("p", x, z);
		final PositiveLiteral atom3 = Expressions.makePositiveLiteral("q", x, y);
		final PositiveLiteral atom4 = Expressions.makePositiveLiteral("r", x, d);
		final Conjunction<Literal> body = Expressions.makeConjunction(atom1, atom2);
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom3, atom4);
		final Rule rule = Expressions.makeRule(head, body);

		assertEquals(body, rule.getBody());
		assertEquals(head, rule.getHead());
	}

	@Test
	public void testEquals() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeExistentialVariable("Y");
		final Variable z = Expressions.makeUniversalVariable("Z");
		final Constant c = Expressions.makeAbstractConstant("c");

		final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
		final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, z);
		final PositiveLiteral headAtom1 = Expressions.makePositiveLiteral("q", x, y);

		final Conjunction<Literal> bodyLiterals = Expressions.makeConjunction(atom1, atom2);
		final Conjunction<PositiveLiteral> headPositiveLiterals = Expressions.makePositiveConjunction(headAtom1);

		final Conjunction<PositiveLiteral> bodyPositiveLiterals = Expressions.makePositiveConjunction(atom1, atom2);

		final Rule rule1 = new RuleImpl(headPositiveLiterals, bodyLiterals);
		final Rule rule2 = Expressions.makeRule(headAtom1, atom1, atom2);

		final Rule rule6 = Expressions.makeRule(headAtom1, atom1, atom2);
		final Rule rule7 = Expressions.makeRule(headAtom1, atom1, atom2);
		final Rule rule8 = Expressions.makePositiveLiteralsRule(headPositiveLiterals, bodyPositiveLiterals);

		assertEquals(rule1, rule1);
		assertEquals(rule2, rule1);
		assertEquals(rule2.hashCode(), rule1.hashCode());

		assertEquals(rule6, rule1);
		assertEquals(rule6.hashCode(), rule1.hashCode());
		assertEquals(rule7, rule1);
		assertEquals(rule7.hashCode(), rule1.hashCode());
		assertEquals(rule8, rule1);
		assertEquals(rule8.hashCode(), rule1.hashCode());

		final Rule rule4 = new RuleImpl(bodyPositiveLiterals, bodyLiterals);
		final Rule rule5 = new RuleImpl(bodyPositiveLiterals, bodyLiterals);

//		assertNotEquals(rule3, rule1);
		assertNotEquals(rule4, rule1);
		assertNotEquals(rule5, rule1);
		assertFalse(rule1.equals(null));
		assertFalse(rule1.equals(c));
	}

	@Test(expected = IllegalArgumentException.class)
	public void bodyNonEmpty() {
		Expressions.makeRule(Expressions.makePositiveLiteral("p", Expressions.makeUniversalVariable("X")));
	}

	@Test(expected = NullPointerException.class)
	public void bodyNotNull() {
		final Conjunction<PositiveLiteral> head = Expressions
				.makePositiveConjunction(Expressions.makePositiveLiteral("p", Expressions.makeUniversalVariable("X")));
		Expressions.makeRule(head, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void headNonEmpty() {
		final Literal literal = Expressions.makePositiveLiteral("p", Expressions.makeUniversalVariable("X"));
		final Conjunction<Literal> body = Expressions.makeConjunction(literal);
		Expressions.makeRule(Expressions.makePositiveConjunction(), body);
	}

	@Test(expected = NullPointerException.class)
	public void headNotNull() {
		final Literal literal = Expressions.makePositiveLiteral("p", Expressions.makeUniversalVariable("X"));
		final Conjunction<Literal> body = Expressions.makeConjunction(literal);
		Expressions.makeRule(null, body);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noExistentialInBody() {
		final Literal literal1 = Expressions.makePositiveLiteral("p", Expressions.makeExistentialVariable("X"));
		final PositiveLiteral literal2 = Expressions.makePositiveLiteral("q", Expressions.makeUniversalVariable("Y"));
		Expressions.makeRule(literal2, literal1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noUnsafeVariables() {
		final PositiveLiteral literal1 = Expressions.makePositiveLiteral("p", Expressions.makeUniversalVariable("X"));
		final Literal literal2 = Expressions.makePositiveLiteral("q", Expressions.makeUniversalVariable("Y"));
		Expressions.makeRule(literal1, literal2);
	}

}
