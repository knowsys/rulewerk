package org.semanticweb.rulewerk.examples.core;

/*-
 * #%L
 * Rulewerk Examples
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

import java.io.IOException;

import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

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
	private static String reasonerWarningLogFilePath = logsFolder + "ReasonerWarningLogFile.log";

	/**
	 * Path to the file where INFO level reasoner logs will be exported.
	 */
	private static String reasonerInfoLogFilePath = logsFolder + "ReasonerInfoLogFile.log";

	/**
	 * Path to the file where DEBUG level reasoner logs will be exported.
	 */
	private static String reasonerDebugLogFilePath = logsFolder + "ReasonerDebugLogFile.log";

	public static void main(final String[] args) throws IOException, ParsingException {

		try (final Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			final KnowledgeBase kb = reasoner.getKnowledgeBase();
			/* exists z. B(?y, !z) :- A(?x, ?y) . */
			kb.addStatements(RuleParser.parseRule("B(?Y, !Z) :- A(?X, ?Y) ."));
			/* B(?y, ?x), A(?y, ?x) :- B(?x, ?y) . */
			kb.addStatements(RuleParser.parseRule("B(?Y, ?X), A(?Y, ?X) :- B(?X, ?Y) ."));
			/* A(c,d) */
			kb.addStatement(RuleParser.parseFact("A(\"c\",\"d\")"));

			/*
			 * Default reasoner log level is WARNING.
			 */
			reasoner.setLogFile(reasonerWarningLogFilePath);
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
			reasoner.reason();
		}

	}

}
