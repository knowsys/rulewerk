package org.semanticweb.rulewerk.client.shell;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.jline.reader.LineReader;
import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter;
import org.semanticweb.rulewerk.commands.CommandExecutionException;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.parser.ParsingException;

public class ShellTest {

	@Test
	public void processReadLine_Blank() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" ");
		assertEquals("", processedReadLine);
	}

	@Test
	public void processReadLine_StartsWithAt() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" @ ");
		assertEquals("@ .", processedReadLine);
	}

	@Test
	public void processReadLine_EndsWithStop() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" . ");
		assertEquals("@.", processedReadLine);
	}

	@Test
	public void processReadLine_StartsWithAtEndsWithStop() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" @. ");
		assertEquals("@.", processedReadLine);
	}

	@Test
	public void processReadLine_DoesNotStartWithAt_DoesNotEndWithStop() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" .@ ");
		assertEquals("@.@ .", processedReadLine);
	}

	@Test
	public void readCommand_Blank() {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final String prompt = "myPrompt";
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));

		Mockito.when(lineReaderMock.readLine(prompt)).thenReturn(" ");

		final Command command = shell.readCommand(lineReaderMock, prompt);
		assertNull(command);

		// TODO test interpreter.parseCommand was not called
		// TODO test exceptions have not been thrown
	}

	@Test
	public void readCommand_Unknown() throws ParsingException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final String prompt = "myPrompt";

		final StringWriter stringWriter = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(stringWriter);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		Mockito.when(lineReaderMock.readLine(prompt)).thenReturn("unknown");

		final Command command = shell.readCommand(lineReaderMock, prompt);

		Mockito.verify(interpreterSpy).parseCommand("@unknown .");
		assertEquals("unknown", command.getName());
		assertTrue(command.getArguments().isEmpty());

		// TODO test Parsing exception has not been thrown
	}

	@Test
	public void readCommand_ParsingException() throws ParsingException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final String prompt = "myPrompt";

		final StringWriter stringWriter = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(stringWriter);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		Mockito.when(lineReaderMock.readLine(prompt)).thenReturn("@");

		final Command command = shell.readCommand(lineReaderMock, prompt);

		Mockito.verify(interpreterSpy).parseCommand("@ .");
		assertNull(command);
		
		// TODO test Parsing exception has been thrown
		assertTrue(stringWriter.toString().startsWith("Error: "));
	}

	@Test
	public void readCommand_Exit() throws CommandExecutionException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final String prompt = "myPrompt";

		final StringWriter stringWriter = new StringWriter();
		final Interpreter interpreterMock = ShellTestUtils.getMockInterpreter(stringWriter);
		final Shell shell = new Shell(interpreterMock);

		Mockito.when(lineReaderMock.readLine(prompt)).thenReturn("exit");

		final Command command = shell.readCommand(lineReaderMock, prompt);
		assertEquals(ExitCommandInterpreter.EXIT_COMMAND.getName(), command.getName());
		assertTrue(command.getArguments().isEmpty());

		// TODO test Parsing exception has not been thrown
		assertFalse(shell.running);
	}

}
