package org.semanticweb.rulewerk.commands;

import org.semanticweb.rulewerk.core.model.api.Argument;
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

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class AssertCommandInterpreter implements CommandInterpreter {

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		int factCount = 0;
		int ruleCount = 0;
		for (Argument argument : command.getArguments()) {
			if (argument.fromPositiveLiteral().isPresent()) {
				PositiveLiteral literal = argument.fromPositiveLiteral().get();
				Fact fact;
				try {
					fact = Expressions.makeFact(literal.getPredicate(), literal.getArguments());
				} catch (IllegalArgumentException e) {
					throw new CommandExecutionException("Literal " + literal.toString() + " is not a fact.", e);
				}
				interpreter.getKnowledgeBase().addStatement(fact);
				factCount++;
			} else if (argument.fromRule().isPresent()) {
				interpreter.getKnowledgeBase().addStatement(argument.fromRule().get());
				ruleCount++;
			} else {
				throw new CommandExecutionException(
						"Only facts and rules can be asserted. Encountered " + argument.toString());
			}
		}

		interpreter.printNormal("Asserted " + factCount + " fact(s) and " + ruleCount + " rule(s).\n");
	}

	@Override
	public void printHelp(String commandName, Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " (<fact or rule>)+ .\n"
				+ " fact or rule: statement(s) to be added to the knowledge base\n"
				+ "Reasoning needs to be invoked after finishing addition of statements.\n");
	}

	@Override
	public String getSynopsis() {
		return "add facts and rules to the knowledge base";
	}

}
