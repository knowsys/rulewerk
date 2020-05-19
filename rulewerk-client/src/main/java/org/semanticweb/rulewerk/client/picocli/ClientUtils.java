package org.semanticweb.rulewerk.client.picocli;

/*-
 * #%L
 * Rulewerk Client
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

import java.util.Iterator;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;

/**
 * Utility class for interacting with the Rulewerk client.
 *
 * @author dragoste
 *
 */
public final class ClientUtils {

	/**
	 * Private constructor. This is a utility class. Therefore, it is best practice
	 * to do the following: (1) Make the class final, (2) make its constructor
	 * private, (3) make all its fields and methods static. This prevents the
	 * classes instantiation and inheritance.
	 */
	private ClientUtils() {

	}

	/**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 *
	 * Note: The VLog C++ backend performs its own logging. The log-level for this
	 * can be configured using
	 * {@link Reasoner#setLogLevel(org.semanticweb.rulewerk.core.reasoner.LogLevel)}.
	 * It is also possible to specify a separate log file for this part of the logs.
	 */
	public static void configureLogging() {
		// Create the appender that will write log messages to the console.
		final ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		final String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
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

			System.out.println("Query answers are: " + answers.getCorrectness());
		}
		System.out.println();
	}

	/**
	 * Returns the number of answers returned by {@code reasoner} to the query
	 * ({@code queryAtom}).
	 *
	 * @param queryAtom query to be answered
	 * @param reasoner  reasoner to query on
	 *
	 * @return number of answers to the given query
	 */
	public static int getQueryAnswerCount(final PositiveLiteral queryAtom, final Reasoner reasoner) {
		try (final QueryResultIterator answers = reasoner.answerQuery(queryAtom, true)) {
			return iteratorSize(answers);
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
	private static <T> int iteratorSize(final Iterator<T> iterator) {
		int size = 0;
		for (; iterator.hasNext(); ++size) {
			iterator.next();
		}
		return size;
	}

}
