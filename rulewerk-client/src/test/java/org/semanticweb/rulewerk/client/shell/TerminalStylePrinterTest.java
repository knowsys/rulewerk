package org.semanticweb.rulewerk.client.shell;

import java.io.PrintWriter;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.junit.Test;
import org.mockito.Mockito;

public class TerminalStylePrinterTest {
	final Terminal terminal;
	final PrintWriter writer;
	final TerminalStyledPrinter terminalStyledPrinter;

	public static final String TEST_STRING = "test";

	public TerminalStylePrinterTest() {
		this.writer = Mockito.mock(PrintWriter.class);
		this.terminal = Mockito.mock(Terminal.class);
		Mockito.when(this.terminal.writer()).thenReturn(this.writer);

		this.terminalStyledPrinter = new TerminalStyledPrinter(this.terminal);

	}

	@Test
	public void testPrintNormal() {
		this.terminalStyledPrinter.printNormal(TEST_STRING);
		this.testPrintStyledExpected(AttributedStyle.DEFAULT);
	}

	@Test
	public void testPrintSection() {
		this.terminalStyledPrinter.printSection(TEST_STRING);
		this.testPrintStyledExpected(AttributedStyle.DEFAULT.bold());
	}

	@Test
	public void testPrintEmph() {
		this.terminalStyledPrinter.printEmph(TEST_STRING);
		this.testPrintStyledExpected(AttributedStyle.DEFAULT.bold());
	}

	@Test
	public void testPrintCode() {
		this.terminalStyledPrinter.printCode(TEST_STRING);
		this.testPrintStyledExpected(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold());
	}

	@Test
	public void testPrintImportant() {
		this.terminalStyledPrinter.printImportant(TEST_STRING);
		this.testPrintStyledExpected(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
	}

	private void testPrintStyledExpected(final AttributedStyle expectedStyle) {
		final AttributedString expectedAttributedString = new AttributedString(TEST_STRING, expectedStyle);
		Mockito.verify(this.writer).print(expectedAttributedString.toAnsi(this.terminal));
		Mockito.verify(this.writer).flush();
	}

}
