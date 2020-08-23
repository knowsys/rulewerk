package org.semanticweb.rulewerk.commands;

import java.io.IOException;

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

import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Terms;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.LiteralQueryResultPrinter;
import org.semanticweb.rulewerk.core.reasoner.QueryAnswerCount;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Timer;

public class QueryCommandInterpreter implements CommandInterpreter {

	public static Term KEYWORD_LIMIT = Expressions.makeAbstractConstant("LIMIT");
	public static Term KEYWORD_COUNT = Expressions.makeAbstractConstant("COUNT");
	public static Term KEYWORD_TOFILE = Expressions.makeAbstractConstant("EXPORTCSV");

	private PositiveLiteral queryLiteral;
	private int limit;
	private boolean doCount;
	private String csvFile;

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		processArguments(command.getArguments());

		if (doCount) {
			printCountQueryResults(interpreter);
		} else if (csvFile == null) {
			printQueryResults(interpreter);
		} else {
			exportQueryResults(interpreter);
		}
	}

	@Override
	public String getHelp(String commandName) {
		return "Usage: @" + commandName + " [COUNT] <query literal> [LIMIT <limit>] [EXPORTCSV <filename>] .\n"
				+ " query literal: positive literal, possibly with ?queryVariables\n"
				+ " limit: maximal number of results to be shown\n"
				+ " filename: string path to CSV file for exporting query results";
	}

	@Override
	public String getSynopsis() {
		return "print or export query results";
	}

	private void processArguments(List<Argument> arguments) throws CommandExecutionException {
		int pos = 0;
		limit = -1;
		doCount = false;
		csvFile = null;

		if (arguments.size() > 0 && KEYWORD_COUNT.equals(arguments.get(0).fromTerm().orElse(null))) {
			doCount = true;
			pos++;
		}

		if (arguments.size() > pos && arguments.get(pos).fromPositiveLiteral().isPresent()) {
			queryLiteral = arguments.get(pos).fromPositiveLiteral().get();
			pos++;
		} else {
			throw new CommandExecutionException("A query literal must be given.");
		}

		while (arguments.size() > pos) {
			if (arguments.size() > pos + 1 && KEYWORD_LIMIT.equals(arguments.get(pos).fromTerm().orElse(null))
					&& arguments.get(pos + 1).fromTerm().isPresent()) {
				try {
					limit = Terms.extractInt(arguments.get(pos + 1).fromTerm().get());
					pos += 2;
				} catch (IllegalArgumentException e) {
					throw new CommandExecutionException(
							"Invalid limit given: " + arguments.get(pos + 1).fromTerm().get());
				}
			} else if (arguments.size() > pos + 1 && KEYWORD_TOFILE.equals(arguments.get(pos).fromTerm().orElse(null))
					&& arguments.get(pos + 1).fromTerm().isPresent()) {
				try {
					csvFile = Terms.extractString(arguments.get(pos + 1).fromTerm().get());
					pos += 2;
				} catch (IllegalArgumentException e) {
					throw new CommandExecutionException(
							"Invalid filename given: " + arguments.get(pos + 1).fromTerm().get());
				}
			} else {
				throw new CommandExecutionException("Unrecognized arguments");
			}
		}
	}

	private void printCountQueryResults(Interpreter interpreter) throws CommandExecutionException {
		if (limit != -1) {
			throw new CommandExecutionException("LIMIT not supported with COUNT");
		}
		if (csvFile != null) {
			throw new CommandExecutionException("COUNT results cannot be exported to CSV");
		}

		Timer timer = new Timer("query");
		timer.start();
		QueryAnswerCount count = interpreter.getReasoner().countQueryAnswers(queryLiteral);
		timer.stop();

		interpreter.printNormal(String.valueOf(count.getCount()) + "\n");
		interpreter.printNormal("Answered in " + timer.getTotalCpuTime() / 1000000 + "ms.");
		interpreter.printNormal(" This result is " + count.getCorrectness() + ".\n");
	}

	private void printQueryResults(Interpreter interpreter) throws CommandExecutionException {
		LiteralQueryResultPrinter printer = new LiteralQueryResultPrinter(queryLiteral, interpreter.getWriter(),
				interpreter.getKnowledgeBase().getPrefixDeclarationRegistry());

		Timer timer = new Timer("query");
		timer.start();
		try (final QueryResultIterator answers = interpreter.getReasoner().answerQuery(queryLiteral, true)) {
			while (printer.getResultCount() != limit && answers.hasNext()) {
				printer.write(answers.next());
			}
			timer.stop();

			if (printer.isBooleanQuery()) {
				interpreter.printEmph(printer.hadResults() ? "true\n" : "false\n");
				interpreter.printNormal("Answered in " + timer.getTotalCpuTime() / 1000000 + "ms.");
			} else {
				interpreter.printNormal(
						printer.getResultCount() + " result(s) in " + timer.getTotalCpuTime() / 1000000 + "ms.");
			}
			interpreter.printNormal(" Results are " + answers.getCorrectness() + ".\n");
		} catch (IOException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		}
	}

	private void exportQueryResults(Interpreter interpreter) throws CommandExecutionException {
		if (limit != -1) {
			throw new CommandExecutionException("LIMIT not supported for CSV export");
		}

		Timer timer = new Timer("query");
		timer.start();
		Correctness correctness;
		try {
			correctness = interpreter.getReasoner().exportQueryAnswersToCsv(queryLiteral, csvFile, true);
		} catch (IOException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		}
		timer.stop();

		interpreter.printNormal("Written query result file in " + timer.getTotalCpuTime() / 1000000 + "ms.");
		interpreter.printNormal(" This result is " + correctness + ".\n");
	}
}
