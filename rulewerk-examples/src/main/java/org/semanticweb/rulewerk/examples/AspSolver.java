package org.semanticweb.rulewerk.examples;

/*-
 * #%L
 * Rulewerk Examples
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

import org.apache.commons.cli.*;
import org.semanticweb.rulewerk.asp.implementation.AspReasonerImpl;
import org.semanticweb.rulewerk.asp.implementation.KnowledgeBaseAnalyser;
import org.semanticweb.rulewerk.asp.model.AnswerSet;
import org.semanticweb.rulewerk.asp.model.AnswerSetIterator;
import org.semanticweb.rulewerk.asp.model.AspReasoner;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This example can be used to create a basic ASP solver.
 */
public class AspSolver {

	public static void main(final String[] args) {
		CommandLine line;
		String[] programs;
		boolean cautious = false;
		boolean groundOnly = false;
		String groundingFile = null;
		int limit = 0;

		// Prepare command line options
		Options options = new Options();
		options.addOption(Option.builder("n").hasArg().numberOfArgs(1).desc("Maximal number of answer sets that should be returned").build());
		options.addOption(Option.builder("e").longOpt("enum-mode").hasArg().numberOfArgs(1).desc("Enumeration mode:\n" +
			"\tcautious:\t Compute cautious consequences").build());
		options.addOption(Option.builder("f").longOpt("filter").hasArg().numberOfArgs(2).desc("Filter answer set for given predicate").build());
		options.addOption(Option.builder("g").longOpt("ground").hasArg().desc("Compute the (aspif) grounding only").build());

		// Parse command line arguments
		CommandLineParser parser = new DefaultParser();
		try {
			// parse the command line arguments
			line = parser.parse(options, args);
			programs = line.getArgs();
			if (line.hasOption("e")) {
				if (line.getOptionValue("e").equals("cautious")) {
					cautious = true;
				} else {
					System.out.println("Enumeration mode supports only cautious reasoning: -e cautious");
					return;
				}
			}
			if (line.hasOption("g")) {
				groundOnly = true;
				groundingFile = line.getOptionValue("g");

			}
			if (line.hasOption("n")) {
				try {
					limit = Integer.parseInt(line.getOptionValue("n"));
				} catch (NumberFormatException numberFormatException) {
					System.out.println("Limiting the number of answer sets requires a non-negative integer, e.g., -n 4");
					return;
				}
				if (limit < 0) {
					System.out.println("Limiting the number of answer sets requires a non-negative integer, e.g., -n 4");
					return;
				}
			}
		} catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			return;
		}

		ExamplesUtils.configureLogging();
		System.out.print("Load rules and facts: ");
		KnowledgeBase kb = new KnowledgeBase();
		try {
			for (String program : programs) {
				RuleParser.parseInto(kb, new FileInputStream(program));
			}
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " +  e.getMessage());
			return;
		}
		System.out.println("done");

		// Original rules
		System.out.println("Original rules: ");
		kb.getRules().forEach(System.out::println);
		System.out.println();

		AspReasoner reasoner = new AspReasonerImpl(kb);
		reasoner.setLogFile("vlog.log");
		reasoner.setLogLevel(LogLevel.DEBUG);

		// Analysis of knowledge base
		System.out.println("Over-approximated predicates: ");
		KnowledgeBaseAnalyser knowledgeBaseAnalyser = new KnowledgeBaseAnalyser(reasoner.getDatalogKnowledgeBase());
		knowledgeBaseAnalyser.getOverApproximatedPredicates().forEach(System.out::println);
		System.out.println();

		// Over-approximated rules
		System.out.println("Transformed rules: ");
		reasoner.getDatalogKnowledgeBase().getRules().forEach(System.out::println);
		System.out.println();

		if (cautious) {
			System.out.println("Cautious reasoning:");
			for (Predicate predicate : kb.getPredicates()) {
				System.out.println("Results for predicate: " + predicate);
				List<Term> terms = new ArrayList<>();
				for (int i=0; i<predicate.getArity(); i++) {
					terms.add(Expressions.makeUniversalVariable("X" + i));
				}
				QueryResultIterator iterator = reasoner.answerQuery(new PositiveLiteralImpl(predicate, terms), true);
				int resultCount = 1;
				while (iterator.hasNext()) {
					System.out.println("Result " + resultCount + ": " + iterator.next());
					resultCount++;
				}
				System.out.println();
			}
		} else if (groundOnly) {
			Set<Predicate> predicates;
			if (line.hasOption("f")) {
				String name = line.getOptionValues("f")[0];
				int arity = Integer.parseInt(line.getOptionValues("f")[1]);
				predicates = new HashSet<>();
				predicates.add(Expressions.makePredicate(name, arity));
			} else {
				predicates = kb.getPredicates();
			}
			try {
				reasoner.groundToFile(groundingFile, predicates);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Searching for answer sets:");
			AnswerSetIterator iterator;
			try {
				iterator = reasoner.getAnswerSets(limit);
			} catch (IOException ioException) {
				System.out.println("An error occurred while computing the answer sets:");
				ioException.printStackTrace();
				return;
			}

			System.out.println("State: " + iterator.getReasoningState());
			int answerSetCount = 1;
			while (iterator.hasNext()) {
				System.out.println("Answer set " + answerSetCount++ + ":");
				AnswerSet answerSet = iterator.next();
				System.out.println("Overall literal count: " + answerSet.getLiterals().size());
				int resultCount = 1;
				Set<Literal> literals;
				if (line.hasOption("f")) {
					String name = line.getOptionValues("f")[0];
					int arity = Integer.parseInt(line.getOptionValues("f")[1]);
					literals = answerSet.getLiterals(Expressions.makePredicate(name, arity));
				} else {
					literals = answerSet.getLiterals();
				}
				for (Literal literal : literals) {
					System.out.print(literal + "\t");
					resultCount++;
					if (resultCount > 100) {
						break;
					}
				}
				System.out.println();
			}
		}
	}
}
