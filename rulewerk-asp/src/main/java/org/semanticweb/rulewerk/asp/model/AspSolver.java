package org.semanticweb.rulewerk.asp.model;

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

import java.io.*;

public interface AspSolver extends AutoCloseable {

	/**
	 * Gets the writer which writes to the solver.
	 *
	 * @return a buffered writer
	 */
	BufferedWriter getWriterToSolver();

	/**
	 * Gets the reader to which the solver writes its results.
	 *
	 * @return a buffered reader
	 */
	BufferedReader getReaderFromSolver();

	/**
	 * Executes the solver process.
	 *
	 * @throws IOException an IO exception
	 */
	void exec() throws IOException;

	/**
	 * Performs the solving process.
	 *
	 * @throws IOException an IO exception
	 * @throws InterruptedException if the solving process was interrupted
	 */
	void solve() throws IOException, InterruptedException;

	@Override
	void close();
}
