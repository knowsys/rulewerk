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

public class SaveQueryResults {

	/**
	 * If true, Vlog4jClient will save the query result in
	 * {@code --output-query-result-directory}
	 *
	 * @default false
	 */
	@Option(names = "--save-query-results", description = "Boolean. If true, Vlog4jClient will save the query result into --output-query-result-directory. False by default.")
	public boolean saveResults = false;

	/**
	 * Directory to store the model. Used only if {@code --save-query-results} is
	 * true
	 *
	 * @default query-results
	 */
	@Option(names = "--output-query-result-directory", description = "Directory to store the model. Used only if --save-query-results is true. \"query-results\" by default.")
	public String outputQueryResultDirectory = "query-results";

	public boolean isConfigOk() {
		if (saveResults & (outputQueryResultDirectory == null || outputQueryResultDirectory.isEmpty())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Print configuration error and exit the program
	 */
	public void printErrorAndExit() {
		System.out.println(
				"Configuration error: --save-query-results requires a non-null --output-query-result-directory.");
		System.exit(1);
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

}
