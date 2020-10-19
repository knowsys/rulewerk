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

import org.junit.Test;
import org.semanticweb.rulewerk.asp.implementation.AspReasonerImpl;
import org.semanticweb.rulewerk.asp.model.AnswerSet;
import org.semanticweb.rulewerk.asp.model.AnswerSetIterator;
import org.semanticweb.rulewerk.asp.model.AspReasoner;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

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

	@Test
	public void answerQueryForFactsOnly() throws IOException {
		PositiveLiteral query = Expressions.makePositiveLiteral("p", x, y);

		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatement(Expressions.makeFact("p", c, d));
		knowledgeBase.addStatement(Expressions.makeFact("p", d, c));
		knowledgeBase.addStatement(Expressions.makeFact("q", d, d));
		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);

		assertTrue(aspReasoner.reason());
		QueryResultIterator queryResultIterator = aspReasoner.answerQuery(query, false);
		Set<List<Term>> expectedQueryResults = new HashSet<>();
		expectedQueryResults.add(Arrays.asList(c, d));
		expectedQueryResults.add(Arrays.asList(d, c));
		int answerCounter = 0;
		while (queryResultIterator.hasNext()) {
			answerCounter++;
			assertTrue(expectedQueryResults.contains(queryResultIterator.next().getTerms()));
		}
		assertEquals(2, answerCounter);
	}

	@Test
	public void answerQueryWithIntermediateAnswerSets() {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", c);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", d);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", c);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", d);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(Expressions.makeRule(atomA, atomNegB), Expressions.makeRule(atomB, atomNegA));

		AspReasoner reasoner = new AspReasonerImpl(kb);
		PositiveLiteral query = Expressions.makePositiveLiteral("p", x);
		QueryResultIterator queryResultIterator = reasoner.answerQuery(query, true);
		int countResults = 0;
		while (queryResultIterator.hasNext()) {
			queryResultIterator.next();
			countResults++;
		}
		assertEquals(0, countResults);
	}

	@Test
	public void getAnswerSets() throws IOException {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", c);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", d);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", c);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", d);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(Expressions.makeRule(atomA, atomNegB), Expressions.makeRule(atomB, atomNegA));
		AspReasoner reasoner = new AspReasonerImpl(kb);
		AnswerSetIterator answerSetIterator = reasoner.getAnswerSets();
		AnswerSet answerSet1 = answerSetIterator.next();
		AnswerSet answerSet2 = answerSetIterator.next();
		assertFalse(answerSetIterator.hasNext());
		assertEquals(answerSet1.getLiterals(), Collections.singleton(atomA));
		assertEquals(answerSet2.getLiterals(), Collections.singleton(atomB));
	}

	@Test
	public void getAnswerSetsWithMaximum() throws IOException {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", c);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", d);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", c);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", d);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(Expressions.makeRule(atomA, atomNegB), Expressions.makeRule(atomB, atomNegA));
		AspReasoner reasoner = new AspReasonerImpl(kb);
		AnswerSetIterator answerSetIterator = reasoner.getAnswerSets(1);
		AnswerSet answerSet1 = answerSetIterator.next();
		assertFalse(answerSetIterator.hasNext());
		assertEquals(answerSet1.getLiterals(), Collections.singleton(atomA));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getAnswerSetsNoNegativeMaximum() throws IOException {
		KnowledgeBase kb = new KnowledgeBase();
		AspReasoner reasoner = new AspReasonerImpl(kb);
		reasoner.getAnswerSets(-1);
	}
}
