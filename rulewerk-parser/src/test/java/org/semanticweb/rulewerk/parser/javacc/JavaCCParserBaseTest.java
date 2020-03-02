package org.semanticweb.rulewerk.parser.javacc;

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
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.parser.DatatypeConstantHandler;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.parser.ParsingException;

public class JavaCCParserBaseTest {
	private JavaCCParserBase parserBase;
	private static final String DATATYPE_NAME = "https://example.org/test-type";

	private DatatypeConstantHandler datatypeConstantHandler = mock(DatatypeConstantHandler.class);

	@Before
	public void init() {
		parserBase = new JavaCCParserBase();
	}

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test
	public void createConstant_undeclaredPrefix_throws() throws ParseException {
		exceptionRule.expect(ParseException.class);
		exceptionRule.expectMessage("Failed to parse IRI");
		parserBase.createConstant("ïnvälid://test");
	}

	@Test
	public void createConstant_throwingDatatypeConstantHandler_throws() throws ParseException, ParsingException {
		exceptionRule.expect(ParseException.class);
		exceptionRule.expectMessage("Failed to parse Constant");

		when(datatypeConstantHandler.createConstant(anyString())).thenThrow(ParsingException.class);
		ParserConfiguration parserConfiguration = new DefaultParserConfiguration().registerDatatype(DATATYPE_NAME,
				datatypeConstantHandler);
		parserBase.setParserConfiguration(parserConfiguration);
		parserBase.createConstant("test", DATATYPE_NAME);
	}

	@Test
	public void unescapeStr_escapeChars_succeeds() throws ParseException {
		String input = "\\\\test\r\ntest: \\n\\t\\r\\b\\f\\'\\\"\\\\";
		String expected = "\\test\r\ntest: \n\t\r\b\f\'\"\\";
		String result = JavaCCParserBase.unescapeStr(input, 0, 0);
		assertEquals(result, expected);
	}

	@Test
	public void unescapeStr_illegalEscapeAtEndOfString_throws() throws ParseException {
		exceptionRule.expect(ParseException.class);
		exceptionRule.expectMessage("Illegal escape at end of string");

		JavaCCParserBase.unescapeStr("\\", 0, 0);
	}

	@Test
	public void unescapeStr_unknownEscapeSequence_throws() throws ParseException {
		exceptionRule.expect(ParseException.class);
		exceptionRule.expectMessage("Unknown escape");

		JavaCCParserBase.unescapeStr("\\y", 0, 0);
	}

	@Test
	public void setBase_changingBase_throws() throws PrefixDeclarationException {
		exceptionRule.expect(PrefixDeclarationException.class);
		exceptionRule.expectMessage("Base is already defined as");

		parserBase.setBase("https://example.org/");
		parserBase.setBase("https://example.com/");
	}
}
