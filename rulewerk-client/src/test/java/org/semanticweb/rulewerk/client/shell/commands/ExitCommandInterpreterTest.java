package org.semanticweb.rulewerk.client.shell.commands;

import static org.junit.Assert.assertEquals;

/*-
 * #%L
 * Rulewerk Client
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

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.client.shell.Shell;
import org.semanticweb.rulewerk.client.shell.ShellTestUtils;
import org.semanticweb.rulewerk.commands.CommandExecutionException;
import org.semanticweb.rulewerk.commands.CommandInterpreter;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.parser.ParsingException;

public class ExitCommandInterpreterTest {

	@Test
	public void exitShell_succeeds() throws CommandExecutionException {
		final Interpreter interpreterMock = Mockito.mock(Interpreter.class);
		final Shell shell = new Shell(interpreterMock);
		final Shell shellSpy = Mockito.spy(shell);
		final ExitCommandInterpreter commandInterpreter = new ExitCommandInterpreter(shellSpy);

		commandInterpreter.run(Mockito.mock(Command.class), interpreterMock);

		Mockito.verify(shellSpy).exitShell();
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		final Shell shellMock = Mockito.mock(Shell.class);
		final ExitCommandInterpreter commandInterpreter = new ExitCommandInterpreter(shellMock);

		final StringWriter writer = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(writer);

		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		commandInterpreter.printHelp("commandname", interpreterSpy);

		Mockito.verify(interpreterSpy).printNormal("Usage: @commandname .\n");

		final String result = writer.toString();
		assertEquals("Usage: @commandname .\n", result);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		final Shell shellMock = Mockito.mock(Shell.class);
		final CommandInterpreter commandInterpreter = new ExitCommandInterpreter(shellMock);
		final String synopsis = commandInterpreter.getSynopsis();
		assertTrue(synopsis.length() < 70);
	}

}
