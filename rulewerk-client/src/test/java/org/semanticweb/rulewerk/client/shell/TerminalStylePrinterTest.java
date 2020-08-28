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
