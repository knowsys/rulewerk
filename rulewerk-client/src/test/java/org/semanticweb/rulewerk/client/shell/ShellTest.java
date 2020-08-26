//package org.semanticweb.rulewerk.client.shell;
//
///*-
// * #%L
// * Rulewerk Client
// * %%
// * Copyright (C) 2018 - 2020 Rulewerk Developers
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//import static org.junit.Assert.assertNull;
//
//import java.io.PrintWriter;
//
//import org.jline.reader.LineReader;
//import org.jline.terminal.Terminal;
//import org.jline.utils.AttributedString;
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.semanticweb.rulewerk.commands.Interpreter;
//import org.semanticweb.rulewerk.core.model.api.Command;
//import org.semanticweb.rulewerk.core.reasoner.Reasoner;
//import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;
//import org.semanticweb.rulewerk.parser.ParserConfiguration;
//
//public class ShellTest {
//
//	@Test
//	public void testProcessLineEmpty() {
//		final Terminal terminalMock = Mockito.mock(Terminal.class);
//		final Interpreter interpreter = getMockInterpreter(terminalMock);
//		DefaultConfiguration.buildLineReader(terminalMock, interpreter);
//		final LineReader lineReaderMock = Mockito.mock(LineReader.class);
//		final AttributedString prompt = Mockito.mock(AttributedString.class);
//		final Shell shell = new Shell(interpreter);
//
//		Mockito.when(lineReaderMock.readLine(Mockito.anyString())).thenReturn("");
//
//		final Command readCommand = shell.readCommand(lineReaderMock, prompt);
//		assertNull(readCommand);
//	}
//
//	static public Interpreter getMockInterpreter(final Terminal terminal) {
//		final Reasoner reasonerMock = Mockito.mock(Reasoner.class);
//		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
//
//		final Interpreter interpreter = new Interpreter(reasonerMock, new TerminalStyledPrinter(terminal),
//				parserConfiguration);
//
//		final PrintWriter printWriter = Mockito.mock(PrintWriter.class);
//		Mockito.when(terminal.writer()).thenReturn(printWriter);
////
////		// final TerminalStyledPrinter printer = new TerminalStyledPrinter(writer);
////		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
////		final KnowledgeBase knowledgeBase = new KnowledgeBase();
//
////		Mockito.when(reasoner.getKnowledgeBase()).thenReturn(knowledgeBase);
////		return new Interpreter(reasoner, printer, parserConfiguration);
//		return interpreter;
//	}
//
//}
