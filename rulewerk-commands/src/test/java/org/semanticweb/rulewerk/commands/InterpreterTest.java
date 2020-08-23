package org.semanticweb.rulewerk.commands;

/*-
 * #%L
 * Rulewerk command execution support
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

import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.parser.ParsingException;

public class InterpreterTest {

	static public Interpreter getMockInterpreter(Writer writer) {
		SimpleStyledPrinter printer = new SimpleStyledPrinter(writer);
		ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		Reasoner reasoner = Mockito.mock(Reasoner.class);
		Mockito.when(reasoner.getKnowledgeBase()).thenReturn(knowledgeBase);
		return new Interpreter(reasoner, printer, parserConfiguration);
	}

	/**
	 * Checks the basic format of command usage instructions and verifies that the
	 * given command name is used (not a fixed one).
	 * 
	 * @param commandInterpreter
	 * @param interpreter
	 * @param writer
	 */
	static public void checkHelpFormat(CommandInterpreter commandInterpreter, Interpreter interpreter,
			StringWriter writer) {
		commandInterpreter.printHelp("commandname", interpreter);
		String result = writer.toString();

		assertTrue(result.startsWith("Usage: @commandname "));
		assertTrue(result.endsWith("\n"));
	}

	static public void checkSynopsisFormat(CommandInterpreter commandInterpreter) {
		String synopsis = commandInterpreter.getSynopsis();
		assertTrue(synopsis.length() < 70);
	}

	@Test
	public void getters_succeed() {
		StringWriter writer = new StringWriter();
		SimpleStyledPrinter printer = new SimpleStyledPrinter(writer);
		ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		Reasoner reasoner = Mockito.mock(Reasoner.class);
		Mockito.when(reasoner.getKnowledgeBase()).thenReturn(knowledgeBase);
		Interpreter interpreter = new Interpreter(reasoner, printer, parserConfiguration);

		assertEquals(knowledgeBase, interpreter.getKnowledgeBase());
		assertEquals(reasoner, interpreter.getReasoner());
		assertEquals(writer, interpreter.getWriter());
		assertEquals(parserConfiguration, interpreter.getParserConfiguration());
	}

	@Test(expected = CommandExecutionException.class)
	public void unknownCommand_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@unknown .");
		interpreter.runCommand(command);
	}

	@Test(expected = ParsingException.class)
	public void malformedCommand_fails() throws ParsingException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = getMockInterpreter(writer);

		interpreter.parseCommand("malformed .");
	}

}
