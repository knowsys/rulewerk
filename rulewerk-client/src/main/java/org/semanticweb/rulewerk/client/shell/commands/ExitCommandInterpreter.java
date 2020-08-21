package org.semanticweb.rulewerk.client.shell.commands;

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

import java.util.ArrayList;

import org.semanticweb.rulewerk.client.shell.Shell;
import org.semanticweb.rulewerk.commands.CommandExecutionException;
import org.semanticweb.rulewerk.commands.CommandInterpreter;
import org.semanticweb.rulewerk.core.model.api.Command;

public class ExitCommandInterpreter implements CommandInterpreter {

	public static final Command EXIT_COMMAND = new Command(ExitCommandName.exit.toString(), new ArrayList<>());

	public static enum ExitCommandName
	{
		exit, quit;

		public static boolean isExitCommand(final String commandName) {
			for(final ExitCommandName name: values()) {
				if (name.toString().equals(commandName)) {
					return true;
				}
			}
			return false;
		}
	}
	
	final Shell shell;
	
	public ExitCommandInterpreter(Shell shell) {
		this.shell = shell;
	}

	@Override
	public String getHelp(final String commandName) {
		return "Usage: " + commandName + ".";
	}

	@Override
	public String getSynopsis() {
		return "exit Rulewerk shell";
	}

	@Override
	public void run(final Command command, final org.semanticweb.rulewerk.commands.Interpreter interpreter)
			throws CommandExecutionException {
		this.shell.exitShell();
	}

}
