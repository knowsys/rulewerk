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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.reasoner.Algorithm;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Class to implement a command to execute full materialization.
 *
 * @author Larry Gonzalez
 *
 */
@Command(name = "materialize", description = "Execute the chase and store the literal's extensions")
public class RulewerkClientMaterialize implements Runnable {

	private final KnowledgeBase kb = new KnowledgeBase();
	private final List<PositiveLiteral> queries = new ArrayList<>();

	@Option(names = "--rule-file", description = "Rule file(s) in {@link https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar} syntax", required = true)
	private final List<String> ruleFiles = new ArrayList<>();

//  TODO
//	Support graal rule files
//	@Option(names = "--graal-rule-file", description = "Rule file(s) in graal syntax", required = true)
//	private List<String> graalRuleFiles = new ArrayList<>();

	@Option(names = "--log-level", description = "Log level of VLog (c++ library). One of: DEBUG, INFO, WARNING (default), ERROR.", required = false)
	private LogLevel logLevel = LogLevel.WARNING;

	@Option(names = "--log-file", description = "Log file of VLog (c++ library). VLog will log to the default system output by default", required = false)
	private String logFile;

	@Option(names = "--chase-algorithm", description = "Chase algorithm. RESTRICTED_CHASE (default) or SKOLEM_CHASE.", required = false)
	private Algorithm chaseAlgorithm = Algorithm.RESTRICTED_CHASE;

	@Option(names = "--timeout", description = "Timeout in seconds. Infinite by default", required = false)
	private int timeout = 0;

	@Option(names = "--query", description = "Positive not-ground Literals to query after materialization in rls syntax. RulewerkClient will print the size of its extension", required = true)
	private List<String> queryStrings = new ArrayList<>();

	@ArgGroup(exclusive = false)
	private final PrintQueryResults printQueryResults = new PrintQueryResults();

	@ArgGroup(exclusive = false)
	private final SaveQueryResults saveQueryResults = new SaveQueryResults();

	// TODO
	// @ArgGroup(exclusive = false)
	// private SaveModel saveModel = new SaveModel();

	@Override
	public void run() {
		ClientUtils.configureLogging();

		/* Validate configuration */
		this.validateConfiguration();

		/* Configure rules */
		this.configureRules();

		/* Configure queries */
		this.configureQueries();

		/* Print configuration */
		this.printConfiguration();

		try (Reasoner reasoner = new VLogReasoner(this.kb)) {

			this.materialize(reasoner);
			// TODO if (saveModel.saveModel) { this.saveModel(); }

			this.answerQueries(reasoner);
		}
		System.out.println("Process completed.");
	}

	private void validateConfiguration() {
		if (!this.printQueryResults.isValid()) {
			this.printErrorMessageAndExit(PrintQueryResults.configurationErrorMessage);
		}
		if (!this.saveQueryResults.isConfigurationValid()) {
			this.printErrorMessageAndExit(SaveQueryResults.configurationErrorMessage);
		}
		if (this.saveQueryResults.isSaveResults() && !this.saveQueryResults.isDirectoryValid()) {
			this.printErrorMessageAndExit(SaveQueryResults.wrongDirectoryErrorMessage);
		}
		// TODO
		// if (!saveModel.isConfigurationValid()) {
		// printMessageAndExit(SaveModel.configurationErrorMessage);
		// }
		// if (saveModel.isSaveResults() && !saveModel.isDirectoryValid()) {
		// printMessageAndExit(SaveModel.wrongDirectoryErrorMessage);
		// }
	}

	private void configureRules() {
		for (final String ruleFile : this.ruleFiles) {
			try {
				RuleParser.parseInto(this.kb, new FileInputStream(ruleFile));
			} catch (final FileNotFoundException e1) {
				this.printErrorMessageAndExit("File not found: " + ruleFile + "\n " + e1.getMessage());
			} catch (final ParsingException e2) {
				this.printErrorMessageAndExit("Failed to parse rule file: " + ruleFile + "\n " + e2.getMessage());
			}
		}
	}

	private void configureQueries() {
		for (final String queryString : this.queryStrings) {
			try {
				final PositiveLiteral query = RuleParser.parsePositiveLiteral(queryString);
				this.queries.add(query);
			} catch (final ParsingException e) {
				System.err.println("Failed to parse query: \"\"\"" + queryString + "\"\"\".");
				System.err.println(e.getMessage());
				System.err.println("The query was skipped. Continuing ...");
			}
		}
	}

	private void materialize(final Reasoner reasoner) {
		// logFile
		reasoner.setLogFile(this.logFile);
		// logLevel
		reasoner.setLogLevel(this.logLevel);
		// chaseAlgorithm
		reasoner.setAlgorithm(this.chaseAlgorithm);
		// timeout
		if (this.timeout > 0) {
			reasoner.setReasoningTimeout(this.timeout);
		}

		System.out.println("Executing the chase ...");
		try {
			reasoner.reason();
		} catch (final IOException e) {
			this.printErrorMessageAndExit(
					"Something went wrong during reasoning. Please check the reasoner log file.\n" + e.getMessage());
		}

	}

	// TODO private void saveModel() {...}

	private void answerQueries(final Reasoner reasoner) {
		if (!this.queries.isEmpty()) {
			System.out.println("Answering queries ...");
			for (final PositiveLiteral query : this.queries) {
				if (this.saveQueryResults.isSaveResults()) {
					// Save the query results
					this.doSaveQueryResults(reasoner, query);
				}

				if (this.printQueryResults.isSizeOnly()) {
					// print number of facts in results
					this.doPrintResults(reasoner, query);
				} else if (this.printQueryResults.isComplete()) {
					// print facts
					ClientUtils.printOutQueryAnswers(query, reasoner);
				}
			}
		}
	}

	private void printConfiguration() {
		System.out.println("Configuration:");

		for (final String ruleFile : this.ruleFiles) {
			System.out.println("  --rule-file: " + ruleFile);
		}

		for (final PositiveLiteral query : this.queries) {
			System.out.println("  --query: " + query);
		}

		System.out.println("  --log-file: " + this.logFile);
		System.out.println("  --log-level: " + this.logLevel);
		System.out.println("  --chase-algorithm: " + this.chaseAlgorithm);
		System.out.println("  --timeout: " + ((this.timeout > 0) ? this.timeout : "none"));

		/* Print what to do with the result */
		this.printQueryResults.printConfiguration();
		this.saveQueryResults.printConfiguration();
		// TODO saveModel.printConfiguration();
	}

	private void doSaveQueryResults(final Reasoner reasoner, final PositiveLiteral query) {
		this.saveQueryResults.mkdir();
		try {
			reasoner.exportQueryAnswersToCsv(query, this.queryOputputPath(query), true);
		} catch (final IOException e) {
			System.err.println("Can't save query: \"\"\"" + query + "\"\"\".");
			System.err.println(e.getMessage());
		}
	}

	private void doPrintResults(final Reasoner reasoner, final PositiveLiteral query) {
		System.out.println("Number of query answers in " + query + ": " + reasoner.countQueryAnswers(query).getCount());
	}

	private String queryOputputPath(final PositiveLiteral query) {
		return this.saveQueryResults.getOutputQueryResultDirectory() + "/" + query + ".csv";
	}

	private void printErrorMessageAndExit(final String message) {
		System.err.println(message);
		System.out.println("Exiting the program.");
		System.exit(1);
	}

}
