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

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.NegativeLiteralImpl;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.rulewerk.core.model.implementation.PredicateImpl;

public class PositiveLiteralImplTest {

	@Test
	public void testGetters() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		final PositiveLiteral atomP = Expressions.makePositiveLiteral("p", x, c, d, y);
		final PositiveLiteral atomQ = Expressions.makePositiveLiteral("q", c, d);

		assertEquals("p", atomP.getPredicate().getName());
		assertEquals(atomP.getArguments().size(), atomP.getPredicate().getArity());

		assertEquals(Arrays.asList(x, c, d, y), atomP.getArguments());

		assertEquals("q", atomQ.getPredicate().getName());
		assertEquals(atomQ.getArguments().size(), atomQ.getPredicate().getArity());

		assertEquals(Arrays.asList(c, d), atomQ.getArguments());

		assertFalse(atomP.isNegated());
		assertFalse(atomQ.isNegated());
	}

	@Test
	public void testEquals() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Constant c = Expressions.makeAbstractConstant("c");

		final Predicate predicateP = new PredicateImpl("p", 2);
		final Predicate predicateQ = new PredicateImpl("q", 2);

		final Literal atom1 = Expressions.makePositiveLiteral("p", Arrays.asList(x, c));
		final Literal atom2 = Expressions.makePositiveLiteral("p", x, c);
		final Literal atom3 = new PositiveLiteralImpl(predicateP, Arrays.asList(x, c));
		final Literal atom4 = new PositiveLiteralImpl(predicateQ, Arrays.asList(x, c));
		final Literal atom5 = new PositiveLiteralImpl(predicateP, Arrays.asList(c, x));

		assertEquals(atom1, atom1);
		assertEquals(atom1, atom2);
		assertEquals(atom1, atom3);
		assertEquals(atom1.hashCode(), atom1.hashCode());
		assertNotEquals(atom4, atom1);
		assertNotEquals(atom4.hashCode(), atom1.hashCode());
		assertNotEquals(atom5, atom1);
		assertNotEquals(atom5.hashCode(), atom1.hashCode());
		assertFalse(atom1.equals(null));
		assertFalse(atom1.equals(c));

		assertNotEquals(atom1, new NegativeLiteralImpl(atom1.getPredicate(), atom1.getArguments()));
		assertNotEquals(atom2, new NegativeLiteralImpl(atom2.getPredicate(), atom2.getArguments()));
		assertNotEquals(atom3, new NegativeLiteralImpl(atom3.getPredicate(), atom3.getArguments()));
		assertNotEquals(atom4, new NegativeLiteralImpl(atom4.getPredicate(), atom4.getArguments()));
		assertNotEquals(atom5, new NegativeLiteralImpl(atom5.getPredicate(), atom5.getArguments()));
	}

	@Test(expected = NullPointerException.class)
	public void termsNotNull() {
		final Predicate predicate1 = Expressions.makePredicate("p", 1);
		new PositiveLiteralImpl(predicate1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void termsNoNullElements() {
		final Predicate predicate1 = Expressions.makePredicate("p", 1);
		final Variable x = Expressions.makeUniversalVariable("X");
		new PositiveLiteralImpl(predicate1, Arrays.asList(x, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void termsNonEmpty() {
		Expressions.makePositiveLiteral("p");
	}

	@Test(expected = NullPointerException.class)
	public void predicateNotNull() {
		final Predicate nullPredicate = null;
		Expressions.makePositiveLiteral(nullPredicate, Expressions.makeAbstractConstant("c"));
	}

	@Test(expected = NullPointerException.class)
	public void predicateNameNotNull() {
		final String nullPredicateName = null;
		Expressions.makePositiveLiteral(nullPredicateName, Expressions.makeAbstractConstant("c"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateNameNotEmpty() {
		Expressions.makePositiveLiteral("", Expressions.makeAbstractConstant("c"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateNameNotWhitespace() {
		Expressions.makePositiveLiteral("  ", Expressions.makeAbstractConstant("c"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void termSizeMatchesPredicateArity() {
		final Predicate predicateArity1 = Expressions.makePredicate("p", 1);
		Expressions.makePositiveLiteral(predicateArity1, Expressions.makeAbstractConstant("c"),
				Expressions.makeUniversalVariable("X"));
	}

	@Test
	public void positiveLiteralTostringTest() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Predicate predicateP = new PredicateImpl("p", 2);
		final Literal atom2 = Expressions.makePositiveLiteral("p", x, c);
		final Literal atom3 = new PositiveLiteralImpl(predicateP, Arrays.asList(x, c));
		assertEquals("p(?X, c)", atom2.toString());
		assertEquals("p(?X, c)", atom3.toString());

	}

}
