package org.semanticweb.rulewerk.parser;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParser;

public class CommandParserTest {

	@Test
	public void parseCommand() throws ParsingException {
		String input = "@query p(?X, a):- q(?X) \"string\" abcd  p(a) <http://example.org>.";
		Command command = RuleParser.parseSyntaxFragment(input, JavaCCParser::command, "command", null);
		assertEquals("query", command.getName());
		assertEquals(5, command.getArguments().size());
		assertTrue(command.getArguments().get(0).fromRule().isPresent());
		assertTrue(command.getArguments().get(1).fromString().isPresent());
		assertTrue(command.getArguments().get(2).fromTerm().isPresent());
		assertTrue(command.getArguments().get(3).fromPositiveLiteral().isPresent());
		assertTrue(command.getArguments().get(4).fromIri().isPresent());
	}
}
