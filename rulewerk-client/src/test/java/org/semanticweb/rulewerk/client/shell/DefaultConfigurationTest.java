package org.semanticweb.rulewerk.client.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.junit.Test;
import org.mockito.Mockito;

public class DefaultConfigurationTest {

	@Test
	public void buildPromptProvider() {
		final AttributedString promptProvider = DefaultConfiguration.getDefaultPromptStyle();
		assertEquals("rulewerk> ", promptProvider.toString());
	}

	@Test
	public void buildPrompt() {
		final Terminal terminal = Mockito.mock(Terminal.class);
		Mockito.when(terminal.getType()).thenReturn(Terminal.TYPE_DUMB);
		final String string = DefaultConfiguration.buildPrompt(terminal);
		assertTrue(string.length() >= 10);
	}

	public void buildTerminal() throws IOException {
		final TerminalBuilder terminalBuilderMock = Mockito.mock(TerminalBuilder.class);
		Mockito.when(TerminalBuilder.builder()).thenReturn(terminalBuilderMock);

		Mockito.verify(terminalBuilderMock.dumb(true));
		Mockito.verify(terminalBuilderMock.jansi(true));
		Mockito.verify(terminalBuilderMock.jna(false));
		Mockito.verify(terminalBuilderMock.system(true));
		Mockito.verify(terminalBuilderMock.build());

	}

}
