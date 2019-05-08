package org.semanticweb.vlog4j.examples.core;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveConjunction;

/*-
 * #%L
 * VLog4j Examples
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

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.KnowledgeBaseImpl;

/**
 * This class exemplifies setting a log file and log level for VLog reasoner
 * logging information (like materialisation duration, number of iterations,
 * number of derivations).
 * <ul>
 * <li>Setting the <b>log level</b> is done via
 * {@link Reasoner#setLogLevel(LogLevel)}, the default being
 * {@link LogLevel#WARNING}.</li>
 * <li>The <b>log file</b> where the logging information will be exported is set
 * via {@link Reasoner#setLogFile(String)}. If no log file is set, or the log
 * file is invalid, the logging will be redirected to the System output. If the
 * log file does not exist at given path, it will be created. If a file already
 * exists, it will be over-written, so we suggest backing up and versioning log
 * files.</li>
 * </ul>
 */
public class ConfigureReasonerLogging {
	private static String logsFolder = "src/main/logs/";

	/**
	 * Path to the file where the default WARNING level reasoner logs will be
	 * exported.
	 */
	private static @Nullable String reasonerWarningLogFilePath = logsFolder + "ReasonerWarningLogFile.log";

	/**
	 * Path to the file where INFO level reasoner logs will be exported.
	 */
	private static @Nullable String reasonerInfoLogFilePath = logsFolder + "ReasonerInfoLogFile.log";

	/**
	 * Path to the file where DEBUG level reasoner logs will be exported.
	 */
	private static @Nullable String reasonerDebugLogFilePath = logsFolder + "ReasonerDebugLogFile.log";

	private static @NonNull List<Rule> rules = Arrays.asList(
			/* A(?x, ?y) :- A_EDB(?x, ?y) . */
			makeRule(makePositiveLiteral("A", makeVariable("x"), makeVariable("y")),
					makePositiveLiteral("A_EDB", makeVariable("x"), makeVariable("y"))),
			/* exists z. B(?y, !z) :- A(?x, ?y) . */
			makeRule(makePositiveLiteral("B", makeVariable("y"), makeVariable("z")),
					makePositiveLiteral("A", makeVariable("x"), makeVariable("y"))),
			/* B(?y, ?x), A(?y, ?x) :- B(?x, ?y) . */
			makeRule(
					makePositiveConjunction(makePositiveLiteral("B", makeVariable("y"), makeVariable("x")),
							makePositiveLiteral("A", makeVariable("y"), makeVariable("x"))),
					makeConjunction(makePositiveLiteral("B", makeVariable("x"), makeVariable("y")))));

	/* A(c,d) */
	private static @NonNull PositiveLiteral fact = makePositiveLiteral("A_EDB", makeConstant("c"), makeConstant("d"));

	public static void main(final String[] args)
			throws EdbIdbSeparationException, IncompatiblePredicateArityException, IOException, ReasonerStateException {
		final KnowledgeBase kb = new KnowledgeBaseImpl();
		kb.addRules(rules);

		try (final Reasoner reasoner = Reasoner.getInstance(kb)) {

			reasoner.addFacts(fact);

			/*
			 * Default reasoner log level is WARNING.
			 */
			reasoner.setLogFile(reasonerWarningLogFilePath);
			reasoner.load();
			reasoner.reason();

			/*
			 * We reset the reasoner and repeat reasoning over the same knowledge base, with
			 * different log levels
			 */
			reasoner.resetReasoner();

			/*
			 * INFO level logs the number of iterations, the materialisation duration (in
			 * miliseconds), and the number of derivations.
			 */
			reasoner.setLogLevel(LogLevel.INFO);
			/*
			 * If no log file is set, or given log file is invalid, the reasoner logging is
			 * redirected to System output by default.
			 */
			reasoner.setLogFile(reasonerInfoLogFilePath);

			reasoner.load();
			reasoner.reason();

			reasoner.resetReasoner();

			/*
			 * DEBUG level is the most informative, logging internal details useful for
			 * debugging: rule optimisations, rule application details etc.
			 */
			reasoner.setLogLevel(LogLevel.DEBUG);
			/*
			 * If no log file is set, or given log file is invalid, the reasoner logging is
			 * redirected to System output by default.
			 */
			reasoner.setLogFile(reasonerDebugLogFilePath);
			reasoner.load();
			reasoner.reason();
		}

	}

}
