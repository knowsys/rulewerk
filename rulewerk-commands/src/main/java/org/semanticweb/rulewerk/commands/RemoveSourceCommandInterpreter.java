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
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;

public class RemoveSourceCommandInterpreter implements CommandInterpreter {

	@Override
	public void run(final Command command, final Interpreter interpreter) throws CommandExecutionException {
		if (command.getArguments().size() == 0 || command.getArguments().size() > 2) {
			throw new CommandExecutionException("This command requires one or two arguments.");
		}

		final String predicateDeclaration = Interpreter.extractStringArgument(command, 0, "predicateName[arity]");
		final Predicate predicate = AddSourceCommandInterpreter.extractPredicate(predicateDeclaration);
		DataSource dataSource = null;
		if (command.getArguments().size() == 2) {
			final PositiveLiteral sourceDeclaration = Interpreter.extractPositiveLiteralArgument(command, 1,
					"source declaration");
			dataSource = AddSourceCommandInterpreter.extractDataSource(sourceDeclaration, interpreter);
		}

		if (dataSource != null) {
			final DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate, dataSource);
			if (interpreter.getKnowledgeBase().removeStatement(dataSourceDeclaration) > 0) {
				interpreter.printNormal("Removed specified data source declaration.\n");
			} else {
				interpreter.printNormal("Specified data source declaration not found in knowledge base.\n");
			}
		} else {
			int count = 0;
			for (final DataSourceDeclaration dataSourceDeclaration : interpreter.getKnowledgeBase()
					.getDataSourceDeclarations()) {
				if (dataSourceDeclaration.getPredicate().equals(predicate)) {
					interpreter.getKnowledgeBase().removeStatement(dataSourceDeclaration);
					count++;
				}
			}
			interpreter.printNormal("Removed " + count + " matching data source declaration(s).\n");
		}

	}

	@Override
	public void printHelp(final String commandName, final Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " <predicateName>[<arity>]: <source declartion> .\n"
				+ " <predicateName>[<arity>] : the name of the predicate and its arity\n"
				+ " <source declaration> (optional): a fact specifying a source declaration\n\n"
				+ "Note that every predicate can have multiple sources.\n");
	}

	@Override
	public String getSynopsis() {
		return "remove one or all external data sources for a predicate";
	}

}
