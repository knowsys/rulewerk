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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.commands.CommandExecutionException;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.parser.ParsingException;

public class ShellTest {

	private final String prompt = "myPrompt";

	@Test
	public void processReadLine_blank() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" ");
		assertEquals("", processedReadLine);
	}

	@Test
	public void processReadLine_startsWithAt() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" @ ");
		assertEquals("@ .", processedReadLine);
	}

	@Test
	public void processReadLine_endsWithStop() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" . ");
		assertEquals("@.", processedReadLine);
	}

	@Test
	public void processReadLine_startsWithAtEndsWithStop() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" @. ");
		assertEquals("@.", processedReadLine);
	}

	@Test
	public void processReadLine_doesNotStartWithAt_DoesNotEndWithStop() {
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));
		final String processedReadLine = shell.processReadLine(" .@ ");
		assertEquals("@.@ .", processedReadLine);
	}

	@Test
	public void readCommand_blank() throws ParsingException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final Interpreter interpreterMock = Mockito.mock(Interpreter.class);
		final Shell shell = new Shell(interpreterMock);

		Mockito.when(lineReaderMock.readLine(this.prompt)).thenReturn(" ");

		final Command command = shell.readCommand(lineReaderMock, this.prompt);
		assertNull(command);

		Mockito.verify(interpreterMock, Mockito.never()).parseCommand(Mockito.anyString());
		// TODO test exceptions have not been thrown
	}

	@Test
	public void readCommand_unknown() throws ParsingException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final StringWriter stringWriter = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(stringWriter);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		Mockito.when(lineReaderMock.readLine(this.prompt)).thenReturn("unknown");

		final Command command = shell.readCommand(lineReaderMock, this.prompt);

		Mockito.verify(interpreterSpy).parseCommand("@unknown .");
		assertEquals("unknown", command.getName());
		assertTrue(command.getArguments().isEmpty());

		// TODO test Parsing exception has not been thrown
	}

	@Test
	public void readCommand_parsingException() throws ParsingException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final StringWriter stringWriter = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(stringWriter);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		Mockito.when(lineReaderMock.readLine(this.prompt)).thenReturn("@");

		final Command command = shell.readCommand(lineReaderMock, this.prompt);

		Mockito.verify(interpreterSpy).parseCommand("@ .");
		assertNull(command);

		// TODO test Parsing exception has been thrown
		assertTrue(stringWriter.toString().startsWith("Error: failed to parse command"));
	}

	@Test
	public void readCommand_exit() throws CommandExecutionException, ParsingException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final StringWriter stringWriter = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(stringWriter);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		Mockito.when(lineReaderMock.readLine(this.prompt)).thenReturn("exit");

		final Command command = shell.readCommand(lineReaderMock, this.prompt);
		ShellTestUtils.testIsExitCommand(command);
		Mockito.verify(interpreterSpy).parseCommand("@exit .");

		// TODO test Parsing exception has not been thrown
	}

	@Test
	public void readCommand_interruptRequest_CTRLC_emptyPartialLine() throws ParsingException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);
		final Interpreter interpreterMock = Mockito.mock(Interpreter.class);
		final Shell shell = new Shell(interpreterMock);

		Mockito.doThrow(new UserInterruptException("")).when(lineReaderMock).readLine(this.prompt);

		final Command command = shell.readCommand(lineReaderMock, this.prompt);
		ShellTestUtils.testIsExitCommand(command);

		Mockito.verify(interpreterMock, Mockito.never()).parseCommand(Mockito.anyString());
	}

	@Test
	public void readCommand_interruptRequest_CTRLC_nonEmptyPartialLine() throws ParsingException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);
		final Interpreter interpreterMock = Mockito.mock(Interpreter.class);
		final Shell shell = new Shell(interpreterMock);

		Mockito.doThrow(new UserInterruptException(" ")).when(lineReaderMock).readLine(this.prompt);

		final Command command = shell.readCommand(lineReaderMock, this.prompt);
		assertNull(command);

		Mockito.verify(interpreterMock, Mockito.never()).parseCommand(Mockito.anyString());
	}

	@Test
	public void readCommand_interruptRequest_CTRLD_emptyPartialLine() throws ParsingException {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);
		final Interpreter interpreterMock = Mockito.mock(Interpreter.class);
		final Shell shell = new Shell(interpreterMock);

		Mockito.doThrow(EndOfFileException.class).when(lineReaderMock).readLine(this.prompt);

		final Command command = shell.readCommand(lineReaderMock, this.prompt);
		ShellTestUtils.testIsExitCommand(command);

		Mockito.verify(interpreterMock, Mockito.never()).parseCommand(Mockito.anyString());
	}

}
