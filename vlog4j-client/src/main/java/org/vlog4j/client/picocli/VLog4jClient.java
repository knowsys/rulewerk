package org.vlog4j.client.picocli;

import picocli.CommandLine;

import picocli.CommandLine.Command;

@Command(name = "java -jar VLog4jClient.jar", description = "VLog4jClient: A command line client of VLog4j.", subcommands = {
		VLog4jClientMaterialize.class, VLog4jClientTestAcyclicity.class })
public class VLog4jClient implements Runnable {

	public static void main(String[] args) {
		CommandLine commandline = new CommandLine(new VLog4jClient());
		commandline.execute(args);
	}

	@Override
	public void run() {
		(new CommandLine(new VLog4jClient())).usage(System.out);
	}
}
