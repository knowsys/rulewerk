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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.parser.ParsingException;

public class LoadCommandInterpreterTest {

	@Test
	public void correctUse_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		InputStream inputStream = new ByteArrayInputStream("p(a) .".getBytes(StandardCharsets.UTF_8));
		Interpreter origInterpreter = InterpreterTest.getMockInterpreter(writer);
		Interpreter interpreter = Mockito.spy(origInterpreter);
		Mockito.doReturn(inputStream).when(interpreter).getFileInputStream(Mockito.eq("loadtest.rls"));

		Predicate predicate = Expressions.makePredicate("p", 1);
		Term term = Expressions.makeAbstractConstant("a");
		Fact fact = Expressions.makeFact(predicate, term);

		Command command = interpreter.parseCommand("@load 'loadtest.rls' .");
		interpreter.runCommand(command);

		assertEquals("load", command.getName());
		assertEquals(1, command.getArguments().size());
		assertTrue(command.getArguments().get(0).fromTerm().isPresent());

		assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
	}

	@Test
	public void correctUseWithRulesTask_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		InputStream inputStream = new ByteArrayInputStream("p(a) .".getBytes(StandardCharsets.UTF_8));
		Interpreter origInterpreter = InterpreterTest.getMockInterpreter(writer);
		Interpreter interpreter = Mockito.spy(origInterpreter);
		Mockito.doReturn(inputStream).when(interpreter).getFileInputStream(Mockito.eq("loadtest.rls"));

		Predicate predicate = Expressions.makePredicate("p", 1);
		Term term = Expressions.makeAbstractConstant("a");
		Fact fact = Expressions.makeFact(predicate, term);

		Command command = interpreter.parseCommand("@load RULES 'loadtest.rls' .");
		interpreter.runCommand(command);

		assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
	}

	@Test(expected = CommandExecutionException.class)
	public void correctUseParseError_fails() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		InputStream inputStream = new ByteArrayInputStream("not parsable".getBytes(StandardCharsets.UTF_8));
		Interpreter origInterpreter = InterpreterTest.getMockInterpreter(writer);
		Interpreter interpreter = Mockito.spy(origInterpreter);
		Mockito.doReturn(inputStream).when(interpreter).getFileInputStream(Mockito.eq("loadtest.rls"));

		Command command = interpreter.parseCommand("@load 'loadtest.rls' .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void correctUseFileNotFoundError_fails() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter origInterpreter = InterpreterTest.getMockInterpreter(writer);
		Interpreter interpreter = Mockito.spy(origInterpreter);
		Mockito.doThrow(FileNotFoundException.class).when(interpreter).getFileInputStream(Mockito.eq("loadtest.rls"));

		Command command = interpreter.parseCommand("@load 'loadtest.rls' .");
		interpreter.runCommand(command);
	}

	@Test
	public void correctUseWithOwlTask_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Predicate predicate = Expressions.makePredicate("http://example.org/C", 1);
		Term term = Expressions.makeAbstractConstant("http://example.org/a");
		Fact fact = Expressions.makeFact(predicate, term);

		Command command = interpreter.parseCommand("@load OWL 'src/test/data/loadtest.owl' .");
		interpreter.runCommand(command);

		assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
	}

	@Test
	public void correctUseWithOwlTask_UnsupportedAxioms_succeeds()
			throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Predicate predicate = Expressions.makePredicate("http://example.org/C", 1);
		Term term = Expressions.makeAbstractConstant("http://example.org/a");
		Fact fact = Expressions.makeFact(predicate, term);

		Command command = interpreter.parseCommand("@load OWL 'src/test/data/loadtest-unsupported.owl' .");
		interpreter.runCommand(command);

		assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
		// OUtput mentions the offending axiom in Functional-Style Syntax:
		assertTrue(writer.toString().contains("InverseFunctionalObjectProperty(<http://example.org/p>)"));
	}

	@Test(expected = CommandExecutionException.class)
	public void correctUseWithOwlTask_malformedOwl_fails()
			throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load OWL 'src/test/data/loadtest-fails.owl' .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void correctUseWithOwlTask_missingFile_fails()
			throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load OWL 'src/test/data/file-does-not-exist.owl' .");
		interpreter.runCommand(command);
	}

	@Test
	public void correctUseWithRdfTask_Nt_succeeds()
			throws ParsingException, CommandExecutionException, IOException, PrefixDeclarationException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Predicate predicate = Expressions.makePredicate("TRIPLE", 3);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termb = Expressions.makeAbstractConstant("http://example.org/b");
		Term termc = Expressions.makeAbstractConstant("http://example.org/c");
		Fact fact = Expressions.makeFact(predicate, terma, termb, termc);

		Command command = interpreter.parseCommand("@load RDF 'src/test/data/loadtest.nt' .");
		interpreter.runCommand(command);

		assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
	}

	@Test
	public void correctUseWithRdfTask_NtCustomPredicate_succeeds()
			throws ParsingException, CommandExecutionException, IOException, PrefixDeclarationException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Predicate predicate = Expressions.makePredicate("http://example.org/mytriple", 3);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termb = Expressions.makeAbstractConstant("http://example.org/b");
		Term termc = Expressions.makeAbstractConstant("http://example.org/c");
		Fact fact = Expressions.makeFact(predicate, terma, termb, termc);

		Command command = interpreter
				.parseCommand("@load RDF 'src/test/data/loadtest.nt' <http://example.org/mytriple>.");
		interpreter.runCommand(command);

		assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
	}

	@Test
	public void correctUseWithRdfTask_NtABoxLoading_succeeds()
			throws ParsingException, CommandExecutionException, IOException, PrefixDeclarationException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Predicate predicate = Expressions.makePredicate("http://example.org/b", 2);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termc = Expressions.makeAbstractConstant("http://example.org/c");
		Fact fact = Expressions.makeFact(predicate, terma, termc);

		Command command = interpreter.parseCommand("@load RDF 'src/test/data/loadtest.nt' ABOX.");
		interpreter.runCommand(command);

		assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
	}

	@Test
	public void correctUseWithRdfTask_Turtle_succeeds()
			throws ParsingException, CommandExecutionException, IOException, PrefixDeclarationException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Predicate predicate = Expressions.makePredicate("TRIPLE", 3);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termb = Expressions.makeAbstractConstant("http://example.org/b");
		Term termc = Expressions.makeAbstractConstant("http://example.org/c");
		Fact fact = Expressions.makeFact(predicate, terma, termb, termc);

		Command command = interpreter.parseCommand("@load RDF 'src/test/data/loadtest.ttl' .");
		interpreter.runCommand(command);

		assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
		assertEquals("http://example.org/", interpreter.getKnowledgeBase().getPrefixIri(":"));
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
	}

	@Test
	public void correctUseWithRdfTask_RdfXml_succeeds()
			throws ParsingException, CommandExecutionException, IOException, PrefixDeclarationException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Predicate predicate = Expressions.makePredicate("TRIPLE", 3);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termb = Expressions.makeAbstractConstant("http://example.org/b");
		Term termc = Expressions.makeAbstractConstant("http://example.org/c");
		Fact fact = Expressions.makeFact(predicate, terma, termb, termc);

		Command command = interpreter.parseCommand("@load RDF 'src/test/data/loadtest.rdf' .");
		interpreter.runCommand(command);

		assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
		assertEquals("http://example.org/", interpreter.getKnowledgeBase().getPrefixIri("eg:"));
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
	}

	@Test(expected = CommandExecutionException.class)
	public void correctUseWithRdfTask_malformedRdf_fails()
			throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load RDF 'src/test/data/loadtest-fails.owl' .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void correctUseWithRdfTask_missingFile_fails()
			throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load RDF 'src/test/data/file-does-not-exist.rdf' .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCount_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load .");
		interpreter.runCommand(command);
	}
	
	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCountWithOptional_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load OWL .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongRdfPredicateTermType_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load RDF \"file.nt\" \"string\" .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongRdfPredicateArgumentType_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load RDF \"file.nt\" p(a) .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentType_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load p(a) .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongTask_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@load UNKOWNTASK 'loadtest.rls' .");
		interpreter.runCommand(command);
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		CommandInterpreter commandInterpreter = new LoadCommandInterpreter();
		InterpreterTest.checkHelpFormat(commandInterpreter, interpreter, writer);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		CommandInterpreter commandInterpreter = new LoadCommandInterpreter();
		InterpreterTest.checkSynopsisFormat(commandInterpreter);
	}

}
