package org.semanticweb.vlog4j.examples;

/*-
 * #%L
 * VLog4j Examples
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

public final class ExamplesUtils {

	public static final String OUTPUT_FOLDER = "src/main/data/output/";
	public static final String INPUT_FOLDER = "src/main/data/input/";

	/*
	 * This is a utility class. Therefore, it is best practice to do the following:
	 * (1) Make the class final, (2) make its constructor private, (3) make all its
	 * fields and methods static. This prevents the classes instantiation and
	 * inheritance.
	 */
	private ExamplesUtils() {

	}

	/**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 * 
	 * Note: The VLog C++ backend performs its own logging. The log-level for this
	 * can be configured using
	 * {@link Reasoner#setLogLevel(org.semanticweb.vlog4j.core.reasoner.LogLevel)}.
	 * It is also possible to specify a separate log file for this part of the logs.
	 */
	public static void configureLogging() {
		// Create the appender that will write log messages to the console.
		ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(Level.INFO);

		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}

	/**
	 * Prints out the answers given by {@code reasoner} to the query
	 * ({@code queryAtom}).
	 *
	 * @param queryAtom query to be answered
	 * @param reasoner  reasoner to query on
	 */
	public static void printOutQueryAnswers(final PositiveLiteral queryAtom, final Reasoner reasoner) {
		System.out.println("Answers to query " + queryAtom + " :");
		try (final QueryResultIterator answers = reasoner.answerQuery(queryAtom, true)) {
			answers.forEachRemaining(answer -> System.out.println(" - " + answer));
			System.out.println();
		}
	}

	/**
	 * Prints out the answers given by {@code reasoner} to the query
	 * ({@code queryAtom}).
	 *
	 * @param queryAtom query to be answered
	 * @param reasoner  reasoner to query on
	 */
	public static void printOutQueryAnswers(final String queryString, final Reasoner reasoner) {
		try {
			PositiveLiteral query = RuleParser.parsePositiveLiteral(queryString);
			printOutQueryAnswers(query, reasoner);
		} catch (ParsingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the number of answers returned by {@code reasoner} to the query
	 * ({@code queryAtom}).
	 *
	 * @param queryAtom query to be answered
	 * @param reasoner  reasoner to query on
	 * @throws ReasonerStateException in case the reasoner has not yet been loaded.
	 */
	public static int getQueryAnswerCount(final String queryString, final Reasoner reasoner) {
		try {
			PositiveLiteral query = RuleParser.parsePositiveLiteral(queryString);
			try (final QueryResultIterator answers = reasoner.answerQuery(query, true)) {
				return iteratorSize(answers);
			}
		} catch (ParsingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the size of an iterator.
	 *
	 * @FIXME This is an inefficient way of counting results. It should be done at a
	 *        lower level instead
	 * @param Iterator<T> to iterate over
	 * @return number of elements in iterator
	 */
	public static <T> int iteratorSize(Iterator<T> iterator) {
		int size = 0;
		for (; iterator.hasNext(); ++size)
			iterator.next();
		return size;
	}

	/**
	 * Creates an Atom with @numberOfVariables distinct variables
	 *
	 * @param predicateName for the new predicate
	 * @param arity         number of variables
	 */
	private static PositiveLiteral makeQueryAtom(String predicateName, int arity) {
		final List<Term> vars = new ArrayList<>();
		for (int i = 0; i < arity; i++)
			vars.add(Expressions.makeVariable("x" + i));
		return Expressions.makePositiveLiteral(predicateName, vars);
	}

	/**
	 * Exports the extension of the Atom with name @predicateName
	 *
	 * @param reasoner reasoner to query on
	 * @param atomName atom's name
	 * @param arity    atom's arity
	 */
	public static void exportQueryAnswersToCSV(Reasoner reasoner, String atomName, int arity)
			throws ReasonerStateException, IOException {
		final PositiveLiteral atom = makeQueryAtom(atomName, arity);
		String path = ExamplesUtils.OUTPUT_FOLDER + atomName + ".csv";
		reasoner.exportQueryAnswersToCsv(atom, path, true);
	}

}
