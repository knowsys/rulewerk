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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.io.Writer;

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

	@Test
	public void run_exit() throws CommandExecutionException {
		final Writer writer = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(writer);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		final LineReader lineReader = Mockito.mock(LineReader.class);
		Mockito.when(lineReader.readLine(this.prompt)).thenReturn("exit");

		shell.run(lineReader, this.prompt);

		assertFalse(shell.isRunning());

		this.testPrintWelcome(interpreterSpy);

		Mockito.verify(interpreterSpy).runCommand(Mockito.any(Command.class));

		this.testPrintExit(interpreterSpy);

		final String[] lines = writer.toString().split("\r\n|\r|\n");
		assertEquals(7, lines.length);
	}

	@Test
	public void run_empty_exit() throws CommandExecutionException {
		final Writer writer = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(writer);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		final LineReader lineReader = Mockito.mock(LineReader.class);
		Mockito.when(lineReader.readLine(this.prompt)).thenReturn("", "exit");

		shell.run(lineReader, this.prompt);

		assertFalse(shell.isRunning());

		this.testPrintWelcome(interpreterSpy);

		Mockito.verify(interpreterSpy).runCommand(Mockito.any(Command.class));

		this.testPrintExit(interpreterSpy);

		final String[] lines = writer.toString().split("\r\n|\r|\n");
		assertEquals(7, lines.length);
	}

	@Test
	public void run_help_exit() throws CommandExecutionException {
		final Writer writer = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(writer);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		final LineReader lineReader = Mockito.mock(LineReader.class);
		Mockito.when(lineReader.readLine(this.prompt)).thenReturn("help", "exit");

		shell.run(lineReader, this.prompt);

		assertFalse(shell.isRunning());

		this.testPrintWelcome(interpreterSpy);

		Mockito.verify(interpreterSpy, Mockito.times(2)).runCommand(Mockito.any(Command.class));

		this.testPrintExit(interpreterSpy);

		final String[] lines = writer.toString().split("\r\n|\r|\n");
		assertTrue(lines.length > 7);
	}

	@Test
	public void runCommand_unknown() throws CommandExecutionException {
		final Writer writer = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(writer);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		final LineReader lineReader = Mockito.mock(LineReader.class);
		Mockito.when(lineReader.readLine(this.prompt)).thenReturn("unknown", "exit");

		final Command command = shell.runCommand(lineReader, this.prompt);
		assertNotNull(command);
		assertEquals("unknown", command.getName());

		Mockito.verify(interpreterSpy).runCommand(Mockito.any(Command.class));

		final String printedResult = writer.toString();
		assertTrue(printedResult.startsWith("Error: "));
	}

	@Test
	public void runCommand_exceptionDuringReading() throws CommandExecutionException {
		final Writer writer = new StringWriter();
		final Interpreter interpreter = ShellTestUtils.getMockInterpreter(writer);
		final Interpreter interpreterSpy = Mockito.spy(interpreter);
		final Shell shell = new Shell(interpreterSpy);

		final LineReader lineReader = Mockito.mock(LineReader.class);
		final RuntimeException exception = Mockito.mock(RuntimeException.class);
		Mockito.when(exception.getMessage())
				.thenReturn("This exception is thrown intentionally as part of a unit test");

		Mockito.when(lineReader.readLine(this.prompt)).thenThrow(exception);

		final Command command = shell.runCommand(lineReader, this.prompt);
		assertNull(command);

		Mockito.verify(interpreterSpy, Mockito.never()).runCommand(Mockito.any(Command.class));

		final String printedResult = writer.toString();
		assertTrue(printedResult.startsWith("Unexpected error: " + exception.getMessage()));

		Mockito.verify(exception).printStackTrace();
	}

	public void testPrintWelcome(final Interpreter interpreterSpy) {
		Mockito.verify(interpreterSpy, Mockito.times(2)).printNormal("\n");
		Mockito.verify(interpreterSpy).printSection("Welcome to the Rulewerk interactive shell.\n");
		Mockito.verify(interpreterSpy).printNormal("For further information, type ");
		Mockito.verify(interpreterSpy).printCode("@help.");
		Mockito.verify(interpreterSpy).printNormal(" To quit, type ");
		Mockito.verify(interpreterSpy).printCode("@exit.\n");
	}

	public void testPrintExit(final Interpreter interpreterSpy) {
		Mockito.verify(interpreterSpy).printSection("Exiting Rulewerk shell ... bye.\n\n");
	}

}
