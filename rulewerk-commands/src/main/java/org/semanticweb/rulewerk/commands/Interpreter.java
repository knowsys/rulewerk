package org.semanticweb.rulewerk.commands;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

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

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Terms;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParser;
import org.semanticweb.rulewerk.parser.javacc.ParseException;
import org.semanticweb.rulewerk.parser.javacc.TokenMgrError;

public class Interpreter implements AutoCloseable {

	@FunctionalInterface
	public interface ReasonerProvider {
		public Reasoner reasoner(KnowledgeBase knowledgeBase);
	}

	@FunctionalInterface
	public interface KnowledgeBaseProvider {
		public KnowledgeBase knowledgeBase();
	}

	final public static KnowledgeBaseProvider EMPTY_KNOWLEDGE_BASE_PROVIDER = new KnowledgeBaseProvider() {
		@Override
		public KnowledgeBase knowledgeBase() {
			return new KnowledgeBase();
		}
	};

	final ReasonerProvider reasonerProvider;
	final KnowledgeBaseProvider knowledgeBaseProvider;

	Reasoner reasoner = null;
	final StyledPrinter printer;
	final ParserConfiguration parserConfiguration;

	final LinkedHashMap<String, CommandInterpreter> commandInterpreters = new LinkedHashMap<>();

	public Interpreter(final KnowledgeBaseProvider knowledgeBaseProvider, final ReasonerProvider reasonerProvider,
			final StyledPrinter printer, final ParserConfiguration parserConfiguration) {
		this.knowledgeBaseProvider = knowledgeBaseProvider;
		this.reasonerProvider = reasonerProvider;
		this.clearReasonerAndKnowledgeBase();
		this.printer = printer;
		this.parserConfiguration = parserConfiguration;
		this.registerDefaultCommandInterpreters();
	}

	public void registerCommandInterpreter(final String command, final CommandInterpreter commandInterpreter) {
		this.commandInterpreters.put(command, commandInterpreter);
	}

	public Set<String> getRegisteredCommands() {
		return this.commandInterpreters.keySet();
	}

	public void runCommands(final List<Command> commands) throws CommandExecutionException {
		for (final Command command : commands) {
			this.runCommand(command);
		}
	}

	public void runCommand(final Command command) throws CommandExecutionException {
		if (this.commandInterpreters.containsKey(command.getName())) {
			try {
				this.commandInterpreters.get(command.getName()).run(command, this);
			} catch (final Exception e) {
				throw new CommandExecutionException(e.getMessage(), e);
			}
		} else {
			throw new CommandExecutionException("Unknown command '" + command.getName() + "'");
		}
	}

	public Command parseCommand(final String commandString) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(commandString.getBytes(StandardCharsets.UTF_8));
		final JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		localParser.setParserConfiguration(this.parserConfiguration);

		// Copy prefixes from KB:
		try {
			localParser.getPrefixDeclarationRegistry().setBaseIri(this.reasoner.getKnowledgeBase().getBaseIri());
			for (final Entry<String, String> prefix : this.reasoner.getKnowledgeBase().getPrefixDeclarationRegistry()) {
				localParser.getPrefixDeclarationRegistry().setPrefixIri(prefix.getKey(), prefix.getValue());
			}
		} catch (final PrefixDeclarationException e) { // unlikely!
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
		return this.reasoner;
	}

	public KnowledgeBase getKnowledgeBase() {
		return this.reasoner.getKnowledgeBase();
	}

	public ParserConfiguration getParserConfiguration() {
		return this.parserConfiguration;
	}

	public Writer getWriter() {
		return this.printer.getWriter();
	}

	public void printNormal(final String string) {
		this.printer.printNormal(string);
	}

	public void printSection(final String string) {
		this.printer.printSection(string);
	}

	public void printEmph(final String string) {
		this.printer.printEmph(string);
	}

	public void printCode(final String string) {
		this.printer.printCode(string);
	}

	public void printImportant(final String string) {
		this.printer.printImportant(string);
	}

