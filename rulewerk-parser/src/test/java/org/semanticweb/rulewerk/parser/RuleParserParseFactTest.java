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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class RuleParserParseFactTest implements ParserTestUtils {

	private final Constant a = Expressions.makeDatatypeConstant("a", PrefixDeclarationRegistry.XSD_STRING);
	private final Constant b = Expressions.makeDatatypeConstant("b%c", PrefixDeclarationRegistry.XSD_STRING);

	private final Fact factA = Expressions.makeFact("p", a);
	private final Fact factAB = Expressions.makeFact("p", a, b);

	@Test
	public void parseFact_string_succeeds() throws ParsingException {
		assertEquals(factA, RuleParser.parseFact("p(\"a\") ."));
	}

	@Test
	public void parseFact_twoStrings_succeeds() throws ParsingException {
		assertEquals(factAB, RuleParser.parseFact("p(\"a\",\"b%c\") ."));
	}

	@Test(expected = ParsingException.class)
	public void parseFact_nonGroundFact_throws() throws ParsingException {
		String input = "p(?X) .";
		RuleParser.parseFact(input);
	}

	@Test(expected = ParsingException.class)
	public void parseFact_arityZeroFact_throws() throws ParsingException {
		String input = "p() .";
		RuleParser.parseFact(input);
	}

	@Test(expected = ParsingException.class)
	public void parseFact_namedNullDisallowed_throws() throws ParsingException {
		String input = "p(_:1) .";
		ParserConfiguration parserConfiguration = new ParserConfiguration().disallowNamedNulls();
		RuleParser.parseFact(input, parserConfiguration);
	}

	@Test
	public void parseFact_namedNull_succeeds() throws ParsingException {
		String input = "p(_:1) .";
		Fact result = RuleParser.parseFact(input);
		assertArgumentIsNamedNull(result, 1);
	}

	@Test(expected = ParsingException.class)
	public void parseFact_namedNullAsPredicateName_throws() throws ParsingException {
		String input = "_:p(\"a\") .";
		RuleParser.parseFact(input);
	}

	@Test(expected = ParsingException.class)
	public void parseRule_namedNullInBody_throws() throws ParsingException {
		String input = "q(_:head) :- p(_:body) .";
		RuleParser.parseRule(input);
	}

	@Test
	public void parseRule_namedNullInHead_succeeds() throws ParsingException {
		String input = "q(_:head) :- p(\"a\") .";
		Rule result = RuleParser.parseRule(input);
		Literal literal = result.getHead().getLiterals().get(0);
		assertArgumentIsNamedNull(literal, 1);
	}
}
