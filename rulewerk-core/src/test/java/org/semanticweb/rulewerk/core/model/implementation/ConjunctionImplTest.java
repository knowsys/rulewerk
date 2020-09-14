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
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.NegativeLiteral;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.ConjunctionImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class ConjunctionImplTest {

	@Test
	public void testGettersLiterals() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Variable z = Expressions.makeExistentialVariable("Z");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		final Literal positiveLiteral1 = Expressions.makePositiveLiteral("p", x, c);
		final NegativeLiteral negativeLiteral2 = Expressions.makeNegativeLiteral("p", y, x);
		final Literal positiveLiteral3 = Expressions.makePositiveLiteral("q", x, d);
		final Literal positiveLiteral4 = Expressions.makePositiveLiteral("q", y, d, z);
		final List<Literal> literalList = Arrays.asList(positiveLiteral1, negativeLiteral2, positiveLiteral3,
				positiveLiteral4);

		final Conjunction<Literal> conjunction = new ConjunctionImpl<>(literalList);

		assertEquals(literalList, conjunction.getLiterals());
		assertEquals(Sets.newSet(x, y, z), conjunction.getVariables().collect(Collectors.toSet()));
		assertEquals(Sets.newSet(x, y), conjunction.getUniversalVariables().collect(Collectors.toSet()));
		assertEquals(Sets.newSet(z), conjunction.getExistentialVariables().collect(Collectors.toSet()));
		assertEquals(Sets.newSet(), conjunction.getNamedNulls().collect(Collectors.toSet()));
		assertEquals(Sets.newSet(c, d), conjunction.getAbstractConstants().collect(Collectors.toSet()));
	}

	@Test
	public void testEqualsPositiveLiterals() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		final PositiveLiteral positiveLiteral1 = Expressions.makePositiveLiteral("p", x, c);
		final PositiveLiteral positiveLiteral2 = Expressions.makePositiveLiteral("p", y, x);
		final PositiveLiteral positiveLiteral3 = Expressions.makePositiveLiteral("q", x, d);
		final List<PositiveLiteral> positiveLiteralList = Arrays.asList(positiveLiteral1, positiveLiteral2,
				positiveLiteral3);
		final Conjunction<PositiveLiteral> conjunction1 = new ConjunctionImpl<>(positiveLiteralList);
		final Conjunction<PositiveLiteral> conjunction2 = Expressions.makePositiveConjunction(positiveLiteral1,
				positiveLiteral2, positiveLiteral3);
		final Conjunction<Literal> conjunction3 = Expressions.makeConjunction(positiveLiteral1, positiveLiteral2,
				positiveLiteral3);
		final Conjunction<PositiveLiteral> conjunction4 = Expressions.makePositiveConjunction(positiveLiteral1,
				positiveLiteral3, positiveLiteral2);

		assertEquals(conjunction1, conjunction1);
		assertEquals(conjunction2, conjunction1);
		assertEquals(conjunction3, conjunction1);
		assertEquals(conjunction2.hashCode(), conjunction1.hashCode());
		assertEquals(conjunction3.hashCode(), conjunction1.hashCode());
		assertNotEquals(conjunction4, conjunction1);
		assertNotEquals(conjunction4.hashCode(), conjunction1.hashCode());
		assertFalse(conjunction1.equals(null));
		assertFalse(conjunction1.equals(c));
	}

	@Test
	public void testEqualsNegativeLiterals() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		final NegativeLiteral negativeLiteral1 = Expressions.makeNegativeLiteral("p", x, c);
		final NegativeLiteral negativeLiteral2 = Expressions.makeNegativeLiteral("p", y, x);
		final NegativeLiteral negativeLiteral3 = Expressions.makeNegativeLiteral("q", x, d);
		final List<NegativeLiteral> negativeLiteralList = Arrays.asList(negativeLiteral1, negativeLiteral2,
				negativeLiteral3);
		final Conjunction<NegativeLiteral> conjunction1 = new ConjunctionImpl<>(negativeLiteralList);
		final Conjunction<Literal> conjunction2 = Expressions.makeConjunction(negativeLiteral1, negativeLiteral2,
				negativeLiteral3);
		final Conjunction<Literal> conjunction3 = Expressions.makeConjunction(negativeLiteral1, negativeLiteral3,
				negativeLiteral2);

		assertEquals(conjunction1, conjunction1);
		assertEquals(conjunction2, conjunction1);
		assertEquals(conjunction2.hashCode(), conjunction1.hashCode());
		assertNotEquals(conjunction3, conjunction1);
		assertNotEquals(conjunction3.hashCode(), conjunction1.hashCode());
		assertFalse(conjunction1.equals(null));
		assertFalse(conjunction1.equals(c));
	}

	@Test
	public void testEqualsLiterals() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Constant c = Expressions.makeAbstractConstant("c");

		final PositiveLiteral positiveLiteral1 = Expressions.makePositiveLiteral("p", x, c);
		final NegativeLiteral negativeLiteral1 = Expressions.makeNegativeLiteral("p", x, c);
		final ConjunctionImpl<Literal> conjunction1 = new ConjunctionImpl<>(
				Arrays.asList(positiveLiteral1, negativeLiteral1));

		final Literal positiveLiteral2 = Expressions.makePositiveLiteral("p", x, c);
		final Literal negativeLiteral2 = Expressions.makeNegativeLiteral("p", x, c);
		final ConjunctionImpl<Literal> conjunction2 = new ConjunctionImpl<>(
				Arrays.asList(positiveLiteral2, negativeLiteral2));

		assertEquals(conjunction1, conjunction1);
		assertEquals(conjunction2, conjunction1);
		assertEquals(conjunction2.hashCode(), conjunction1.hashCode());

	}

	@Test(expected = NullPointerException.class)
	public void literalsNotNull() {
		new ConjunctionImpl<Literal>(null);
	}

	@Test(expected = NullPointerException.class)
	public void positiveLiteralsNotNull() {
		new ConjunctionImpl<PositiveLiteral>(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void positiveLiteralsNoNullElements() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final PositiveLiteral positiveLiteral = Expressions.makePositiveLiteral("p", x);
		final List<PositiveLiteral> positiveLiteralList = Arrays.asList(positiveLiteral, null);
		Expressions.makeConjunction(positiveLiteralList);
	}

	@Test(expected = IllegalArgumentException.class)
	public void literalsNoNullElements() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final NegativeLiteral negativeLiteral = Expressions.makeNegativeLiteral("p", x);
		final PositiveLiteral positiveLiteral = Expressions.makePositiveLiteral("p", x);
		final List<Literal> literalList = Arrays.asList(negativeLiteral, positiveLiteral, null);
		Expressions.makeConjunction(literalList);
	}

	@Test(expected = NullPointerException.class)
	public void negativeLiteralsNotNull() {
		new ConjunctionImpl<NegativeLiteral>(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void negativeLiteralsNoNullElements() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final NegativeLiteral negativeLiteral = Expressions.makeNegativeLiteral("p", x);
		final List<NegativeLiteral> negativeLiteralList = Arrays.asList(negativeLiteral, null);
		Expressions.makeConjunction(negativeLiteralList);
	}

	@Test
	public void conjunctionToStringTest() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		final PositiveLiteral positiveLiteral1 = Expressions.makePositiveLiteral("p", x, c);
		final PositiveLiteral positiveLiteral2 = Expressions.makePositiveLiteral("p", y, x);
		final PositiveLiteral positiveLiteral3 = Expressions.makePositiveLiteral("q", x, d);
		final NegativeLiteral NegativeLiteral = Expressions.makeNegativeLiteral("r", x, d);
		final PositiveLiteral PositiveLiteral4 = Expressions.makePositiveLiteral("s", c, d);
		final List<Literal> LiteralList = Arrays.asList(positiveLiteral1, positiveLiteral2, positiveLiteral3,
				NegativeLiteral, PositiveLiteral4);
		final Conjunction<Literal> conjunction1 = new ConjunctionImpl<>(LiteralList);
		assertEquals("p(?X, c), p(?Y, ?X), q(?X, d), ~r(?X, d), s(c, d)", conjunction1.toString());
	}

}
