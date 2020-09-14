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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.DatatypeConstant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParserBase.FormulaContext;

public class RuleParserTest implements ParserTestUtils {

	private final Variable x = Expressions.makeUniversalVariable("X");
	private final Variable y = Expressions.makeExistentialVariable("Y");
	private final Variable z = Expressions.makeUniversalVariable("Z");
	private final Constant c = Expressions.makeAbstractConstant("http://example.org/c");
	private final Constant d = Expressions.makeAbstractConstant("http://example.org/d");
	private final Constant e = Expressions.makeAbstractConstant("https://example.org/e");
	private final Constant abc = Expressions.makeDatatypeConstant("abc", PrefixDeclarationRegistry.XSD_STRING);
	private final Constant xyz = Expressions.makeDatatypeConstant("xyz", PrefixDeclarationRegistry.XSD_STRING);
	private final Literal atom1 = Expressions.makePositiveLiteral("http://example.org/p", x, c);
	private final Literal negAtom1 = Expressions.makeNegativeLiteral("http://example.org/p", x, c);
	private final Literal atom2 = Expressions.makePositiveLiteral("http://example.org/p", x, z);
	private final PositiveLiteral atom3 = Expressions.makePositiveLiteral("http://example.org/q", x, y);
	private final PositiveLiteral atom4 = Expressions.makePositiveLiteral("http://example.org/r", x, d);
	private final PositiveLiteral fact1 = Expressions.makePositiveLiteral("http://example.org/s", c);
	private final PositiveLiteral fact2 = Expressions.makePositiveLiteral("p", abc);
	private final PositiveLiteral fact3 = Expressions.makePositiveLiteral("http://example.org/p", abc);
	private final PositiveLiteral fact4 = Expressions.makePositiveLiteral("https://example.org/s", e);
	private final PositiveLiteral fact5 = Expressions.makePositiveLiteral("q", xyz);
	private final PositiveLiteral fact6 = Expressions.makePositiveLiteral("http://example.org/p", abc);
	private final Conjunction<Literal> body1 = Expressions.makeConjunction(atom1, atom2);
	private final Conjunction<Literal> body2 = Expressions.makeConjunction(negAtom1, atom2);
	private final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom3, atom4);
	private final Rule rule1 = Expressions.makeRule(head, body1);
	private final Rule rule2 = Expressions.makeRule(head, body2);

	@Test
	public void testExplicitIri() throws ParsingException {
		String input = "<http://example.org/s>(<http://example.org/c>) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact1), statements);
	}

	@Test
	public void testPrefixResolution() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . ex:s(ex:c) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact1), statements);
	}

	@Test
	public void testBaseRelativeResolution() throws ParsingException {
		String input = "@base <http://example.org/> . <s>(<c>) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact1), statements);
	}

	@Test
	public void testBaseResolution() throws ParsingException {
		String input = "@base <http://example.org/> . s(c) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact1), statements);
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
	public void testFactWithCommentSymbol() throws ParsingException {
		String input = "t(\"%test\") . ";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(Expressions.makeFact("t",
				Expressions.makeDatatypeConstant("%test", PrefixDeclarationRegistry.XSD_STRING))), statements);
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
		String input = "p(42)";
		PositiveLiteral integerLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("42", PrefixDeclarationRegistry.XSD_INTEGER));
		assertEquals(integerLiteral, RuleParser.parseLiteral(input));
	}

	@Test
	public void testAbbreviatedIntegerLiteral() throws ParsingException {
		String input = "@prefix xsd: <" + PrefixDeclarationRegistry.XSD + "> . " + "p(\"42\"^^xsd:integer) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		PositiveLiteral integerLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("42", PrefixDeclarationRegistry.XSD_INTEGER));
		assertEquals(Arrays.asList(integerLiteral), statements);
	}

	@Test
	public void testFullIntegerLiteral() throws ParsingException {
		String input = "p(\"42\"^^<" + PrefixDeclarationRegistry.XSD_INTEGER + "> )";
		PositiveLiteral integerLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("42", PrefixDeclarationRegistry.XSD_INTEGER));
		assertEquals(integerLiteral, RuleParser.parseLiteral(input));
	}

	@Test
	public void testDecimalLiteral() throws ParsingException {
		String input = "p(-5.0)";
		PositiveLiteral decimalLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("-5.0", PrefixDeclarationRegistry.XSD_DECIMAL));
		assertEquals(decimalLiteral, RuleParser.parseLiteral(input));
	}

	@Test
	public void testDoubleLiteral() throws ParsingException {
		String input = "p(4.2E9)";
		PositiveLiteral doubleLiteral = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("4.2E9", PrefixDeclarationRegistry.XSD_DOUBLE));
		assertEquals(doubleLiteral, RuleParser.parseLiteral(input));
	}

	@Test
	public void testStringLiteral() throws ParsingException {
		String input = "p(\"abc\")";
		assertEquals(fact2, RuleParser.parseLiteral(input));
	}

	@Test(expected = ParsingException.class)
	public void testIncompleteStringLiteral() throws ParsingException {
		String input = "p(\"abc)";
		RuleParser.parseLiteral(input);
	}

	@Test
	public void parseLiteral_escapeSequences_succeeds() throws ParsingException {
		String input = "p(\"_\\\"_\\\\_\\n_\\t_\")"; // User input: p("_\"_\\_\n_\t_")
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("_\"_\\_\n_\t_", PrefixDeclarationRegistry.XSD_STRING));
		assertEquals(fact, RuleParser.parseLiteral(input));
	}

	@Test
	public void parseLiteral_escapeSequences_roundTrips() throws ParsingException {
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("_\"_\\_\n_\t_", PrefixDeclarationRegistry.XSD_STRING));
		assertEquals(fact, RuleParser.parseLiteral(fact.toString()));
	}

	@Test
	public void parseLiteral_allEscapeSequences_succeeds() throws ParsingException {
		// User input: p("_\n_\t_\r_\b_\f_\'_\"_\\_")
		String input = "p(\"_\\n_\\t_\\r_\\b_\\f_\\'_\\\"_\\\\_\")";
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("_\n_\t_\r_\b_\f_\'_\"_\\_", PrefixDeclarationRegistry.XSD_STRING));
		assertEquals(fact, RuleParser.parseLiteral(input));
	}

	@Test
	public void parseLiteral_allEscapeSequences_roundTrips() throws ParsingException {
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("_\n_\t_\r_\b_\f_\'_\"_\\_", PrefixDeclarationRegistry.XSD_STRING));
		assertEquals(fact, RuleParser.parseLiteral(fact.toString()));
	}

	@Test(expected = ParsingException.class)
	public void parseLiteral_invalidEscapeSequence_throws() throws ParsingException {
		String input = "p(\"\\ÿ\")";
		RuleParser.parseLiteral(input);
	}

	@Test(expected = ParsingException.class)
	public void parseLiteral_incompleteEscapeAtEndOfLiteral_throws() throws ParsingException {
		String input = "p(\"\\\")";
		RuleParser.parseLiteral(input);
	}

	@Test
	public void parseLiteral_multiLineLiteral_succeeds() throws ParsingException {
		String input = "p('''line 1\n\n" + "line 2\n" + "line 3''')"; // User input: p("a\"b\\c")
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("line 1\n\nline 2\nline 3", PrefixDeclarationRegistry.XSD_STRING));
		assertEquals(fact, RuleParser.parseLiteral(input));
	}

	@Test
	public void parseLiteral_multiLineLiteral_roundTrips() throws ParsingException {
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeDatatypeConstant("line 1\n\nline 2\nline 3", PrefixDeclarationRegistry.XSD_STRING));
		assertEquals(fact, RuleParser.parseLiteral(fact.toString()));
	}

	@Test(expected = ParsingException.class)
	public void testIncompleteStringLiteralMultiLine() throws ParsingException {
		String input = "p('''abc\ndef'')";
		RuleParser.parseLiteral(input);
	}

	@Test
	public void testFullLiteral() throws ParsingException {
		String input = "p(\"abc\"^^<http://www.w3.org/2001/XMLSchema#string>)";
		assertEquals(fact2, RuleParser.parseLiteral(input));
	}

	@Test
	public void testUnicodeLiteral() throws ParsingException {
		String input = "p(\"\\u0061\\u0062\\u0063\")"; // "abc"
		assertEquals(fact2, RuleParser.parseLiteral(input));
	}

	@Test
	public void testUnicodeUri() throws ParsingException {
		String input = "@base <http://example.org/> . @prefix ex: <http://example.org/> .  ex:\\u0073(c) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact1), statements);
	}

	@Test
	public void testPrefixedLiteral() throws ParsingException {
		String input = "@prefix xsd: <" + PrefixDeclarationRegistry.XSD + "> . " + "p(\"abc\"^^xsd:string) .";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact2), statements);
	}

	@Test
	public void testLangStringLiteral() throws ParsingException {
		String input = "p(\"abc\"@en-gb)";
		PositiveLiteral fact = Expressions.makePositiveLiteral("p",
				Expressions.makeLanguageStringConstant("abc", "en-gb"));
		assertEquals(fact, RuleParser.parseLiteral(input));
	}

	@Test
	public void testLineComments() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . % comment \n" + "%@prefix ex: <http:nourl> \n"
				+ " ex:s(ex:c) . % comment \n";
		ArrayList<Statement> statements = new ArrayList<>(RuleParser.parse(input).getStatements());
		assertEquals(Arrays.asList(fact1), statements);
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

	@Test
	public void parse_NamedNullInFact_succeeds() throws ParsingException {
		String input = "<http://example.org/p>(_:blank) .";
		KnowledgeBase result = RuleParser.parse(input);
		List<Fact> facts = result.getFacts();

		assertEquals(1, facts.size());
		assertArgumentIsNamedNull(facts.get(0), 1);
	}

	@Test
	public void parseTerm_NamedNull_succeeds() throws ParsingException {
		String input = "_:blank";
		Term result = RuleParser.parseTerm(input);
		assertUuid(result.getName());
	}

	@Test
	public void parseTerm_NamedNullInHead_succeeds() throws ParsingException {
		String input = "_:blank";
		Term result = RuleParser.parseTerm(input, FormulaContext.HEAD);
		assertUuid(result.getName());
	}

	@Test(expected = ParsingException.class)
	public void parseTerm_NamedNullInBodyContext_throws() throws ParsingException {
		String input = "_:blank";
		RuleParser.parseTerm(input, FormulaContext.BODY);
	}

	@Test(expected = ParsingException.class)
	public void testBParsingExceptione() throws ParsingException {
		String input = "_:(a) .";
		RuleParser.parse(input);
	}

	@Test(expected = ParsingException.class)
	public void parseLiteral_invalidLiteralString_throws() throws ParsingException {
		final String input = "P(\"a\")^^whatever";
		RuleParser.parseLiteral(input);
	}

	@Test(expected = ParsingException.class)
	public void testNonIriTypeInDatatypeLiteral() throws ParsingException {
		final String input = "\"a\"^^whatever";
		RuleParser.parseTerm(input);
	}

	@Test
	public void testIriTypeInDatatypeLiteral() throws ParsingException {
		final String iri = "whatever";
		final String input = "P(\"a\"^^<" + iri + ">)";
		Literal literal = RuleParser.parseLiteral(input);
		DatatypeConstant result = (DatatypeConstant) literal.getConstants().toArray()[0];
		assertEquals(iri, result.getDatatype());
	}

	@Test
	public void predicateRelativeNumericIRITest() throws ParsingException {
		AbstractConstantImpl a = new AbstractConstantImpl("a");
		Fact f = RuleParser.parseFact("<1.e1>(a)."); // 1.e1 == "10"^^xsd:double
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
	public void parse_absoluteIriInRuleHead_succeeds() throws ParsingException {
		RuleParser.parseRule("<A>(?x) :- B(?x), C(?x) .");
	}

	@Test
	public void parse_absoluteIriInRuleBody_succeeds() throws ParsingException {
		RuleParser.parseRule("A(?x) :- B(?x), <C>(?x) .");
	}

	@Test
	public void parse_absoluteIrisInRule_succeeds() throws ParsingException {
		RuleParser.parseRule("<A>(?x) :- B(?x), <C>(?x) .");
	}

	@Test
	public void testCustomDatatype() throws ParsingException {
		final String typename = "http://example.org/#test";
		DatatypeConstant constant = Expressions.makeDatatypeConstant("test", typename);
		DatatypeConstantHandler handler = mock(DatatypeConstantHandler.class);
		ParserConfiguration parserConfiguration = new ParserConfiguration();
		parserConfiguration.registerDatatype(typename, handler);
		doReturn(constant).when(handler).createConstant(ArgumentMatchers.eq("hello, world"));

		String input = "p(\"hello, world\"^^<" + typename + ">)";
		Literal literal = RuleParser.parseLiteral(input, parserConfiguration);
		DatatypeConstant result = (DatatypeConstant) literal.getConstants().toArray()[0];
		assertEquals(constant, result);
	}

	@Test
	public void parse_importStatement_succeeds() throws ParsingException {
		String input = "@import \"src/test/resources/facts.rls\" .";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		List<PositiveLiteral> expected = Arrays.asList(fact1, fact2);
		List<Fact> result = knowledgeBase.getFacts();
		assertEquals(expected, result);
	}

	@Test
	public void parse_relativeImportStatement_succeeds() throws ParsingException {
		String input = "@base <http://example.org/> . @import-relative \"src/test/resources/facts.rls\" .";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		List<PositiveLiteral> expected = Arrays.asList(fact1, fact3);
		List<Fact> result = knowledgeBase.getFacts();
		assertEquals(expected, result);
	}

	@Test
	public void parse_importStatement_relativeImport_succeeds() throws ParsingException {
	String input = "@import \"src/test/resources/subdir/sibling.rls\" .";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		List<PositiveLiteral> expected = Arrays.asList(fact4, fact5);
		List<Fact> result = knowledgeBase.getFacts();
		assertEquals(expected, result);
	}

	@Test
	public void parse_importStatement_relativeParentImport_succeeds() throws ParsingException {
	String input = "@import \"src/test/resources/subdir/parent.rls\" .";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		List<PositiveLiteral> expected = Arrays.asList(fact1, fact2);
		List<Fact> result = knowledgeBase.getFacts();
		assertEquals(expected, result);
	}

	@Test
	public void parse_relativeImportStatement_relativeImport_succeeds() throws ParsingException {
		String input = "@base <http://example.org/> . @import-relative \"src/test/resources/subdir/sibling.rls\" .";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		List<PositiveLiteral> expected = Arrays.asList(fact4, fact5);
		List<Fact> result = knowledgeBase.getFacts();
		assertEquals(expected, result);
	}

	@Test
	public void parse_relativeImportStatement_relativeParentImport_succeeds() throws ParsingException {
		String input = "@base <http://example.org/> . @import-relative \"src/test/resources/subdir/parent.rls\" .";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		List<PositiveLiteral> expected = Arrays.asList(fact1, fact2);
		List<Fact> result = knowledgeBase.getFacts();
		assertEquals(expected, result);
	}

	@Test
	public void parse_import_renamesNamedNulls() throws ParsingException {
		String input = "p(_:blank) . @import \"src/test/resources/blank.rls\" .";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		List<Fact> facts = knowledgeBase.getFacts();
		assertEquals(2, facts.size());
		Fact fact1 = facts.get(0);
		Fact fact2 = facts.get(1);

		assertNotEquals(fact1, fact2);
		assertArgumentIsNamedNull(fact1, 1);
		assertArgumentIsNamedNull(fact2, 1);
	}

	@Test
	public void parse_reusedNamedNulls_identical() throws ParsingException {
		String input = "p(_:blank) . q(_:blank) . p(_:other) .";

		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		List<Fact> facts = knowledgeBase.getFacts();
		assertEquals(3, facts.size());
		Fact fact1 = facts.get(0);
		Fact fact2 = facts.get(1);
		Fact fact3 = facts.get(2);

		assertEquals(fact1.getArguments().get(0), fact2.getArguments().get(0));
		assertNotEquals(fact1.getArguments().get(0), fact3.getArguments().get(0));
		assertArgumentIsNamedNull(fact1, 1);
		assertArgumentIsNamedNull(fact2, 1);
		assertArgumentIsNamedNull(fact3, 1);
	}

	@Test
	public void parseInto_duplicateImportStatements_succeeds() throws ParsingException {
		String input = "@import \"src/test/resources/facts.rls\" . ";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		RuleParser.parseInto(knowledgeBase, input);
	}

	@Test
	public void parseInto_duplicateRelativeImportStatements_succeeds() throws ParsingException {
		String input = "@import \"src/test/resources/facts.rls\" . @import-relative \"src/test/resources/facts.rls\" .";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		RuleParser.parseInto(knowledgeBase, input);
	}

	@Test
	public void parseInto_relativeImportRedeclaringBase_succeeds() throws ParsingException {
		String input = "@base <http://example.com/> . @import-relative \"src/test/resources/base.rls\" .";
		KnowledgeBase knowledgeBase = RuleParser.parse(input);
		List<PositiveLiteral> expected = Arrays.asList(fact1, fact3);
		List<Fact> result = knowledgeBase.getFacts();
		assertEquals(expected, result);
	}
}
