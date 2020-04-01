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

import java.io.File;

import picocli.CommandLine.Option;

/**
 * Helper class to save query results.
 *
 * @author Larry Gonzalez
 *
 */
public class SaveQueryResults {
	public static final String DEFAULT_OUTPUT_DIR_NAME = "query-results";

	static final String configurationErrorMessage = "Configuration Error: If @code{--save-query-results} is true, then a non empty @code{--output-query-result-directory} is required.";
	static final String wrongDirectoryErrorMessage = "Configuration Error: wrong @code{--output-query-result-directory}. Please check the path.";

	/**
	 * If true, RulewerkClient will save the query result in
	 * {@code --output-query-result-directory}
	 *
	 * @default false
	 */
	@Option(names = "--save-query-results", description = "Boolean. If true, RulewerkClient will save the query result into --output-query-result-directory. False by default.")
	private boolean saveResults = false;

	/**
	 * Directory to store the model. Used only if {@code --save-query-results} is
	 * true
	 *
	 * @default query-results
	 */
	@Option(names = "--output-query-result-directory", description = "Directory to store the model. Used only if --save-query-results is true. \""
			+ DEFAULT_OUTPUT_DIR_NAME + "\" by default.")
	private String outputQueryResultDirectory = DEFAULT_OUTPUT_DIR_NAME;

	public SaveQueryResults() {
	}

	public SaveQueryResults(final boolean saveResults, final String outputDir) {
		this.saveResults = saveResults;
		this.outputQueryResultDirectory = outputDir;
	}

	/**
	 * Check correct configuration of the class. If {@code --save-query-results} is
	 * true, then a non-empty {@code --output-query-result-directory} is required.
	 *
	 * @return {@code true} if configuration is valid.
	 */
	public boolean isConfigurationValid() {
		return !this.saveResults
				|| ((this.outputQueryResultDirectory != null) && !this.outputQueryResultDirectory.isEmpty());
	}

	/**
	 * Check that the path to store the query results is either non-existing or a
	 * directory.
	 *
	 * @return {@code true} if conditions are satisfied.
	 */
	public boolean isDirectoryValid() {
		final File file = new File(this.outputQueryResultDirectory);
		return !file.exists() || file.isDirectory();
	}

	public boolean isSaveResults() {
		return this.saveResults;
	}

	public void setSaveResults(final boolean saveResults) {
		this.saveResults = saveResults;
	}

	public String getOutputQueryResultDirectory() {
		return this.outputQueryResultDirectory;
	}

	public void setOutputQueryResultDirectory(final String outputQueryResultDirectory) {
		this.outputQueryResultDirectory = outputQueryResultDirectory;
	}

	/**
	 * Create directory to store query results if not present. It assumes that
	 * configuration and directory are valid.
	 */
	void mkdir() {
		if (this.saveResults) {
			final File file = new File(this.outputQueryResultDirectory);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	void printConfiguration() {
		System.out.println("  --save-query-results: " + this.saveResults);
		System.out.println("  --output-query-result-directory: " + this.outputQueryResultDirectory);
	}

}
