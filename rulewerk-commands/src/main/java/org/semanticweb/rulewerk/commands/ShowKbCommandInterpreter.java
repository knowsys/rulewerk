package org.semanticweb.rulewerk.commands;

import java.io.IOException;

import org.semanticweb.rulewerk.core.model.api.Command;

public class ShowKbCommandInterpreter implements CommandInterpreter {

	@Override
	public void run(Command command, Interpreter interpreter) throws CommandExecutionException {
		Interpreter.validateArgumentCount(command, 0);
		try {
			interpreter.getReasoner().getKnowledgeBase().writeKnowledgeBase(interpreter.getOut());
		} catch (IOException e) {
			throw new CommandExecutionException(e.getMessage(), e);
		}
	}

	@Override
	public String getHelp(String commandName) {
		return "Usage: @" + commandName + ".";
	}

	@Override
	public String getSynopsis() {
		return "displays the content of the knowledge base";
	}

}
