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
import java.io.PrintStream;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.semanticweb.rulewerk.client.shell.DefaultShellConfiguration;
import org.semanticweb.rulewerk.client.shell.InteractiveShellClient;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Class with main method that is a command with subcommands {@code shell}
 * (default) and {@code materialize}.
 * 
 * @author Irina Dragoste
 *
 */
@Command(name = "", description = "A command line client for Rulewerk.", subcommands = { InteractiveShellClient.class,
		RulewerkClientMaterialize.class })
public class Main {

	public static String INTERACTIVE_SHELL_COMMAND = "shell";
	public static String COMMAND_LINE_CLIENT_COMMAND = "materialize";
	public static String HELP_COMMAND = "help";

	/**
	 * Launches the client application for Rulewerk. The functionality depends on
	 * the given command-line args ({@code args}):
	 * <ul>
	 * <li>empty args (<b>""</b>) or argument <b>"shell"</b></li> launch an
	 * interactive shell.
	 * <li>argument "materialize" can be used with different options to complete
	 * several materialization and querying tasks from the command line.</li>
	 * </ul>
	 * <li>help</li>
	 * 
	 * @param args
	 * 
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		configureLogging();

		if (args.length == 0 || (args.length > 0 && INTERACTIVE_SHELL_COMMAND.equals(args[0]))) {
			new InteractiveShellClient().launchShell(new DefaultShellConfiguration());
		} else {
			if (COMMAND_LINE_CLIENT_COMMAND.equals(args[0])) {
				final CommandLine commandline = new CommandLine(new RulewerkClientMaterialize());
				commandline.execute(args);
			} else {
				displayHelp(args, System.out);
			}
		}
	}

	static void displayHelp(final String[] args, final PrintStream printStream) {
		if (!HELP_COMMAND.equals(args[0])) {
			printStream.println("Invalid command.");
		}

		if (HELP_COMMAND.equals(args[0]) && args.length > 1 && COMMAND_LINE_CLIENT_COMMAND.equals(args[1])) {
			(new CommandLine(new RulewerkClientMaterialize())).usage(printStream);
		} else {
			(new CommandLine(new Main())).usage(printStream);
		}
	}

	/**
	 * Configures {@link Logger} settings. Messages are logged to the console. Log
	 * level is set to {@link Level.FATAL}.
	 */
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
