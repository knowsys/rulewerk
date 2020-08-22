package org.semanticweb.rulewerk.parser;

/*-
 * #%L
 * Rulewerk Parser
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

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.NegativeLiteral;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.RuleImpl;

public class EntityTest {

	@Test
	public void languageStringConstantToStringRoundTripTest() throws ParsingException {
		LanguageStringConstantImpl s = new LanguageStringConstantImpl("Test", "en");
		Predicate p = Expressions.makePredicate("p", 1);
		Fact f3 = Expressions.makeFact(p, s);
		assertEquals(f3, RuleParser.parseFact(f3.toString()));
	}

	@Test
	public void abstractConstantStringToStringRoundTripTest() throws ParsingException {
		AbstractConstantImpl f = new AbstractConstantImpl("f");
		Fact f1 = Expressions.makeFact("p", f);
		assertEquals(f1, RuleParser.parseFact(f1.toString()));
	}

	@Test
	public void abstractConstantAbsoluteToStringRoundTripTest() throws ParsingException {
		AbstractConstantImpl a = new AbstractConstantImpl("http://example.org/test");
		Fact f1 = Expressions.makeFact("p", a);
		assertEquals(f1, RuleParser.parseFact(f1.toString()));
	}

	@Test
	public void abstractConstantRelativeDoubleToStringRoundTripTest() throws ParsingException {
		AbstractConstantImpl b = new AbstractConstantImpl("4.2E9");
		Fact f1 = Expressions.makeFact("p", b);
		assertEquals(f1, RuleParser.parseFact(f1.toString()));
	}

	@Test
	public void abstractConstantRelativeIntegerToStringRoundTripTest() throws ParsingException {
		AbstractConstantImpl b = new AbstractConstantImpl("11");
		Fact f1 = Expressions.makeFact("p", b);
		assertEquals(f1, RuleParser.parseFact(f1.toString()));
	}

	@Test
	public void abstractConstantRelativeBooleanToStringRoundTripTest() throws ParsingException {
		AbstractConstantImpl b = new AbstractConstantImpl("false");
		Fact f1 = Expressions.makeFact("p", b);
		assertEquals(f1, RuleParser.parseFact(f1.toString()));
	}

	@Test
	public void abstractConstantRelativeDecimalToStringRoundTripTest() throws ParsingException {
		AbstractConstantImpl b = new AbstractConstantImpl("-5.0");
		Fact f1 = Expressions.makeFact("p", b);
		assertEquals(f1, RuleParser.parseFact(f1.toString()));
	}

	@Test
	public void iriRoundTripTest() throws ParsingException {
		String abstractConstant = "<1.0>";
		Fact f2 = RuleParser.parseFact("p(" + abstractConstant + ").");
		assertEquals(abstractConstant, f2.getArguments().get(0).toString());
	}

	@Test
	public void iriRoundTripTest2() throws ParsingException {
		String abstractConstant = "<a:b>";
		Fact f2 = RuleParser.parseFact("p(" + abstractConstant + ").");
		assertEquals(abstractConstant, f2.getArguments().get(0).toString());
	}

	@Test
	public void iriRoundTripTest3() throws ParsingException {
		String abstractConstant = "<a:1>";
		Fact f2 = RuleParser.parseFact("p(" + abstractConstant + ").");
		assertEquals(abstractConstant, f2.getArguments().get(0).toString());
	}

	@Test
	public void predicateIriRoundTripTest() throws ParsingException {
		AbstractConstantImpl a = new AbstractConstantImpl("a");
		Fact f = Expressions.makeFact("1.e1", a);
		assertEquals(f, RuleParser.parseFact(f.toString()));
	}

	@Test
	public void predicateRoundTripTest3() throws ParsingException {
		AbstractConstantImpl a = new AbstractConstantImpl("a");
		Fact f2 = Expressions.makeFact("a:1", a);
		assertEquals(f2, RuleParser.parseFact(f2.toString()));
	}

	@Test
	public void iriAngularBracketsTest() throws ParsingException {
		String constant = "a";
		Fact fact = RuleParser.parseFact("p(" + constant + ").");
		Term abstractConst = fact.getArguments().get(0);
		assertEquals(constant, abstractConst.toString());
		Fact fact2 = RuleParser.parseFact("p(<" + constant + ">).");
		Term abstractConst2 = fact2.getArguments().get(0);
		assertEquals(abstractConst, abstractConst2);
	}

	@Test
	public void ruleToStringRoundTripTest() throws ParsingException {
		Constant c = Expressions.makeAbstractConstant("c");
		Variable x = Expressions.makeUniversalVariable("X");
		Variable y = Expressions.makeUniversalVariable("Y");
		Variable z = Expressions.makeExistentialVariable("Z");
		PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
		PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, y);
		PositiveLiteral headAtom1 = Expressions.makePositiveLiteral("q", x, z);
		Conjunction<Literal> bodyLiterals = Expressions.makeConjunction(atom1, atom2);
		Conjunction<PositiveLiteral> headPositiveLiterals = Expressions.makePositiveConjunction(headAtom1);
		Rule rule1 = new RuleImpl(headPositiveLiterals, bodyLiterals);
		assertEquals(rule1, RuleParser.parseRule(rule1.toString()));
	}

	@Test
	public void conjunctionToStringRoundTripTest() throws ParsingException {
		Constant c = Expressions.makeAbstractConstant("c");
		Variable x = Expressions.makeUniversalVariable("X");
		Variable y = Expressions.makeUniversalVariable("Y");
		Variable z = Expressions.makeExistentialVariable("Z");
		NegativeLiteral atom1 = Expressions.makeNegativeLiteral("p", x, c);
		PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, y);
		PositiveLiteral headAtom1 = Expressions.makePositiveLiteral("q", x, z);
		Conjunction<Literal> bodyLiterals = Expressions.makeConjunction(atom1, atom2);
		Conjunction<PositiveLiteral> headPositiveLiterals = Expressions.makePositiveConjunction(headAtom1);
		Rule rule1 = new RuleImpl(headPositiveLiterals, bodyLiterals);
		assertEquals(bodyLiterals, RuleParser.parseRule(rule1.toString()).getBody());
		assertEquals(headPositiveLiterals, RuleParser.parseRule(rule1.toString()).getHead());
	}

	@Test
	public void positiveLiteralToStringRoundTripTest() throws ParsingException {
		Constant c = Expressions.makeAbstractConstant("c");
		Variable x = Expressions.makeUniversalVariable("X");
		PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
		assertEquals(atom1, RuleParser.parseLiteral(atom1.toString()));
	}

	@Test
	public void literalToStringRoundTripTest() throws ParsingException {
		Constant c = Expressions.makeAbstractConstant("c");
		Variable x = Expressions.makeUniversalVariable("X");
		NegativeLiteral atom1 = Expressions.makeNegativeLiteral("p", x, c);
		assertEquals(atom1, RuleParser.parseLiteral(atom1.toString()));
	}

	@Test
	public void datatypeDoubleConstantToStringRoundTripTest() throws ParsingException {
		String doubleConstant = "\"12.345E67\"^^<http://www.w3.org/2001/XMLSchema#double>";
		assertEquals(doubleConstant,
				RuleParser.parseFact("p(" + doubleConstant + ").").getArguments().get(0).toString());
		assertEquals(doubleConstant, RuleParser.parseFact("p(12.345E67).").getArguments().get(0).toString());
	}

	@Test
	public void datatypeFloatConstantToStringRoundTripTest() throws ParsingException {
		String floatConstant = "\"0.5\"^^<http://www.w3.org/2001/XMLSchema#float>";
		assertEquals(floatConstant, RuleParser.parseFact("p(" + floatConstant + ").").getArguments().get(0).toString());
	}

	@Test
	public void datatypeStringConstantToStringRoundTripTest() throws ParsingException {
		String shortStringConstant = "\"data\"";
		assertEquals(shortStringConstant,
				RuleParser.parseFact("p(" + shortStringConstant + "^^<http://www.w3.org/2001/XMLSchema#string>).")
						.getArguments().get(0).toString());
		assertEquals(shortStringConstant,
				RuleParser.parseFact("p(" + shortStringConstant + ").").getArguments().get(0).toString());
	}

	@Test
	public void datatypeIntegerConstantToStringRoundTripTest() throws ParsingException {
		String shortIntegerConstant = "1";
		assertEquals(shortIntegerConstant,
				RuleParser.parseFact("p(\"" + shortIntegerConstant + "\"^^<http://www.w3.org/2001/XMLSchema#integer>).")
						.getArguments().get(0).toString());
		assertEquals(shortIntegerConstant,
				RuleParser.parseFact("p(" + shortIntegerConstant + ").").getArguments().get(0).toString());
	}

	@Test
	public void datatypeDecimalToStringRoundTripTest() throws ParsingException {
		String decimalConstant = "\"0.23\"^^<http://www.w3.org/2001/XMLSchema#decimal>";
		assertEquals(decimalConstant,
				RuleParser.parseFact("p(" + decimalConstant + ").").getArguments().get(0).toString());
		assertEquals(decimalConstant, RuleParser.parseFact("p(0.23).").getArguments().get(0).toString());
	}
}
