package org.semanticweb.rulewerk.utils;

/*-
 * #%L
 * Rulewerk Reliances
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.core.model.api.Literal;

public class BCQTest {

	Literal pa;
	Literal px;

	List<Literal> instance;
	List<Literal> query;

	public void init() throws ParsingException {
		pa = RuleParser.parseLiteral("p(a)");
		px = RuleParser.parseLiteral("p(?X)");

		instance = new ArrayList<>();
		query = new ArrayList<>();
	}

	@Test
	public void test001() throws ParsingException {
		init();
		instance.add(pa);
		query.add(px);
		assertTrue(BCQ.query(instance, query));
	}
}
