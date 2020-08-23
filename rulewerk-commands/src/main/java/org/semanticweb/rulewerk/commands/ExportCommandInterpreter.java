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

	static final String TASK_KB = "KB";
	static final String TASK_INFERENCES = "INFERENCES";

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		Interpreter.validateArgumentCount(command, 2);

		String task = Interpreter.extractNameArgument(command, 0, "task");
		String fileName = Interpreter.extractStringArgument(command, 1, "filename");

		if (TASK_KB.equals(task)) {
			exportKb(interpreter, fileName);
		} else if (TASK_INFERENCES.equals(task)) {
			exportInferences(interpreter, fileName);
		} else {
			throw new CommandExecutionException(
					"Unknown task " + task + ". Should be " + TASK_KB + " or " + TASK_INFERENCES);
		}

	}

	@Override
	public void printHelp(String commandName, Interpreter interpreter) {
		interpreter.printNormal("Usage: @" + commandName + " TASK \"filename\" .\n" //
				+ " TASK: what to export; can be KB or INFERENCES\n" //
				+ " \"filename\": string path export file (suggested extension: .rls)\n");
	}

	@Override
	public String getSynopsis() {
		return "export knowledgebase or inferences to a Rulewerk file";
	}

	private void exportInferences(Interpreter interpreter, String fileName) throws CommandExecutionException {
		Timer timer = new Timer("export");
		Correctness correctness;
		try (Writer writer = interpreter.getFileWriter(fileName)) {
			timer.start();
			correctness = interpreter.getReasoner().writeInferences(writer);
			timer.stop();
		} catch (IOException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		}

		interpreter.printNormal("Exported all inferences in " + timer.getTotalWallTime() / 1000000 + "ms ("
				+ timer.getTotalCpuTime() / 1000000 + "ms CPU time).");
		interpreter.printNormal(" This result is " + correctness + ".\n");
	}

	private void exportKb(Interpreter interpreter, String fileName) throws CommandExecutionException {
		Timer timer = new Timer("export");
		try (Writer writer = interpreter.getFileWriter(fileName)) {
			timer.start();
			interpreter.getKnowledgeBase().writeKnowledgeBase(writer);
			timer.stop();
		} catch (IOException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		}
		interpreter.printNormal("Exported knowledge base in " + timer.getTotalWallTime() / 1000000 + "ms ("
				+ timer.getTotalCpuTime() / 1000000 + "ms CPU time).\n");
	}

}
