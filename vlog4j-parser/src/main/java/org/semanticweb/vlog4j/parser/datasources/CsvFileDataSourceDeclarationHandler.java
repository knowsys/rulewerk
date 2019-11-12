package org.semanticweb.vlog4j.parser.datasources;

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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.parser.DataSourceDeclarationHandler;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.javacc.SubParserFactory;

/**
 * Handler for parsing {@link CsvFileDataSource} declarations
 *
 * @author Maximilian Marx
 */
public class CsvFileDataSourceDeclarationHandler implements DataSourceDeclarationHandler {
	@Override
	public DataSource handleDeclaration(List<String> arguments, final SubParserFactory subParserFactory)
			throws ParsingException {
		DataSourceDeclarationHandler.verifyCorrectNumberOfArguments(arguments, 1);
		String fileName = arguments.get(0);

		try {
			return new CsvFileDataSource(new File(fileName));
		} catch (IOException e) {
			throw new ParsingException("Could not use source file \"" + fileName + "\": " + e.getMessage(), e);
		}
	}
}
