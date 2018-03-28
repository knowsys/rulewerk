package org.semanticweb.vlog4j.core.reasoner.implementation;

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
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.BlankImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;

public class ModelToVLogConverterTest {

	@Test
	public void testToVLogTermVariable() {
		final Variable variable = Expressions.makeVariable("var");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "var");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(variable);

		assertNotNull(vLogTerm);
		assertEquals(karmaresearch.vlog.Term.TermType.VARIABLE, vLogTerm.getTermType());
		assertEquals("var", vLogTerm.getName());
		assertEquals(expectedVLogTerm, vLogTerm);
	}

	@Test
	public void testToVLogTermConstant() {
		final Constant constant = Expressions.makeConstant("const");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, "const");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(constant);

		assertNotNull(vLogTerm);
		assertEquals(karmaresearch.vlog.Term.TermType.CONSTANT, vLogTerm.getTermType());
		assertEquals("const", vLogTerm.getName());
		assertEquals(expectedVLogTerm, vLogTerm);

	}

	@Test
	public void testToVLogTermBlank() {
		final Blank blank = new BlankImpl("blank");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK, "blank");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(blank);

		assertNotNull(vLogTerm);
		assertEquals(karmaresearch.vlog.Term.TermType.BLANK, vLogTerm.getTermType());
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

		final karmaresearch.vlog.Term expectedVx = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "x");
		final karmaresearch.vlog.Term expectedVy = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "y");
		final karmaresearch.vlog.Term expectedCx = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, "x");
		final karmaresearch.vlog.Term expectedBx = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK, "x");
		final karmaresearch.vlog.Term[] expectedTermArray = { expectedVx, expectedCx, expectedVx, expectedBx, expectedVy };

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
	public void testToVLogPredicate() {
		final Predicate predicate = Expressions.makePredicate("pred", 1);
		final String vLogPredicate = ModelToVLogConverter.toVLogPredicate(predicate);
		assertEquals("pred-1", vLogPredicate);
	}

	@Test
	public void testToVLogAtom() {
		final Constant c = Expressions.makeConstant("c");
		final Variable x = Expressions.makeVariable("x");
		final Blank b = new BlankImpl("_:b");
		final Atom atom = Expressions.makeAtom("pred", c, x, b);

		final karmaresearch.vlog.Term expectedC = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, "c");
		final karmaresearch.vlog.Term expectedX = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "x");
		final karmaresearch.vlog.Term expectedB = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK, "_:b");

		final String expectedPredicateName = "pred" + ModelToVLogConverter.PREDICATE_ARITY_SUFFIX_SEPARATOR + 3;
		final karmaresearch.vlog.Term[] expectedTerms = { expectedC, expectedX, expectedB };
		final karmaresearch.vlog.Atom expectedAtom = new karmaresearch.vlog.Atom(expectedPredicateName, expectedTerms);

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(atom);
		assertEquals(expectedAtom, vLogAtom);
	}

	@Test
	public void testToVLogRuleArray() {
		final Variable x = Expressions.makeVariable("x");
		final Variable y = Expressions.makeVariable("y");
		final Variable z = Expressions.makeVariable("z");
		final Variable w = Expressions.makeVariable("w");
		final Variable v = Expressions.makeVariable("v");
		final Atom atomP1X = Expressions.makeAtom("p1", x);
		final Atom atomP2XY = Expressions.makeAtom("p2", x, y);
		final Atom atomP3YZ = Expressions.makeAtom("p3", y, z);
		final Rule rule1 = Expressions.makeRule(atomP1X, atomP2XY, atomP3YZ);
		final Atom atomQXYZ = Expressions.makeAtom("q", x, y, z);
		final Atom atomQYW = Expressions.makeAtom("q", y, w);
		final Atom atomQ1XWZ = Expressions.makeAtom("q1", x, w, z);
		final Atom atomQ2XV = Expressions.makeAtom("q2", x, v);
		final Rule rule2 = Expressions.makeRule(atomQ2XV, atomQ1XWZ, atomQYW, atomQXYZ);

		final karmaresearch.vlog.Term expX = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "x");
		final karmaresearch.vlog.Term expY = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "y");
		final karmaresearch.vlog.Term expZ = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "z");
		final karmaresearch.vlog.Term expW = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "w");
		final karmaresearch.vlog.Term expV = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "v");
		final karmaresearch.vlog.Atom expAtomP1X = new karmaresearch.vlog.Atom("p1-1", expX);
		final karmaresearch.vlog.Atom expAtomP2XY = new karmaresearch.vlog.Atom("p2-2", expX, expY);
		final karmaresearch.vlog.Atom expAtomP3YZ = new karmaresearch.vlog.Atom("p3-2", expY, expZ);
		final karmaresearch.vlog.Rule expectedRule1 = new karmaresearch.vlog.Rule(new karmaresearch.vlog.Atom[] { expAtomP1X },
				new karmaresearch.vlog.Atom[] { expAtomP2XY, expAtomP3YZ });
		final karmaresearch.vlog.Atom expAtomQXYZ = new karmaresearch.vlog.Atom("q-3", expX, expY, expZ);
		final karmaresearch.vlog.Atom expAtomQYW = new karmaresearch.vlog.Atom("q-2", expY, expW);
		final karmaresearch.vlog.Atom expAtomQ1XWZ = new karmaresearch.vlog.Atom("q1-3", expX, expW, expZ);
		final karmaresearch.vlog.Atom expAtomQ2XV = new karmaresearch.vlog.Atom("q2-2", expX, expV);
		final karmaresearch.vlog.Rule expectedRule2 = new karmaresearch.vlog.Rule(new karmaresearch.vlog.Atom[] { expAtomQ2XV },
				new karmaresearch.vlog.Atom[] { expAtomQ1XWZ, expAtomQYW, expAtomQXYZ });

		final karmaresearch.vlog.Rule[] vLogRuleArray = ModelToVLogConverter.toVLogRuleArray(Arrays.asList(rule1, rule2));
		final karmaresearch.vlog.Rule[] expectedRuleArray = new karmaresearch.vlog.Rule[] { expectedRule1, expectedRule2 };
		assertArrayEquals(expectedRuleArray, vLogRuleArray);
	}

	@Test
	public void testVLogRuleRewritingStrategy() {
		assertEquals(karmaresearch.vlog.VLog.RuleRewriteStrategy.NONE, ModelToVLogConverter.toVLogRuleRewriteStrategy(RuleRewriteStrategy.NONE));
		assertEquals(karmaresearch.vlog.VLog.RuleRewriteStrategy.AGGRESSIVE,
				ModelToVLogConverter.toVLogRuleRewriteStrategy(RuleRewriteStrategy.SPLIT_HEAD_PIECES));
	}

}
