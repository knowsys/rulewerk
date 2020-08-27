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
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Rule;

public class ClearCommandInterpreter implements CommandInterpreter {

	static final String TASK_ALL = "ALL";
	static final String TASK_INFERENCES = "INF";
	static final String TASK_FACTS = "FACTS";
	static final String TASK_RULES = "RULES";
	static final String TASK_SOURCES = "DATASOURCES";
	static final String TASK_PREFIXES = "PREFIXES";

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		Interpreter.validateArgumentCount(command, 1);
		String task = Interpreter.extractNameArgument(command, 0, "task");
		if (TASK_ALL.equals(task)) {
			interpreter.clearReasonerAndKnowledgeBase();
			interpreter.printNormal("Knowledge base has been cleared; reasoner has been completely reset.\n");
		} else if (TASK_INFERENCES.equals(task)) {
			interpreter.getReasoner().resetReasoner();
			interpreter.printNormal("Reasoner has been reset.\n");
		} else if (TASK_FACTS.equals(task)) {
			for (Fact fact : interpreter.getKnowledgeBase().getFacts()) {
				interpreter.getKnowledgeBase().removeStatement(fact);
			}
			interpreter.printNormal("All facts have been removed from the knowledge base.\n");
		} else if (TASK_RULES.equals(task)) {
			for (Rule rule : interpreter.getKnowledgeBase().getRules()) {
				interpreter.getKnowledgeBase().removeStatement(rule);
			}
			interpreter.printNormal("All rules have been removed from the knowledge base.\n");
		} else if (TASK_SOURCES.equals(task)) {
			for (DataSourceDeclaration dataSourceDeclaration : interpreter.getKnowledgeBase()
					.getDataSourceDeclarations()) {
				interpreter.getKnowledgeBase().removeStatement(dataSourceDeclaration);
			}
			interpreter.printNormal("All datasource declarations have been removed from the knowledge base.\n");
		} else if (TASK_PREFIXES.equals(task)) {
			interpreter.getKnowledgeBase().getPrefixDeclarationRegistry().clear();
			interpreter.printNormal("All prefixes and the base namespace have been removed from the knowledge base.\n");
		} else {
			throw new CommandExecutionException(
					"Task \"" + task + "\" not supported; should be one of:  " + TASK_ALL + ", " + TASK_INFERENCES
							+ ", " + TASK_FACTS + ", " + TASK_RULES + ", " + TASK_SOURCES + ", " + TASK_PREFIXES);
		}
	}

	@Override
	public void printHelp(String commandName, Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " TASK\n" //
				+ " TASK: what to reset, possuble values:\n" //
				+ "   ALL: empty knowledge base and completely reset reasoner\n" //
				+ "   INF: reset reasoner to clear all loaded data and inferences\n" //
				+ "   FACTS: remove all facts from knowledge base\n" //
				+ "   RULES: remove all rules from knowledge base\n" //
				+ "   DATASOURCES: remove all data source declarations from knowledge base\n" //
				+ "   PREFIXES: undeclare all prefixes and base namespace\n");
	}

	@Override
	public String getSynopsis() {
		return "discards (parts of) the knowledge base or computed inferences";
	}

}
