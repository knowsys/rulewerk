package org.semanticweb.rulewerk.commands;

/*-
 * #%L
 * Rulewerk command execution support
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

public class ClearCommandInterpreter implements CommandInterpreter {

	static final String TASK_ALL = "ALL";
	static final String TASK_INFERENCES = "INF";

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		Interpreter.validateArgumentCount(command, 1);
		String task = Interpreter.extractNameArgument(command, 0, "task");
		if (TASK_ALL.equals(task)) {
			interpreter.clearReasonerAndKnowledgeBase();
			interpreter.printNormal("Knowledge has been cleared; reasoner has been completely reset.\n");
		} else if (TASK_INFERENCES.equals(task)) {
			interpreter.getReasoner().resetReasoner();
			interpreter.printNormal("Reasoner has been reset.\n");
		} else {
			throw new CommandExecutionException(
					"Task \"" + task + "\" not supported; should be one of:  " + TASK_ALL + ", " + TASK_INFERENCES);
		}
	}

	@Override
	public void printHelp(String commandName, Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " TASK\n" + //
				" TASK: what to reset, ALL (knowledge base), INF (inferences)\n");
	}

	@Override
	public String getSynopsis() {
		return "discards the knowledge base and/or previously computed inferences";
	}

}
