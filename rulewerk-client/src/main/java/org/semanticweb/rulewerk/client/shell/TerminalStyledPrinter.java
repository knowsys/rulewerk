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
import org.semanticweb.rulewerk.commands.StyledPrinter;

/**
 * StyledPrinter that uses the {@link PrintWriter} of a {@link Terminal} and has
 * various styling.
 * 
 * @author Irina Dragoste
 *
 */
public class TerminalStyledPrinter implements StyledPrinter {

	final Terminal terminal;

	/**
	 * Constructor providing a terminal for the StyledPrinter to write to.
	 * 
	 * @param terminal the terminal to write to
	 */
	public TerminalStyledPrinter(final Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public void printNormal(final String string) {
		this.printStyled(string, AttributedStyle.DEFAULT);
	}

	@Override
	public void printSection(final String string) {
		this.printStyled(string, AttributedStyle.DEFAULT.bold());
	}

	@Override
	public void printEmph(final String string) {
		this.printStyled(string, AttributedStyle.DEFAULT.bold());
	}

	@Override
	public void printCode(final String string) {
		this.printStyled(string, AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold());
	}

	@Override
	public void printImportant(final String string) {
		this.printStyled(string, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
	}

	@Override
	public PrintWriter getWriter() {
		return this.terminal.writer();
	}

	private void printStyled(final String string, final AttributedStyle attributedStyle) {
		final AttributedString attributedString = new AttributedString(string, attributedStyle);
		this.getWriter().print(attributedString.toAnsi(this.terminal));
		this.getWriter().flush();
	}
}
