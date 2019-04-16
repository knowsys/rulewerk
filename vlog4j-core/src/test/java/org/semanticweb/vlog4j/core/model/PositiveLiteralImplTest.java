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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.vlog4j.core.model.implementation.PredicateImpl;

public class PositiveLiteralImplTest {
	
	//FIXME add also test class for negative literals
	// move general methods for test class for AbstractLiteralImpl

	@Test
	public void testGetters() {
		final Variable x = Expressions.makeVariable("X");
		final Variable y = Expressions.makeVariable("Y");
		final Constant c = Expressions.makeConstant("c");
		final Constant d = Expressions.makeConstant("d");
		final PositiveLiteral atomP = Expressions.makePositiveLiteral("p", x, c, d, y);
		final PositiveLiteral atomQ = Expressions.makePositiveLiteral("q", c, d);

		final Set<Variable> variables = Sets.newSet(x, y);
		final Set<Constant> constants = Sets.newSet(c, d);

		assertEquals("p", atomP.getPredicate().getName());
		assertEquals(atomP.getTerms().size(), atomP.getPredicate().getArity());

		assertEquals(variables, atomP.getVariables());
		assertEquals(constants, atomP.getConstants());
		assertEquals(Arrays.asList(x, c, d, y), atomP.getTerms());

		assertEquals("q", atomQ.getPredicate().getName());
		assertEquals(atomQ.getTerms().size(), atomQ.getPredicate().getArity());

		assertTrue(atomQ.getVariables().isEmpty());
		assertEquals(constants, atomQ.getConstants());
		assertEquals(Arrays.asList(c, d), atomQ.getTerms());
		
		
		assertFalse(atomP.isNegated());
		assertFalse(atomQ.isNegated());
	}

	@Test
	public void testEquals() {
		final Variable x = Expressions.makeVariable("X");
		final Constant c = Expressions.makeConstant("c");

		final Predicate predicateP = new PredicateImpl("p", 2);
		final Predicate predicateQ = new PredicateImpl("q", 2);

		final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", Arrays.asList(x, c));
		final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, c);
		final PositiveLiteral atom3 = new PositiveLiteralImpl(predicateP, Arrays.asList(x, c));
		final PositiveLiteral atom4 = new PositiveLiteralImpl(predicateQ, Arrays.asList(x, c));
		final PositiveLiteral atom5 = new PositiveLiteralImpl(predicateP, Arrays.asList(c, x));

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
	}

	@Test(expected = NullPointerException.class)
	public void termsNotNull() {
		final Predicate predicate1 = Expressions.makePredicate("p", 1);
		new PositiveLiteralImpl(predicate1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void termsNoNullElements() {
		final Predicate predicate1 = Expressions.makePredicate("p", 1);
		final Variable x = Expressions.makeVariable("X");
		new PositiveLiteralImpl(predicate1, Arrays.asList(x, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void termsNonEmpty() {
		Expressions.makePositiveLiteral("p");
	}

	@Test(expected = NullPointerException.class)
	public void predicateNotNull() {
		final Predicate nullPredicate = null;
		Expressions.makePositiveLiteral(nullPredicate, Expressions.makeConstant("c"));
	}

	@Test(expected = NullPointerException.class)
	public void predicateNameNotNull() {
		final String nullPredicateName = null;
		Expressions.makePositiveLiteral(nullPredicateName, Expressions.makeConstant("c"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateNameNotEmpty() {
		Expressions.makePositiveLiteral("", Expressions.makeConstant("c"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateNameNotWhitespace() {
		Expressions.makePositiveLiteral("  ", Expressions.makeConstant("c"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void termSizeMatchesPredicateArity() {
		final Predicate predicateArity1 = Expressions.makePredicate("p", 1);
		Expressions.makePositiveLiteral(predicateArity1, Expressions.makeConstant("c"), Expressions.makeVariable("X"));
	}

}
