package org.semanticweb.rulewerk.reasoner.vlog.issues;

import static org.junit.Assert.assertTrue;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
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

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class Issue67 {

	@Ignore
	@Test
	public void part01() throws ParsingException, IOException {
		KnowledgeBase kb = new KnowledgeBase();

		RuleParser.parseInto(kb, "B1_(a, b, c, d, prov1) .");
		RuleParser.parseInto(kb, "B2_(a, a, c, prov2) . ");
		RuleParser.parseInto(kb, "H1_(a, n1_2_0, n1_2_0, n1_3_0, n1_4_0) .");
		RuleParser.parseInto(kb, "H2_(n1_3_0, n1_5_0, n1_6_0) .");
		RuleParser.parseInto(kb,
				"true(?x1) :- B1_(?x1, ?x2, ?y1, ?y2, ?F_1), B2_(?x1, ?x1, ?y1, ?F_2), H1_(?x1, ?z1, ?z1, ?z2, ?F_3), H2_(?z2, ?z3, ?F_4) .");

		Reasoner reasoner = new VLogReasoner(kb);
		reasoner.reason();
		Set<Fact> inferences = reasoner.getInferences().collect(Collectors.toSet());

		Fact query = RuleParser.parseFact("true(a).");
		assertTrue(inferences.contains(query));

		reasoner.close();
	}

}
