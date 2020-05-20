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

import picocli.CommandLine;

import picocli.CommandLine.Command;

/**
 * Stand alone client for Rulewerk.
 *
 * @author Larry Gonzalez
 *
 */
@Command(name = "java -jar RulewerkClient.jar", description = "RulewerkClient: A command line client for Rulewerk.", subcommands = {
		RulewerkClientMaterialize.class })
public class RulewerkClient implements Runnable {

	public static void main(String[] args) {
		CommandLine commandline = new CommandLine(new RulewerkClient());
		commandline.execute(args);
	}

	@Override
	public void run() {
		(new CommandLine(new RulewerkClient())).usage(System.out);
	}
}
