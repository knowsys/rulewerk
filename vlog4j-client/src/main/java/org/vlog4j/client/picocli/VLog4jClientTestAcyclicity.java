package org.vlog4j.client.picocli;

import picocli.CommandLine.Command;

@Command(name = "testacyclicity", description = "Test if the rule set satisfy any acyclicity notion")
public class VLog4jClientTestAcyclicity implements Runnable {

//	@Option(names = "--acyclicity-notion", required = false, description = "Acyclicity notion. One of:JA (Joint Acyclicity), RJA (Restricted Joint Acyclicity), RFA (Model-Faithful Acyclicity), RMFA (Restricted Model-Faithful Acyclicity). All by default.")
//	String acyclicityNotion;
//
//	@Option(names = "--rule-file", description = "Rule file in rls syntax", required = true)
//	private String rulePath;

	@Override
	public void run() {
		System.out.println("Not implemented yet.");
	}

}
