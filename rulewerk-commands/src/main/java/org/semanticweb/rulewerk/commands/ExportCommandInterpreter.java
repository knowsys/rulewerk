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

import java.io.IOException;
import java.io.Writer;

import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.Timer;

public class ExportCommandInterpreter implements CommandInterpreter {

	public static final String TASK_KB = "KB";
	public static final String TASK_INFERENCES = "INFERENCES";

	@Override
	public void run(final Command command, final Interpreter interpreter) throws CommandExecutionException {
		Interpreter.validateArgumentCount(command, 2);

		final String task = Interpreter.extractNameArgument(command, 0, "task");
		final String fileName = Interpreter.extractStringArgument(command, 1, "filename");

		if (TASK_KB.equals(task)) {
			this.exportKb(interpreter, fileName);
		} else if (TASK_INFERENCES.equals(task)) {
			this.exportInferences(interpreter, fileName);
		} else {
			throw new CommandExecutionException(
					"Unknown task " + task + ". Should be " + TASK_KB + " or " + TASK_INFERENCES);
		}

	}

	@Override
	public void printHelp(final String commandName, final Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " TASK \"file\" .\n" //
				+ " TASK: what to export; can be KB or INFERENCES\n" //
				+ " \"file\": path to export file (suggested extension: .rls), enclosed in quotes\n");
	}

	@Override
	public String getSynopsis() {
		return "export knowledgebase or inferences to a Rulewerk file";
	}

	private void exportInferences(final Interpreter interpreter, final String fileName) throws CommandExecutionException {
		final Timer timer = new Timer("export");
		Correctness correctness;
		try (Writer writer = interpreter.getFileWriter(fileName)) {
			timer.start();
			correctness = interpreter.getReasoner().writeInferences(writer);
			timer.stop();
		} catch (final IOException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		}

		interpreter.printNormal("Exported all inferences in " + timer.getTotalWallTime() / 1000000 + "ms ("
				+ timer.getTotalCpuTime() / 1000000 + "ms CPU time).");
		interpreter.printNormal(" This result is " + correctness + ".\n");
	}

	private void exportKb(final Interpreter interpreter, final String fileName) throws CommandExecutionException {
		final Timer timer = new Timer("export");
		try (Writer writer = interpreter.getFileWriter(fileName)) {
			timer.start();
			interpreter.getKnowledgeBase().writeKnowledgeBase(writer);
			timer.stop();
		} catch (final IOException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		}
		interpreter.printNormal("Exported knowledge base in " + timer.getTotalWallTime() / 1000000 + "ms ("
				+ timer.getTotalCpuTime() / 1000000 + "ms CPU time).\n");
	}

}
