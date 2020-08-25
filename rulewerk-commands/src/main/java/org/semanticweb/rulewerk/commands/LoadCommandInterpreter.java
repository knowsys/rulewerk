package org.semanticweb.rulewerk.commands;

import java.io.File;

/*-
 * #%L
 * Rulewerk Core Components
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

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.owlapi.OwlToRulesConverter;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * Interpreter for the load command.
 * 
 * @author Markus Kroetzsch
 *
 */
public class LoadCommandInterpreter implements CommandInterpreter {

	static final String TASK_RLS = "RULES";
	static final String TASK_OWL = "OWL";
	static final String TASK_RDF = "RDF";

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		String task;
		int pos = 0;
		if (command.getArguments().size() > 0 && command.getArguments().get(0).fromTerm().isPresent()
				&& command.getArguments().get(0).fromTerm().get().getType() == TermType.ABSTRACT_CONSTANT) {
			task = Interpreter.extractNameArgument(command, 0, "task");
			Interpreter.validateArgumentCount(command, 2);
			pos++;
		} else {
			task = TASK_RLS;
			Interpreter.validateArgumentCount(command, 1);
		}

		String fileName = Interpreter.extractStringArgument(command, pos, "filename");

		int countRulesBefore = interpreter.getKnowledgeBase().getRules().size();
		int countFactsBefore = interpreter.getKnowledgeBase().getFacts().size();

		if (TASK_RLS.equals(task)) {
			loadKb(interpreter, fileName);
		} else if (TASK_OWL.equals(task)) {
			loadOwl(interpreter, fileName);
		} else {
			throw new CommandExecutionException("Unknown task " + task + ". Should be " + TASK_RLS + " or " + TASK_OWL);
		}

		interpreter.printNormal(
				"Loaded " + (interpreter.getKnowledgeBase().getFacts().size() - countFactsBefore) + " new fact(s) and "
						+ (interpreter.getKnowledgeBase().getRules().size() - countRulesBefore) + " new rule(s).\n");

	}

	private void loadKb(Interpreter interpreter, String fileName) throws CommandExecutionException {
		try {
			InputStream inputStream = interpreter.getFileInputStream(fileName);
			RuleParser.parseInto(interpreter.getKnowledgeBase(), inputStream);
		} catch (FileNotFoundException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		} catch (ParsingException e) {
			throw new CommandExecutionException("Error parsing file: " + e.getMessage(), e);
		}
	}

	private void loadOwl(Interpreter interpreter, String fileName) throws CommandExecutionException {
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology;
		try {
			ontology = ontologyManager.loadOntologyFromOntologyDocument(new File(fileName));
		} catch (OWLOntologyCreationException e) {
			throw new CommandExecutionException("Problem loading OWL ontology: " + e.getMessage(), e);
		}
		interpreter.printNormal(
				"Found OWL ontology with " + ontology.getLogicalAxiomCount() + " logical OWL axioms ...\n");

		final OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter(false);
		owlToRulesConverter.addOntology(ontology);
		if (owlToRulesConverter.getUnsupportedAxiomsCount() > 0) {
			interpreter.printImportant("Warning: Some OWL axioms could not be converted to rules.\n");
			owlToRulesConverter.getUnsupportedAxiomsSample()
					.forEach((owlAxiom) -> interpreter.printNormal(owlAxiom.toString() + "\n"));
			if (owlToRulesConverter.getUnsupportedAxiomsSample().size() < owlToRulesConverter
					.getUnsupportedAxiomsCount()) {
				interpreter.printNormal("...\n");
			}
			interpreter.printNormal("Encountered " + owlToRulesConverter.getUnsupportedAxiomsCount()
					+ " unsupported logical axioms in total.\n");
		}

		interpreter.getKnowledgeBase().addStatements(owlToRulesConverter.getRules());
		interpreter.getKnowledgeBase().addStatements(owlToRulesConverter.getFacts());
	}

	@Override
	public void printHelp(String commandName, Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " [TASK] <file>\n" //
				+ " file: path to the file to load\n" //
				+ " TASK: optional; one of RULES (default) or OWL:\n" //
				+ "       RULES to load a knowledge base in Rulewerk rls format\n" //
				+ "       OWL to load an OWL ontology and convert it to rules\n");
	}

	@Override
	public String getSynopsis() {
		return "load a knowledge base from file (in Rulewerk rls format)";
	}

}
