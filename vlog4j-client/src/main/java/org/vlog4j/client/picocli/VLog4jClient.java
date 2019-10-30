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

import picocli.CommandLine;

import picocli.CommandLine.Command;

/**
 * Stand alone client for VLog4j.
 * 
 * @author Larry Gonzalez
 *
 */
@Command(name = "java -jar VLog4jClient.jar", description = "VLog4jClient: A command line client of VLog4j.", subcommands = {
		VLog4jClientMaterialize.class })
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
