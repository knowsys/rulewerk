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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.ConjunctionImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class ConjunctionImplTest {

	@Test
	public void testGetters() {
		Variable x = Expressions.makeVariable("X");
		Variable y = Expressions.makeVariable("Y");
		Constant c = Expressions.makeConstant("c");
		Constant d = Expressions.makeConstant("d");
		Atom atom1 = Expressions.makeAtom("p", x, c);
		Atom atom2 = Expressions.makeAtom("p", y, x);
		Atom atom3 = Expressions.makeAtom("q", x, d);
		List<Atom> atomList = Arrays.asList(atom1, atom2, atom3);

		Conjunction conjunction = new ConjunctionImpl(atomList);

		assertEquals(atomList, conjunction.getAtoms());
		assertEquals(Sets.newSet(x, y), conjunction.getVariables());
		assertEquals(Sets.newSet(c, d), conjunction.getTerms(TermType.CONSTANT));
	}

	@Test
	public void testEquals() {
		Variable x = Expressions.makeVariable("X");
		Variable y = Expressions.makeVariable("Y");
		Constant c = Expressions.makeConstant("c");
		Constant d = Expressions.makeConstant("d");
		Atom atom1 = Expressions.makeAtom("p", x, c);
		Atom atom2 = Expressions.makeAtom("p", y, x);
		Atom atom3 = Expressions.makeAtom("q", x, d);
		List<Atom> atomList = Arrays.asList(atom1, atom2, atom3);
		Conjunction conjunction1 = new ConjunctionImpl(atomList);
		Conjunction conjunction2 = Expressions.makeConjunction(atom1, atom2, atom3);
		Conjunction conjunction3 = Expressions.makeConjunction(atom1, atom3, atom2);

		assertEquals(conjunction1, conjunction1);
		assertEquals(conjunction2, conjunction1);
		assertEquals(conjunction2.hashCode(), conjunction1.hashCode());
		assertNotEquals(conjunction3, conjunction1);
		assertNotEquals(conjunction3.hashCode(), conjunction1.hashCode());
		assertFalse(conjunction1.equals(null));
		assertFalse(conjunction1.equals(c));
	}
	
	@Test(expected = NullPointerException.class)
	public void atomsNotNull() {
		new ConjunctionImpl(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void atomsNoNullElements() {
		Variable x = Expressions.makeVariable("X");
		Atom atom1 = Expressions.makeAtom("p", x);
		List<Atom> atomList = Arrays.asList(atom1, null);
		Expressions.makeConjunction(atomList);
	}

}
