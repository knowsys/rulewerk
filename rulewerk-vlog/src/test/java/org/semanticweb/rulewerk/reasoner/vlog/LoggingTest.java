package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;

public class LoggingTest {

	public static final String LOGS_DIRECTORY = "src/test/data/logs/";

	private static final Variable vx = Expressions.makeUniversalVariable("x");
	// p(?x) -> q(?x)
	private static final PositiveLiteral ruleHeadQx = Expressions.makePositiveLiteral("q", vx);
	private static final PositiveLiteral ruleBodyPx = Expressions.makePositiveLiteral("p", vx);
	private static final Rule rule = Expressions.makeRule(ruleHeadQx, ruleBodyPx);

	private static final Constant constantC = Expressions.makeAbstractConstant("c");
	private static final Fact factPc = Expressions.makeFact("p", Arrays.asList(constantC));

	private static final KnowledgeBase kb = new KnowledgeBase();

	static {
		kb.addStatements(rule, factPc);
	}

	@BeforeClass
	public static void emptyLogDirectory() {

		final File logsDir = new File(LOGS_DIRECTORY);

		if (!logsDir.exists()) {
			logsDir.mkdir();
		}

		final File[] listFiles = logsDir.listFiles();
		for (final File file : listFiles) {
			file.delete();
		}
	}

	// TODO remaining tests: change log file
	// TODO remaining tests: test that the log level and the log files can be set
	// any time

	@Test
	public void testSetLogFileNull() throws IOException {
		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogFile(null);
			reasoner.setLogLevel(LogLevel.INFO);

			reasoner.reason();
		}
		// TODO test that logging is redirected to system output
	}

	@Test
	public void testSetLogFileInexistent() throws IOException {
		final String inexistentFilePath = LOGS_DIRECTORY + "a/b";

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogFile(inexistentFilePath);
			assertFalse(new File(inexistentFilePath).exists());
			reasoner.setLogLevel(LogLevel.INFO);

			reasoner.reason();
		}
		// TODO test that logging is redirected to system output
		assertFalse(new File(inexistentFilePath).exists());
	}

	@Test(expected = NullPointerException.class)
	public void testSetLogLevelNull() {
		try (final Reasoner instance = new VLogReasoner(new KnowledgeBase())) {
			instance.setLogLevel(null);
		}
	}

	@Test
	public void testSetLogFileAppendsToFile() throws IOException {
		final String logFilePath = LOGS_DIRECTORY + "-testSetLogFileAppendsToFile.log";
		assertFalse(new File(logFilePath).exists());
		int countLinesBeforeReset = 0;

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogLevel(LogLevel.INFO);
			reasoner.setLogFile(logFilePath);
			reasoner.reason();

			countLinesBeforeReset = readFile(logFilePath);
			assertTrue(countLinesBeforeReset > 0);

			reasoner.resetReasoner();
			reasoner.reason();
		}
		final int countLinesAfterReset = readFile(logFilePath);
		// the logger appends to the same file after reset
		assertTrue(countLinesAfterReset > countLinesBeforeReset);

	}

	@Test
	public void testLogLevelInfo() throws IOException {
		final String logFilePath = LOGS_DIRECTORY + "-testLogLevelInfo.log";
		assertFalse(new File(logFilePath).exists());

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {

			reasoner.setLogLevel(LogLevel.INFO);
			reasoner.setLogFile(logFilePath);
			reasoner.reason();
			reasoner.setLogLevel(LogLevel.INFO);
		}
		final int countLinesReasonLogLevelInfo = readFile(logFilePath);
		assertTrue(countLinesReasonLogLevelInfo > 0);

	}

	@Test
	public void testLogLevelDebug() throws IOException {
		final String logFilePath = LOGS_DIRECTORY + "-testLogLevelDebug.log";
		assertFalse(new File(logFilePath).exists());

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {

			reasoner.setLogLevel(LogLevel.DEBUG);
			reasoner.setLogFile(logFilePath);
			reasoner.reason();
			reasoner.setLogLevel(LogLevel.DEBUG);
			reasoner.close();
		}
		final int countLinesReasonLogLevelDebug = readFile(logFilePath);
		assertTrue(countLinesReasonLogLevelDebug > 0);

	}

	@Test
	public void testLogLevelDefault() throws IOException {
		final String defaultLogFilePath = LOGS_DIRECTORY + "-testLogLevelDefault.log";
		assertFalse(new File(defaultLogFilePath).exists());

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogFile(defaultLogFilePath);

			reasoner.reason();
			reasoner.close();
		}
		final int countLinesReasonLogLevelDefault = readFile(defaultLogFilePath);

		final String warningLogFilePath = LOGS_DIRECTORY + "-testLogLevelDefault2.log";
		assertFalse(new File(warningLogFilePath).exists());

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogFile(warningLogFilePath);
			reasoner.setLogLevel(LogLevel.WARNING);
			reasoner.reason();
			reasoner.close();
		}
		final int countLinesReasonLogLevelWarning = readFile(warningLogFilePath);

		assertEquals(countLinesReasonLogLevelDefault, countLinesReasonLogLevelWarning);
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
