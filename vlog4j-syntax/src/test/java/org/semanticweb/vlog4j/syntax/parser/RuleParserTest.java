package org.semanticweb.vlog4j.syntax.parser;

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
	final Literal atom1 = Expressions.makePositiveLiteral("http://example.org/p", x, c);
	final Literal atom2 = Expressions.makePositiveLiteral("http://example.org/p", x, z);
	final PositiveLiteral atom3 = Expressions.makePositiveLiteral("http://example.org/q", x, y);
	final PositiveLiteral atom4 = Expressions.makePositiveLiteral("http://example.org/r", x, d);
	final PositiveLiteral atom5 = Expressions.makePositiveLiteral("http://example.org/s", c);
	final Conjunction<Literal> body = Expressions.makeConjunction(atom1, atom2);
	final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom3, atom4);
	final Rule rule = Expressions.makeRule(head, body);

	@Test
	public void testExplicitIri() throws ParsingException {
		String input = "<http://example.org/s>(<http://example.org/c>) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(atom5), ruleParser.getFacts());
	}
	
	@Test
	public void testPrefixResolution() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . ex:s(ex:c) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(atom5), ruleParser.getFacts());
	}

	@Test
	public void testBaseRelativeResolution() throws ParsingException {
		String input = "@base <http://example.org/> . <s>(<c>) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(atom5), ruleParser.getFacts());
	}
	
	@Test
	public void testBaseResolution() throws ParsingException {
		String input = "@base <http://example.org/> . s(c) .";
		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(input);
		assertEquals(Arrays.asList(atom5), ruleParser.getFacts());
	}

}
