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

import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.TermType;

public class HelpCommandInterpreter implements CommandInterpreter {

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		if (command.getArguments().size() == 0) {
			int maxLength = 0;
			for (String commandName : interpreter.commandInterpreters.keySet()) {
				maxLength = (commandName.length() > maxLength) ? commandName.length() : maxLength;
			}
			final int padLength = maxLength + 1;

			interpreter.printSection("Available commands:\n");
			interpreter.commandInterpreters.forEach((commandName, commandForName) -> {
				interpreter.printCode(" @" + String.format("%1$-" + padLength + "s", commandName));
				interpreter.printNormal(": " + commandForName.getSynopsis() + "\n");
			});
			interpreter.printNormal("\nFor more information on any command, use ");
			interpreter.printCode("@" + command.getName() + " [command name].\n");
		} else if (command.getArguments().size() == 1 && command.getArguments().get(0).fromTerm().isPresent()
				&& command.getArguments().get(0).fromTerm().get().getType() == TermType.ABSTRACT_CONSTANT) {
			String helpCommand = command.getArguments().get(0).fromTerm().get().getName();
			if (interpreter.commandInterpreters.containsKey(helpCommand)) {
				interpreter.printCode("@" + helpCommand);
				interpreter.printNormal(": " + interpreter.commandInterpreters.get(helpCommand).getSynopsis() + "\n");
				interpreter.commandInterpreters.get(helpCommand).printHelp(helpCommand, interpreter);
			} else {
				interpreter.printNormal("Command '" + helpCommand + "' not known.\n");
			}
		} else {
			printHelp(command.getName(), interpreter);
		}
	}

	@Override
	public void printHelp(String commandName, Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " [command name] .\n" //
				+ "\t command name: command to get detailed help for\n");
	}

	@Override
	public String getSynopsis() {
		return "print help on available commands";
	}

}
