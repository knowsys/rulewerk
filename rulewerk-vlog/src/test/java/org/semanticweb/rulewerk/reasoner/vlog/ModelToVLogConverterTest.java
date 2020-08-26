package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.StatementVisitor;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.rulewerk.core.model.implementation.RenamedNamedNull;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.RuleRewriteStrategy;
import org.semanticweb.rulewerk.core.reasoner.implementation.Skolemization;

public class ModelToVLogConverterTest {

	@Test
	public void testToVLogTermVariable() {
		final Variable variable = Expressions.makeUniversalVariable("var");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.VARIABLE, "var");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(variable);

		assertEquals(expectedVLogTerm, vLogTerm);
	}

	@Test
	public void testToVLogTermAbstractConstant() {
		final Constant constant = Expressions.makeAbstractConstant("const");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "const");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(constant);

		assertEquals(expectedVLogTerm, vLogTerm);
		assertEquals(expectedVLogTerm.getName(), TermToVLogConverter.getVLogNameForConstantName(constant.getName()));
		assertEquals(expectedVLogTerm.getName(), TermToVLogConverter.getVLogNameForConstant(constant));
	}

	@Test
	public void testToVLogTermAbstractConstantIri() {
		final Constant constant = Expressions.makeAbstractConstant("http://example.org");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "<http://example.org>");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(constant);

		assertEquals(expectedVLogTerm, vLogTerm);
		assertEquals(expectedVLogTerm.getName(), TermToVLogConverter.getVLogNameForConstantName(constant.getName()));
		assertEquals(expectedVLogTerm.getName(), TermToVLogConverter.getVLogNameForConstant(constant));
	}

	@Test
	public void testToVLogTermDatatypeConstant() {
		final Constant constant = Expressions.makeDatatypeConstant("c", "http://example.org");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "\"c\"^^<http://example.org>");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(constant);

		assertEquals(expectedVLogTerm, vLogTerm);
		assertEquals(expectedVLogTerm.getName(), TermToVLogConverter.getVLogNameForConstantName(constant.getName()));
		assertEquals(expectedVLogTerm.getName(), TermToVLogConverter.getVLogNameForConstant(constant));
	}

	@Test
	public void testToVLogTermLanguageStringConstant() {
		final Constant constant = Expressions.makeLanguageStringConstant("c", "en");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "\"c\"@en");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(constant);

		assertEquals(expectedVLogTerm, vLogTerm);
		assertEquals(expectedVLogTerm.getName(), TermToVLogConverter.getVLogNameForConstantName(constant.getName()));
	}

	@Test
	public void testToVLogTermBlank() {
		final NamedNull blank = new NamedNullImpl("blank");
		final karmaresearch.vlog.Term expectedVLogTerm = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.BLANK, "blank");

		final karmaresearch.vlog.Term vLogTerm = ModelToVLogConverter.toVLogTerm(blank);

		assertEquals(expectedVLogTerm, vLogTerm);
	}

	@Test
	public void testToVLogTermBlankSkolemization() {
		final Skolemization skolemization = new Skolemization();
		final NamedNull blank = new NamedNullImpl("blank");

		final String vLogSkolemConstant = TermToVLogConverter.getVLogNameForNamedNull(blank);

		assertNotEquals("blank", vLogSkolemConstant);
		// generated ids differ by Skolemization instance, but should have the same
		// length:
		assertEquals(skolemization.getSkolemConstantName(blank).length(), vLogSkolemConstant.length());
	}

	@Test
	public void testToVLogTermBlankRenamedSkolemization() {
		final Skolemization skolemization = new Skolemization();
		final UUID uuid = UUID.randomUUID();
		final NamedNull blank = new RenamedNamedNull(uuid);

		final String vLogSkolemConstant = TermToVLogConverter.getVLogNameForNamedNull(blank);

		assertEquals(skolemization.getSkolemConstantName(blank), vLogSkolemConstant);
	}

	@Test
	public void testToVLogTermArray() {
		final Variable vx = Expressions.makeUniversalVariable("x");
		final Variable vxToo = Expressions.makeUniversalVariable("x");
		final Variable vy = Expressions.makeUniversalVariable("y");
		final Constant cx = Expressions.makeAbstractConstant("x");
		final NamedNull bx = new NamedNullImpl("x");
		final List<Term> terms = Arrays.asList(vx, cx, vxToo, bx, vy);

		final karmaresearch.vlog.Term expectedVx = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.VARIABLE, "x");
		final karmaresearch.vlog.Term expectedVy = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.VARIABLE, "y");
		final karmaresearch.vlog.Term expectedCx = new karmaresearch.vlog.Term(
				karmaresearch.vlog.Term.TermType.CONSTANT, "x");
		final karmaresearch.vlog.Term expectedBx = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK,
				"x");
		final karmaresearch.vlog.Term[] expectedTermArray = { expectedVx, expectedCx, expectedVx, expectedBx,
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
		final Constant c1 = Expressions.makeAbstractConstant("1");
		final Constant c2 = Expressions.makeAbstractConstant("2");
		final Constant c3 = Expressions.makeAbstractConstant("3");
		final Fact atom1 = Expressions.makeFact("p1", Arrays.asList(c1));
		final Fact atom2 = Expressions.makeFact("p2", Arrays.asList(c2, c3));

		final String[][] vLogTuples = ModelToVLogConverter.toVLogFactTuples(Arrays.asList(atom1, atom2));

		final String[][] expectedTuples = { { "1" }, { "2", "3" } };
		assertArrayEquals(expectedTuples, vLogTuples);
	}

	@Test
	public void testToVLogFactTupleNulls() {
		final Skolemization skolemization = new Skolemization();
		final UUID uuid = UUID.randomUUID();
		final NamedNull n = new RenamedNamedNull(uuid);
		final Fact atom1 = Expressions.makeFact("p1", Arrays.asList(n));

		final String[] expectedTuple = { skolemization.getSkolemConstantName(n) };

		final String[] actualTuple = ModelToVLogConverter.toVLogFactTuple(atom1);

		assertArrayEquals(expectedTuple, actualTuple);
	}

	@Test(expected = RulewerkRuntimeException.class)
	public void testToVLogFactTupleUnsupported() {
		// We need a fact that accepts exception-causing terms in the first place:
		class NonValidatingFact extends PositiveLiteralImpl implements Fact {

			public NonValidatingFact(Predicate predicate, List<Term> terms) {
				super(predicate, terms);
			}

			@Override
			public <T> T accept(StatementVisitor<T> statementVisitor) {
				return statementVisitor.visit(this);
			}

		}

		final Variable x = Expressions.makeUniversalVariable("X");
		final Fact atom1 = new NonValidatingFact(Expressions.makePredicate("p1", 1), Arrays.asList(x));

		ModelToVLogConverter.toVLogFactTuple(atom1);
	}

	@Test
	public void testToVLogPredicate() {
		final Predicate predicate = Expressions.makePredicate("pred", 1);
		final String vLogPredicate = ModelToVLogConverter.toVLogPredicate(predicate);
		assertEquals("pred-1", vLogPredicate);
	}

	@Test
	public void testToVLogAtom() {
		final Constant c = Expressions.makeAbstractConstant("c");
		final Variable x = Expressions.makeUniversalVariable("x");
		final NamedNull b = new NamedNullImpl("_:b");
		final PositiveLiteral atom = Expressions.makePositiveLiteral("pred", c, x, b);

		final karmaresearch.vlog.Term expectedC = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"c");
		final karmaresearch.vlog.Term expectedX = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				"x");
		final karmaresearch.vlog.Term expectedB = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK,
				"_:b");

		final String expectedPredicateName = "pred" + ModelToVLogConverter.PREDICATE_ARITY_SUFFIX_SEPARATOR + 3;
		final karmaresearch.vlog.Term[] expectedTerms = { expectedC, expectedX, expectedB };
		final karmaresearch.vlog.Atom expectedAtom = new karmaresearch.vlog.Atom(expectedPredicateName, expectedTerms);

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(atom);
		assertEquals(expectedAtom, vLogAtom);
	}

	@Test
	public void testToVLogRuleArray() {
		final Variable x = Expressions.makeUniversalVariable("x");
		final Variable y = Expressions.makeUniversalVariable("y");
		final Variable z = Expressions.makeUniversalVariable("z");
		final Variable w = Expressions.makeUniversalVariable("w");
		final Variable v = Expressions.makeExistentialVariable("v");
		final PositiveLiteral atomP1X = Expressions.makePositiveLiteral("p1", x);
		final PositiveLiteral atomP2XY = Expressions.makePositiveLiteral("p2", x, y);
		final PositiveLiteral atomP3YZ = Expressions.makePositiveLiteral("p3", y, z);
		final Rule rule1 = Expressions.makeRule(atomP1X, atomP2XY, atomP3YZ);
		final PositiveLiteral atomQXYZ = Expressions.makePositiveLiteral("q", x, y, z);
		final PositiveLiteral atomQYW = Expressions.makePositiveLiteral("q", y, w);
		final PositiveLiteral atomQ1XWZ = Expressions.makePositiveLiteral("q1", x, w, z);
		final PositiveLiteral atomQ2XV = Expressions.makePositiveLiteral("q2", x, v);
		final Rule rule2 = Expressions.makeRule(atomQ2XV, atomQ1XWZ, atomQYW, atomQXYZ);

		final karmaresearch.vlog.Term expX = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				"x");
		final karmaresearch.vlog.Term expY = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				"y");
		final karmaresearch.vlog.Term expZ = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				"z");
		final karmaresearch.vlog.Term expW = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				"w");
		final karmaresearch.vlog.Term expV = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				"!v");
		final karmaresearch.vlog.Atom expAtomP1X = new karmaresearch.vlog.Atom("p1-1", expX);
		final karmaresearch.vlog.Atom expAtomP2XY = new karmaresearch.vlog.Atom("p2-2", expX, expY);
		final karmaresearch.vlog.Atom expAtomP3YZ = new karmaresearch.vlog.Atom("p3-2", expY, expZ);
		final karmaresearch.vlog.Rule expectedRule1 = new karmaresearch.vlog.Rule(
				new karmaresearch.vlog.Atom[] { expAtomP1X },
				new karmaresearch.vlog.Atom[] { expAtomP2XY, expAtomP3YZ });
		final karmaresearch.vlog.Atom expAtomQXYZ = new karmaresearch.vlog.Atom("q-3", expX, expY, expZ);
		final karmaresearch.vlog.Atom expAtomQYW = new karmaresearch.vlog.Atom("q-2", expY, expW);
		final karmaresearch.vlog.Atom expAtomQ1XWZ = new karmaresearch.vlog.Atom("q1-3", expX, expW, expZ);
		final karmaresearch.vlog.Atom expAtomQ2XV = new karmaresearch.vlog.Atom("q2-2", expX, expV);
		final karmaresearch.vlog.Rule expectedRule2 = new karmaresearch.vlog.Rule(
				new karmaresearch.vlog.Atom[] { expAtomQ2XV },
				new karmaresearch.vlog.Atom[] { expAtomQ1XWZ, expAtomQYW, expAtomQXYZ });

		final karmaresearch.vlog.Rule[] vLogRuleArray = ModelToVLogConverter
				.toVLogRuleArray(Arrays.asList(rule1, rule2));
		final karmaresearch.vlog.Rule[] expectedRuleArray = new karmaresearch.vlog.Rule[] { expectedRule1,
				expectedRule2 };
		assertArrayEquals(expectedRuleArray, vLogRuleArray);
	}

	@Test
	public void testVLogRuleRewritingStrategy() {
		assertEquals(karmaresearch.vlog.VLog.RuleRewriteStrategy.NONE,
				ModelToVLogConverter.toVLogRuleRewriteStrategy(RuleRewriteStrategy.NONE));
		assertEquals(karmaresearch.vlog.VLog.RuleRewriteStrategy.AGGRESSIVE,
				ModelToVLogConverter.toVLogRuleRewriteStrategy(RuleRewriteStrategy.SPLIT_HEAD_PIECES));
	}

}
