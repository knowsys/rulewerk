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
import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.parser.ParsingException;

public class AddSourceCommandInterpreter implements CommandInterpreter {

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		Interpreter.validateArgumentCount(command, 2);
		String predicateDeclaration = Interpreter.extractStringArgument(command, 0, "predicateName[arity]");
		PositiveLiteral sourceDeclaration = Interpreter.extractPositiveLiteralArgument(command, 1,
				"source declaration");

		Predicate predicate = extractPredicate(predicateDeclaration);
		DataSource dataSource = extractDataSource(sourceDeclaration, interpreter);

		if (dataSource.getRequiredArity().isPresent()) {
			Integer requiredArity = dataSource.getRequiredArity().get();
			if (predicate.getArity() != requiredArity) {
				throw new CommandExecutionException("Invalid arity " + predicate.getArity() + " for data source, "
						+ "expected " + requiredArity + ".");
			}
		}

		interpreter.getKnowledgeBase().addStatement(new DataSourceDeclarationImpl(predicate, dataSource));
	}

	@Override
	public void printHelp(String commandName, Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " <predicateName>[<arity>]: <source declaration>.\n"
				+ " <predicateName>[<arity>] : the name of the predicate and its arity\n"
				+ " <source declaration> : a fact specifying a source declaration\n\n"
				+ "Note that every predicate can have multiple sources.\n");
	}

	@Override
	public String getSynopsis() {
		return "define a new external data source for a predicate";
	}

	static Predicate extractPredicate(String predicateDeclaration) throws CommandExecutionException {
		String predicateName;
		int arity;
		try {
			int openBracket = predicateDeclaration.indexOf('[');
			int closeBracket = predicateDeclaration.indexOf(']');
			predicateName = predicateDeclaration.substring(0, openBracket);
			String arityString = predicateDeclaration.substring(openBracket + 1, closeBracket);
			arity = Integer.parseInt(arityString);
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			throw new CommandExecutionException(
					"Predicate declaration must have the format \"predicateName[number]\" but was \""
							+ predicateDeclaration + "\".");
		}
		return Expressions.makePredicate(predicateName, arity);
	}

	static DataSource extractDataSource(PositiveLiteral sourceDeclaration, Interpreter interpreter)
			throws CommandExecutionException {
		try {
			return interpreter.getParserConfiguration()
					.parseDataSourceSpecificPartOfDataSourceDeclaration(sourceDeclaration);
		} catch (ParsingException e) {
			throw new CommandExecutionException("Could not parse source declaration: " + e.getMessage());
		}
	}

}
