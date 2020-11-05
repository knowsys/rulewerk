package org.semanticweb.rulewerk.asp.implementation;

/*-
 * #%L
 * Rulewerk ASP Components
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

import org.semanticweb.rulewerk.asp.model.AspSolver;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class Clasp implements AspSolver {

	private final int answerSetMaximum;
	private final boolean cautious;
	private final Integer timeout;

	private Process process;
	private BufferedReader reader;
	private BufferedWriter writer;

	/**
	 * Constructor. Creates a representation of a clasp process.
	 *
	 * @param cautious whether reasoning should be cautious
	 * @param answerSetMaximum determines the maximum number of answer sets (if this is set to 0, all answer sets are returned)
	 * @param timeout an optional timeout
	 *
	 * @throws IOException an IO exception
	 */
	public Clasp(boolean cautious, int answerSetMaximum, Integer timeout) throws IOException {
		this.answerSetMaximum = answerSetMaximum;
		this.cautious = cautious;
		this.timeout = timeout;
	}

	@Override
	public void exec() throws IOException {
		StringBuilder command = new StringBuilder("clasp");
		if (cautious) {
			command.append(" -e cautious");
		} else {
			command.append(" -n ").append(answerSetMaximum);
		}
		process = Runtime.getRuntime().exec(command.toString());
		writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	}

	/**
	 * Waits until underlying solver process returns. A timeout might be specified.
	 *
	 * @throws InterruptedException exception if the solving process was interrupted
	 */
	private void waitFor() throws InterruptedException {
		if (timeout == null) {
			process.waitFor();
		} else {
			process.waitFor(timeout, TimeUnit.SECONDS);
		}
	}

	@Override
	public void close() {
		process.destroy();
	}

	@Override
	public BufferedWriter getWriterToSolver() {
		return writer;
	}

	@Override
	public BufferedReader getReaderFromSolver() {
		return reader;
	}

	@Override
	public void solve() throws IOException, InterruptedException {
		writer.close();
		waitFor();
	}
}
