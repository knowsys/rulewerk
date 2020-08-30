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

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.semanticweb.rulewerk.client.shell.DefaultShellConfiguration;
import org.semanticweb.rulewerk.client.shell.InteractiveShellClient;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Dummy class with main method that is a command with subcommands shell and
 * materialize
 * 
 * @author Irina Dragoste
 *
 */
@Command(name = "", description = "A command line client for Rulewerk.", subcommands = { InteractiveShellClient.class,
		RulewerkClientMaterialize.class })
public class Main {

	public static void main(final String[] args) throws IOException {
		configureLogging();
		
		if (args.length == 0 || (args.length > 0 && args[0].equals("shell"))) {
			new InteractiveShellClient().run(new DefaultShellConfiguration());
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
	
	public static void configureLogging() {
		// Create the appender that will write log messages to the console.
		final ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		final String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(Level.FATAL);

		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}

}
