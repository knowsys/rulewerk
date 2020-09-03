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

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.parser.ParsingException;

public class HelpCommandInterpreterTest {

	@Test
	public void correctUse_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@help .");
		interpreter.runCommand(command);

		String output = writer.toString();
		for (String commandName : interpreter.getRegisteredCommands()) {
			assertTrue(output.contains("@" + commandName));
		}
	}

	@Test
	public void correctUseWithCommand_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@help query.");
		interpreter.runCommand(command);
		// Nothing much to test here.
		assertTrue(writer.toString().length() > 0);
	}

	@Test
	public void wrongArgumentCount_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@help query showkb .");
		interpreter.runCommand(command);
		// Nothing much to test here.
		assertTrue(writer.toString().length() > 0);
	}
	
	@Test
	public void unknownCommandHelp_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@help unknowncommand .");
		interpreter.runCommand(command);
		// Nothing much to test here.
		assertTrue(writer.toString().length() > 0);
	}
	
	@Test
	public void wrongArgumentTypeTerm_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@help 123 .");
		interpreter.runCommand(command);
		// Nothing much to test here.
		assertTrue(writer.toString().length() > 0);
	}
	
	@Test
	public void wrongArgumentTypeFact_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@help p(a) .");
		interpreter.runCommand(command);
		// Nothing much to test here.
		assertTrue(writer.toString().length() > 0);
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		CommandInterpreter commandInterpreter = new HelpCommandInterpreter();
		InterpreterTest.checkHelpFormat(commandInterpreter, interpreter, writer);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		CommandInterpreter commandInterpreter = new HelpCommandInterpreter();
		InterpreterTest.checkSynopsisFormat(commandInterpreter);
	}

}
