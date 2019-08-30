package org.vlog4j.client.picocli;

import picocli.CommandLine.Option;

class PrintQueryResults {
	/* Store and/or print the query result */
	@Option(names = "--print-query-result-size", description = "Boolean. If true, Vlog4jClient will print only the size of the query result. True by default.")
	public boolean sizeOnly = true;

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
