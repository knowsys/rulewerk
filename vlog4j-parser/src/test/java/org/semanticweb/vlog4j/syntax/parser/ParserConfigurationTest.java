package org.semanticweb.vlog4j.syntax.parser;

/*-
 * #%L
 * VLog4j Syntax
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

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.semanticweb.vlog4j.parser.DataSourceDeclarationHandler;
import org.semanticweb.vlog4j.parser.DatatypeConstantHandler;
import org.semanticweb.vlog4j.parser.ParserConfiguration;
import org.semanticweb.vlog4j.parser.ParsingException;

public class ParserConfigurationTest {
	private static final String TYPE_NAME = "test-type";
	private static final String SOURCE_NAME = "test-source";

	private final DatatypeConstantHandler datatypeConstantHandler = mock(DatatypeConstantHandler.class);
	private final DataSourceDeclarationHandler dataSourceDeclarationHandler = mock(DataSourceDeclarationHandler.class);

	@Test(expected = IllegalArgumentException.class)
	public void registerDataSource_duplicateName_throws() {
		ParserConfiguration parserConfiguration = new ParserConfiguration();

		parserConfiguration.registerDataSource(SOURCE_NAME, dataSourceDeclarationHandler)
				.registerDataSource(SOURCE_NAME, dataSourceDeclarationHandler);
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerDatatype_duplicateName_throws() {
		ParserConfiguration parserConfiguration = new ParserConfiguration();
		parserConfiguration.registerDatatype(TYPE_NAME, datatypeConstantHandler).registerDatatype(TYPE_NAME,
				datatypeConstantHandler);
	}

	@Test
	public void registerDataSource_datatypeName_succeeds() {
		ParserConfiguration parserConfiguration = new ParserConfiguration();
		parserConfiguration.registerDatatype(TYPE_NAME, datatypeConstantHandler).registerDataSource(TYPE_NAME,
				dataSourceDeclarationHandler);
	}

	@Test
	public void registerDatatype_dataSourceName_succeeds() {
		ParserConfiguration parserConfiguration = new ParserConfiguration();
		parserConfiguration.registerDataSource(SOURCE_NAME, dataSourceDeclarationHandler).registerDatatype(SOURCE_NAME,
				datatypeConstantHandler);
	}
}
