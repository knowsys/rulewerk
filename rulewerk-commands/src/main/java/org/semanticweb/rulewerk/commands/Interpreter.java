package org.semanticweb.rulewerk.commands;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Terms;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParser;
import org.semanticweb.rulewerk.parser.javacc.ParseException;
import org.semanticweb.rulewerk.parser.javacc.TokenMgrError;

public class Interpreter {

	final Reasoner reasoner;
	final PrintWriter out;
	final ParserConfiguration parserConfiguration;

	final HashMap<String, CommandInterpreter> commandInterpreters = new HashMap<>();

	public Interpreter(Reasoner reasoner, PrintWriter out, ParserConfiguration parserConfiguration) {
		this.reasoner = reasoner;
		this.out = out;
		this.parserConfiguration = parserConfiguration;
		registerDefaultCommandInterpreters();
	}

	public void registerCommandInterpreter(String command, CommandInterpreter commandInterpreter) {
		commandInterpreters.put(command, commandInterpreter);
	}

	public void runCommands(List<Command> commands) throws CommandExecutionException {
		for (Command command : commands) {
			runCommand(command);
		}
	}

	public void runCommand(Command command) throws CommandExecutionException {
		if (commandInterpreters.containsKey(command.getName())) {
			try {
				commandInterpreters.get(command.getName()).run(command, this);
			} catch (Exception e) {
				throw new CommandExecutionException(e.getMessage(), e);
			}
		} else {
			throw new CommandExecutionException("Unknown command '" + command.getName() + "'");
		}
	}

	public Command parseCommand(String commandString) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(commandString.getBytes(StandardCharsets.UTF_8));
		final JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		localParser.setParserConfiguration(parserConfiguration);

		// Copy prefixes from KB:
		try {
			localParser.getPrefixDeclarationRegistry().setBaseIri(reasoner.getKnowledgeBase().getBaseIri());
			for (Entry<String, String> prefix : reasoner.getKnowledgeBase().getPrefixDeclarationRegistry()) {
				localParser.getPrefixDeclarationRegistry().setPrefixIri(prefix.getKey(), prefix.getValue());
			}
		} catch (PrefixDeclarationException e) { // unlikely!
			throw new RuntimeException(e);
		}

		Command result;
		try {
			result = localParser.command();
			localParser.ensureEndOfInput();
		} catch (ParseException | PrefixDeclarationException | TokenMgrError | RuntimeException e) {
			throw new ParsingException("failed to parse command \"\"\"" + commandString + "\"\"\"", e);
		}
		return result;
	}

	public Reasoner getReasoner() {
		return reasoner;
	}

	public ParserConfiguration getParserConfiguration() {
		return parserConfiguration;
	}

	public PrintWriter getOut() {
		return out;
	}

	private void registerDefaultCommandInterpreters() {
		registerCommandInterpreter("help", new HelpCommandInterpreter());
		registerCommandInterpreter("assert", new AssertCommandInterpreter());
		registerCommandInterpreter("query", new QueryCommandInterpreter());
		registerCommandInterpreter("reason", new ReasonCommandInterpreter());
		registerCommandInterpreter("load", new LoadCommandInterpreter());
		registerCommandInterpreter("setprefix", new SetPrefixCommandInterpreter());
		registerCommandInterpreter("addsource", new AddSourceCommandInterpreter());
		registerCommandInterpreter("showkb", new ShowKbCommandInterpreter());
	}

	/**
	 * Validate that the correct number of arguments was passed to a command.
	 * 
	 * @param command Command to validate
	 * @param number  expected number of parameters
	 * @throws CommandExecutionException if the number is not correct
	 */
	public static void validateArgumentCount(Command command, int number) throws CommandExecutionException {
		if (command.getArguments().size() != number) {
			throw new CommandExecutionException("This command requires exactly " + number + " argument(s), but "
					+ command.getArguments().size() + " were given.");
		}
	}

	private static CommandExecutionException getArgumentTypeError(int index, String expectedType,
			String parameterName) {
		return new CommandExecutionException(
				"Argument at position " + index + " needs to be of type " + expectedType + " (" + parameterName + ").");
	}

	public static String extractStringArgument(Command command, int index, String parameterName)
			throws CommandExecutionException {
		try {
			return Terms.extractString(command.getArguments().get(index).fromTerm()
					.orElseThrow(() -> getArgumentTypeError(index, "string", parameterName)));
		} catch (IllegalArgumentException e) {
			throw getArgumentTypeError(index, "string", parameterName);
		}
	}

	public static String extractNameArgument(Command command, int index, String parameterName)
			throws CommandExecutionException {
		try {
			return Terms.extractName(command.getArguments().get(index).fromTerm()
					.orElseThrow(() -> getArgumentTypeError(index, "constant", parameterName)));
		} catch (IllegalArgumentException e) {
			throw getArgumentTypeError(index, "constant", parameterName);
		}
	}

	public static PositiveLiteral extractPositiveLiteralArgument(Command command, int index, String parameterName)
			throws CommandExecutionException {
		return command.getArguments().get(index).fromPositiveLiteral()
				.orElseThrow(() -> getArgumentTypeError(index, "literal", parameterName));
	}

}
