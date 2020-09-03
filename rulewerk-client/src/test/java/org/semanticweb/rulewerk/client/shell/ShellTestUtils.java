package org.semanticweb.rulewerk.client.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import java.io.PrintWriter;
import java.io.Writer;

import org.jline.terminal.Terminal;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;
import org.semanticweb.rulewerk.parser.ParserConfiguration;

public final class ShellTestUtils {

	private ShellTestUtils() {
	}

	public static Interpreter getMockInterpreter(final Writer writer) {
		final Terminal terminalMock = Mockito.mock(Terminal.class);
		final PrintWriter printWriter = new PrintWriter(writer);
		Mockito.when(terminalMock.writer()).thenReturn(printWriter);

		return getMockInterpreter(terminalMock);
	}

	public static Interpreter getMockInterpreter(final Terminal terminal) {
		final TerminalStyledPrinter terminalStyledPrinter = new TerminalStyledPrinter(terminal);

		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
		return new Interpreter(Interpreter.EMPTY_KNOWLEDGE_BASE_PROVIDER, (knowledgeBase) -> {
			final Reasoner reasoner = Mockito.mock(Reasoner.class);
			Mockito.when(reasoner.getKnowledgeBase()).thenReturn(knowledgeBase);
			return reasoner;
		}, terminalStyledPrinter, parserConfiguration);
	}

	public static void testIsExitCommand(final Command command) {
		assertEquals(ExitCommandInterpreter.EXIT_COMMAND.getName(), command.getName());
		assertTrue(command.getArguments().isEmpty());
	}

}
