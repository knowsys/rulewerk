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

import picocli.CommandLine.Option;

public class PrintQueryResults {

	/**
	 * If true, Vlog4jClient will print the size of the query result. Mutually
	 * exclusive with {@code --print-complete-query-result}
	 * 
	 * @default true
	 */
	@Option(names = "--print-query-result-size", description = "Boolean. If true, Vlog4jClient will print the size of the query result. True by default.")
	private boolean sizeOnly = true;

	/**
	 * If true, Vlog4jClient will print the query result in stdout. Mutually
	 * exclusive with {@code --print-query-result-size}
	 * 
	 * @default false
	 */
	@Option(names = "--print-complete-query-result", description = "Boolean. If true, Vlog4jClient will print the query result in stdout. False by default.")
	private boolean complete = false;

	public boolean isConfigValid() {
		return !sizeOnly || !complete;
	}

	/**
	 * Print configuration error and exit the program
	 */
	public void printErrorAndExit() {
		System.out.println("Configuration error: --print-query-result-size and "
				+ "--print-query-result are mutually exclusive. Set only one to true.");
		System.exit(1);
	}

	public void printConfiguration() {
		System.out.println("  --print-query-result-size: " + sizeOnly);
		System.out.println("  --print-complete-query-result: " + complete);
	}

	public boolean isSizeOnly() {
		return sizeOnly;
	}

	public void setSizeOnly(boolean sizeOnly) {
		this.sizeOnly = sizeOnly;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
}
