package org.semanticweb.rulewerk.commands;

import org.semanticweb.rulewerk.core.model.api.Command;

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

/**
 * Interface for classes that interpret (execute) specific commands.
 * 
 * @author Markus Kroetzsch
 *
 */
public interface CommandInterpreter {

	/**
	 * Execute the commands in the context of the given reasoner and output stream.
	 * 
	 * @param command     command to be interpreted
	 * @param interpreter surrounding interpreter that provides the execution
	 *                    context
	 */
	void run(Command command, Interpreter interpreter) throws CommandExecutionException;

	/**
	 * Prints a text that describes command use and parameters, using the given
	 * command name. The output should start with a "Usage:" line, followed by
	 * single-space-indented parameter descriptions, and it should end with a
	 * newline.
	 */
	void printHelp(String commandName, Interpreter interpreter);

	/**
	 * Returns a short line describing the purpose of the command.
	 * 
	 * @return short command synopsis
	 */
	String getSynopsis();

}
