package org.semanticweb.rulewerk.commands;

/*-
 * #%L
 * Rulewerk command execution support
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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.QueryAnswerCount;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.implementation.QueryAnswerCountImpl;
import org.semanticweb.rulewerk.core.reasoner.implementation.QueryResultImpl;
import org.semanticweb.rulewerk.parser.ParsingException;

public class QueryCommandInterpreterTest {

	class TestQueryResultIterator implements QueryResultIterator {

		final Iterator<QueryResult> results;

		public TestQueryResultIterator(List<QueryResult> results) {
			this.results = results.iterator();
		}

		@Override
		public boolean hasNext() {
			return results.hasNext();
		}

		@Override
		public QueryResult next() {
			return results.next();
		}

		@Override
		public Correctness getCorrectness() {
			return Correctness.SOUND_AND_COMPLETE;
		}

		@Override
		public void close() {
		}

	}

	@Test
	public void correctUseQuery_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		QueryResult r1 = new QueryResultImpl(Arrays.asList(Expressions.makeAbstractConstant("#TEST-1#")));
		QueryResult r2 = new QueryResultImpl(Arrays.asList(Expressions.makeAbstractConstant("#TEST-2#")));
		QueryResult r3 = new QueryResultImpl(Arrays.asList(Expressions.makeAbstractConstant("#TEST-3#")));

		QueryResultIterator results = new TestQueryResultIterator(Arrays.asList(r1, r2, r3));

		Mockito.when(interpreter.getReasoner().answerQuery(Mockito.any(), Mockito.eq(true))).thenReturn(results);

		Command command = interpreter.parseCommand("@query p(?X) LIMIT 2 .");
		interpreter.runCommand(command);
		// correct operation largely verified by not throwing an exception on the
		// previous line, since only very few calls to the reasoner are not mocked
		String output = writer.toString();

		assertEquals("query", command.getName());
		assertEquals(3, command.getArguments().size());
		assertTrue(output.contains("#TEST-1#"));
		assertTrue(output.contains("#TEST-2#"));
		assertFalse(output.contains("#TEST-3#"));
		assertTrue(output.contains(Correctness.SOUND_AND_COMPLETE.toString()));
	}

	@Test
	public void correctUseBooleanQueryTrue_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		QueryResult r1 = new QueryResultImpl(Arrays.asList(Expressions.makeAbstractConstant("TEST-1")));
		QueryResult r2 = new QueryResultImpl(Arrays.asList(Expressions.makeAbstractConstant("#TEST-2#")));
		QueryResult r3 = new QueryResultImpl(Arrays.asList(Expressions.makeAbstractConstant("#TEST-3#")));

		QueryResultIterator results = new TestQueryResultIterator(Arrays.asList(r1, r2, r3));

		Mockito.when(interpreter.getReasoner().answerQuery(Mockito.any(), Mockito.eq(true))).thenReturn(results);

		Command command = interpreter.parseCommand("@query p(TEST-1) LIMIT 2 .");
		interpreter.runCommand(command);
		// correct operation largely verified by not throwing an exception on the
		// previous line, since only very few calls to the reasoner are not mocked
		String output = writer.toString();

		assertEquals("query", command.getName());
		assertEquals(3, command.getArguments().size());
		assertFalse(output.contains("TEST-1"));
		assertFalse(output.contains("#TEST-2#"));
		assertFalse(output.contains("#TEST-3#"));
		assertTrue(output.startsWith("true"));
		assertTrue(output.contains(Correctness.SOUND_AND_COMPLETE.toString()));
	}
	
	@Test
	public void correctUseBooleanQueryFalse_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		QueryResultIterator results = new TestQueryResultIterator(Arrays.asList());

		Mockito.when(interpreter.getReasoner().answerQuery(Mockito.any(), Mockito.eq(true))).thenReturn(results);

		Command command = interpreter.parseCommand("@query p(TEST-1) LIMIT 2 .");
		interpreter.runCommand(command);
		// correct operation largely verified by not throwing an exception on the
		// previous line, since only very few calls to the reasoner are not mocked
		String output = writer.toString();

		assertEquals("query", command.getName());
		assertEquals(3, command.getArguments().size());
		assertTrue(output.startsWith("false"));
		assertTrue(output.contains(Correctness.SOUND_AND_COMPLETE.toString()));
	}

	@Test
	public void correctUseCount_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		QueryAnswerCount queryAnswerCount = new QueryAnswerCountImpl(Correctness.SOUND_AND_COMPLETE, 42);
		Mockito.when(interpreter.getReasoner().countQueryAnswers(Mockito.any(), Mockito.eq(true)))
				.thenReturn(queryAnswerCount);
		Mockito.when(interpreter.getReasoner().countQueryAnswers(Mockito.any())).thenReturn(queryAnswerCount);

		Command command = interpreter.parseCommand("@query COUNT p(?X) .");
		interpreter.runCommand(command);
		// correct operation largely verified by not throwing an exception on the
		// previous line, since only very few calls to the reasoner are not mocked

		assertEquals("query", command.getName());
		assertEquals(2, command.getArguments().size());
		assertTrue(writer.toString().startsWith("42\n"));
		assertTrue(writer.toString().contains(Correctness.SOUND_AND_COMPLETE.toString()));
	}

	@Test
	public void correctUseExport_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Mockito.when(interpreter.getReasoner().exportQueryAnswersToCsv(Mockito.any(), Mockito.eq("file.csv"),
				Mockito.anyBoolean())).thenReturn(Correctness.SOUND_BUT_INCOMPLETE);

		Command command = interpreter.parseCommand("@query p(?X) EXPORTCSV \"file.csv\" .");
		interpreter.runCommand(command);
		// correct operation largely verified by not throwing an exception on the
		// previous line, since only very few calls to the reasoner are not mocked

		assertEquals("query", command.getName());
		assertEquals(3, command.getArguments().size());
		assertTrue(writer.toString().contains(Correctness.SOUND_BUT_INCOMPLETE.toString()));
	}

	@Test(expected = CommandExecutionException.class)
	public void exportIoError_fails() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Mockito.when(interpreter.getReasoner().exportQueryAnswersToCsv(Mockito.any(), Mockito.eq("file.csv"),
				Mockito.anyBoolean())).thenThrow(IOException.class);

		Command command = interpreter.parseCommand("@query p(?X) EXPORTCSV \"file.csv\" .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCountZero_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentNoLiteral_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query COUNT LIMIT 10 .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCountWithLimit_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query COUNT p(?X) LIMIT 10 .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCountWithExportFile_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query COUNT p(?X) EXPORTCSV \"file.csv\" .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentWrongLimitTerm_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query p(?X) LIMIT \"10\" .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentWrongLimitNoTerm_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query p(?X) LIMIT p(a) .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentMissingLimit_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query p(?X) LIMIT .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentWrongExportFileTerm_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query p(?X) EXPORTCSV 123 .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentWrongExportFileNoTerm_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query p(?X) EXPORTCSV p(a) .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentMissingExportFile_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query p(?X) EXPORTCSV .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentExportWithLimit_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@query p(?X) LIMIT 10 EXPORTCSV \"test.csv\" .");
		interpreter.runCommand(command);
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		CommandInterpreter commandInterpreter = new QueryCommandInterpreter();
		InterpreterTest.checkHelpFormat(commandInterpreter, interpreter, writer);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		CommandInterpreter commandInterpreter = new QueryCommandInterpreter();
		InterpreterTest.checkSynopsisFormat(commandInterpreter);
	}

}
