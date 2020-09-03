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

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.model.api.Command;

public class SetPrefixCommandInterpreter implements CommandInterpreter {

	@Override
	public void run(final Command command, final Interpreter interpreter) throws CommandExecutionException {
		Interpreter.validateArgumentCount(command, 2);
		final String prefixName = Interpreter.extractStringArgument(command, 0, "prefix name");
		final String prefixIri = Interpreter.extractNameArgument(command, 1, "prefix IRI");

		interpreter.getKnowledgeBase().getPrefixDeclarationRegistry().unsetPrefix(prefixName);
		try {
			interpreter.getKnowledgeBase().getPrefixDeclarationRegistry().setPrefixIri(prefixName, prefixIri);
		} catch (final PrefixDeclarationException e) { // practically impossible
			throw new CommandExecutionException("Setting prefix failed: " + e.getMessage());
		}
	}

	@Override
	public void printHelp(final String commandName, final Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " <prefix>: <IRI> .\n");
	}

	@Override
	public String getSynopsis() {
		return "set a prefix to abbreviate long IRIs (only affects future inputs)";
	}

}
