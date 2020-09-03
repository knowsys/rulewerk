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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.parser.ParsingException;

public class ExportCommandInterpreterTest {

	@Test
	public void correctUseKb_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		StringWriter fileWriter = new StringWriter();
		Interpreter origInterpreter = InterpreterTest.getMockInterpreter(writer);
		Interpreter interpreter = Mockito.spy(origInterpreter);
		Mockito.doReturn(fileWriter).when(interpreter).getFileWriter(Mockito.eq("test.rls"));
		Predicate predicate = Expressions.makePredicate("p", 1);
		Term term = Expressions.makeAbstractConstant("a");
		Fact fact = Expressions.makeFact(predicate, term);
		interpreter.getKnowledgeBase().addStatement(fact);

		Command command = interpreter.parseCommand("@export KB \"test.rls\" .");
		interpreter.runCommand(command);

		StringWriter anotherWriter = new StringWriter();
		interpreter.getKnowledgeBase().writeKnowledgeBase(anotherWriter);

		assertEquals("export", command.getName());
		assertEquals(2, command.getArguments().size());
		assertEquals(anotherWriter.toString(), fileWriter.toString());
	}

	@Test(expected = CommandExecutionException.class)
	public void correctUseKbIoException_failse() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter origInterpreter = InterpreterTest.getMockInterpreter(writer);
		Interpreter interpreter = Mockito.spy(origInterpreter);
		Mockito.doThrow(FileNotFoundException.class).when(interpreter).getFileWriter(Mockito.eq("test.rls"));

		Command command = interpreter.parseCommand("@export KB \"test.rls\" .");
		interpreter.runCommand(command);
	}

	@Test
	public void correctUseInferences_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		StringWriter fileWriter = new StringWriter();
		Interpreter origInterpreter = InterpreterTest.getMockInterpreter(writer);
		Interpreter interpreter = Mockito.spy(origInterpreter);
		Mockito.doReturn(fileWriter).when(interpreter).getFileWriter(Mockito.eq("test.rls"));
		Mockito.when(interpreter.getReasoner().writeInferences(Mockito.any(Writer.class)))
				.thenReturn(Correctness.SOUND_BUT_INCOMPLETE);

		Command command = interpreter.parseCommand("@export INFERENCES \"test.rls\" .");
		interpreter.runCommand(command);

		assertEquals("export", command.getName());
		assertEquals(2, command.getArguments().size());
		assertTrue(writer.toString().contains(Correctness.SOUND_BUT_INCOMPLETE.toString()));
	}

	@Test(expected = CommandExecutionException.class)
	public void correctUseInferencesIoException_fails()
			throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter origInterpreter = InterpreterTest.getMockInterpreter(writer);
		Interpreter interpreter = Mockito.spy(origInterpreter);
		Mockito.doThrow(FileNotFoundException.class).when(interpreter).getFileWriter(Mockito.eq("test.rls"));

		Command command = interpreter.parseCommand("@export INFERENCES \"test.rls\" .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void unknonwTask_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@export UNKNOWN \"file.csv\" .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongFirstArgumentType_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@export \"string\" \"file.rls\".");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongSecondArgumentType_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@export KB 123 .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCount_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@export KB \"file.rls\" more .");
		interpreter.runCommand(command);
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		CommandInterpreter commandInterpreter = new ExportCommandInterpreter();
		InterpreterTest.checkHelpFormat(commandInterpreter, interpreter, writer);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		CommandInterpreter commandInterpreter = new ExportCommandInterpreter();
		InterpreterTest.checkSynopsisFormat(commandInterpreter);
	}

}
