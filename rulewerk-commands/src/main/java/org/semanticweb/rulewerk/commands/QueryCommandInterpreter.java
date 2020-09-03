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
	public void run(final Command command, final Interpreter interpreter) throws CommandExecutionException {
		this.processArguments(command.getArguments());

		if (this.doCount) {
			this.printCountQueryResults(interpreter);
		} else if (this.csvFile == null) {
			this.printQueryResults(interpreter);
		} else {
			this.exportQueryResults(interpreter);
		}
	}

	@Override
	public void printHelp(final String commandName, final Interpreter interpreter) {
		interpreter.printNormal(
				"Usage: @" + commandName + " [COUNT] <query literal> [LIMIT <limit>] [EXPORTCSV <\"file\">] .\n"
						+ " query literal: positive literal, possibly with ?queryVariables\n"
						+ " limit: maximal number of results to be shown\n"
						+ " \"file\": path to CSV file for exporting query results, enclosed in quotes\n");
	}

	@Override
	public String getSynopsis() {
		return "print or export query results";
	}

	private void processArguments(final List<Argument> arguments) throws CommandExecutionException {
		int pos = 0;
		this.limit = -1;
		this.doCount = false;
		this.csvFile = null;

		if (arguments.size() > 0 && KEYWORD_COUNT.equals(arguments.get(0).fromTerm().orElse(null))) {
			this.doCount = true;
			pos++;
		}

		if (arguments.size() > pos && arguments.get(pos).fromPositiveLiteral().isPresent()) {
			this.queryLiteral = arguments.get(pos).fromPositiveLiteral().get();
			pos++;
		} else {
			throw new CommandExecutionException("A query literal must be given.");
		}

		while (arguments.size() > pos) {
			if (arguments.size() > pos + 1 && KEYWORD_LIMIT.equals(arguments.get(pos).fromTerm().orElse(null))
					&& arguments.get(pos + 1).fromTerm().isPresent()) {
				try {
					this.limit = Terms.extractInt(arguments.get(pos + 1).fromTerm().get());
					pos += 2;
				} catch (final IllegalArgumentException e) {
					throw new CommandExecutionException(
							"Invalid limit given: " + arguments.get(pos + 1).fromTerm().get());
				}
			} else if (arguments.size() > pos + 1 && KEYWORD_TOFILE.equals(arguments.get(pos).fromTerm().orElse(null))
					&& arguments.get(pos + 1).fromTerm().isPresent()) {
				try {
					this.csvFile = Terms.extractString(arguments.get(pos + 1).fromTerm().get());
					pos += 2;
				} catch (final IllegalArgumentException e) {
					throw new CommandExecutionException(
							"Invalid filename given: " + arguments.get(pos + 1).fromTerm().get());
				}
			} else {
				throw new CommandExecutionException("Unrecognized arguments");
			}
		}
	}

	private void printCountQueryResults(final Interpreter interpreter) throws CommandExecutionException {
		if (this.limit != -1) {
			throw new CommandExecutionException("LIMIT not supported with COUNT");
		}
		if (this.csvFile != null) {
			throw new CommandExecutionException("COUNT results cannot be exported to CSV");
		}

		final Timer timer = new Timer("query");
		timer.start();
		final QueryAnswerCount count = interpreter.getReasoner().countQueryAnswers(this.queryLiteral);
		timer.stop();

		interpreter.printNormal(String.valueOf(count.getCount()) + "\n");
		interpreter.printNormal("Answered in " + timer.getTotalCpuTime() / 1000000 + "ms.");
		interpreter.printNormal(" This result is " + count.getCorrectness() + ".\n");
	}

	private void printQueryResults(final Interpreter interpreter) throws CommandExecutionException {
		final LiteralQueryResultPrinter printer = new LiteralQueryResultPrinter(this.queryLiteral, interpreter.getWriter(),
				interpreter.getKnowledgeBase().getPrefixDeclarationRegistry());

		final Timer timer = new Timer("query");
		timer.start();
		try (final QueryResultIterator answers = interpreter.getReasoner().answerQuery(this.queryLiteral, true)) {
			while (printer.getResultCount() != this.limit && answers.hasNext()) {
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
		} catch (final IOException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		}
	}

	private void exportQueryResults(final Interpreter interpreter) throws CommandExecutionException {
		if (this.limit != -1) {
			throw new CommandExecutionException("LIMIT not supported for CSV export");
		}

		final Timer timer = new Timer("query");
		timer.start();
		Correctness correctness;
		try {
			correctness = interpreter.getReasoner().exportQueryAnswersToCsv(this.queryLiteral, this.csvFile, true);
		} catch (final IOException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		}
		timer.stop();

		interpreter.printNormal("Written query result file in " + timer.getTotalCpuTime() / 1000000 + "ms.");
		interpreter.printNormal(" This result is " + correctness + ".\n");
	}
}
