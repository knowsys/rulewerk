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
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.rulewerk.parser.ParsingException;

public class AddSourceCommandInterpreterTest {

	@Test
	public void correctUse_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter
				.parseCommand("@addsource p[1] : sparql(<http://example.org>, \"?x\", \"?x <p> <o>\") .");
		interpreter.runCommand(command);
		List<Fact> facts = interpreter.getKnowledgeBase().getFacts();
		List<Rule> rules = interpreter.getKnowledgeBase().getRules();
		List<DataSourceDeclaration> dataSourceDeclarations = interpreter.getKnowledgeBase().getDataSourceDeclarations();

		assertEquals("addsource", command.getName());
		assertEquals(2, command.getArguments().size());
		assertTrue(command.getArguments().get(0).fromTerm().isPresent());
		assertTrue(command.getArguments().get(1).fromPositiveLiteral().isPresent());

		assertTrue(facts.isEmpty());
		assertTrue(rules.isEmpty());
		assertEquals(1, dataSourceDeclarations.size());
		assertTrue(dataSourceDeclarations.get(0).getDataSource() instanceof SparqlQueryResultDataSource);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongFirstArgumentType_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@addsource \"string\" p(a).");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongSecondArgumentType_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@addsource p[1]: \"string\" .");
		interpreter.runCommand(command);
	}
	
	@Test(expected = CommandExecutionException.class)
	public void wrongSecondArgumentUnknownSource_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@addsource p[1]: unknown(a) .");
		interpreter.runCommand(command);
	}
	
	@Test(expected = CommandExecutionException.class)
	public void wrongSecondArgumentWrongAritySource_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@addsource p[1]: load-rdf(\"file.nt\") .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCount_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@addsource p[2]: p(a) p(b) .");
		interpreter.runCommand(command);
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		CommandInterpreter commandInterpreter = new AddSourceCommandInterpreter();
		InterpreterTest.checkHelpFormat(commandInterpreter, interpreter, writer);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		CommandInterpreter commandInterpreter = new AddSourceCommandInterpreter();
		InterpreterTest.checkSynopsisFormat(commandInterpreter);
	}

}
