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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
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
				.appName("Rulewerk Shell")
				.completer(buildCompleter(interpreter))
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
		final Set<String> registeredCommandNames = interpreter.getRegisteredCommands();
		final List<String> serializedCommandNames = registeredCommandNames.stream()
				.map(commandName -> "@" + commandName).collect(Collectors.toList());
		final Completer commandNamesCompleter = new StringsCompleter(serializedCommandNames);
		// do not complete command arguments
		return new ArgumentCompleter(commandNamesCompleter, NullCompleter.INSTANCE);
	}


	public static Terminal buildTerminal() throws IOException {
		return TerminalBuilder.builder().dumb(true).jansi(true).jna(false).system(true).build();
	}

}
