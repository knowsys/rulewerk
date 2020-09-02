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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;

public class InteractiveShellClientTest {

	@Test
	public void initializeInterpreter() {
		final Terminal terminal = Mockito.mock(Terminal.class);
		final PrintWriter writer = Mockito.mock(PrintWriter.class);
		Mockito.when(terminal.writer()).thenReturn(writer);

		final InteractiveShellClient interactiveShell = new InteractiveShellClient();
		final Interpreter interpreter = interactiveShell.initializeInterpreter(terminal);

		assertTrue(interpreter.getParserConfiguration() instanceof DefaultParserConfiguration);
		assertTrue(interpreter.getKnowledgeBase().getStatements().isEmpty());
		assertEquals(writer, interpreter.getWriter());
	}

	@Test
	public void run_mockConfiguration() throws IOException {
		final ShellConfiguration configuration = Mockito.mock(ShellConfiguration.class);
		final Terminal terminal = Mockito.mock(DumbTerminal.class);
		final StringWriter output = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(output);
		Mockito.when(terminal.writer()).thenReturn(printWriter);
		
		final LineReader lineReader = Mockito.mock(LineReader.class);
		Mockito.when(lineReader.readLine("prompt")).thenReturn("help", "exit");

		Mockito.when(configuration.buildTerminal()).thenReturn(terminal);
		Mockito.when(configuration.buildPrompt(terminal)).thenReturn("prompt");
		Mockito.when(configuration.buildLineReader(Mockito.eq(terminal), ArgumentMatchers.anyCollection()))
				.thenReturn(lineReader);

		final InteractiveShellClient shellClient = new InteractiveShellClient();
		shellClient.launchShell(configuration);

		assertTrue(output.toString().contains("Welcome to the Rulewerk interactive shell."));
		
		assertTrue(output.toString().contains("Available commands:"));

		assertTrue(output.toString().contains("Exiting Rulewerk"));
	}


}
