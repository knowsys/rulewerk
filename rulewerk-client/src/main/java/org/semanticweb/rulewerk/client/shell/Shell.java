package org.semanticweb.rulewerk.client.shell;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;

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

import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter;
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter.ExitCommandName;
import org.semanticweb.rulewerk.commands.CommandExecutionException;
import org.semanticweb.rulewerk.commands.CommandInterpreter;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.parser.ParsingException;

public class Shell {

	private final Interpreter interpreter;

	boolean running;

	public Shell(final Interpreter interpreter) {
		this.interpreter = interpreter;

		this.registerExitCommand();
	}

	private void registerExitCommand() {
		final CommandInterpreter exitCommandInterpreter = new ExitCommandInterpreter(this);
		for (final ExitCommandName exitCommandName : ExitCommandName.values()) {
			this.interpreter.registerCommandInterpreter(exitCommandName.toString(), exitCommandInterpreter);
		}
	}

	public void run(final LineReader lineReader, final AttributedString prompt) {
		this.printWelcome();

		this.running = true;
		while (this.running) {
			final Command command;
			try {
				command = this.readCommand(lineReader, prompt);
			} catch (final Exception e) {
				this.interpreter.printNormal("Unexpected error: " + e.getMessage() + "\n");
				e.printStackTrace();
				continue;
			}

			if (command != null) {
				try {
					this.interpreter.runCommand(command);
				} catch (final CommandExecutionException e) {
					this.interpreter.printNormal("Error: " + e.getMessage() + "\n");
				}
			}
		}
		this.interpreter.printSection("Exiting Rulewerk shell ... bye.\n\n");
	}

	/**
	 * Reads a command from the prompt and returns a corresponding {@link Command}
	 * object. If no command should be executed, null is returned. Some effort is
	 * made to interpret mistyped commands by adding @ and . before and after the
	 * input, if forgotten.
	 * 
	 * @return command or null
	 */
	public Command readCommand(final LineReader lineReader, final AttributedString prompt) {
		String readLine;
		try {
			readLine = this.readLine(lineReader, prompt);
		} catch (final UserInterruptException e) {
			if (e.getPartialLine().isEmpty()) {
				// Exit request from user CTRL+C
				return ExitCommandInterpreter.EXIT_COMMAND;
			} else {
				return null; // used as empty command
			}
		} catch (final EndOfFileException e) {
			// Exit request from user CTRL+D
			return ExitCommandInterpreter.EXIT_COMMAND;

		}

		readLine = this.processReadLine(readLine);
		if (readLine.isEmpty()) {
			return null;
		}

		try {
			return this.interpreter.parseCommand(readLine);
		} catch (final ParsingException e) {
			this.interpreter.printNormal("Error: " + e.getMessage() + "\n" + e.getCause().getMessage() + "\n");
			return null;
		}
	}

	private String readLine(final LineReader lineReader, final AttributedString prompt) {
		final Terminal terminal = lineReader.getTerminal();
		return lineReader.readLine(prompt.toAnsi(terminal));
	}

	String processReadLine(final String readLine) {
		String result = readLine.trim();
		if (!result.isEmpty()) {
			if (readLine.charAt(0) != '@') {
				result = "@" + readLine;
			}
			if (readLine.charAt(readLine.length() - 1) != '.') {
				result = readLine + " .";
			}
		}
		return result;
	}

	public void exitShell() {
		this.running = false;
	}

	private void printWelcome() {
		this.interpreter.printNormal("\n");
		this.interpreter.printSection("Welcome to the Rulewerk interactive shell.\n");
		this.interpreter.printNormal("For further information, type ");
		this.interpreter.printCode("@help.");
		this.interpreter.printNormal(" To quit, type ");
		this.interpreter.printCode("@exit.\n");
		this.interpreter.printNormal("\n");
	}

}
