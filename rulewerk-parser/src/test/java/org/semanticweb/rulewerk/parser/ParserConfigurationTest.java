package org.semanticweb.rulewerk.parser;

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

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.parser.DatatypeConstantHandler;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.parser.datasources.DataSourceDeclarationHandler;
import org.semanticweb.rulewerk.parser.javacc.SubParserFactory;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParserBase.ConfigurableLiteralDelimiter;

public class ParserConfigurationTest {
	private static final String TYPE_NAME = "test-type";
	private static final String SOURCE_NAME = "test-source";
	private static final String DIRECTIVE_NAME = "test-directive";

	private ParserConfiguration parserConfiguration;

	@Mock
	private DatatypeConstantHandler datatypeConstantHandler;
	@Mock
	private DataSourceDeclarationHandler dataSourceDeclarationHandler;
	@Mock
	private SubParserFactory subParserFactory;
	@Mock
	private DirectiveHandler<KnowledgeBase> directiveHandler;

	@Before
	public void init() {
		parserConfiguration = new ParserConfiguration();
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerDataSource_duplicateName_throws() {
		parserConfiguration.registerDataSource(SOURCE_NAME, dataSourceDeclarationHandler)
				.registerDataSource(SOURCE_NAME, dataSourceDeclarationHandler);
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerDatatype_duplicateName_throws() {
		parserConfiguration.registerDatatype(TYPE_NAME, datatypeConstantHandler).registerDatatype(TYPE_NAME,
				datatypeConstantHandler);
	}

	@Test
	public void registerDataSource_datatypeName_succeeds() {
		parserConfiguration.registerDatatype(TYPE_NAME, datatypeConstantHandler).registerDataSource(TYPE_NAME,
				dataSourceDeclarationHandler);
	}

	@Test
	public void registerDatatype_dataSourceName_succeeds() {
		parserConfiguration.registerDataSource(SOURCE_NAME, dataSourceDeclarationHandler).registerDatatype(SOURCE_NAME,
				datatypeConstantHandler);
	}

	@Test
	public void isParsingOfNamedNullsAllowed_default_returnsTrue() {
		assertTrue("named nulls are allowed by default", parserConfiguration.isParsingOfNamedNullsAllowed());
	}

	@Test
	public void isParsingOfNamedNullsAllowed_disabled_returnsFalse() {
		parserConfiguration.disallowNamedNulls();
		assertFalse("named nulls are disallowed after disallowing them",
				parserConfiguration.isParsingOfNamedNullsAllowed());
	}

	@Test
	public void isParsingOfNamedNullsAllowed_disabledAndEnabled_returnsTrue() {
		parserConfiguration.disallowNamedNulls();
		assertFalse("named nulls are disallowed after disallowing them",
				parserConfiguration.isParsingOfNamedNullsAllowed());
		parserConfiguration.allowNamedNulls();
		assertTrue("named nulls are allowed after allowing them", parserConfiguration.isParsingOfNamedNullsAllowed());
	}

	@Test(expected = ParsingException.class)
	public void parseConfigurableLiteral_unregisteredLiteral_throws() throws ParsingException {
		parserConfiguration.parseConfigurableLiteral(ConfigurableLiteralDelimiter.BRACE, "test", subParserFactory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerDirective_reservedName_throws() throws IllegalArgumentException {
		parserConfiguration.registerDirective("base", directiveHandler);
	}

	@Test
	public void registerDirective_unreserverdName_succeeds() throws IllegalArgumentException {
		parserConfiguration.registerDirective(DIRECTIVE_NAME, directiveHandler);
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerDirective_duplicateName_throws() throws IllegalArgumentException {
		parserConfiguration.registerDirective(DIRECTIVE_NAME, directiveHandler);
		parserConfiguration.registerDirective(DIRECTIVE_NAME, directiveHandler);
	}

	@Test(expected = ParsingException.class)
	public void parseDirectiveStatement_unregisteredDirective_throws() throws ParsingException {
		parserConfiguration.parseDirectiveStatement(DIRECTIVE_NAME, new ArrayList<>(), subParserFactory);
	}

}
