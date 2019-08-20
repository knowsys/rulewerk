package org.semanticweb.vlog4j.core.reasoner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import org.junit.Test;
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class LoggingTest {

	public static final String LOGS_FOLDER = "src/test/data/logs/";

	private static final Variable vx = Expressions.makeVariable("x");
	// p(?x) -> q(?x)
	private static final PositiveLiteral ruleHeadQx = Expressions.makePositiveLiteral("q", vx);
	private static final PositiveLiteral ruleBodyPx = Expressions.makePositiveLiteral("p", vx);
	private static final Rule rule = Expressions.makeRule(ruleHeadQx, ruleBodyPx);

	private static final Constant constantC = Expressions.makeConstant("c");
	private static final PositiveLiteral factPc = Expressions.makePositiveLiteral("p", constantC);

	// TODO remaining tests: change log file
	// TODO remaining tests: test that the log level and the log files can be set
	// any time

	@Test
	public void testSetLogFileNull() throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.setLogFile(null);
			instance.setLogLevel(LogLevel.INFO);

			instance.addFacts(factPc);
			instance.addRules(rule);
			instance.load();
			instance.reason();
		}
		// TODO test that logging is redirected to system output
	}

	@Test
	public void testSetLogFileInexistent() throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final String inexistentFilePath = LOGS_FOLDER + "a/b";

		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.setLogFile(inexistentFilePath);
			assertFalse(new File(inexistentFilePath).exists());
			instance.setLogLevel(LogLevel.INFO);

			instance.addFacts(factPc);
			instance.addRules(rule);
			instance.load();
			instance.reason();
		}
		// TODO test that logging is redirected to system output
		assertFalse(new File(inexistentFilePath).exists());
	}

	@Test(expected = NullPointerException.class)
	public void testSetLogLevelNull() throws ReasonerStateException {
		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.setLogLevel(null);
		}
	}

	@Test
	public void testSetLogFileAppendsToFile() throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		final String logFilePath = LOGS_FOLDER + System.currentTimeMillis() + "-testSetLogFileAppendsToFile.log";
		assertFalse(new File(logFilePath).exists());
		int countLinesBeforeReset = 0;

		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.addFacts(factPc);
			instance.addRules(rule);
			instance.setLogLevel(LogLevel.INFO);
			instance.setLogFile(logFilePath);
			instance.load();
			instance.reason();

			countLinesBeforeReset = readFile(logFilePath);
			assertTrue(countLinesBeforeReset > 0);

			instance.resetReasoner();
			instance.load();
			instance.reason();
		}
		final int countLinesAfterReset = readFile(logFilePath);
		// the logger appends to the same file after reset
		assertTrue(countLinesAfterReset > countLinesBeforeReset);

	}

	@Test
	public void testLogLevelInfo() throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final String logFilePath = LOGS_FOLDER + System.currentTimeMillis() + "-testLogLevelInfo.log";
		assertFalse(new File(logFilePath).exists());

		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.addFacts(factPc);
			instance.addRules(rule);

			instance.setLogLevel(LogLevel.INFO);
			instance.setLogFile(logFilePath);
			instance.load();
			instance.setLogLevel(LogLevel.INFO);
			instance.reason();
			instance.setLogLevel(LogLevel.INFO);
		}
		final int countLinesReasonLogLevelInfo = readFile(logFilePath);
		assertTrue(countLinesReasonLogLevelInfo > 0);

	}

	@Test
	public void testLogLevelDebug() throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final String logFilePath = LOGS_FOLDER + System.currentTimeMillis() + "-testLogLevelDebug.log";
		assertFalse(new File(logFilePath).exists());

		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.addFacts(factPc);
			instance.addRules(rule);

			instance.setLogLevel(LogLevel.DEBUG);
			instance.setLogFile(logFilePath);
			instance.load();
			instance.setLogLevel(LogLevel.DEBUG);
			instance.reason();
			instance.setLogLevel(LogLevel.DEBUG);
			instance.close();
		}
		final int countLinesReasonLogLevelDebug = readFile(logFilePath);
		assertTrue(countLinesReasonLogLevelDebug > 0);

	}

	private int readFile(final String logFilePath) throws IOException, FileNotFoundException {
		int countLines = 0;
		assertTrue(new File(logFilePath).exists());
		try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				assertFalse(sCurrentLine.isEmpty());
				countLines++;
			}
		}

		return countLines;
	}

}
