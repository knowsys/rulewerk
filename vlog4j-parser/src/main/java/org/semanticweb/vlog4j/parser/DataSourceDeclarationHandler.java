package org.semanticweb.vlog4j.parser;

/*-
 * #%L
 * vlog4j-parser
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

import java.util.List;

import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.parser.javacc.SubParserFactory;

/**
 * Handler for parsing a custom Data Source declaration.
 *
 * @author Maximilian Marx
 */
@FunctionalInterface
public interface DataSourceDeclarationHandler {
	/**
	 * Parse a Data Source Declaration.
	 *
	 * This is called by the parser to instantiate the {@link DataSource} component
	 * of a {@link DataSourceDeclaration}.
	 *
	 * @param arguments        Arguments given to the Data Source declaration.
	 * @param subParserFactory a factory for obtaining a SubParser, sharing the
	 *                         parser's state, but bound to new input.
	 *
	 * @throws ParsingException when any of the arguments is invalid for the Data
	 *                          Source, or the number of arguments is invalid.
	 * @return a {@link DataSource} instance corresponding to the given arguments.
	 */
	DataSource handleDeclaration(List<String> arguments, final SubParserFactory subParserFactory)
			throws ParsingException;

	/**
	 * Validate the provided number of arguments to the data source.
	 *
	 * @param arguments Arguments given to the Data Source declaration.
	 * @param number    expected number of arguments
	 *
	 * @throws ParsingException when the given number of Arguments is invalid for
	 *                          the Data Source.
	 */
	static void validateNumberOfArguments(final List<String> arguments, final int number) throws ParsingException {
		if (arguments.size() != number) {
			throw new ParsingException("Invalid number of arguments " + arguments.size()
					+ " for Data Source declaration, expected " + number);
		}
	}
}
