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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jline.builtins.Completers;
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.builtins.Completers.TreeCompleter.Node;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.semanticweb.rulewerk.commands.ClearCommandInterpreter;
import org.semanticweb.rulewerk.commands.ExportCommandInterpreter;
import org.semanticweb.rulewerk.commands.LoadCommandInterpreter;

/**
 * An implementation of {@link ShellConfiguration} with custom styling and
 * completion for recognized commands.
 * 
 * @author Irina Dragoste
 *
 */
public class DefaultShellConfiguration implements ShellConfiguration {

	public static final String PROMPT_STRING = "rulewerk> ";

	@Override
	public LineReader buildLineReader(final Terminal terminal, final Collection<String> registeredCommands) {
		final LineReaderBuilder lineReaderBuilder = this.getDefaultLineReaderConfiguration(LineReaderBuilder.builder());

		lineReaderBuilder.terminal(terminal);
		lineReaderBuilder.completer(this.buildCompleter(registeredCommands));

		return lineReaderBuilder.build();
	}

	LineReaderBuilder getDefaultLineReaderConfiguration(final LineReaderBuilder lineReaderBuilder) {

		lineReaderBuilder.appName("Rulewerk Shell");
		/*
		 * This allows completion on an empty buffer, rather than inserting a tab
		 */
		lineReaderBuilder.option(LineReader.Option.INSERT_TAB, false);
		lineReaderBuilder.option(LineReader.Option.AUTO_FRESH_LINE, true);
		lineReaderBuilder.option(LineReader.Option.USE_FORWARD_SLASH, true);

		return lineReaderBuilder;
	}

	TreeCompleter buildCompleter(final Collection<String> registeredCommands) {
		final Node fileNameCompleterNode = TreeCompleter.node(new Completers.FileNameCompleter());

		final List<TreeCompleter.Node> nodes = new ArrayList<>();
		registeredCommands.stream().map(command -> "@" + command).forEach(commandName -> {
			if (commandName.equals("@load")) {
				nodes.add(TreeCompleter.node(commandName, fileNameCompleterNode));

				final StringsCompleter taskOptionsCompleter = new StringsCompleter(LoadCommandInterpreter.TASK_OWL,
						LoadCommandInterpreter.TASK_RDF, LoadCommandInterpreter.TASK_RLS);
				nodes.add(TreeCompleter.node(commandName,
						TreeCompleter.node(taskOptionsCompleter, fileNameCompleterNode)));
			} else if (commandName.equals("@export")) {
				final StringsCompleter taskOptionsCompleter = new StringsCompleter(
						ExportCommandInterpreter.TASK_INFERENCES, ExportCommandInterpreter.TASK_KB
						);
				nodes.add(TreeCompleter.node(commandName,
						TreeCompleter.node(taskOptionsCompleter, fileNameCompleterNode)));
			} else if (commandName.equals("@clear")) {
				final StringsCompleter taskOptionsCompleter = new StringsCompleter(ClearCommandInterpreter.TASK_ALL,
						ClearCommandInterpreter.TASK_INFERENCES, ClearCommandInterpreter.TASK_FACTS,
						ClearCommandInterpreter.TASK_PREFIXES, ClearCommandInterpreter.TASK_RULES,
						ClearCommandInterpreter.TASK_SOURCES);
				nodes.add(TreeCompleter.node(commandName, TreeCompleter.node(taskOptionsCompleter)));
			} else if (commandName.equals("@help")) {
				nodes.add(
						TreeCompleter.node(commandName, TreeCompleter.node(new StringsCompleter(registeredCommands))));
			} else {
				nodes.add(TreeCompleter.node(commandName));
			}
		});
		return new TreeCompleter(nodes);

	}

	@Override
	public Terminal buildTerminal() throws IOException {
		return this.getDefaultTerminalConfiguration(TerminalBuilder.builder()).build();
	}

	TerminalBuilder getDefaultTerminalConfiguration(final TerminalBuilder terminalBuilder) {
		return terminalBuilder.dumb(true).jansi(true).jna(false).system(true);
	}

	@Override
	public String buildPrompt(final Terminal terminal) {
		return this.getDefaultPromptStyle().toAnsi(terminal);
	}

	AttributedString getDefaultPromptStyle() {
		final AttributedStyle promptStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW);
		return new AttributedString(PROMPT_STRING, promptStyle);
	}

}
