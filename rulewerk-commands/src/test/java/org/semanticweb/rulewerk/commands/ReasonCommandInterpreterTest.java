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

import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.parser.ParsingException;

public class ReasonCommandInterpreterTest {

	@Test
	public void correctUse_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		Mockito.when(interpreter.getReasoner().getCorrectness()).thenReturn(Correctness.SOUND_BUT_INCOMPLETE);
		Mockito.when(interpreter.getReasoner().reason()).thenAnswer(I -> {
			Mockito.when(interpreter.getReasoner().getCorrectness()).thenReturn(Correctness.SOUND_AND_COMPLETE);
			return true;
		});

		Command command = interpreter.parseCommand("@reason .");
		interpreter.runCommand(command);

		assertEquals(Correctness.SOUND_AND_COMPLETE, interpreter.getReasoner().getCorrectness());
	}
	
	@Test(expected = CommandExecutionException.class)
	public void correctUseReasonerException_fails() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		Mockito.when(interpreter.getReasoner().reason()).thenThrow(IOException.class);

		Command command = interpreter.parseCommand("@reason .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCount_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@reason p(?X) .");
		interpreter.runCommand(command);
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		CommandInterpreter commandInterpreter = new ReasonCommandInterpreter();
		InterpreterTest.checkHelpFormat(commandInterpreter, interpreter, writer);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		CommandInterpreter commandInterpreter = new ReasonCommandInterpreter();
		InterpreterTest.checkSynopsisFormat(commandInterpreter);
	}

}
