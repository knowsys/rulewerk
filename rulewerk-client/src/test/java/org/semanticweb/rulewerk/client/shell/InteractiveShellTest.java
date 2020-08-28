package org.semanticweb.rulewerk.client.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;

import org.jline.terminal.Terminal;
import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;

public class InteractiveShellTest {

	@Test
	public void initializeInterpreter() {
		final Terminal terminal = Mockito.mock(Terminal.class);
		final PrintWriter writer = Mockito.mock(PrintWriter.class);
		Mockito.when(terminal.writer()).thenReturn(writer);

		final InteractiveShell interactiveShell = new InteractiveShell();
		final Interpreter interpreter = interactiveShell.initializeInterpreter(terminal);

		assertTrue(interpreter.getParserConfiguration() instanceof DefaultParserConfiguration);
		assertTrue(interpreter.getKnowledgeBase().getStatements().isEmpty());
		assertEquals(writer, interpreter.getWriter());
	}


}
