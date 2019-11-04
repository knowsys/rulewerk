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

import javax.naming.ConfigurationException;

import picocli.CommandLine.Option;

/**
 * Helper class to save query results.
 * 
 * @author Larry Gonzalez
 *
 */
public class SaveQueryResults {

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

	/**
	 * Check correct configuration of the class. If @code{--save-query-results} is
	 * true, then a non-empty @code{--output-query-result-directory} is required.
	 * 
	 * @throws ConfigurationException
	 */
	public void validate() throws ConfigurationException {
		String error_message = "If @code{--save-query-results} is true, then a non empty @code{--output-query-result-directory} is required.";
		if (saveResults && (outputQueryResultDirectory == null || outputQueryResultDirectory.isEmpty())) {
			throw new ConfigurationException(error_message);
		}
	}

	/**
	 * Create directory to store query results
	 */
	public void prepare() {
		if (saveResults) {
			new File(outputQueryResultDirectory).mkdirs();
		}
	}

	public void printConfiguration() {
		if (saveResults) {
			System.out.println("  --save-query-results: " + saveResults);
			System.out.println("  --output-query-result-directory: " + outputQueryResultDirectory);
		}
	}

	public boolean isSaveResults() {
		return saveResults;
	}

	public void setSaveResults(boolean saveResults) {
		this.saveResults = saveResults;
	}

	public String getOutputQueryResultDirectory() {
		return outputQueryResultDirectory;
	}

	public void setOutputQueryResultDirectory(String outputQueryResultDirectory) {
		this.outputQueryResultDirectory = outputQueryResultDirectory;
	}

}
