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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.DatatypeConstant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Statement;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.parser.DatatypeConstantHandler;
import org.semanticweb.vlog4j.parser.ParserConfiguration;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

public class RuleParserTest {

	private final Variable x = Expressions.makeUniversalVariable("X");
	private final Variable y = Expressions.makeExistentialVariable("Y");
	private final Variable z = Expressions.makeUniversalVariable("Z");
	private final Constant c = Expressions.makeAbstractConstant("http://example.org/c");
	private final Constant d = Expressions.makeAbstractConstant("http://example.org/d");
	private final Constant abc = Expressions.makeDatatypeConstant("abc", PrefixDeclarations.XSD_STRING);
	private final Literal atom1 = Expressions.makePositiveLiteral("http://example.org/p", x, c);
	private final Literal negAtom1 = Expressions.makeNegativeLiteral("http://example.org/p", x, c);
	private final Literal atom2 = Expressions.makePositiveLiteral("http://example.org/p", x, z);
	private final PositiveLiteral atom3 = Expressions.makePositiveLiteral("http://example.org/q", x, y);
	private final PositiveLiteral atom4 = Expressions.makePositiveLiteral("http://example.org/r", x, d);
	private final PositiveLiteral fact = Expressions.makePositiveLiteral("http://example.org/s", c);
	private final PositiveLiteral fact2 = Expressions.makePositiveLiteral("p", abc);
	private final Conjunction<Literal> body1 = Expressions.makeConjunction(atom1, atom2);
	private final Conjunction<Literal> body2 = Expressions.makeConjunction(negAtom1, atom2);
	private final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom3, atom4);
	private final Rule rule1 = Expressions.makeRule(head, body1);
	private final Rule rule2 = Expressions.makeRule(head, body2);

	@Test
	public void testExplicitIri() throws ParsingException {
		String input = "<http://example.org/s>(<http://example.org/c>) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test
	public void testPrefixResolution() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . ex:s(ex:c) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test
	public void testBaseRelativeResolution() throws ParsingException {
		String input = "@base <http://example.org/> . <s>(<c>) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test
	public void testBaseResolution() throws ParsingException {
		String input = "@base <http://example.org/> . s(c) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test
	public void testNoBaseRelativeIri() throws ParsingException {
		String input = "s(c) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral atom = Expressions.makePositiveLiteral("s", Expressions.makeAbstractConstant("c"));
		assertEquals(Arrays.asList(atom), statements);
	}

	@Test(expected = ParsingException.class)
	public void testPrefixConflict() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . @prefix ex: <http://example.org/2/> . s(c) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testBaseConflict() throws ParsingException {
		String input = "@base <http://example.org/> . @base <http://example.org/2/> . s(c) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testMissingPrefix() throws ParsingException {
		String input = "ex:s(c) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testNoUniversalLiterals() throws ParsingException {
		String input = "p(?X) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testNoExistentialLiterals() throws ParsingException {
		String input = "p(!X) .";
		RuleParser.parse(input);
	}

	@Test
	public void testSimpleRule() throws ParsingException {
		String input = "@base <http://example.org/> . " + " q(?X, !Y), r(?X, d) :- p(?X,c), p(?X,?Z) . ";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(rule1), statements);
	}

	@Test
	public void testNegationRule() throws ParsingException {
		String input = "@base <http://example.org/> . " + " q(?X, !Y), r(?X, d) :- ~p(?X,c), p(?X,?Z) . ";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(rule2), statements);
	}

	@Test(expected = ParsingException.class)
	public void testUnsafeNegationRule() throws ParsingException {
		String input = "@base <http://example.org/> . " + " q(?X, !Y), r(?X, d) :- ~p(?Y,c), p(?X,?Z) . ";
		RuleParser.parse(input);
	}

	@Test
	public void testWhiteSpace() throws ParsingException {
		String input = "@base \n\n<http://example.org/> . "
				+ " q(?X, !Y)  , r(?X,    d\t ) \n\n:- p(?X,c), p(?X,\n?Z) \n. ";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(rule1), statements);
	}

	@Test(expected = ParsingException.class)
	public void testNoUnsafeVariables() throws ParsingException {
		String input = "p(?X,?Y) :- q(?X) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testNoConflictingQuantificationVariables() throws ParsingException {
		String input = "p(?X,!X) :- q(?X) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testNoBodyExistential() throws ParsingException {
		String input = "p(?X) :- q(?X,!Y) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testNoDollarVariables() throws ParsingException {
		String input = "p($X) :- q($X) .";
		RuleParser.parse(input);
	}

	@Test
	public void testIntegerLiteral() throws ParsingException {
		String input = "p(42) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral integerLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("42", PrefixDeclarations.XSD_INTEGER));
		assertEquals(Arrays.asList(integerLiteral), statements);
	}

	@Test
	public void testAbbreviatedIntegerLiteral() throws ParsingException {
		String input = "@prefix xsd: <" + PrefixDeclarations.XSD + "> . " + "p(\"42\"^^xsd:integer) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral integerLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("42", PrefixDeclarations.XSD_INTEGER));
		assertEquals(Arrays.asList(integerLiteral), statements);
	}

	@Test
	public void testFullIntegerLiteral() throws ParsingException {
		String input = "p(\"42\"^^<" + PrefixDeclarations.XSD_INTEGER + "> ) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral integerLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("42", PrefixDeclarations.XSD_INTEGER));
		assertEquals(Arrays.asList(integerLiteral), statements);
	}

	@Test
	public void testDecimalLiteral() throws ParsingException {
		String input = "p(-5.0) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral decimalLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("-5.0", PrefixDeclarations.XSD_DECIMAL));
		assertEquals(Arrays.asList(decimalLiteral), statements);
	}

	@Test
	public void testDoubleLiteral() throws ParsingException {
		String input = "p(4.2E9) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral doubleLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("4.2E9", PrefixDeclarations.XSD_DOUBLE));
		assertEquals(Arrays.asList(doubleLiteral), statements);
	}

	@Test
	public void testStringLiteral() throws ParsingException {
		String input = "p(\"abc\") .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact2), statements);
	}

	@Test(expected = ParsingException.class)
	public void testIncompleteStringLiteral() throws ParsingException {
		String input = "p(\"abc) .";
		RuleParser.parse(input);
	}

	@Test
	public void testStringLiteralEscapes() throws ParsingException {
		String input = "p(\"_\\\"_\\\\_\\n_\\t_\") ."; // User input: p("_\"_\\_\n_\t_")
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("_\"_\\_\n_\t_", PrefixDeclarations.XSD_STRING));
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test
	public void testStringLiteralAllEscapes() throws ParsingException {
		// User input: p("_\n_\t_\r_\b_\f_\'_\"_\\_")
		String input = "p(\"_\\n_\\t_\\r_\\b_\\f_\\'_\\\"_\\\\_\") .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("_\n_\t_\r_\b_\f_\'_\"_\\_", PrefixDeclarations.XSD_STRING));
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test
	public void testStringLiteralMultiLine() throws ParsingException {
		String input = "p('''line 1\n\n" + "line 2\n" + "line 3''') ."; // User input: p("a\"b\\c")
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("line 1\n\nline 2\nline 3", PrefixDeclarations.XSD_STRING));
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test(expected = ParsingException.class)
	public void testIncompleteStringLiteralMultiLine() throws ParsingException {
		String input = "p('''abc\ndef'') .";
		RuleParser.parse(input);
	}

	@Test
	public void testFullLiteral() throws ParsingException {
		String input = "p(\"abc\"^^<http://www.w3.org/2001/XMLSchema#string>) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact2), statements);
	}

	@Test
	public void testUnicodeLiteral() throws ParsingException {
		String input = "p(\"\\u0061\\u0062\\u0063\") ."; // "abc"
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact2), statements);
	}

	@Test
	public void testUnicodeUri() throws ParsingException {
		String input = "@base <http://example.org/> . @prefix ex: <http://example.org/> .  ex:\\u0073(c) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test
	public void testPrefixedLiteral() throws ParsingException {
		String input = "@prefix xsd: <" + PrefixDeclarations.XSD + "> . " + "p(\"abc\"^^xsd:string) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact2), statements);
	}

	@Test
	public void testLangStringLiteral() throws ParsingException {
		String input = "p(\"abc\"@en-gb) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeLanguageStringConstant("abc", "en-gb"));
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test
	public void testLineComments() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . % comment \n" + "%@prefix ex: <http:nourl> \n"
				+ " ex:s(ex:c) . % comment \n";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact), statements);
	}

	@Test
	public void testPositiveLiteral() throws ParsingException {
		String input = "<http://example.org/p>(?X,<http://example.org/c>)";
		Literal literal = RuleParser.parsePositiveLiteral(input);
		assertEquals(atom1, literal);
	}

	@Test(expected = ParsingException.class)
	public void testPositiveLiteralError() throws ParsingException {
		String input = "~ <http://example.org/p>(?X,<http://example.org/c>)";
		RuleParser.parsePositiveLiteral(input);
	}

	@Test
	public void testLiteral() throws ParsingException {
		String input = "~ <http://example.org/p>(?X,<http://example.org/c>)";
		Literal literal = RuleParser.parseLiteral(input);
		assertEquals(negAtom1, literal);
	}

	@Test(expected = ParsingException.class)
	public void tesLiteralError() throws ParsingException {
		String input = "<http://example.org/p>(?X,<http://example.org/c)";
		RuleParser.parseLiteral(input);
	}

	@Test(expected = ParsingException.class)
	public void testBlankPrefixDeclaration() throws ParsingException {
		String input = "@prefix _: <http://example.org/> . s(c) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testBlankNodeTerm() throws ParsingException {
		String input = "<http://example.org/p>(_:blank) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void testBlankPredicateName() throws ParsingException {
		String input = "_:(a) .";
		RuleParser.parse(input);
	}

	@Test
	public void predicateRelativeNumericIRITest() throws ParsingException {
		AbstractConstantImpl a = new AbstractConstantImpl("a");
		Fact f = RuleParser.parseFact("<1.e1>(a).");
		Fact f2 = Expressions.makeFact("1.e1", a);
		assertEquals(f, f2);
	}

	@Test
	public void predicateAbsoluteIRITest() throws ParsingException {
		AbstractConstantImpl a = new AbstractConstantImpl("a");
		Fact f = RuleParser.parseFact("<a:b>(a).");
		Fact f2 = Expressions.makeFact("a:b", a);
		assertEquals(f, f2);
	}

	@Test
	public void testCustomDatatype() throws ParsingException {
		final String typename = "http://example.org/#test";
		DatatypeConstant constant = Expressions.makeDatatypeConstant("test", typename);
		DatatypeConstantHandler handler = mock(DatatypeConstantHandler.class);
		ParserConfiguration parserConfiguration = new ParserConfiguration();
		parserConfiguration.registerDatatype(typename, handler);
		doReturn(constant).when(handler).createConstant(ArgumentMatchers.eq("hello, world"));

		String input = "p(\"hello, world\"^^<" + typename + ">) .";
		Literal literal = RuleParser.parseLiteral(input, parserConfiguration);
		DatatypeConstant result = (DatatypeConstant) literal.getConstants().toArray()[0];
		assertEquals(constant, result);
	}
}
