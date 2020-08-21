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

import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter;
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter.ExitCommandName;
import org.semanticweb.rulewerk.commands.CommandExecutionException;
import org.semanticweb.rulewerk.commands.CommandInterpreter;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;

public class Shell {

	private final Interpreter interpreter;

	boolean running;

	public Shell(final Interpreter interpreter) {
		this.interpreter = interpreter;

		CommandInterpreter exitCommandInterpreter = new ExitCommandInterpreter(this);
		for (final ExitCommandName exitCommandName : ExitCommandName.values()) {
			interpreter.registerCommandInterpreter(exitCommandName.toString(), exitCommandInterpreter);
		}
	}

	public void run(final CommandReader commandReader) {
		printWelcome();

		running = true;
		while (running) {
			final Command command;
			try {
				command = commandReader.readCommand();
			} catch (final Exception e) {
				interpreter.getWriter().println("Unexpected error: " + e.getMessage());
				e.printStackTrace();
				continue;
			}

			if (command != null) {
				try {
					this.interpreter.runCommand(command);
				} catch (final CommandExecutionException e) {
					interpreter.getWriter().println("Error: " + e.getMessage());
				}
			}
		}
		interpreter.printSection("Existing Rulewerk shell ... bye.\n\n");
		interpreter.getWriter().flush();
	}

	public void exitShell() {
		this.running = false;
	}

	private void printWelcome() {
		interpreter.printNormal("\n");
		interpreter.printSection("Welcome to the Rulewerk interactive shell.\n");
		interpreter.printNormal("For further information, type ");
		interpreter.printCode("@help.");
		interpreter.printNormal(" To quit, type ");
		interpreter.printCode("@exit.\n");
		interpreter.printNormal("\n");
	}

//	@Override
//	public void handleResult(final Object result) {
//		this.terminal.writer().println(result);
//		this.terminal.writer().flush();
//	}

//	@Override
//	public void handleResult(final AttributedCharSequence result) {
//		this.terminal.writer().println(result.toAnsi(this.terminal));
//		this.terminal.writer().flush();
//	}
}
