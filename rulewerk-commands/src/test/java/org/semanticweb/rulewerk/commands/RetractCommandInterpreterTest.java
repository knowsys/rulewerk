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

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.parser.ParsingException;

public class RetractCommandInterpreterTest {

	@Test
	public void correctUse_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		Term a = Expressions.makeAbstractConstant("a");
		Term x = Expressions.makeUniversalVariable("X");
		Predicate p = Expressions.makePredicate("p", 1);
		Predicate q = Expressions.makePredicate("q", 1);
		Predicate r = Expressions.makePredicate("r", 1);
		Fact fact = Expressions.makeFact(p, a);
		Fact fact2 = Expressions.makeFact(q, a);
		PositiveLiteral headLiteral = Expressions.makePositiveLiteral(q, x);
		PositiveLiteral bodyLiteral = Expressions.makePositiveLiteral(r, x);
		Rule rule = Expressions.makeRule(headLiteral, bodyLiteral);
		interpreter.getKnowledgeBase().addStatement(fact);
		interpreter.getKnowledgeBase().addStatement(fact2);
		interpreter.getKnowledgeBase().addStatement(rule);

		Command command = interpreter.parseCommand("@retract p(a) q(?X) :- r(?X) .");
		interpreter.runCommand(command);
		List<Fact> facts = interpreter.getKnowledgeBase().getFacts();
		List<Rule> rules = interpreter.getKnowledgeBase().getRules();
		List<DataSourceDeclaration> dataSourceDeclarations = interpreter.getKnowledgeBase().getDataSourceDeclarations();

		assertEquals("retract", command.getName());
		assertEquals(2, command.getArguments().size());
		assertTrue(command.getArguments().get(0).fromPositiveLiteral().isPresent());
		assertTrue(command.getArguments().get(1).fromRule().isPresent());

		assertEquals(Arrays.asList(fact2), facts);
		assertTrue(rules.isEmpty());
		assertTrue(dataSourceDeclarations.isEmpty());
	}
	
	@Test
	public void correctUse_retractPredicate_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		Term a = Expressions.makeAbstractConstant("a");
		Term b = Expressions.makeAbstractConstant("b");
		Predicate p = Expressions.makePredicate("p", 1);
		Predicate q = Expressions.makePredicate("q", 1);
		Fact pa = Expressions.makeFact(p, a);
		Fact pb = Expressions.makeFact(p, b);
		Fact qa = Expressions.makeFact(q, a);

		interpreter.getKnowledgeBase().addStatement(pa);
		interpreter.getKnowledgeBase().addStatement(pb);
		interpreter.getKnowledgeBase().addStatement(qa);

		Command command = interpreter.parseCommand("@retract p[1] .");
		interpreter.runCommand(command);
		List<Fact> facts = interpreter.getKnowledgeBase().getFacts();
		List<Rule> rules = interpreter.getKnowledgeBase().getRules();
		List<DataSourceDeclaration> dataSourceDeclarations = interpreter.getKnowledgeBase().getDataSourceDeclarations();

		assertEquals(Arrays.asList(qa), facts);
		assertTrue(rules.isEmpty());
		assertTrue(dataSourceDeclarations.isEmpty());
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentTermNumber_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@retract 42 .");
		interpreter.runCommand(command);
	}
	
	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentTermStringNoPredicate_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@retract \"string\" .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentNonFact_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@retract p(?X) .");
		interpreter.runCommand(command);
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		CommandInterpreter commandInterpreter = new RetractCommandInterpreter();
		InterpreterTest.checkHelpFormat(commandInterpreter, interpreter, writer);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		CommandInterpreter commandInterpreter = new RetractCommandInterpreter();
		InterpreterTest.checkSynopsisFormat(commandInterpreter);
	}

}
