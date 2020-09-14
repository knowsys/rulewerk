package org.semanticweb.rulewerk.core.model.api;

/*-
 * #%L
 * Rulewerk Core Components
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
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeCommand(this));
	}

}
