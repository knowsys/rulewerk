package org.semanticweb.rulewerk.commands;

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
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Timer;

public class QueryCommandInterpreter implements CommandInterpreter {

	public static Term KEYWORD_LIMIT = Expressions.makeAbstractConstant("LIMIT");

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {

		List<Argument> arguments = command.getArguments();
		PositiveLiteral literal;

		if (arguments.size() > 0 && arguments.get(0).fromPositiveLiteral().isPresent()) {
			literal = arguments.get(0).fromPositiveLiteral().get();
		} else {
			throw new CommandExecutionException("First argument must be a query literal.");
		}

		int limit = -1;
		if (arguments.size() == 3 && KEYWORD_LIMIT.equals(arguments.get(1).fromTerm().orElse(null))
				&& arguments.get(2).fromTerm().isPresent()) {
			try {
				limit = Terms.extractInt(arguments.get(2).fromTerm().get());
			} catch (IllegalArgumentException e) {
				throw new CommandExecutionException("Invalid limit given: " + arguments.get(3).fromTerm().get());
			}
		} else if (arguments.size() != 1) {
			throw new CommandExecutionException("Unrecognized arguments");
		}

		Timer timer = new Timer("query");
		timer.start();
		try (final QueryResultIterator answers = interpreter.getReasoner().answerQuery(literal, true)) {
			int count = 0;
			while (count != limit && answers.hasNext()) {
				interpreter.getOut().println(" " + answers.next());
				count++;
			}
			timer.stop();
			interpreter.getOut().println(count + " result(s) in " + timer.getTotalCpuTime() / 1000000
					+ "ms. Results are " + answers.getCorrectness() + ".");
		}
	}

	@Override
	public String getHelp(String commandName) {
		return "Usage: @" + commandName + " <query literal> [LIMIT <limit>] .\n"
				+ " query literal: positive literal; may use ?queryVariables and ?existentialVariables\n"
				+ " limit: maximal number of results to be shown";
	}

	@Override
	public String getSynopsis() {
		return "print results to queries";
	}
}
