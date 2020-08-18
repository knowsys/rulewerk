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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.Terms;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

public class LoadCommandInterpreter implements CommandInterpreter {

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		if (command.getArguments().size() == 1) {
			String fileName;
			try {
				fileName = Terms.extractString(
						command.getArguments().get(0).fromTerm().orElseThrow(() -> new CommandExecutionException(
								"Expected string for file name, but did not find a term.")));
			} catch (IllegalArgumentException e) {
				throw new CommandExecutionException("Failed to convert term given for file name to string.");
			}
			try {
				FileInputStream fileInputStream = new FileInputStream(fileName);
				RuleParser.parseInto(interpreter.getReasoner().getKnowledgeBase(), fileInputStream);
			} catch (FileNotFoundException e) {
				throw new CommandExecutionException(e.getMessage(), e);
			} catch (ParsingException e) {
				interpreter.getOut().println("Error parsing file: " + e.getMessage());
			}

		} else {
			throw new CommandExecutionException(getHelp(command.getName()));
		}

	}

	@Override
	public String getHelp(String commandName) {
		return "Usage: @" + commandName + " <file>\n" + " file: path to a Rulewerk rls file";
	}

	@Override
	public String getSynopsis() {
		return "load a knowledge base from file (in Rulewerk rls format)";
	}

}
