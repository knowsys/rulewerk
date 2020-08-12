package org.semanticweb.rulewerk.core.model.api;

import java.util.List;

import org.semanticweb.rulewerk.core.model.implementation.Serializer;

/**
 * Class for representing a generic command that can be executed.
 * 
 * @author Markus Kroetzsch
 *
 */
public class Command implements Entity {

	final String name;
	final List<Argument> arguments;

	/**
	 * Constructor
	 * 
	 * @param name      String name of the command
	 * @param arguments list of arguments of the command
	 */
	public Command(String name, List<Argument> arguments) {
		this.name = name;
		this.arguments = arguments;
	}

	/**
	 * Returns the command name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the command arguments.
	 * 
	 * @return
	 */
	public List<Argument> getArguments() {
		return arguments;
	}

	@Override
	public String getSyntacticRepresentation() {
		StringBuilder result = new StringBuilder("@");
		result.append(name);
		for (Argument argument : arguments) {
			result.append(" ");
			if (argument.fromRule().isPresent()) {
				Rule rule = argument.fromRule().get();
				result.append(Serializer.getString(rule.getHead())).append(Serializer.RULE_SEPARATOR)
						.append(Serializer.getString(rule.getBody()));
			} else if (argument.fromPositiveLiteral().isPresent()) {
				result.append(argument.fromPositiveLiteral().get().getSyntacticRepresentation());
			} else if (argument.fromString().isPresent()) {
				result.append(Serializer.getString(argument.fromString().get()));
			} else {
				throw new UnsupportedOperationException("Serialisation of commands is not fully implemented yet.");
			}
		}
		result.append(Serializer.STATEMENT_SEPARATOR);
		return result.toString();
	}

}
