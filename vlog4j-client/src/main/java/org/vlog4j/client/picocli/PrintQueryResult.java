package org.vlog4j.client.picocli;

import picocli.CommandLine.Option;

class PrintQueryResults {

	/**
	 * If true, Vlog4jClient will print the size of the query result. Mutually
	 * exclusive with {@code --print-complete-query-result}
	 * 
	 * @default true
	 */
	@Option(names = "--print-query-result-size", description = "Boolean. If true, Vlog4jClient will print the size of the query result. True by default.")
	public boolean sizeOnly = true;

	/**
	 * If true, Vlog4jClient will print the query result in stdout. Mutually
	 * exclusive with {@code --print-query-result-size}
	 * 
	 * @default false
	 */
	@Option(names = "--print-complete-query-result", description = "Boolean. If true, Vlog4jClient will print the query result in stdout. False by default.")
	public boolean complete = false;

	public boolean checkConfiguration() {
		if (sizeOnly & complete) {
			System.out.println(
					"--print-query-result-size and --print-query-result are mutually exclusive. Set only one to true.");
			return false;
		} else {
			return true;
		}
	}

	public void print() {
		System.out.println("  --print-query-result-size: " + sizeOnly);
		System.out.println("  --print-complete-query-result: " + complete);
	}
}
