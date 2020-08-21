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

import java.io.IOException;

import org.semanticweb.rulewerk.client.shell.InteractiveShell;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Dummy class with main method that is a command with subcommands shell and
 * materialize
 * 
 * @author Irina Dragoste
 *
 */
@Command(name = "", description = "A command line client for Rulewerk.", subcommands = { InteractiveShell.class,
		RulewerkClientMaterialize.class })
public class Main {

	public static void main(final String[] args) throws IOException {
		if (args.length == 0 || (args.length > 0 && args[0].equals("shell"))) {
			final InteractiveShell interactiveShell = new InteractiveShell();
			interactiveShell.run();
		} else {
			if (args[0].equals("materialize")) {
				final CommandLine commandline = new CommandLine(new RulewerkClientMaterialize());
				commandline.execute(args);
			} else {
				if (!args[0].equals("help")) {
					System.out.println("Invalid command.");
				}
				// TODO improve help
				// TODO do we need to create a Help command?
				(new CommandLine(new Main())).usage(System.out);

			}
		}

	}

}
