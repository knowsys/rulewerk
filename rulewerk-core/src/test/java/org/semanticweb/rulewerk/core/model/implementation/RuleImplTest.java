package org.semanticweb.rulewerk.core.model.implementation;

/*-
 * #%L
 * Rulewerk Core Components
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.NegativeLiteral;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.ConjunctionImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.RuleImpl;

public class RuleImplTest {

	final Variable x = Expressions.makeUniversalVariable("X");
	final Variable y = Expressions.makeExistentialVariable("Y");
	final Variable y2 = Expressions.makeUniversalVariable("Y");
	final Variable z = Expressions.makeUniversalVariable("Z");

	final Constant c = Expressions.makeAbstractConstant("c");
	final Constant d = Expressions.makeAbstractConstant("d");
	final LanguageStringConstantImpl s = new LanguageStringConstantImpl("Test", "en");

	final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
	final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, z);
	final PositiveLiteral atom3 = Expressions.makePositiveLiteral("p", c, z);
	final PositiveLiteral atom4 = Expressions.makePositiveLiteral("q", x, y2, z);
	final NegativeLiteral negativeLiteral = Expressions.makeNegativeLiteral("r", x, d);

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

	@Test
	public void ruleToStringTest() {
		final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
		final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, z);
		final PositiveLiteral headAtom1 = Expressions.makePositiveLiteral("q", x, y);
		final PositiveLiteral positiveLiteral1 = Expressions.makePositiveLiteral("p", x, c);
		final PositiveLiteral positiveLiteral2 = Expressions.makePositiveLiteral("p", y2, x);
		final PositiveLiteral positiveLiteral3 = Expressions.makePositiveLiteral("q", x, d);
		final NegativeLiteral NegativeLiteral = Expressions.makeNegativeLiteral("r", x, d);
		final PositiveLiteral PositiveLiteral4 = Expressions.makePositiveLiteral("s", c, s);
		final List<Literal> LiteralList = Arrays.asList(positiveLiteral1, positiveLiteral2, positiveLiteral3,
				NegativeLiteral, PositiveLiteral4);
		final Conjunction<Literal> bodyLiterals = Expressions.makeConjunction(atom1, atom2);
		final Conjunction<PositiveLiteral> headPositiveLiterals = Expressions.makePositiveConjunction(headAtom1);
		final Conjunction<Literal> bodyConjunction = new ConjunctionImpl<>(LiteralList);
		final Rule rule1 = new RuleImpl(headPositiveLiterals, bodyLiterals);
		final Rule rule2 = new RuleImpl(headPositiveLiterals, bodyConjunction);
		assertEquals("q(?X, !Y) :- p(?X, c), p(?X, ?Z) .", rule1.toString());
		assertEquals("q(?X, !Y) :- p(?X, c), p(?Y, ?X), q(?X, d), ~r(?X, d), s(c, \"Test\"@en) .", rule2.toString());
	}
}
