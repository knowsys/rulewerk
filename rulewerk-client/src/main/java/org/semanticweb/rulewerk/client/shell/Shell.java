package org.semanticweb.rulewerk.client.shell;

import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter;
import org.semanticweb.rulewerk.client.shell.commands.ExitCommandInterpreter.ExitCommandName;
import org.semanticweb.rulewerk.commands.CommandExecutionException;
import org.semanticweb.rulewerk.commands.CommandInterpreter;
import org.semanticweb.rulewerk.commands.Interpreter;
import org.semanticweb.rulewerk.core.model.api.Command;

public class Shell {

	private final Interpreter interpreter;
	boolean running;

	public Shell(final Interpreter interpreter) {
		this.interpreter = interpreter;

		CommandInterpreter exitCommandInterpreter = new ExitCommandInterpreter(this);
		for (final ExitCommandName exitCommandName : ExitCommandName.values()) {
			interpreter.registerCommandInterpreter(exitCommandName.toString(), exitCommandInterpreter);
		}
	}

	public void run(final CommandReader commandReader) {
		running = true;
		while (running) {
			final Command command;
			try {
				command = commandReader.readCommand();
			} catch (final Exception e) {
				interpreter.getOut().println("Unexpected error: " + e.getMessage());
				continue;
			}

			if (command != null) {
				try {
					this.interpreter.runCommand(command);
				} catch (final CommandExecutionException e) {
					interpreter.getOut().println("Error: " + e.getMessage());
				}
			}
		}
		interpreter.getOut().println("Rulewerk shell is stopped. Bye.");
	}

	public void exitShell() {
		this.running = false;
	}

//	@Override
//	public void handleResult(final Object result) {
//		this.terminal.writer().println(result);
//		this.terminal.writer().flush();
//	}

//	@Override
//	public void handleResult(final AttributedCharSequence result) {
//		this.terminal.writer().println(result.toAnsi(this.terminal));
//		this.terminal.writer().flush();
//	}
}
