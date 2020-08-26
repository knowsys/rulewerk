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
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

import picocli.CommandLine.Command;

@Command(name = "shell", description = "An interactive shell for Rulewerk. The default command.")
public class InteractiveShell
//implements Runnable
{

//	@Override
	public void run() throws IOException {

		final Terminal terminal = DefaultConfiguration.buildTerminal();
		final Interpreter interpreter = this.initializeInterpreter(terminal);
		final Shell shell = new Shell(interpreter);

		final LineReader lineReader = DefaultConfiguration.buildLineReader(terminal, interpreter);
		final String prompt = DefaultConfiguration.buildPrompt(terminal);

		shell.run(lineReader, prompt);
	}

	Interpreter initializeInterpreter(final Terminal terminal) {
		final KnowledgeBase knowledgeBase = new KnowledgeBase();
		final Reasoner reasoner = new VLogReasoner(knowledgeBase);
		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
		final Interpreter interpreter = new Interpreter(reasoner, new TerminalStyledPrinter(terminal),
				parserConfiguration);

		return interpreter;
	}

}
