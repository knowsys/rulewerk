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

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

public class RuleParserParseFactTest {

	private final Constant a = Expressions.makeDatatypeConstant("a", PrefixDeclarations.XSD_STRING);
	private final Constant b = Expressions.makeDatatypeConstant("b", PrefixDeclarations.XSD_STRING);

	private final Fact factA = Expressions.makeFact("p", a);
	private final Fact factAB = Expressions.makeFact("p", a, b);

	@Test
	public void testFactArityOne() throws ParsingException {
		assertEquals(RuleParser.parseFact("p(\"a\")"), factA);
	}

	@Test
	public void testFactArityOneWithDataType() throws ParsingException {
		assertEquals(RuleParser.parseFact("p(\"a\")"), factA);
	}

	@Test
	public void testFactArityTwo() throws ParsingException {
		assertEquals(RuleParser.parseFact("p(\"a\",\"b\")"), factAB);
	}

	@Test(expected = ParsingException.class)
	public void testFactWithVariable() throws ParsingException {
		String input = "p(?X)";
		RuleParser.parseFact(input);
	}

	@Test(expected = ParsingException.class)
	public void testZeroArityFact() throws ParsingException {
		String input = "p()";
		RuleParser.parseFact(input);
	}

}
