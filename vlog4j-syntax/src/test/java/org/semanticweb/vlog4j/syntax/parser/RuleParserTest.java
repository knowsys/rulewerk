package org.semanticweb.vlog4j.syntax.parser;

/*-
 * #%L
 * VLog4j Syntax
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class RuleParserTest {

	final Variable x = Expressions.makeVariable("X");
	final Variable y = Expressions.makeVariable("Y");
	final Variable z = Expressions.makeVariable("Z");
	final Constant c = Expressions.makeConstant("http://example.org/c");
	final Constant d = Expressions.makeConstant("http://example.org/d");
	final Constant abc = Expressions.makeConstant("\"abc\"^^<http://www.w3.org/2001/XMLSchema#string>");
	final Literal atom1 = Expressions.makePositiveLiteral("http://example.org/p", x, c);
	final Literal atom2 = Expressions.makePositiveLiteral("http://example.org/p", x, z);
	final PositiveLiteral atom3 = Expressions.makePositiveLiteral("http://example.org/q", x, y);
	final PositiveLiteral atom4 = Expressions.makePositiveLiteral("http://example.org/r", x, d);
	final PositiveLiteral fact = Expressions.makePositiveLiteral("http://example.org/s", c);
	final PositiveLiteral fact2 = Expressions.makePositiveLiteral("p", abc);
	final Conjunction<Literal> body = Expressions.makeConjunction(atom1, atom2);
	final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom3, atom4);
	final Rule rule = Expressions.makeRule(head, body);

	@Test
	public void testExplicitIri() throws ParsingException {
		String input = "<http://example.org/s>(<http://example.org/c>) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(fact), ruleParser.getFacts());
	}

	@Test
	public void testPrefixResolution() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . ex:s(ex:c) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(fact), ruleParser.getFacts());
	}

	@Test
	public void testBaseRelativeResolution() throws ParsingException {
		String input = "@base <http://example.org/> . <s>(<c>) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(fact), ruleParser.getFacts());
	}

	@Test
	public void testBaseResolution() throws ParsingException {
		String input = "@base <http://example.org/> . s(c) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(fact), ruleParser.getFacts());
	}

	@Test
	public void testNoBaseRelativeIri() throws ParsingException {
		String input = "s(c) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		PositiveLiteral atom = Expressions.makePositiveLiteral("s", Expressions.makeConstant("c"));
		assertEquals(Arrays.asList(atom), ruleParser.getFacts());
	}

	@Test(expected = ParsingException.class)
	public void testPrefixConflict() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . @prefix ex: <http://example.org/2/> . s(c) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testBaseConflict() throws ParsingException {
		String input = "@base <http://example.org/> . @base <http://example.org/2/> . s(c) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testMissingPrefix() throws ParsingException {
		String input = "ex:s(c) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
	}

	@Test
	public void testSimpleRule() throws ParsingException {
		String input = "@base <http://example.org/> . " + " q(?X, !Y), r(?X, d) :- p(?X,c), p(?X,?Z) . ";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(rule), ruleParser.getRules());
	}

	@Test
	public void testWhiteSpace() throws ParsingException {
		String input = "@base \n\n<http://example.org/> . "
				+ " q(?X, !Y)  , r(?X,    d\t ) \n\n:- p(?X,c), p(?X,\n?Z) \n. ";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(rule), ruleParser.getRules());
	}

	@Test(expected = ParsingException.class)
	public void testNoUnsafeVariables() throws ParsingException {
		String input = "p(?X,?Y) :- q(?X) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
	}

	@Test
	public void testStringLiteral() throws ParsingException {
		String input = "p(\"abc\") .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(fact2), ruleParser.getFacts());
	}

	@Test
	public void testFullLiteral() throws ParsingException {
		String input = "p(\"abc\"^^<http://www.w3.org/2001/XMLSchema#string>) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(fact2), ruleParser.getFacts());
	}

	@Test
	public void testPrefixedLiteral() throws ParsingException {
		String input = "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> . " + "p(\"abc\"^^xsd:string) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(fact2), ruleParser.getFacts());
	}

}
