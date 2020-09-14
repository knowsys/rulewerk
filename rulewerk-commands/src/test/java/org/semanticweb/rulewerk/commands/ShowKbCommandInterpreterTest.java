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
import java.io.Writer;

import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.parser.ParsingException;

public class ShowKbCommandInterpreterTest {
	
	@Test
	public void correctUse_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		Predicate predicate = Expressions.makePredicate("p", 1);
		Term term = Expressions.makeAbstractConstant("a");
		Fact fact = Expressions.makeFact(predicate, term);
		interpreter.getKnowledgeBase().addStatement(fact);

		Command command = interpreter.parseCommand("@showkb .");
		interpreter.runCommand(command);

		StringWriter anotherWriter = new StringWriter();
		interpreter.getKnowledgeBase().writeKnowledgeBase(anotherWriter);

		assertEquals("showkb", command.getName());
		assertEquals(0, command.getArguments().size());
		assertEquals(writer.toString(), anotherWriter.toString());
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCount_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@showkb p(?X) .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void ioError_fails() throws ParsingException, CommandExecutionException, IOException {
		Writer writer = Mockito.mock(Writer.class);
		Mockito.doThrow(IOException.class).when(writer).write(Mockito.anyString());
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		Predicate predicate = Expressions.makePredicate("p", 1);
		Term term = Expressions.makeAbstractConstant("a");
		Fact fact = Expressions.makeFact(predicate, term);
		interpreter.getKnowledgeBase().addStatement(fact);

		Command command = interpreter.parseCommand("@showkb .");
		interpreter.runCommand(command);
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		CommandInterpreter commandInterpreter = new ShowKbCommandInterpreter();
		InterpreterTest.checkHelpFormat(commandInterpreter, interpreter, writer);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		CommandInterpreter commandInterpreter = new ShowKbCommandInterpreter();
		InterpreterTest.checkSynopsisFormat(commandInterpreter);
	}

}
