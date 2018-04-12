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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

@Ignore
public class LoggingTest {
	private static final String logFilePath = "src/test/data/log.out";

	private static final Variable vx = Expressions.makeVariable("x");
	// p(?x) -> q(?x)
	private static final Atom ruleHeadQx = Expressions.makeAtom("q", vx);
	private static final Atom ruleBodyPx = Expressions.makeAtom("p", vx);
	private static final Rule rule = Expressions.makeRule(ruleHeadQx, ruleBodyPx);

	private static final Constant constantC = Expressions.makeConstant("c");
	private static final Atom factPc = Expressions.makeAtom("p", constantC);

	@Before
	public void assertLogTestFileNotExists() {
		assertFalse(new File(logFilePath).exists());
	}

	// TODO remaining tests: change log file
	// TODO remaining tests: test that the log level and the log files can be set
	// any time

	@Test
	public void testSetLogFileNull() throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.setLogFile(null);
			assertFalse(new File(logFilePath).exists());
			instance.setLogLevel(LogLevel.INFO);

			instance.addFacts(factPc);
			instance.addRules(rule);
			instance.load();
			instance.reason();
		}
		// TODO test that logging is redirected to system output
		assertFalse(new File(logFilePath).exists());
	}

	@Test
	public void testSetLogFileInexistent() throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.setLogFile("/a/b");
			assertFalse(new File("/a/b").exists());
			instance.setLogLevel(LogLevel.INFO);

			instance.addFacts(factPc);
			instance.addRules(rule);
			instance.load();
			instance.reason();
		}
		// TODO test that logging is redirected to system output
		assertFalse(new File(logFilePath).exists());
		assertFalse(new File("/a/b").exists());
	}

	@Test(expected = NullPointerException.class)
	public void testSetLogLevelNull() {
		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.setLogLevel(null);
		}
	}

	@Test
	public void testSetLogFileAppendsToFile() throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.addFacts(factPc);
			instance.addRules(rule);
			instance.setLogLevel(LogLevel.INFO);
			instance.setLogFile(logFilePath);
			instance.load();
			instance.reason();

			final int countLinesBeforeReset = readFile();
			assertTrue(countLinesBeforeReset > 0);

			instance.resetReasoner();
			instance.load();
			instance.reason();

			final int countLinesAfterReset = readFile();

			// the logger appends to the same file after reset
			assertTrue(countLinesAfterReset > countLinesBeforeReset);
		}
	}

	@Test
	public void testLogLevelInfo() throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.addFacts(factPc);
			instance.addRules(rule);

			instance.setLogLevel(LogLevel.INFO);
			instance.setLogFile(logFilePath);
			instance.load();
			instance.setLogLevel(LogLevel.INFO);
			instance.reason();
			instance.setLogLevel(LogLevel.INFO);

			final int countLinesReasonLogLevelInfo = readFile();
			assertTrue(countLinesReasonLogLevelInfo > 0);
		}
	}

	@Test
	public void testLogLevelDebug() throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		try (final Reasoner instance = Reasoner.getInstance()) {
			instance.addFacts(factPc);
			instance.addRules(rule);

			instance.setLogLevel(LogLevel.DEBUG);
			instance.setLogFile(logFilePath);
			instance.load();
			instance.setLogLevel(LogLevel.DEBUG);
			instance.reason();
			instance.setLogLevel(LogLevel.DEBUG);

			final int countLinesReasonLogLevelDebug = readFile();
			assertTrue(countLinesReasonLogLevelDebug > 0);
		}
	}

	@After
	public void deleteLogTestFile() {
		new File(logFilePath).delete();
	}

	private int readFile() throws IOException, FileNotFoundException {
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
