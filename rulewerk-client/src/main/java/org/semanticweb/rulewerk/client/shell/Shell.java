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

import java.io.PrintStream;

import org.jline.terminal.Terminal;
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter;
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter.ExitCommandName;
import org.semanticweb.rulewerk.commands.CommandExecutionException;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.slf4j.Logger;

public class Shell {

	private final Terminal terminal;

	private final Interpreter interpreter;

	public Shell(final Terminal terminal) {
		this.terminal = terminal;
		this.interpreter = this.initializeInterpreter();
	}

	private Interpreter initializeInterpreter() {
		// FIXME connect terminal writer
//		final PrintStream out = this.terminal.writer().;
		final PrintStream out = System.out;

		// FIXME connect logger;
		final Logger logger = null;
		// TODO reasoner initial KB from args
		final KnowledgeBase knowledgeBase = new KnowledgeBase();
		final Reasoner reasoner = new VLogReasoner(knowledgeBase);
		final Interpreter interpreter = new Interpreter(reasoner, out, logger);

		for (final ExitCommandName exitCommandName : ExitCommandName.values()) {
			interpreter.registerCommandInterpreter(exitCommandName.toString(), new ExitCommandInterpreter());
		}

		return interpreter;
	}

	public void run(final CommandReader commandReader) {
		while (true) {
			final Command command;
			try {
				command = commandReader.readCommand();
			} catch (final Exception e) {
				// TODO: handle exception
				continue;
			}

			if (command != null) {
				try {
					this.interpreter.runCommand(command);
				} catch (final CommandExecutionException e) {
					// TODO: handle exception
					continue;
				}

				if (ExitCommandName.isExitCommand(command.getName())) {
					break;
				}
			}
		}
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
