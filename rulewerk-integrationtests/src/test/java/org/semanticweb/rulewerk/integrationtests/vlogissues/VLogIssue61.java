package org.semanticweb.rulewerk.integrationtests.vlogissues;

/*-
 * #%L
 * Rulewerk Integration Tests
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

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class VLogIssue61 extends VLogIssue{

	@Ignore
	@Test
	public void test01() throws ParsingException, IOException {
		KnowledgeBase kb = RuleParser.parse(new FileInputStream(RESOURCES + "61-1.rls"));

		Reasoner reasoner = new VLogReasoner(kb);
		reasoner.reason();

		PositiveLiteral query = RuleParser.parsePositiveLiteral("q(?X,?Y,?Z)");
		assertEquals(2, reasoner.countQueryAnswers(query, true).getCount());

		reasoner.close();
	}

	@Test
	public void test02() throws ParsingException, IOException {
		KnowledgeBase kb = RuleParser.parse(new FileInputStream(RESOURCES + "61-2.rls"));

		Reasoner reasoner = new VLogReasoner(kb);
		reasoner.reason();

		PositiveLiteral query = RuleParser.parsePositiveLiteral("q(?X,?Y,?Z)");
		assertEquals(1, reasoner.countQueryAnswers(query, true).getCount());

		reasoner.close();
	}
	
}
