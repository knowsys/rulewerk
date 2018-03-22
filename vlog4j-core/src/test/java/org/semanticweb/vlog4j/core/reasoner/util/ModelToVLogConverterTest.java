package org.semanticweb.vlog4j.core.reasoner.util;

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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.impl.BlankImpl;
import org.semanticweb.vlog4j.core.model.impl.Expressions;


public class ModelToVLogConverterTest {

	@Test
	public void testToVLogTermVariable() {
		final Variable variable = Expressions.makeVariable("var");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.VARIABLE, "var");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(variable);
		
		assertNotNull(vLogTerm);
		assertEquals(karmaresearch.vlog.Term.TermType.VARIABLE, vLogTerm.getTermType());
		assertEquals("var", vLogTerm.getName());
		assertEquals(expectedVLogTerm, vLogTerm);
	}

	@Test
	public void testToVLogTermConstant() {
		final Constant constant = Expressions.makeConstant("const");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "const");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(constant);

		assertNotNull(vLogTerm);
		assertEquals(karmaresearch.vlog.Term.TermType.CONSTANT, vLogTerm.getTermType());
		assertEquals("const", vLogTerm.getName());
		assertEquals(expectedVLogTerm, vLogTerm);

	}

	@Test
	public void testToVlogTermBlank() {
		final Blank blank = new BlankImpl("blank");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "blank");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(blank);

		assertNotNull(vLogTerm);
		assertEquals(karmaresearch.vlog.Term.TermType.CONSTANT, vLogTerm.getTermType());
		assertEquals("blank", vLogTerm.getName());
		assertEquals(expectedVLogTerm, vLogTerm);
	}

	@Test
	public void testToVLogTermArray() {
		final Variable vx = Expressions.makeVariable("x");
		final Variable vxToo = Expressions.makeVariable("x");
		final Variable vy = Expressions.makeVariable("y");
		final Constant cx = Expressions.makeConstant("x");
		final Blank bx = new BlankImpl("x");
		final List<Term> terms = Arrays.asList(vx, cx, vxToo, bx, vy);

		final karmaresearch.vlog.Term expectedVx = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.VARIABLE, "x");
		final karmaresearch.vlog.Term expectedVy = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.VARIABLE, "y");
		final karmaresearch.vlog.Term expectedCx = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "x");
		final karmaresearch.vlog.Term[] expectedTermArray = { expectedVx, expectedCx, expectedVx, expectedCx,
				expectedVy };

		final karmaresearch.vlog.Term[] vLogTermArray = ModelToVLogConverter.toVLogTermArray(terms);
		assertArrayEquals(expectedTermArray, vLogTermArray);
	}

	@Test
	public void testToVLogTermArrayEmpty() {
		final List<Term> terms = new ArrayList<>();
		final karmaresearch.vlog.Term[] vLogTermArray = ModelToVLogConverter.toVLogTermArray(terms);

		assertNotNull(vLogTermArray);
		assertTrue(vLogTermArray.length == 0);
	}

	@Test
	public void testToVLogFactTuples() {
		final Constant c1 = Expressions.makeConstant("1");
		final Constant c2 = Expressions.makeConstant("2");
		final Constant c3 = Expressions.makeConstant("3");
		final Atom atom1 = Expressions.makeAtom("p1", c1);
		final Atom atom2 = Expressions.makeAtom("p2", c2, c3);

		final String[][] vLogTuples = ModelToVLogConverter.toVLogFactTuples(Arrays.asList(atom1, atom2));

		final String[][] expectedTuples = { { "1" }, { "2", "3" } };
		assertArrayEquals(expectedTuples, vLogTuples);
	}

	@Test
	public void testToVlogPredicate() {
		final Predicate predicate = Expressions.makePredicate("pred", 1);
		final String vlogPredicate = ModelToVLogConverter.toVlogPredicate(predicate);
		assertEquals("pred-1", vlogPredicate);
	}

	@Test
	public void testToVLogAtom() {
		final Constant c = Expressions.makeConstant("c");
		final Variable x = Expressions.makeVariable("x");
		final Blank b = new BlankImpl("_:b");
		final Atom atom = Expressions.makeAtom("pred", c, x, b);
		
		final karmaresearch.vlog.Term expectedC = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "c");
		final karmaresearch.vlog.Term expectedX = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.VARIABLE, "x");
		final karmaresearch.vlog.Term expectedB = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "_:b");

		final String expectedPredicateName = "pred" + ModelToVLogConverter.PREDICATE_ARITY_SUFFIX_SEPARATOR + 3;
		final karmaresearch.vlog.Term[] expectedTerms = { expectedC, expectedX, expectedB };
		final karmaresearch.vlog.Atom expectedAtom = new karmaresearch.vlog.Atom(expectedPredicateName, expectedTerms);

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(atom);
		assertEquals(expectedAtom, vLogAtom);
	}

	// TODO toVLogRuleArray
	// TOOD toVLogRuleRewritingStrategy

}
