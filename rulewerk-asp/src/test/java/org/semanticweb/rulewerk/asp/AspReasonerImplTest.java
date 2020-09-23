package org.semanticweb.rulewerk.asp;

/*-
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.rulewerk.asp.implementation.AspReasonerImpl;
import org.semanticweb.rulewerk.asp.model.AspReasoner;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AspReasonerImplTest {

	final Variable x = Expressions.makeUniversalVariable("X");
	final Variable y = Expressions.makeExistentialVariable("Y");
	final Variable y2 = Expressions.makeUniversalVariable("Y");
	final Variable z = Expressions.makeUniversalVariable("Z");

	final Constant c = Expressions.makeAbstractConstant("c");
	final Constant d = Expressions.makeAbstractConstant("d");

	final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
	final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, z);
	final PositiveLiteral atom3 = Expressions.makePositiveLiteral("p", c, z);
	final PositiveLiteral atom4 = Expressions.makePositiveLiteral("q", x, y2, z);
	final NegativeLiteral negativeLiteral = Expressions.makeNegativeLiteral("r", x, d);

	@Test
	public void overApproximateFact() {
		Fact fact = Expressions.makeFact("p", c, d);
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatement(fact);
		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		KnowledgeBase overApproximatedKnowledgeBase = aspReasoner.getDatalogKnowledgeBase();
		assertEquals(1, overApproximatedKnowledgeBase.getStatements().size());
		assertTrue(overApproximatedKnowledgeBase.getStatements().contains(fact));
	}

	@Test
	public void overApproximateDataSourceDeclaration() throws MalformedURLException {
		final DataSource dataSource = new SparqlQueryResultDataSource(new URL("https://example.org/"), "var",
			"?var wdt:P31 wd:Q5 .");
		final Predicate predicate = Expressions.makePredicate("p", 3);
		final DataSourceDeclaration dataSourceDeclaration = new DataSourceDeclarationImpl(predicate, dataSource);
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatement(dataSourceDeclaration);
		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		KnowledgeBase overApproximatedKnowledgeBase = aspReasoner.getDatalogKnowledgeBase();
		assertEquals(1, overApproximatedKnowledgeBase.getStatements().size());
		assertTrue(overApproximatedKnowledgeBase.getStatements().contains(dataSourceDeclaration));
	}

	@Test
	public void overApproximateRule() {
		List<Literal> bodyList = Arrays.asList(atom1, negativeLiteral, atom4);
		Conjunction<Literal> body = Expressions.makeConjunction(bodyList);
		List<PositiveLiteral> headList = Collections.singletonList(atom2);
		Conjunction<PositiveLiteral> head = Expressions.makeConjunction(headList);
		Rule rule = Expressions.makeRule(head, body);
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatement(rule);
		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		KnowledgeBase overApproximatedKnowledgeBase = aspReasoner.getDatalogKnowledgeBase();

		Conjunction<Literal> positiveBodyConjunction = Expressions.makeConjunction(atom1, atom4);
		Conjunction<PositiveLiteral> bodyLiteralConjunction = Expressions.makeConjunction(
			Collections.singletonList(rule.getBodyVariablesLiteral()));
		Conjunction<Literal> bodyLiteralConjunction2 = Expressions.makeConjunction(
			Collections.singletonList(rule.getBodyVariablesLiteral()));
		Rule expectedRule1 = Expressions.makeRule(bodyLiteralConjunction, positiveBodyConjunction);
		Rule expectedRule2 = Expressions.makeRule(rule.getHead(), bodyLiteralConjunction2);

		assertEquals(2, overApproximatedKnowledgeBase.getStatements().size());
		assertTrue(overApproximatedKnowledgeBase.getStatements().contains(expectedRule1));
		assertTrue(overApproximatedKnowledgeBase.getStatements().contains(expectedRule2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void noExistentialVariables() {
		PositiveLiteral literalExistential = Expressions.makePositiveLiteral("p", y, x);
		List<Literal> bodyList = Arrays.asList(atom1, negativeLiteral, atom4);
		Conjunction<Literal> body = Expressions.makeConjunction(bodyList);
		List<PositiveLiteral> headList = Collections.singletonList(literalExistential);
		Conjunction<PositiveLiteral> head = Expressions.makeConjunction(headList);
		Rule rule = Expressions.makeRule(head, body);
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatement(rule);
		new AspReasonerImpl(knowledgeBase);
	}
}
