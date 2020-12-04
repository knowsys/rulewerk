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
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
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

	final Variable uniX = Expressions.makeUniversalVariable("X");
	final Variable uniY = Expressions.makeUniversalVariable("Y");
	final Variable uniZ = Expressions.makeUniversalVariable("Z");

	final Variable extY = Expressions.makeExistentialVariable("Y");

	final Constant absConC = Expressions.makeAbstractConstant("c");
	final Constant absConD = Expressions.makeAbstractConstant("d");

	final LanguageStringConstantImpl strConTen = new LanguageStringConstantImpl("T", "en");

	final PositiveLiteral posLitPUniX = Expressions.makePositiveLiteral("p", uniX);
	final PositiveLiteral posLitQUniY = Expressions.makePositiveLiteral("q", uniY);

	final PositiveLiteral posLitPUniXUniZ = Expressions.makePositiveLiteral("p", uniX, uniZ);
	final PositiveLiteral posLitPUniYUniX = Expressions.makePositiveLiteral("p", uniY, uniX);
	final PositiveLiteral posLitQUniXExtY = Expressions.makePositiveLiteral("q", uniX, extY);

	final PositiveLiteral posLitPUniXAbsConC = Expressions.makePositiveLiteral("p", uniX, absConC);
	final PositiveLiteral posLitQUniXAbsConD = Expressions.makePositiveLiteral("q", uniX, absConD);
	final PositiveLiteral posLitRUniXAbsConD = Expressions.makePositiveLiteral("r", uniX, absConD);

	final NegativeLiteral negLitRUniXAbsConD = Expressions.makeNegativeLiteral("r", uniX, absConD);

	final Fact factSAbsConCStrConTen = Expressions.makeFact("s", absConC, strConTen);

	@Test
	public void testGetters() {
		final Conjunction<Literal> body = Expressions.makeConjunction(posLitPUniXAbsConC, posLitPUniXUniZ);
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(posLitQUniXExtY,
				posLitRUniXAbsConD);
		final Rule rule = Expressions.makeRule(head, body);

		assertEquals(body, rule.getBody());
		assertEquals(head, rule.getHead());
	}

	@Test
	public void testEquals() {
		final Conjunction<Literal> bodyLiterals = Expressions.makeConjunction(posLitPUniXAbsConC, posLitPUniXUniZ);
		final Conjunction<PositiveLiteral> headPositiveLiterals = Expressions.makePositiveConjunction(posLitQUniXExtY);

		final Conjunction<PositiveLiteral> bodyPositiveLiterals = Expressions
				.makePositiveConjunction(posLitPUniXAbsConC, posLitPUniXUniZ);

		final Rule rule1 = new RuleImpl(headPositiveLiterals, bodyLiterals);
		final Rule rule2 = Expressions.makeRule(posLitQUniXExtY, posLitPUniXAbsConC, posLitPUniXUniZ);
		final Rule rule6 = Expressions.makeRule(posLitQUniXExtY, posLitPUniXAbsConC, posLitPUniXUniZ);
		final Rule rule7 = Expressions.makeRule(posLitQUniXExtY, posLitPUniXAbsConC, posLitPUniXUniZ);
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
		assertFalse(rule1.equals(absConC));

	}

	@Test(expected = IllegalArgumentException.class)
	public void bodyNonEmpty() {
		Expressions.makeRule(posLitPUniX);
	}

	@Test(expected = NullPointerException.class)
	public void bodyNotNull() {
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(posLitPUniX);
		Expressions.makeRule(head, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void headNonEmpty() {
		final Conjunction<Literal> body = Expressions.makeConjunction(posLitPUniX);
		Expressions.makeRule(Expressions.makePositiveConjunction(), body);
	}

	@Test(expected = NullPointerException.class)
	public void headNotNull() {
		final Conjunction<Literal> body = Expressions.makeConjunction(posLitPUniX);
		Expressions.makeRule(null, body);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noExistentialInBody() {
		Expressions.makeRule(posLitQUniY, posLitPUniX);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noUnsafeVariables() {
		Expressions.makeRule(posLitPUniX, posLitQUniY);
	}

	@Test
	public void ruleToStringTest() {
		final List<Literal> LiteralList = Arrays.asList(posLitPUniXAbsConC, posLitPUniYUniX, posLitQUniXAbsConD,
				negLitRUniXAbsConD, factSAbsConCStrConTen);
		final Conjunction<Literal> bodyLiterals = Expressions.makeConjunction(posLitPUniXAbsConC, posLitPUniXUniZ);
		final Conjunction<PositiveLiteral> headPositiveLiterals = Expressions.makePositiveConjunction(posLitQUniXExtY);
		final Conjunction<Literal> bodyConjunction = new ConjunctionImpl<>(LiteralList);
		final Rule rule1 = new RuleImpl(headPositiveLiterals, bodyLiterals);
		final Rule rule2 = new RuleImpl(headPositiveLiterals, bodyConjunction);
		assertEquals("q(?X, !Y) :- p(?X, c), p(?X, ?Z) .", rule1.toString());
		assertEquals("q(?X, !Y) :- p(?X, c), p(?Y, ?X), q(?X, d), ~r(?X, d), s(c, \"T\"@en) .", rule2.toString());
	}

}
