package org.vlog4j.client.picocli;

import java.io.File;

import picocli.CommandLine.Option;

class SaveQueryResult {

	@Option(names = "--save-query-results", description = "Boolean. If true, Vlog4jClient will save the query result into --output-query-result-folder. False by default.")
	public static boolean saveQueryResults = false;

	@Option(names = "--output-query-result-folder", description = "Folder to store the model. Used only if --save-query-results is true. \"query-results\" by default.")
	public static String outputQueryResultFolder = "query-results";

	public boolean checkConfiguration() {
		if (saveQueryResults & outputQueryResultFolder == null) {
			System.out.println("--save-query-results requires an --output-query-result-folder.");
			return false;
		} else {
			return true;
		}
	}

	public void prepare() {
		if (saveQueryResults)
			new File(outputQueryResultFolder).mkdirs();
	}

	public void print() {
		if (saveQueryResults) {
			System.out.println("  --save-query-results: " + saveQueryResults);
			System.out.println("  --output-query-result-folder: " + outputQueryResultFolder);
		}
	}
	
}
