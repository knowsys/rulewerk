package org.vlog4j.client.picocli;

/*-
 * #%L
 * VLog4j Client
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

import java.io.File;

import picocli.CommandLine.Option;

/**
 * Helper class to save query results.
 * 
 * @author Larry Gonzalez
 *
 */
public class SaveQueryResults {

	static final String configurationErrorMessage = "Configuration Error: If @code{--save-query-results} is true, then a non empty @code{--output-query-result-directory} is required.\nExiting the program.";
	static final String wrongDirectoryErrorMessage = "Configuration Error: wrong @code{--output-query-result-directory}. Please check the path.\nExiting the program.";

	/**
	 * If true, Vlog4jClient will save the query result in
	 * {@code --output-query-result-directory}
	 *
	 * @default false
	 */
	@Option(names = "--save-query-results", description = "Boolean. If true, Vlog4jClient will save the query result into --output-query-result-directory. False by default.")
	private boolean saveResults = false;

	/**
	 * Directory to store the model. Used only if {@code --save-query-results} is
	 * true
	 *
	 * @default query-results
	 */
	@Option(names = "--output-query-result-directory", description = "Directory to store the model. Used only if --save-query-results is true. \"query-results\" by default.")
	private String outputQueryResultDirectory = "query-results";

	public SaveQueryResults() {
	}

	public SaveQueryResults(boolean saveResults, String outputDir) {
		this.saveResults = saveResults;
		this.outputQueryResultDirectory = outputDir;
	}

	/**
	 * Check correct configuration of the class. If @code{--save-query-results} is
	 * true, then a non-empty @code{--output-query-result-directory} is required.
	 * 
	 * @return @code{true} if configuration is valid.
	 */
	protected boolean isConfigurationValid() {
		return !saveResults || (outputQueryResultDirectory != null && !outputQueryResultDirectory.isEmpty());
	}

	/**
	 * Check that the path to store the query results is either non-existing or a
	 * directory.
	 * 
	 * @return @code{true} if conditions are satisfied.
	 */
	protected boolean isDirectoryValid() {
		File file = new File(outputQueryResultDirectory);
		return !file.exists() || file.isDirectory();
	}

	/**
	 * Create directory to store query results if not present. It assumes that
	 * configuration and directory are valid.
	 */
	protected void mkdir() {
		if (saveResults) {
			File file = new File(outputQueryResultDirectory);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	protected void printConfiguration() {
		System.out.println("  --save-query-results: " + saveResults);
		System.out.println("  --output-query-result-directory: " + outputQueryResultDirectory);
	}

	protected boolean isSaveResults() {
		return saveResults;
	}

	protected void setSaveResults(boolean saveResults) {
		this.saveResults = saveResults;
	}

	protected String getOutputQueryResultDirectory() {
		return outputQueryResultDirectory;
	}

	protected void setOutputQueryResultDirectory(String outputQueryResultDirectory) {
		this.outputQueryResultDirectory = outputQueryResultDirectory;
	}

}
