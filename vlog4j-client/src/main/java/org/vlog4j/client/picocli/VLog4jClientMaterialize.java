package org.vlog4j.client.picocli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "materialize", description = "Execute the chase and store the literal's extensions")
public class VLog4jClientMaterialize implements Runnable {

	@Option(names = "--rule-file", description = "Rule file(s) in rls syntax", required = true)
	private List<String> ruleFiles = new ArrayList<>();

//	we need support to parseInto from gral files
//	@Option(names = "--graal-rule-file", description = "Rule file(s) in graal syntax", required = true)
//	private List<String> graalRuleFiles = new ArrayList<>();

	// The value for annotation attribute CommandLine.Option.description must be a
	// constant expression

	@Option(names = "--log-level", description = "Log level of VLog (c++ library). One of: DEBUG, INFO, WARNING (default), ERROR.", required = false)
	private LogLevel logLevel = LogLevel.WARNING;

	@Option(names = "--log-file", description = "Log file of VLog (c++ library). VLog will log to the default system output by default", required = false)
	private String logFile;

	@Option(names = "--chase-algorithm", description = "Chase algorithm. RESTRICTED_CHASE (default) or SKOLEM_CHASE.", required = false)
	private Algorithm chaseAlgorithm = Algorithm.RESTRICTED_CHASE;

	@Option(names = "--timeout", description = "Timeout in seconds. Infinite by default", required = false)
	private int timeout = 0;

	@Option(names = "--query", description = "Positive not-ground Literals to query after materialization in rls syntax. Vlog4jClient will print the size of its extension", required = true)
	private List<String> queryStrings = new ArrayList<>();

// TODO SaveModel
//	/* group to save results */
//	@ArgGroup(exclusive = false)
//	private SaveModel saveModel;

	@ArgGroup(exclusive = false)
	private SaveQueryResult saveQueryResult = new SaveQueryResult();

	/* group to print results */
	@ArgGroup(exclusive = false)
	private PrintQueryResults printQueryResult = new PrintQueryResults();

	@Override
	public void run() {
		ExamplesUtils.configureLogging();

		System.out.println(queryStrings);

//		if (saveModel.check() & saveQueryResult.check() & printQueryResult.check()) {
//			saveModel.prepare();
		if (saveQueryResult.checkConfiguration() & printQueryResult.checkConfiguration()) {
			saveQueryResult.prepare();
		} else {
			System.exit(1);
		}

		System.out.println("Configuration:");

		/* Configure rules */
		final KnowledgeBase kb = new KnowledgeBase();
		for (String ruleFile : ruleFiles) {
			try {
				RuleParser.parseInto(kb, new FileInputStream(ruleFile));
				System.out.println("  --rule-file: " + ruleFile);
			} catch (FileNotFoundException e) {
				System.out.println("File not found: " + ruleFile + ". " + e.getMessage());
				System.exit(1);
			} catch (ParsingException e) {
				System.out.println("Failed to parse rule file: " + ruleFile + ". " + e.getMessage());
				System.exit(1);
			}
		}

		/* Configure queries (We throw parsing query errors before materialization) */
		List<PositiveLiteral> queries = new ArrayList<>();
		for (String queryString : queryStrings) {
			try {
				queries.add(RuleParser.parsePositiveLiteral(queryString));
				System.out.println("  --query: " + queries.get(queries.size() - 1));
			} catch (ParsingException e) {
				System.out.println("Failed to parse query: " + queryString + ". " + e.getMessage());
				System.exit(1);
			}
		}

		/* Print configuration */
		System.out.println("  --log-file: " + logFile);
		System.out.println("  --log-level: " + logLevel);
		System.out.println("  --chase-algorithm: " + chaseAlgorithm);
		System.out.println("  --timeout: " + ((timeout > 0) ? Integer.toString(timeout) : "none"));
		/* Print what to do with the result */
//		saveModel.print();
		saveQueryResult.print();
		printQueryResult.print();

		try (Reasoner reasoner = new VLogReasoner(kb)) {
			// logFile
			reasoner.setLogFile(logFile);
			// logLevel
			reasoner.setLogLevel(logLevel);
			// chaseAlgorithm
			reasoner.setAlgorithm(chaseAlgorithm);
			// timeout
			if (timeout > 0) {
				reasoner.setReasoningTimeout(timeout);
			}

			try {
				System.out.println("Executing the chase ...");
				reasoner.reason();
			} catch (IOException e) {
				System.out.println("Something went wrong. Please check the file paths inside the rule files.");
				e.printStackTrace();
				System.exit(1);
			}

//			// TODO save the model
//			if (saveModel.saveModel) {
//				System.out.println("Saving model ...");
//			}

			String outputPath;
			if (queries.size() > 0) {
				System.out.println("Answering queries ...");
				for (PositiveLiteral query : queries) {
					if (SaveQueryResult.saveQueryResults) {
						try {
							outputPath = SaveQueryResult.outputQueryResultDirectory + "/" + query.toString() + ".csv";
							reasoner.exportQueryAnswersToCsv(query, outputPath, true);
						} catch (IOException e) {
							System.out.println("Can't save query " + query);
							e.printStackTrace();
							System.exit(1);
						}
					}
					if (printQueryResult.sizeOnly) {
						System.out.println(
								"Elements in " + query + ": " + ExamplesUtils.getQueryAnswerCount(query, reasoner));
					} else if (printQueryResult.complete) {
						ExamplesUtils.printOutQueryAnswers(query, reasoner);
					}
				}
			}
		}
	}
}
