package org.semanticweb.rulewerk.client.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.PrintWriter;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;
import org.semanticweb.rulewerk.parser.ParserConfiguration;

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
	public void readCommand_Invalid() {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final String prompt = "myPrompt";
		final Shell shell = new Shell(Mockito.mock(Interpreter.class));

		Mockito.when(lineReaderMock.readLine(prompt)).thenReturn("invalid");

		final Command command = shell.readCommand(lineReaderMock, prompt);
		assertNull(command);

		// TODO test interpreter.parseCommand was called
		// TODO test Parsing exception has been thrown
	}

	@Test
	public void readCommand_Exit() {
		final LineReader lineReaderMock = Mockito.mock(LineReader.class);

		final String prompt = "myPrompt";
		// TODO need real interpreter here
		final Shell shell = new Shell(getMockInterpreter());

		Mockito.when(lineReaderMock.readLine(prompt)).thenReturn("exit");

		final Command command = shell.readCommand(lineReaderMock, prompt);
		assertEquals(ExitCommandInterpreter.EXIT_COMMAND.getName(), command.getName());

		// TODO test Parsing exception has not been thrown
		// TODO test ExitCommandInterpreter.run() has been called

		assertFalse(shell.running);
	}

	static public Interpreter getMockInterpreter() {
		final Terminal terminal = Mockito.mock(Terminal.class);
		final Reasoner reasoner = Mockito.mock(Reasoner.class);
		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration();

		final Interpreter interpreter = new Interpreter(reasoner, new TerminalStyledPrinter(terminal),
				parserConfiguration);

		final PrintWriter printWriter = Mockito.mock(PrintWriter.class);
		Mockito.when(terminal.writer()).thenReturn(printWriter);

		final KnowledgeBase knowledgeBase = new KnowledgeBase();
		Mockito.when(reasoner.getKnowledgeBase()).thenReturn(knowledgeBase);

		return interpreter;
	}

}
