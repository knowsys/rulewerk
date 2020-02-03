package org.semanticweb.vlog4j.parser.datasources;

/*-
 * #%L
 * VLog4j Parser
 * %%
 * Copyright (C) 2018 - 2020 VLog4j Developers
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

import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.parser.DataSourceDeclarationHandler;
import org.semanticweb.vlog4j.parser.DirectiveArgument;
import org.semanticweb.vlog4j.parser.DirectiveHandler;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.javacc.SubParserFactory;

/**
 * Handler for parsing {@link SparqlQueryResultDataSource} declarations
 *
 * @author Maximilian Marx
 */
public class SparqlQueryResultDataSourceDeclarationHandler implements DataSourceDeclarationHandler {
	@Override
	public DataSource handleDirective(List<DirectiveArgument> arguments, final SubParserFactory subParserFactory)
			throws ParsingException {
		DirectiveHandler.validateNumberOfArguments(arguments, 3);

		DirectiveArgument endpointArgument = arguments.get(0);
		URL endpoint;
		try {
			endpoint = endpointArgument.fromIri().get();
		} catch (NoSuchElementException e) {
			throw new ParsingException(
					"SPARQL endpoint \"" + endpointArgument + "\" is not a valid IRI: " + e.getMessage(), e);
		}

		DirectiveArgument variablesArgument = arguments.get(1);
		String variables;
		try {
			variables = variablesArgument.fromString().get();
		} catch (NoSuchElementException e) {
			throw new ParsingException("Variables list \"" + variablesArgument + "\" is not a string.", e);
		}

		DirectiveArgument queryArgument = arguments.get(2);
		String query;
		try {
			query = queryArgument.fromString().get();
		} catch (NoSuchElementException e) {
			throw new ParsingException("Query fragment \"" + queryArgument + "\" is not a string.", e);
		}

		return new SparqlQueryResultDataSource(endpoint, variables, query);
	}
}