	private void registerDefaultCommandInterpreters() {
		this.registerCommandInterpreter("help", new HelpCommandInterpreter());
		this.registerCommandInterpreter("load", new LoadCommandInterpreter());
		this.registerCommandInterpreter("assert", new AssertCommandInterpreter());
		this.registerCommandInterpreter("retract", new RetractCommandInterpreter());
		this.registerCommandInterpreter("addsource", new AddSourceCommandInterpreter());
		this.registerCommandInterpreter("delsource", new RemoveSourceCommandInterpreter());
		this.registerCommandInterpreter("setprefix", new SetPrefixCommandInterpreter());
		this.registerCommandInterpreter("clear", new ClearCommandInterpreter());
		this.registerCommandInterpreter("reason", new ReasonCommandInterpreter());
		this.registerCommandInterpreter("query", new QueryCommandInterpreter());
		this.registerCommandInterpreter("export", new ExportCommandInterpreter());
		this.registerCommandInterpreter("showkb", new ShowKbCommandInterpreter());
	}

	/**
	 * Validate that the correct number of arguments was passed to a command.
	 * 
	 * @param command Command to validate
	 * @param number  expected number of parameters
	 * @throws CommandExecutionException if the number is not correct
	 */
	public static void validateArgumentCount(final Command command, final int number) throws CommandExecutionException {
		if (command.getArguments().size() != number) {
			throw new CommandExecutionException("This command requires exactly " + number + " argument(s), but "
					+ command.getArguments().size() + " were given.");
		}
	}

	private static CommandExecutionException getArgumentTypeError(final int index, final String expectedType,
			final String parameterName) {
		return new CommandExecutionException(
				"Argument at position " + index + " needs to be of type " + expectedType + " (" + parameterName + ").");
	}

	public static String extractStringArgument(final Command command, final int index, final String parameterName)
			throws CommandExecutionException {
		try {
			return Terms.extractString(command.getArguments().get(index).fromTerm()
					.orElseThrow(() -> getArgumentTypeError(index, "string", parameterName)));
		} catch (final IllegalArgumentException | IndexOutOfBoundsException e) {
			throw getArgumentTypeError(index, "string", parameterName);
		}
	}

	public static String extractNameArgument(final Command command, final int index, final String parameterName)
			throws CommandExecutionException {
		try {
			return Terms.extractName(command.getArguments().get(index).fromTerm()
					.orElseThrow(() -> getArgumentTypeError(index, "constant", parameterName)));
		} catch (final IllegalArgumentException | IndexOutOfBoundsException e) {
			throw getArgumentTypeError(index, "constant", parameterName);
		}
	}

	public static PositiveLiteral extractPositiveLiteralArgument(final Command command, final int index,
			final String parameterName) throws CommandExecutionException {
		try {
			return command.getArguments().get(index).fromPositiveLiteral()
					.orElseThrow(() -> getArgumentTypeError(index, "literal", parameterName));
		} catch (final IndexOutOfBoundsException e) {
			throw getArgumentTypeError(index, "constant", parameterName);
		}
	}

	/**
	 * Returns a Writer to write to the specified file.
	 * 
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public Writer getFileWriter(final String fileName) throws FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
	}

	/**
	 * Returns an InputStream to read from the specified file.
	 * 
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public InputStream getFileInputStream(final String fileName) throws FileNotFoundException {
		return new FileInputStream(fileName);
	}

	/**
	 * Completely resets the reasoner and knowledge base. All inferences and
	 * statements are cleared.
	 */
	public void clearReasonerAndKnowledgeBase() {
		this.closeReasoner();
		this.reasoner = this.reasonerProvider.reasoner(this.knowledgeBaseProvider.knowledgeBase());
		this.reasoner.setLogLevel(LogLevel.ERROR);
		try {
			this.reasoner.reason();
		} catch (final IOException e) {
			throw new RulewerkRuntimeException("Failed to initialise reasoner: " + e.getMessage(), e);
		}
	}

	/**
	 * Frees all resources, especially those associated with reasoning.
	 */
	@Override
	public void close() {
		this.closeReasoner();
	}

	/**
	 * Closes and discards the internal {@link Reasoner}.
	 */
	private void closeReasoner() {
		if (this.reasoner != null) {
			this.reasoner.close();
			this.reasoner = null;
		}
	}

}
