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

import org.junit.After;
import org.junit.Test;
import org.semanticweb.rulewerk.asp.implementation.AspReasonerImpl;
import org.semanticweb.rulewerk.asp.model.AnswerSet;
import org.semanticweb.rulewerk.asp.model.AnswerSetIterator;
import org.semanticweb.rulewerk.asp.model.AspReasoner;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.*;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
	final PositiveLiteral atom5 = Expressions.makePositiveLiteral("q", x, x, c);
	final PositiveLiteral atom6 = Expressions.makePositiveLiteral("r", x, d);
	final NegativeLiteral negativeLiteral = Expressions.makeNegativeLiteral("r", x, d);
	final NegativeLiteral negativeLiteral2 = Expressions.makeNegativeLiteral("q", x, c, d);
	final NegativeLiteral negativeLiteral3 = Expressions.makeNegativeLiteral("p", x, c);

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
		Rule rule = Expressions.makeRule(
			Expressions.makeConjunction(Collections.singletonList(atom6)),
			Expressions.makeConjunction(atom2, negativeLiteral2));
		Rule rule2 = Expressions.makeRule(
			Expressions.makeConjunction(Collections.singletonList(atom5)),
			Expressions.makeConjunction(atom6, negativeLiteral3));

		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatements(rule, rule2);
		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		KnowledgeBase overApproximatedKnowledgeBase = aspReasoner.getDatalogKnowledgeBase();

		Rule expectedRule1 = Expressions.makeRule(
			Expressions.makeConjunction(Arrays.asList(atom6, AspReasonerImpl.getBodyVariablesLiteral(rule, 1))),
			Expressions.makeConjunction(atom2));
		Rule expectedRule2 = Expressions.makeRule(
			Expressions.makeConjunction(Arrays.asList(atom5, AspReasonerImpl.getBodyVariablesLiteral(rule2, 2))),
			Expressions.makeConjunction(atom6, negativeLiteral3));

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

	@Test(expected = IllegalArgumentException.class)
	public void allVariablesOccurInPositiveBodyLiteral() {
		List<Literal> bodyList = Collections.singletonList(negativeLiteral);
		Conjunction<Literal> body = Expressions.makeConjunction(bodyList);
		List<PositiveLiteral> headList = Collections.singletonList(atom1);
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

	@Test
	public void onStatementAddedTest() throws IOException {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", x);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", x);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", x);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", x);
		PositiveLiteral atomC = Expressions.makePositiveLiteral("r", x);

		KnowledgeBase kb = new KnowledgeBase();
		Rule ruleA = Expressions.makeRule(atomA, atomNegB, atomC);
		Rule ruleB = Expressions.makeRule(atomB, atomNegA, atomC);
		kb.addStatements(ruleA);
		AspReasoner reasoner = new AspReasonerImpl(kb);
		kb.addStatement(ruleB);

		List<Statement> expectedList = new ArrayList<>();
		expectedList.add(Expressions.makeRule(
			Expressions.makeConjunction(Arrays.asList(atomA, AspReasonerImpl.getBodyVariablesLiteral(ruleA, 1))),
			Expressions.makeConjunction(atomC)
		));
		expectedList.add(Expressions.makeRule(
			Expressions.makeConjunction(Arrays.asList(atomB, AspReasonerImpl.getBodyVariablesLiteral(ruleB, 2))),
			Expressions.makeConjunction(atomC)
		));
		assertEquals(expectedList, reasoner.getDatalogKnowledgeBase().getRules());
	}

	@Test
	public void onStatementsAddedTest() {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", x);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", x);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", x);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", x);
		PositiveLiteral atomC = Expressions.makePositiveLiteral("r", x);

		KnowledgeBase kb = new KnowledgeBase();
		Rule ruleA = Expressions.makeRule(atomA, atomNegB, atomC);
		Rule ruleB = Expressions.makeRule(atomB, atomNegA, atomC);
		kb.addStatements(ruleA);
		AspReasoner reasoner = new AspReasonerImpl(kb);
		kb.addStatements(ruleB, ruleA);

		List<Statement> expectedList = new ArrayList<>();
		expectedList.add(Expressions.makeRule(
			Expressions.makeConjunction(Arrays.asList(atomA, AspReasonerImpl.getBodyVariablesLiteral(ruleA, 1))),
			Expressions.makeConjunction(atomC)
		));
		expectedList.add(Expressions.makeRule(
			Expressions.makeConjunction(Arrays.asList(atomB, AspReasonerImpl.getBodyVariablesLiteral(ruleB, 2))),
			Expressions.makeConjunction(atomC)
		));
		assertEquals(expectedList, reasoner.getDatalogKnowledgeBase().getRules());
	}

	@Test
	public void onStatementRemovedTest() {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", x);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", x);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", x);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", x);
		PositiveLiteral atomC = Expressions.makePositiveLiteral("r", x);

		KnowledgeBase kb = new KnowledgeBase();
		Rule ruleA = Expressions.makeRule(atomA, atomNegB, atomC);
		Rule ruleB = Expressions.makeRule(atomB, atomNegA, atomC);
		kb.addStatements(ruleA, ruleB);
		AspReasoner reasoner = new AspReasonerImpl(kb);
		kb.removeStatement(ruleB);

		List<Statement> expectedList = new ArrayList<>();
		expectedList.add(Expressions.makeRule(
			Expressions.makeConjunction(Collections.singletonList(atomA)),
			Expressions.makeConjunction(atomNegB, atomC)
		));
		assertEquals(expectedList, reasoner.getDatalogKnowledgeBase().getRules());
	}

	@Test
	public void onStatementsRemovedTest() {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", x);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", x);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", x);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", x);
		PositiveLiteral atomC = Expressions.makePositiveLiteral("r", x);

		KnowledgeBase kb = new KnowledgeBase();
		Rule ruleA = Expressions.makeRule(atomA, atomNegB, atomC);
		Rule ruleB = Expressions.makeRule(atomB, atomNegA, atomC);
		kb.addStatements(ruleA, ruleB);
		AspReasoner reasoner = new AspReasonerImpl(kb);
		kb.removeStatements(ruleB);

		List<Statement> expectedList = new ArrayList<>();
		expectedList.add(Expressions.makeRule(
			Expressions.makeConjunction(Collections.singletonList(atomA)),
			Expressions.makeConjunction(atomNegB, atomC)
		));
		assertEquals(expectedList, reasoner.getDatalogKnowledgeBase().getRules());
	}

	@Test
	public void getBodyVariablesLiteral() {
		List<Literal> bodyList = Arrays.asList(atom1, negativeLiteral, atom4);
		List<PositiveLiteral> headList = Collections.singletonList(atom2);
		Conjunction<Literal> body = Expressions.makeConjunction(bodyList);
		Conjunction<PositiveLiteral> head = Expressions.makeConjunction(headList);
		Rule rule = Expressions.makeRule(head, body);
		PositiveLiteral expectedLiteral = Expressions.makePositiveLiteral("_rule_" + 1, x, y2, z);
		assertEquals(expectedLiteral, AspReasonerImpl.getBodyVariablesLiteral(rule, 1));

		PositiveLiteral groundAtom = Expressions.makePositiveLiteral("p", c);
		PositiveLiteral groundAtom2 = Expressions.makePositiveLiteral("q", d);
		Conjunction<Literal> body2 = Expressions.makeConjunction(Collections.singletonList(groundAtom));
		Conjunction<PositiveLiteral> head2 = Expressions.makeConjunction(Collections.singletonList(groundAtom2));
		Rule rule2 = Expressions.makeRule(head2, body2);
		Constant constant = Expressions.makeAbstractConstant("_0");
		PositiveLiteral expectedLiteral2 = Expressions.makePositiveLiteral("_rule_" + 2, constant);
		assertEquals(expectedLiteral2, AspReasonerImpl.getBodyVariablesLiteral(rule2, 2));
	}

	@Test
	public void setAndGetLogLevel() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		assertEquals(LogLevel.WARNING, aspReasoner.getLogLevel());
		aspReasoner.setLogLevel(LogLevel.INFO);
		assertEquals(LogLevel.INFO, aspReasoner.getLogLevel());
	}

	@Test
	public void setAndGetTimeout() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		assertNull(aspReasoner.getReasoningTimeout());
		aspReasoner.setReasoningTimeout(100);
		assertEquals((Integer) 100, aspReasoner.getReasoningTimeout());
	}

	@Test
	public void setAndGetAlgorithm() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		assertEquals(Algorithm.RESTRICTED_CHASE, aspReasoner.getAlgorithm());
		aspReasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
		assertEquals(Algorithm.SKOLEM_CHASE, aspReasoner.getAlgorithm());
	}

	@Test
	public void setAndGetRuleRewriteStrategy() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		assertEquals(RuleRewriteStrategy.NONE, aspReasoner.getRuleRewriteStrategy());
		aspReasoner.setRuleRewriteStrategy(RuleRewriteStrategy.SPLIT_HEAD_PIECES);
		assertEquals(RuleRewriteStrategy.SPLIT_HEAD_PIECES, aspReasoner.getRuleRewriteStrategy());
	}

	/**
	 * Utility function to over-approximate a rule.
	 * Re-implements the functionality of the private over-approximation visitor from the ASP reasoner implementation
	 * and should not be used to verify the correctness of the visitor.
	 *
	 * @param statement the rule to over-approximate
	 * @param index a unique rule index
	 * @return list of statements
	 */
	private List<Statement> overApproximatedRule(Rule statement, int index) {
		List<Literal> positiveBodyLiterals = statement.getBody().getLiterals().stream().filter(
			literal -> !literal.isNegated()).collect(Collectors.toList());

		Conjunction<Literal> positiveBodyConjunction = Expressions.makeConjunction(positiveBodyLiterals);
		PositiveLiteral bodyVariableLiteral = AspReasonerImpl.getBodyVariablesLiteral(statement, index);
		Conjunction<PositiveLiteral> bodyVariablesLiteralConjunction = Expressions.makePositiveConjunction(bodyVariableLiteral);

		List<Statement> rules = new ArrayList<>();
		if (positiveBodyLiterals.isEmpty()) {
			rules.add(Expressions.makeFact(bodyVariableLiteral.getPredicate(), bodyVariableLiteral.getArguments()));
		} else {
			rules.add(Expressions.makeRule(bodyVariablesLiteralConjunction, positiveBodyConjunction));
		}
		rules.add(Expressions.makePositiveLiteralsRule(statement.getHead(), bodyVariablesLiteralConjunction));
		return rules;
	}
}
