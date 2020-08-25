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
import java.util.List;
import java.util.Set;

import org.jline.builtins.Completers;
import org.jline.builtins.Completers.FileNameCompleter;
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.builtins.Completers.TreeCompleter.Node;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.semanticweb.rulewerk.commands.Interpreter;

public final class DefaultConfiguration {

	private DefaultConfiguration() {
	}

	public static PromptProvider buildPromptProvider() {
		return () -> new AttributedString("rulewerk>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
	}

	public static LineReader buildLineReader(final Terminal terminal, final Interpreter interpreter) {
		final LineReaderBuilder lineReaderBuilder = LineReaderBuilder.builder().terminal(terminal)
				.appName("Rulewerk Shell").completer(buildCompleter(interpreter))
		// .expander(expander())
		// .history(buildHistory())
		// .highlighter(buildHighlighter())
		;

		final LineReader lineReader = lineReaderBuilder.build();
		lineReader.unsetOpt(LineReader.Option.INSERT_TAB); // This allows completion on an empty buffer, rather than
															// inserting a tab
		return lineReader;
	}

	private static Completer buildCompleter(final Interpreter interpreter) {
// @load and @export commands require a file name as argument
		final FileNameCompleter fileNameCompleter = new Completers.FileNameCompleter();

		final Set<String> registeredCommandNames = interpreter.getRegisteredCommands();
		final List<Node> nodes = new ArrayList<>();
		registeredCommandNames.stream().map(commandName -> "@" + commandName).forEach(serializedCommandName -> {
			if (serializedCommandName.equals("@load") || serializedCommandName.equals("@export")) {
				nodes.add(TreeCompleter.node(serializedCommandName, TreeCompleter.node(fileNameCompleter)));
			} else {
				nodes.add(TreeCompleter.node(serializedCommandName));
			}
		});
		return new TreeCompleter(nodes);

	}

	public static Terminal buildTerminal() throws IOException {
		return TerminalBuilder.builder().dumb(true).jansi(true).jna(false).system(true).build();
	}

}
