package org.semanticweb.rulewerk.asp;

/*
 * #%L
 * Rulewerk Core Components
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

import org.junit.Test;
import org.semanticweb.rulewerk.asp.implementation.AspifGrounder;
import org.semanticweb.rulewerk.asp.model.Grounder;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AspifGrounderTest {

	final Constant c = Expressions.makeAbstractConstant("c");
	final Constant d = Expressions.makeAbstractConstant("d");

	final Fact fact = Expressions.makeFact("p", d, c);
	final Fact fact2 = Expressions.makeFact("p", c, c);

	@Test
	public void visitFactTest() throws IOException {
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(fact, fact2);
		Reasoner reasoner = new VLogReasoner(kb);
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		Grounder grounder = new AspifGrounder(kb, reasoner, bufferedWriter);
		grounder.ground();
		bufferedWriter.flush();
		assertEquals("asp 1 0 0\n" +
			"4 1 1 1 1\n" +
			"1 0 1 1 0 0\n" +
			"4 1 2 1 2\n" +
			"1 0 1 2 0 0\n" +
			"0\n", writer.toString());
		Map<Integer, Literal> map = grounder.getIntegerLiteralMap();
		assertEquals(2, map.size());
		assertEquals(fact, map.getOrDefault(1, null));
		assertEquals(fact2, map.getOrDefault(2, null));
	}
}
