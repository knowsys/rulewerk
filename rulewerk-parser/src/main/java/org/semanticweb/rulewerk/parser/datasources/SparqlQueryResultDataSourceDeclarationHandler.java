package org.semanticweb.rulewerk.parser.datasources;

/*-
 * #%L
 * Rulewerk Parser
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

import java.net.URL;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.rulewerk.parser.ParsingException;

/**
 * Handler for parsing {@link SparqlQueryResultDataSource} declarations
 *
 * @author Maximilian Marx
 */
public class SparqlQueryResultDataSourceDeclarationHandler implements DataSourceDeclarationHandler {

	public DataSource handleDataSourceDeclaration(List<Term> terms, String importBasePath) throws ParsingException {
		DataSourceDeclarationHandler.validateNumberOfArguments(terms, 3);
		URL endpoint = DataSourceDeclarationHandler.validateUrlArgument(terms.get(0), "SPARQL endpoint URL");
		String variables = DataSourceDeclarationHandler.validateStringArgument(terms.get(1),
				"SPARQL query variable list");
		String query = DataSourceDeclarationHandler.validateStringArgument(terms.get(2), "SPARQL query pattern");

		return new SparqlQueryResultDataSource(endpoint, variables, query);
	}
}
