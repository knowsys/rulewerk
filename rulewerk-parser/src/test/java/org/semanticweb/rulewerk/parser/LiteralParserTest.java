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

import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class LiteralParserTest implements ParserTestUtils {

	private final UniversalVariable uX = Expressions.makeUniversalVariable("X");
	private final ExistentialVariable eX = Expressions.makeExistentialVariable("X");
	private final Literal puXeX = Expressions.makePositiveLiteral("p", uX, eX);
	private final Literal peXuX = Expressions.makePositiveLiteral("p", eX, uX);

	@Test(expected = ParsingException.class)
	public void pX1() throws ParsingException {
		Literal literal = RuleParser.parseLiteral("p(?X, !X)");
		Assert.assertEquals(literal, puXeX);
	}

	@Test(expected = ParsingException.class)
	public void pX2() throws ParsingException {
		Literal literal = RuleParser.parseLiteral("p(!X, ?X)");
		Assert.assertEquals(literal, peXuX);
	}
}
