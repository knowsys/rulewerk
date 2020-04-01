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

import picocli.CommandLine.Option;

/**
 * Helper class to print query results.
 *
 * @author Larry Gonzalez
 *
 */
public class PrintQueryResults {

	static final String configurationErrorMessage = "Configuration Error: @code{--print-query-result-size} and @code{--print-query-result} are mutually exclusive. Set only one to true.";

	/**
	 * If true, RulewerkClient will print the size of the query result. Mutually
	 * exclusive with {@code --print-complete-query-result}
	 *
	 * @default true
	 */
	@Option(names = "--print-query-result-size", description = "Boolean. If true, RulewerkClient will print the size of the query result. True by default.")
	private boolean sizeOnly = true;

	/**
	 * If true, RulewerkClient will print the query result in stdout. Mutually
	 * exclusive with {@code --print-query-result-size}
	 *
	 * @default false
	 */
	@Option(names = "--print-complete-query-result", description = "Boolean. If true, RulewerkClient will print the query result in stdout. False by default.")
	private boolean complete = false;

	public PrintQueryResults() {
	}

	public PrintQueryResults(final boolean sizeOnly, final boolean complete) {
		this.sizeOnly = sizeOnly;
		this.complete = complete;
	}

	/**
	 * Check correct configuration of the class. {@code --print-query-result-size}
	 * and {@code --print-query-result} are mutually exclusive.
	 *
	 * @return {@code true} if configuration is valid.
	 */
	public boolean isValid() {
		return !this.sizeOnly || !this.complete;
	}

	public boolean isSizeOnly() {
		return this.sizeOnly;
	}

	public void setSizeOnly(final boolean sizeOnly) {
		this.sizeOnly = sizeOnly;
	}

	public boolean isComplete() {
		return this.complete;
	}

	public void setComplete(final boolean complete) {
		this.complete = complete;
	}

	void printConfiguration() {
		System.out.println("  --print-query-result-size: " + this.sizeOnly);
		System.out.println("  --print-complete-query-result: " + this.complete);
	}
}
