package org.semanticweb.rulewerk.client.shell;

import java.io.PrintWriter;
import java.io.Writer;

import org.jline.terminal.Terminal;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;
import org.semanticweb.rulewerk.parser.ParserConfiguration;

public final class ShellTestUtils {

	private ShellTestUtils() {
	}

	public static Interpreter getMockInterpreter(final Writer writer) {
		final Terminal terminalMock = Mockito.mock(Terminal.class);
		final TerminalStyledPrinter terminalStyledPrinter = new TerminalStyledPrinter(terminalMock);
		final PrintWriter printWriter = new PrintWriter(writer);
		Mockito.when(terminalMock.writer()).thenReturn(printWriter);

		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
		return new Interpreter(Interpreter.EMPTY_KNOWLEDGE_BASE_PROVIDER, (knowledgeBase) -> {
			final Reasoner reasoner = Mockito.mock(Reasoner.class);
			Mockito.when(reasoner.getKnowledgeBase()).thenReturn(knowledgeBase);
			return reasoner;
		}, terminalStyledPrinter, parserConfiguration);
	}

}
