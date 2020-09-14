package org.semanticweb.rulewerk.client.shell;

/*-
 * #%L
 * Rulewerk Client
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

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

import picocli.CommandLine.Command;

/**
 * Class for executing the default {@code shell} command, which launches an
 * interactive shell.
 * 
 * @author Irina Dragoste
 *
 */
@Command(name = "shell", description = "Launch an interactive shell for Rulewerk. The default command.")
public class InteractiveShellClient
{

	/**
	 * Builds and launches an interactive shell, which accepts commands for running
	 * Rulewerk tasks using VLog Reasosner.
	 * 
	 * @param configuration for shell I/O resources
	 * @throws IOException if {@link Terminal} cannot be built.
	 */
	public void launchShell(final ShellConfiguration configuration) throws IOException {

		final Terminal terminal = configuration.buildTerminal();

		try (Interpreter interpreter = this.initializeInterpreter(terminal)) {
			final Shell shell = new Shell(interpreter);

			final LineReader lineReader = configuration.buildLineReader(terminal, shell.getCommands());
			final String prompt = configuration.buildPrompt(terminal);

			shell.run(lineReader, prompt);
		}
	}

	Interpreter initializeInterpreter(final Terminal terminal) {
		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
		final Interpreter interpreter = new Interpreter(Interpreter.EMPTY_KNOWLEDGE_BASE_PROVIDER,
				(knowledgeBase) -> new VLogReasoner(knowledgeBase), new TerminalStyledPrinter(terminal),
				parserConfiguration);

		return interpreter;
	}

}
