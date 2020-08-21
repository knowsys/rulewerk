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
			interpreter.getOut().println("Available commands:");
			for (String commandName : interpreter.commandInterpreters.keySet()) {
				interpreter.getOut().println(
						" @" + commandName + ": " + interpreter.commandInterpreters.get(commandName).getSynopsis());
			}
			interpreter.getOut().println();
			interpreter.getOut()
					.println("For more information on any command, use @" + command.getName() + " [command name].");
		} else if (command.getArguments().size() == 1 && command.getArguments().get(0).fromTerm().isPresent()
				&& command.getArguments().get(0).fromTerm().get().getType() == TermType.ABSTRACT_CONSTANT) {
			String helpCommand = command.getArguments().get(0).fromTerm().get().getName();
			if (interpreter.commandInterpreters.containsKey(helpCommand)) {
				interpreter.getOut().println(
						"@" + helpCommand + ": " + interpreter.commandInterpreters.get(helpCommand).getSynopsis());
				interpreter.getOut().println(interpreter.commandInterpreters.get(helpCommand).getHelp(helpCommand));
			} else {
				interpreter.getOut().println("Command '" + helpCommand + "' not known.");
			}
		} else {
			interpreter.getOut().println(getHelp(command.getName()));
		}
	}

	@Override
	public String getHelp(String commandName) {
		return "Usage: @" + commandName + " [command name] .\n" + "\t command name: command to get detailed help for";
	}

	@Override
	public String getSynopsis() {
		return "print help on available commands";
	}

}
