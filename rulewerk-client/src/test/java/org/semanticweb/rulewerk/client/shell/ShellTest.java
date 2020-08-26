package org.semanticweb.rulewerk.client.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jline.reader.LineReader;
import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;


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

//
//	static public Interpreter getMockInterpreter(final Terminal terminal) {
//		final Reasoner reasonerMock = Mockito.mock(Reasoner.class);
//		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
//
//		final Interpreter interpreter = new Interpreter(reasonerMock, new TerminalStyledPrinter(terminal),
//				parserConfiguration);
//
//		final PrintWriter printWriter = Mockito.mock(PrintWriter.class);
//		Mockito.when(terminal.writer()).thenReturn(printWriter);
////
////		// final TerminalStyledPrinter printer = new TerminalStyledPrinter(writer);
////		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
////		final KnowledgeBase knowledgeBase = new KnowledgeBase();
//
////		Mockito.when(reasoner.getKnowledgeBase()).thenReturn(knowledgeBase);
////		return new Interpreter(reasoner, printer, parserConfiguration);
//		return interpreter;
//	}

}
