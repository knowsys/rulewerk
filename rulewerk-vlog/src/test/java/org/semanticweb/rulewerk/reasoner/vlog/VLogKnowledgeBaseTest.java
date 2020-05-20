package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;

public class VLogKnowledgeBaseTest {
	private KnowledgeBase knowledgeBase = new KnowledgeBase();
	private Predicate p = Expressions.makePredicate("P", 1);
	private Predicate q = Expressions.makePredicate("Q", 1);
	private UniversalVariable x = Expressions.makeUniversalVariable("x");
	private AbstractConstant c = Expressions.makeAbstractConstant("c");
	private Fact fact = Expressions.makeFact(p, c);
	private PositiveLiteral literal = Expressions.makePositiveLiteral(p, x);
	private Rule rule = Expressions.makeRule(literal, literal);

	@Test
	public void hasData_noData_returnsFalse() {
		VLogKnowledgeBase vKB = new VLogKnowledgeBase(knowledgeBase);
		assertFalse(vKB.hasData());
	}

	@Test
	public void hasData_noAliasedPredicates_returnsTrue() {
		knowledgeBase.addStatement(fact);
		VLogKnowledgeBase vKB = new VLogKnowledgeBase(knowledgeBase);
		assertTrue(vKB.hasData());
	}

	@Test
	public void hasData_onlyAliasedPredicates_returnsTrue() {
		knowledgeBase.addStatement(rule);
		knowledgeBase.addStatement(fact);
		VLogKnowledgeBase vKB = new VLogKnowledgeBase(knowledgeBase);
		assertTrue(vKB.hasData());
	}

	@Test
	public void hasData_bothUnaliasedAndAliasedPredicates_returnsTrue() {
		knowledgeBase.addStatement(Expressions.makeFact(q, c));
		knowledgeBase.addStatement(rule);
		knowledgeBase.addStatement(fact);
		VLogKnowledgeBase vKB = new VLogKnowledgeBase(knowledgeBase);
		assertTrue(vKB.hasData());
	}
}
