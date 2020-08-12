package org.semanticweb.rulewerk.commands;

/*-
 * #%L
 * Rulewerk Core Components
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
import java.util.HashMap;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.slf4j.Logger;

public class Interpreter {

	final Reasoner reasoner;
	final PrintStream out;
	final Logger logger;

	final HashMap<String, CommandInterpreter> commandInterpreters = new HashMap<>();

	public Interpreter(Reasoner reasoner, PrintStream out, Logger logger) {
		this.reasoner = reasoner;
		this.out = out;
		this.logger = logger;
		registerDefaultCommandInterpreters();
	}

	public void registerCommandInterpreter(String command, CommandInterpreter commandInterpreter) {
		commandInterpreters.put(command, commandInterpreter);
	}

	public void runCommands(List<Command> commands) throws CommandExecutionException {
		for (Command command : commands) {
			runCommand(command);
		}
	}

	public void runCommand(Command command) throws CommandExecutionException {
		if (commandInterpreters.containsKey(command.getName())) {
			try {
				commandInterpreters.get(command.getName()).run(command, this);
			} catch (Exception e) {
				throw new CommandExecutionException(e.getMessage(), e);
			}
		} else {
			throw new CommandExecutionException("Unknown command '" + command.getName() + "'");
		}
	}

	public Reasoner getReasoner() {
		return reasoner;
	}

	public PrintStream getOut() {
		return out;
	}

	private void registerDefaultCommandInterpreters() {
		registerCommandInterpreter("help", new HelpCommandInterpreter());
		registerCommandInterpreter("assert", new AssertCommandInterpreter());
		registerCommandInterpreter("query", new QueryCommandInterpreter());
		registerCommandInterpreter("reason", new ReasonCommandInterpreter());
		registerCommandInterpreter("load", new LoadCommandInterpreter());
	}

}
