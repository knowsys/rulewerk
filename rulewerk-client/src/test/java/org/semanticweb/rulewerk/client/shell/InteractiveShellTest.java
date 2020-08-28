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

import java.io.PrintWriter;

import org.jline.terminal.Terminal;
import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;

public class InteractiveShellTest {

	@Test
	public void initializeInterpreter() {
		final Terminal terminal = Mockito.mock(Terminal.class);
		final PrintWriter writer = Mockito.mock(PrintWriter.class);
		Mockito.when(terminal.writer()).thenReturn(writer);

		final InteractiveShell interactiveShell = new InteractiveShell();
		final Interpreter interpreter = interactiveShell.initializeInterpreter(terminal);

		assertTrue(interpreter.getParserConfiguration() instanceof DefaultParserConfiguration);
		assertTrue(interpreter.getKnowledgeBase().getStatements().isEmpty());
		assertEquals(writer, interpreter.getWriter());
	}


}
