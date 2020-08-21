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
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter.ExitCommandName;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

public class CommandReader {

	public CommandReader(final LineReader lineReader, final PromptProvider promptProvider) {
		super();
		this.lineReader = lineReader;
		this.promptProvider = promptProvider;
	}

	private final LineReader lineReader;

	private final PromptProvider promptProvider;

	public Command readCommand() {
		final String readLine;
		try {
			final AttributedString prompt = this.promptProvider.getPrompt();
			readLine = this.lineReader.readLine(prompt.toAnsi(this.lineReader.getTerminal()));

		} catch (final UserInterruptException e) {
			if (e.getPartialLine().isEmpty()) {
				// Exit request from user CTRL+C
				return ExitCommandInterpreter.EXIT_COMMAND;
			} else {
				// TODO maybe create empty command
				return null;
			}
		}
		// TODO can readLIne be null?

		// TODO does it trim trailing spaces?
		if (ExitCommandName.isExitCommand(readLine)) {
			return ExitCommandInterpreter.EXIT_COMMAND;
		}

		try {
			return RuleParser.parseCommand(readLine);
		} catch (final ParsingException e) {
			// FIXME do I need to flush terminal?
			// TODO improve error message
			this.lineReader.getTerminal().writer().println("Command cannot be parsed: " + e.getMessage());
			// return Input.EMPTY;
			// TODO maybe create empty command
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
