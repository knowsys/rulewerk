package org.semanticweb.rulewerk.asp.implementation;

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
	 * Constructor. Creates a process via {@link Runtime} that runs clasp.
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
		execute();
	}

	/**
	 * Executes the clasp command via {@link Runtime}.
	 *
	 * @throws IOException an IO exception
	 */
	private void execute() throws IOException {
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
