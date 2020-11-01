package org.semanticweb.rulewerk.asp.model;

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
	 * Performs the solving process.
	 *
	 * @throws IOException an IO exception
	 * @throws InterruptedException if the solving process was interrupted
	 */
	void solve() throws IOException, InterruptedException;

	@Override
	void close();
}
