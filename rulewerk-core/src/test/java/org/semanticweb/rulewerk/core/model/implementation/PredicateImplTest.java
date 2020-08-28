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

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.PredicateImpl;

public class PredicateImplTest {

	@Test
	public void testEquals() {
		final Predicate p1 = new PredicateImpl("p", 1);
		final Predicate p1too = Expressions.makePredicate("p", 1);
		final Predicate p2 = new PredicateImpl("p", 2);
		final Predicate q1 = new PredicateImpl("q", 1);

		assertEquals(p1, p1);
		assertEquals(p1too, p1);
		assertNotEquals(p2, p1);
		assertNotEquals(q1, p1);
		assertNotEquals(p2.hashCode(), p1.hashCode());
		assertNotEquals(q1.hashCode(), p1.hashCode());
		assertFalse(p1.equals(null)); // written like this for recording coverage properly
		assertFalse(p1.equals("p")); // written like this for recording coverage properly
	}

	@Test(expected = NullPointerException.class)
	public void predicateNameNotNull() {
		new PredicateImpl(null, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateNameNotEmpty() {
		new PredicateImpl("", 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateNameNotWhitespace() {
		new PredicateImpl(" ", 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void arityNegative() {
		new PredicateImpl("p", -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void arityZero() {
		new PredicateImpl("p", 0);
	}

	@Test
	public void predicateToStringTest() {
		final Predicate p1 = new PredicateImpl("p", 1);
		assertEquals("p[1]", p1.toString());
	}

}
