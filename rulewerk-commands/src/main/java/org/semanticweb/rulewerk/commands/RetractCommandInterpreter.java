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

import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class RetractCommandInterpreter implements CommandInterpreter {

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
				factCount += interpreter.getKnowledgeBase().removeStatement(fact);
			} else if (argument.fromRule().isPresent()) {
				ruleCount += interpreter.getKnowledgeBase().removeStatement(argument.fromRule().get());
			} else { // implies argument.fromTerm().isPresent() 
				String predicateDeclaration = Interpreter.extractStringArgument(command, 0, "predicateName[arity]");
				Predicate predicate = AddSourceCommandInterpreter.extractPredicate(predicateDeclaration);
				for (Fact fact : interpreter.getKnowledgeBase().getFacts()) {
					if (predicate.equals(fact.getPredicate())) {
						factCount += interpreter.getKnowledgeBase().removeStatement(fact);
					}
				}
			}
		}

		interpreter.printNormal("Retracted " + factCount + " fact(s) and " + ruleCount + " rule(s).\n");
	}

	@Override
	public void printHelp(String commandName, Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " (<fact or rule>)+ .\n"
				+ " fact or rule: statement(s) to be removed from the knowledge base, or a predicate declaration\n"
				+ "               of the form name[arity] to remove all facts for that predicate.\n"
				+ "Reasoning needs to be invoked after finishing the removal of statements.\n");
	}

	@Override
	public String getSynopsis() {
		return "remove facts and rules to the knowledge base";
	}

}
