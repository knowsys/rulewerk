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

import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedString;
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.parser.ParsingException;

public class CommandReader {

	private final LineReader lineReader;
	private final PromptProvider promptProvider;
	private final Interpreter interpreter;

	public CommandReader(final LineReader lineReader, final PromptProvider promptProvider,
			final Interpreter interpreter) {
		this.lineReader = lineReader;
		this.promptProvider = promptProvider;
		this.interpreter = interpreter;
	}

	/**
	 * Reads a command from the prompt and returns a corresponding {@link Command}
	 * object. If no command should be executed, null is returned. Some effort is
	 * made to interpret mistyped commands by adding @ and . before and after the
	 * input, if forgotten.
	 * 
	 * @return command or null
	 */
	public Command readCommand() {
		String readLine;
		try {
			final AttributedString prompt = this.promptProvider.getPrompt();
			readLine = this.lineReader.readLine(prompt.toAnsi(this.lineReader.getTerminal()));
		} catch (final UserInterruptException e) {
			if (e.getPartialLine().isEmpty()) {
				// Exit request from user CTRL+C
				return ExitCommandInterpreter.EXIT_COMMAND;
			} else {
				return null; // used as empty command
			}
		}

		readLine = readLine.trim();
		if ("".equals(readLine)) {
			return null;
		}
		if (readLine.charAt(0) != '@') {
			readLine = "@" + readLine;
		}
		if (readLine.charAt(readLine.length() - 1) != '.') {
			readLine = readLine + " .";
		}

		try {
			return this.interpreter.parseCommand(readLine);
		} catch (final ParsingException e) {
			// FIXME do I need to flush terminal?
			this.lineReader.getTerminal().writer()
					.println("Error: " + e.getMessage() + "\n" + e.getCause().getMessage());
			return null;
		}
	}

//	/**
//	 * Sanitize the buffer input given the customizations applied to the JLine
//	 * parser (<em>e.g.</em> support for line continuations, <em>etc.</em>)
//	 */
//	static List<String> sanitizeInput(List<String> words) {
//		words = words.stream().map(s -> s.replaceAll("^\\n+|\\n+$", "")) // CR at beginning/end of line introduced by
//																			// backslash continuation
//				.map(s -> s.replaceAll("\\n+", " ")) // CR in middle of word introduced by return inside a quoted string
//				.collect(Collectors.toList());
//		return words;
//	}

}
