package org.vlog4j.client.picocli;

import java.io.File;

import picocli.CommandLine.Option;

class SaveQueryResult {

	@Option(names = "--save-query-results", description = "Boolean. If true, Vlog4jClient will save the query result into --output-query-result-directory. False by default.")
	public static boolean saveQueryResults = false;

	@Option(names = "--output-query-result-directory", description = "Directory to store the model. Used only if --save-query-results is true. \"query-results\" by default.")
	public static String outputQueryResultDirectory = "query-results";

	public boolean checkConfiguration() {
		if (saveQueryResults & outputQueryResultDirectory == null) {
			System.out.println("--save-query-results requires an --output-query-result-directory.");
			return false;
		} else {
			return true;
		}
	}

	public void prepare() {
		if (saveQueryResults) {
			new File(outputQueryResultDirectory).mkdirs();
		}
	}

	public void print() {
		if (saveQueryResults) {
			System.out.println("  --save-query-results: " + saveQueryResults);
			System.out.println("  --output-query-result-directory: " + outputQueryResultDirectory);
		}
	}

}
