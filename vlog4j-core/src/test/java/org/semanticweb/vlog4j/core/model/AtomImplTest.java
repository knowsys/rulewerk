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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.impl.AtomImpl;
import org.semanticweb.vlog4j.core.model.impl.Expressions;

public class AtomImplTest {

	@Test
	public void testGetters() {
		Variable x = Expressions.makeVariable("X");
		Variable y = Expressions.makeVariable("Y");
		Constant c = Expressions.makeConstant("c");
		Constant d = Expressions.makeConstant("d");
		Atom atom = Expressions.makeAtom("p", x, c, d, y);

		Set<Variable> variables = new HashSet<Variable>();
		variables.add(x);
		variables.add(y);

		assertEquals("p", atom.getPredicate());
		assertEquals(variables, atom.getVariables());
		assertEquals(Arrays.asList(x, c, d, y), atom.getTerms());
	}

	@Test
	public void testEquals() {
		Variable x = Expressions.makeVariable("X");
		Constant c = Expressions.makeConstant("c");
		Atom atom1 = Expressions.makeAtom("p", Arrays.asList(x, c));
		Atom atom2 = Expressions.makeAtom("p", x, c);
		Atom atom3 = new AtomImpl("q", Arrays.asList(x, c));
		Atom atom4 = new AtomImpl("p", Arrays.asList(c, x));

		assertEquals(atom1, atom1);
		assertEquals(atom1, atom2);
		assertEquals(atom1.hashCode(), atom2.hashCode());
		assertNotEquals(atom3, atom2);
		assertNotEquals(atom3.hashCode(), atom2.hashCode());
		assertNotEquals(atom4, atom2);
		assertNotEquals(atom4.hashCode(), atom2.hashCode());
		assertFalse(atom2.equals(null));
		assertFalse(atom2.equals(c));
	}

	@Test(expected = NullPointerException.class)
	public void termsNotNull() {
		new AtomImpl("p", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void termsNoNullElements() {
		Variable x = Expressions.makeVariable("X");
		new AtomImpl("p",  Arrays.asList(x, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void temrsNonEmpty() {
		Expressions.makeAtom("p");
	}
	
	@Test(expected = NullPointerException.class)
	public void predicateNotNull() {
		Expressions.makeAtom(null, Expressions.makeConstant("c"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void predicateNotEmpty() {
		Expressions.makeAtom("", Expressions.makeConstant("c"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void predicateNotWhitespace() {
		Expressions.makeAtom("  ", Expressions.makeConstant("c"));
	}

}
