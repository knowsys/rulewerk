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

import java.io.IOException;
import java.util.Collection;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

/**
 * Interface for providing I/O resources for an interactive shell: terminal,
 * terminal prompt, and line reader
 * 
 * @author Irina Dragoste
 *
 */
public interface ShellConfiguration {

	/**
	 * Provides a line reader that reads user input from the given terminal. The
	 * line reader offers tab-completion for the given list of command names.
	 * 
	 * @param terminal terminal to read from.
	 * @param commands list of command names recognized by the interactive shell.
	 * @return a line reader for interacting with the shell terminal.
	 */
	LineReader buildLineReader(Terminal terminal, Collection<String> commands);

	/**
	 * Provides an I/O terminal for the interactive shell.
	 * 
	 * @return the interactive shell terminal.
	 * @throws IOException when the terminal cannot be built
	 */
	Terminal buildTerminal() throws IOException;

	/**
	 * Provides the prompt text (with colour and style) to be displayed on the given
	 * terminal.
	 * 
	 * @param terminal terminal for the prompt to be displayed on
	 * @return the prompt text with embedded style.
	 */
	String buildPrompt(Terminal terminal);

}
