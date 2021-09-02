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

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.rulewerk.asp.implementation.AspReasonerImpl;
import org.semanticweb.rulewerk.asp.implementation.AspifIdentifier;
import org.semanticweb.rulewerk.asp.implementation.Clasp;
import org.semanticweb.rulewerk.asp.model.*;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
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
import static org.mockito.Mockito.*;

public class AspReasonerImplTest {

	final Variable x = Expressions.makeUniversalVariable("X");
	final Variable y = Expressions.makeExistentialVariable("Y");
	final Variable y2 = Expressions.makeUniversalVariable("Y");
	final Variable z = Expressions.makeUniversalVariable("Z");

	final Constant c = Expressions.makeAbstractConstant("c");
	final Constant d = Expressions.makeAbstractConstant("d");
	final Constant e = Expressions.makeAbstractConstant("e");

	final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
	final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, z);
	final PositiveLiteral atom3 = Expressions.makePositiveLiteral("p", c, z);
	final PositiveLiteral atom4 = Expressions.makePositiveLiteral("q", x, y2, z);
	final PositiveLiteral atom5 = Expressions.makePositiveLiteral("q", x, x, c);
	final PositiveLiteral atom6 = Expressions.makePositiveLiteral("r", x, d);
	final NegativeLiteral negativeLiteral = Expressions.makeNegativeLiteral("r", x, d);
	final NegativeLiteral negativeLiteral2 = Expressions.makeNegativeLiteral("q", x, c, d);
	final NegativeLiteral negativeLiteral3 = Expressions.makeNegativeLiteral("p", x, c);

	@Before
	public void setUp() {
		AspifIdentifier.reset();
	}

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

		BufferedReader reader = new BufferedReader(new StringReader(mockClaspAnswer(Collections.singletonList(""), AspReasoningState.SATISFIABLE)));
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		AspReasoner aspReasoner = mockClasp(new AspReasonerImpl(knowledgeBase), reader, writer);

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

		writer.flush();
		assertEquals("asp 1 0 0\n" +
			"0\n", stringWriter.toString());
	}

	@Test
	public void reasonWithClaspInterrupted() throws InterruptedException, IOException {
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		AspReasoner spiedReasoner = spy(new AspReasonerImpl(knowledgeBase));
		AspSolver mockedClasp = mock(Clasp.class);
		doThrow(new InterruptedException()).when(mockedClasp).solve();
		when(mockedClasp.getWriterToSolver()).thenReturn(mock(BufferedWriter.class));
		when(spiedReasoner.instantiateSolver(anyBoolean(), anyInt())).thenReturn(mockedClasp);
		assertFalse(spiedReasoner.reason());
	}

	@Test
	public void reasonWithFailedGrounding() throws IOException {
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		AspReasoner spiedReasoner = mockClasp(new AspReasonerImpl(knowledgeBase), mock(BufferedReader.class), mock(BufferedWriter.class));

		Grounder mockedGrounder = mock(Grounder.class);
		when(mockedGrounder.ground()).thenReturn(false);
		doReturn(mockedGrounder).when(spiedReasoner).instantiateGrounder(any());
		assertFalse(spiedReasoner.reason());
	}

	@Test(expected = NoSuchElementException.class)
	public void reasonReturnsNoAnswerSet() throws IOException {
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		AspReasoner spiedReasoner = spy(new AspReasonerImpl(knowledgeBase));
		AspSolver mockedClasp = mock(Clasp.class);
		BufferedReader reader = new BufferedReader(new StringReader("clasp -n 0 _delete_me\n" +
			"clasp version 3.3.5\n" +
			"Reading from _delete_me\n" +
			"Solving..."));
		when(mockedClasp.getReaderFromSolver()).thenReturn(reader);
		when(mockedClasp.getWriterToSolver()).thenReturn(mock(BufferedWriter.class));
		when(spiedReasoner.instantiateSolver(anyBoolean(), anyInt())).thenReturn(mockedClasp);
		spiedReasoner.reason();
	}

	@Test
	public void reasonCallsSolverOnlyIfNeeded() throws IOException {
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatement(Expressions.makeFact("p", c, d));

		Clasp clasp = mock(Clasp.class);
		when(clasp.getWriterToSolver()).thenReturn(mock(BufferedWriter.class));
		AspReasoner spiedReasoner = spy(new AspReasonerImpl(knowledgeBase));
		when(spiedReasoner.instantiateSolver(anyBoolean(), anyInt())).thenReturn(clasp);

		when(clasp.getReaderFromSolver()).thenReturn(new BufferedReader(new StringReader(mockClaspAnswer(Collections.singletonList(""), AspReasoningState.SATISFIABLE))));
		spiedReasoner.reason();
		verify(spiedReasoner, times(2)).instantiateSolver(anyBoolean(), anyInt());

		when(clasp.getReaderFromSolver()).thenReturn(new BufferedReader(new StringReader(mockClaspAnswer(Collections.singletonList(""), AspReasoningState.SATISFIABLE))));
		spiedReasoner.reason();
		verify(spiedReasoner, times(2)).instantiateSolver(anyBoolean(), anyInt());

		when(clasp.getReaderFromSolver()).thenReturn(new BufferedReader(new StringReader(mockClaspAnswer(Collections.singletonList(""), AspReasoningState.SATISFIABLE))));
		spiedReasoner.resetReasoner();
		spiedReasoner.reason();
		verify(spiedReasoner, times(3)).instantiateSolver(anyBoolean(), anyInt());
	}

	@Test
	public void getAnswerSetsWithInterruptedSolving() throws IOException, InterruptedException {
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		AspReasoner spiedReasoner = spy(new AspReasonerImpl(knowledgeBase));
		AspSolver mockedClasp = mock(Clasp.class);
		doThrow(new InterruptedException()).when(mockedClasp).solve();
		BufferedReader reader = new BufferedReader(new StringReader("clasp version 3.3.5\n" +
			"Reading from _delete_me\n" +
			"Solving...\n" +
			"Answer: 1\n" +
			"2\n" +
			"*** Info : (clingo): INTERRUPTED by signal!\n" +
			"SATISFIABLE\n" +
			"\n" +
			"INTERRUPTED  : 1\n" +
			"Models       : 1+\n"));
		when(mockedClasp.getReaderFromSolver()).thenReturn(reader);
		when(mockedClasp.getWriterToSolver()).thenReturn(mock(BufferedWriter.class));
		when(spiedReasoner.instantiateSolver(anyBoolean(), anyInt())).thenReturn(mockedClasp);
		AnswerSetIterator answerSetIterator = spiedReasoner.getAnswerSets();
		assertTrue(answerSetIterator.hasNext());
		assertEquals(AspReasoningState.INTERRUPTED, answerSetIterator.getReasoningState());
	}

	@Test
	public void getAnswerSetsWithFailedGrounding() throws IOException {
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		AspReasoner spiedReasoner = spy(new AspReasonerImpl(knowledgeBase));
		Grounder mockedGrounder = mock(Grounder.class);
		when(mockedGrounder.ground()).thenReturn(false);
		doReturn(mockedGrounder).when(spiedReasoner).instantiateGrounder(any());
		doReturn(mock(AspSolver.class)).when(spiedReasoner).instantiateSolver(anyBoolean(), anyInt());

		AnswerSetIterator answerSetIterator = spiedReasoner.getAnswerSets();
		assertEquals(AspReasoningState.ERROR, answerSetIterator.getReasoningState());
		assertFalse(answerSetIterator.hasNext());
	}

	@Test
	public void answerQueryWithIntermediateAnswerSets() throws IOException {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", c);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", d);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", c);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", d);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(Expressions.makeRule(atomA, atomNegB), Expressions.makeRule(atomB, atomNegA));

		BufferedReader reader = new BufferedReader(new StringReader("clasp version 3.3.5\n" +
			"Reading from .delete_me\n" +
			"Solving...\n" +
			"Answer: 1\n" +
			"2\n" +
			"Consequences: [0;1]\n" +
			"Answer: 2\n" +
			"\n" +
			"Consequences: [0;0]\n" +
			"SATISFIABLE\n" +
			"\n" +
			"Models       : 2\n" +
			"  Cautious   : yes\n" +
			"Consequences : 0\n" +
			"Calls        : 1\n" +
			"Time         : 0.001s (Solving: 0.00s 1st Model: 0.00s Unsat: 0.00s)\n" +
			"CPU Time     : 0.000s"));
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		AspReasoner reasoner = mockClasp(new AspReasonerImpl(kb), reader, writer);

		PositiveLiteral query = Expressions.makePositiveLiteral("p", x);
		QueryResultIterator queryResultIterator = reasoner.answerQuery(query, true);
		int countResults = 0;
		while (queryResultIterator.hasNext()) {
			queryResultIterator.next();
			countResults++;
		}
		assertEquals(0, countResults);

		writer.flush();
		assertEquals("asp 1 0 0\n" +
			"1 0 1 2 0 1 -1\n" +
			"1 0 1 1 0 1 -2\n" +
			"4 1 2 1 2\n" +
			"4 1 1 1 1\n" +
			"0\n", stringWriter.toString());
	}

	@Test(expected = RulewerkRuntimeException.class)
	public void answerQueryWithException() throws IOException {
		KnowledgeBase kb = new KnowledgeBase();
		AspReasoner reasoner = spy(new AspReasonerImpl(kb));
		doThrow(new IOException()).when(reasoner).reason();
		PositiveLiteral query = Expressions.makePositiveLiteral("p", x);
		reasoner.answerQuery(query, true);
	}

	@Test
	public void getAnswerSets() throws IOException {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", c);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", d);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", c);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", d);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(Expressions.makeRule(atomA, atomNegB), Expressions.makeRule(atomB, atomNegA));

		BufferedReader reader = new BufferedReader(new StringReader(mockClaspAnswer(Arrays.asList("2", "1"), AspReasoningState.SATISFIABLE)));
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		AspReasoner reasoner = mockClasp(new AspReasonerImpl(kb), reader, writer);

		AnswerSetIterator answerSetIterator = reasoner.getAnswerSets();
		AnswerSet answerSet1 = answerSetIterator.next();
		AnswerSet answerSet2 = answerSetIterator.next();
		assertFalse(answerSetIterator.hasNext());
		assertEquals(answerSet1.getLiterals(), Collections.singleton(atomA));
		assertEquals(answerSet2.getLiterals(), Collections.singleton(atomB));

		writer.flush();
		assertEquals("asp 1 0 0\n" +
			"1 0 1 2 0 1 -1\n" +
			"1 0 1 1 0 1 -2\n" +
			"4 1 2 1 2\n" +
			"4 1 1 1 1\n" +
			"0\n", stringWriter.toString());
	}

	@Test
	public void getAnswerSetsWithMaximum() throws IOException {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", c);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", d);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", c);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", d);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(Expressions.makeRule(atomA, atomNegB), Expressions.makeRule(atomB, atomNegA));

		BufferedReader reader = new BufferedReader(new StringReader(mockClaspAnswer(Collections.singletonList("2"), AspReasoningState.SATISFIABLE)));
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		AspReasoner reasoner = mockClasp(new AspReasonerImpl(kb), reader, writer);

		AnswerSetIterator answerSetIterator = reasoner.getAnswerSets(1);
		AnswerSet answerSet1 = answerSetIterator.next();
		assertFalse(answerSetIterator.hasNext());
		assertEquals(answerSet1.getLiterals(), Collections.singleton(atomA));

		writer.flush();
		assertEquals("asp 1 0 0\n" +
			"1 0 1 2 0 1 -1\n" +
			"1 0 1 1 0 1 -2\n" +
			"4 1 2 1 2\n" +
			"4 1 1 1 1\n" +
			"0\n", stringWriter.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getAnswerSetsNoNegativeMaximum() throws IOException {
		KnowledgeBase kb = new KnowledgeBase();
		AspReasoner reasoner = new AspReasonerImpl(kb);
		reasoner.getAnswerSets(-1);
	}

	@Test()
	public void getKnowledgeBaseTest() {
		KnowledgeBase kb = new KnowledgeBase();
		AspReasoner reasoner = new AspReasonerImpl(kb);
		assertEquals(kb, reasoner.getKnowledgeBase());
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

	@Test(expected = UnsupportedOperationException.class)
	public void isJAUnsupported() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		aspReasoner.isJA();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void isRJAUnsupported() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		aspReasoner.isRJA();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void isMFAUnsupported() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		aspReasoner.isMFA();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void isMFCUnsupported() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		aspReasoner.isMFC();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void isRMFAUnsupported() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		aspReasoner.isRMFA();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void checkForCyclesUnsupported() {
		AspReasoner aspReasoner = new AspReasonerImpl(new KnowledgeBase());
		aspReasoner.checkForCycles();
	}

	@Test
	public void getCorrectnessTest() throws IOException {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", c);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", d);
		NegativeLiteral atomNegA = Expressions.makeNegativeLiteral("p", c);
		NegativeLiteral atomNegB = Expressions.makeNegativeLiteral("q", d);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(Expressions.makeRule(atomA, atomNegB), Expressions.makeRule(atomB, atomNegA));

		BufferedReader reader = new BufferedReader(new StringReader(mockClaspAnswer(Collections.singletonList("2"), AspReasoningState.SATISFIABLE)));
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		AspReasoner reasoner = mockClasp(new AspReasonerImpl(kb), reader, writer);

		reasoner.reason();
		assertEquals(Correctness.SOUND_AND_COMPLETE, reasoner.getCorrectness());
	}

	@Test
	public void countQueryAnswersTest() throws IOException {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", x, y2);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", x, y2);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(Expressions.makeRule(atomA, atomB),
			Expressions.makeFact("p", c, c),
			Expressions.makeFact("p", c, d)
		);

		BufferedReader reader = new BufferedReader(new StringReader(mockClaspAnswer(Collections.singletonList(""), AspReasoningState.SATISFIABLE)));
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		AspReasoner reasoner = mockClasp(new AspReasonerImpl(kb), reader, writer);

		QueryAnswerCount queryAnswerCount = reasoner.countQueryAnswers(Expressions.makePositiveLiteral("p", x, c), false);
		assertEquals(Correctness.SOUND_AND_COMPLETE, queryAnswerCount.getCorrectness());
		assertEquals(1, queryAnswerCount.getCount());
	}

	@Test(expected = RulewerkRuntimeException.class)
	public void countQueryAnswersWithExceptionTest() throws IOException {
		AspReasoner aspReasoner = spy(new AspReasonerImpl(new KnowledgeBase()));
		doThrow(new IOException()).when(aspReasoner).reason();
		aspReasoner.countQueryAnswers(Expressions.makePositiveLiteral("p", x), false);
	}

	@Test(expected = RulewerkRuntimeException.class)
	public void exportQueryAnswersToCsvWithExceptionTest() throws IOException {
		AspReasoner aspReasoner = spy(new AspReasonerImpl(new KnowledgeBase()));
		doThrow(new IOException()).when(aspReasoner).reason();
		aspReasoner.exportQueryAnswersToCsv(Expressions.makePositiveLiteral("p", x), "file.csv", false);
	}

	@Test
	public void exportQueryAnswersToCsv() throws IOException {
		PositiveLiteral atomA = Expressions.makePositiveLiteral("p", x, y2);
		PositiveLiteral atomB = Expressions.makePositiveLiteral("q", x, y2);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(Expressions.makeRule(atomA, atomB),
			Expressions.makeFact("p", c, d, d),
			Expressions.makeFact("p", c, e, e),
			Expressions.makeFact("p", d, e, e),
			Expressions.makeFact("p", c, d, e)
		);

		BufferedReader reader = new BufferedReader(new StringReader(mockClaspAnswer(Collections.singletonList(""), AspReasoningState.SATISFIABLE)));
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		AspReasoner reasoner = mockClasp(new AspReasonerImpl(kb), reader, writer);

		String csvFile = FileDataSourceTestUtils.OUTPUT_FOLDER + FileDataSourceTestUtils.ternaryFacts + ".csv";
		reasoner.exportQueryAnswersToCsv(Expressions.makePositiveLiteral("p", c, x, x), csvFile, true);

		List<List<String>> fileContent = FileDataSourceTestUtils.getCSVContent(csvFile);
		assertEquals(2, fileContent.size());
		assertTrue(fileContent.contains(Arrays.asList(c.toString(), d.toString(), d.toString())));
		assertTrue(fileContent.contains(Arrays.asList(c.toString(), e.toString(), e.toString())));
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

	private AspReasoner mockClasp(AspReasoner reasoner, BufferedReader reader, BufferedWriter writer) throws IOException {
		Clasp clasp = mock(Clasp.class);
		when(clasp.getReaderFromSolver()).thenReturn(reader);
		when(clasp.getWriterToSolver()).thenReturn(writer);

		AspReasoner spiedReasoner = spy(reasoner);
		when(spiedReasoner.instantiateSolver(anyBoolean(), anyInt())).thenReturn(clasp);
		return spiedReasoner;
	}

	private String mockClaspAnswer(List<String> answerSetStrings, AspReasoningState state) {
		StringBuilder builder = new StringBuilder("clasp version 3.3.5\n" +
		"Reading from .delete_me\n" +
		"Solving...\n");

		int counter = 1;
		for (String answerSet : answerSetStrings) {
			builder.append("Answer: ").append(counter).append("\n");
			builder.append(answerSet).append("\n");
			counter++;
		}

		builder.append(state.name()).append("\n");
		builder.append("\n" +
			"Models       : 1+\n" +
			"Calls        : 1\n" +
			"Time         : 0.001s (Solving: 0.00s 1st Model: 0.00s Unsat: 0.00s)\n" +
			"CPU Time     : 0.000s\n");
		return builder.toString();
	}
}
