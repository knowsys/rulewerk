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
import org.mockito.Mockito;
import org.semanticweb.rulewerk.asp.implementation.AspReasonerImpl;
import org.semanticweb.rulewerk.asp.implementation.AspifGrounder;
import org.semanticweb.rulewerk.asp.implementation.AspifIdentifier;
import org.semanticweb.rulewerk.asp.model.AspReasoner;
import org.semanticweb.rulewerk.asp.model.Grounder;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AspifGrounderTest {

	final Variable x = Expressions.makeUniversalVariable("X");

	final Constant c = Expressions.makeAbstractConstant("c");
	final Constant d = Expressions.makeAbstractConstant("d");

	final Fact fact = Expressions.makeFact("p", d, c);
	final Fact fact2 = Expressions.makeFact("p", c, c);
	final Fact fact3 = Expressions.makeFact("q", c, d);

	@Test
	public void visitFactTest() throws IOException {
		Set<Predicate> approximatedPredicates = new HashSet<>();
		approximatedPredicates.add(Expressions.makePredicate("p", 2));
		AspifIdentifier.reset();
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(fact, fact2, fact3);
		Reasoner reasoner = new VLogReasoner(kb);
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		Grounder grounder = new AspifGrounder(kb, reasoner, bufferedWriter, approximatedPredicates);
		grounder.ground();
		bufferedWriter.flush();
		assertEquals("asp 1 0 0\n" +
			"1 0 1 1 0 0\n" +
			"1 0 1 2 0 0\n" +
			"4 1 2 1 2\n" +
			"4 1 1 1 1\n" +
			"0\n", writer.toString());
		Map<Integer, Literal> map = grounder.getIntegerLiteralMap();
		assertEquals(2, map.size());
		assertEquals(fact, map.getOrDefault(1, null));
		assertEquals(fact2, map.getOrDefault(2, null));
	}

	@Test
	public void visitRuleTest() throws IOException {
		Set<Predicate> approximatedPredicates = new HashSet<>();
		approximatedPredicates.add(Expressions.makePredicate("r", 1));
		approximatedPredicates.add(Expressions.makePredicate("s", 1));
		approximatedPredicates.add(Expressions.makePredicate("p", 3));
		approximatedPredicates.add(Expressions.makePredicate("q", 1));

		AspifIdentifier.reset();
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		Fact fact = Expressions.makeFact("r", c);
		Fact fact2 = Expressions.makeFact("r", d);
		Fact fact3 = Expressions.makeFact("s", c);
		Fact fact4 = Expressions.makeFact("s", d);
		Fact fact5 = Expressions.makeFact("t", c);
		Fact fact6 = Expressions.makeFact("t", d);
		knowledgeBase.addStatements(fact, fact2, fact3, fact4, fact5, fact6);

		Conjunction<PositiveLiteral> head = Expressions.makeConjunction(Arrays.asList(
			Expressions.makePositiveLiteral("p", c, x, d),
			Expressions.makePositiveLiteral("q", d)
		));
		Conjunction<Literal> body = Expressions.makeConjunction(Arrays.asList(
			Expressions.makePositiveLiteral("r", x),
			Expressions.makePositiveLiteral("t", x),
			Expressions.makeNegativeLiteral("s", x)
		));
		knowledgeBase.addStatement(Expressions.makeRule(head, body));
		knowledgeBase.addStatement(Expressions.makeRule(
			Expressions.makeConjunction(Collections.singletonList(Expressions.makePositiveLiteral("s", x))),
			Expressions.makeConjunction(Expressions.makeNegativeLiteral("s", x), Expressions.makePositiveLiteral("s", x))
		));
		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		Reasoner reasoner = new VLogReasoner(aspReasoner.getDatalogKnowledgeBase());
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		Grounder grounder = new AspifGrounder(knowledgeBase, reasoner, bufferedWriter, approximatedPredicates);
		grounder.ground();
		bufferedWriter.flush();

		assertEquals("asp 1 0 0\n" +
			"1 0 1 1 0 0\n" +
			"1 0 1 2 0 0\n" +
			"1 0 1 3 0 0\n" +
			"1 0 1 4 0 0\n" +
			"1 0 1 5 0 2 1 -3\n" +
			"1 0 1 6 0 2 1 -3\n" +
			"1 0 1 7 0 2 2 -4\n" +
			"1 0 1 6 0 2 2 -4\n" +
			"1 0 1 3 0 2 -3 3\n" +
			"1 0 1 4 0 2 -4 4\n" +
			"4 1 2 1 2\n" +
			"4 1 6 1 6\n" +
			"4 1 4 1 4\n" +
			"4 1 1 1 1\n" +
			"4 1 3 1 3\n" +
			"4 1 7 1 7\n" +
			"4 1 5 1 5\n" +
			"0\n", writer.toString());
		Map<Integer, Literal> map = grounder.getIntegerLiteralMap();
		assertEquals(7, map.size());
		assertEquals(fact, map.getOrDefault(1, null));
		assertEquals(fact2, map.getOrDefault(2, null));
		assertEquals(fact3, map.getOrDefault(3, null));
		assertEquals(fact4, map.getOrDefault(4, null));
		assertEquals(Expressions.makePositiveLiteral("p", c, c, d), map.getOrDefault(5, null));
		assertEquals(Expressions.makePositiveLiteral("q", d), map.getOrDefault(6, null));
		assertEquals(Expressions.makePositiveLiteral("p", c, d, d), map.getOrDefault(7, null));
	}

	@Test
	public void visitDataSourceDeclarationTest() throws IOException {
		AspifIdentifier.reset();
		Predicate approximatedPredicate = Expressions.makePredicate("p", 2);
		Predicate predicate = Expressions.makePredicate("q", 2);
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatements(new DataSourceDeclarationImpl(approximatedPredicate, new CsvFileDataSource("src/test/data/input/binaryFacts.csv")));
		knowledgeBase.addStatements(new DataSourceDeclarationImpl(predicate, new CsvFileDataSource("src/test/data/input/binaryFacts.csv")));

		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		Reasoner reasoner = new VLogReasoner(aspReasoner.getDatalogKnowledgeBase());
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		Grounder grounder = new AspifGrounder(knowledgeBase, reasoner, bufferedWriter, new HashSet<>(Collections.singletonList(approximatedPredicate)));
		grounder.ground();
		bufferedWriter.flush();

		assertEquals("asp 1 0 0\n" +
			"1 0 1 1 0 0\n" +
			"1 0 1 2 0 0\n" +
			"1 0 1 3 0 0\n" +
			"4 1 2 1 2\n" +
			"4 1 1 1 1\n" +
			"4 1 3 1 3\n" +
			"0\n", writer.toString());
		Map<Integer, Literal> map = grounder.getIntegerLiteralMap();
		assertEquals(3, map.size());
		assertEquals(Expressions.makePositiveLiteral("p", c, c), map.getOrDefault(1, null));
		assertEquals(Expressions.makePositiveLiteral("p", c, d), map.getOrDefault(2, null));
		assertEquals(Expressions.makePositiveLiteral("p", d, c), map.getOrDefault(3, null));
	}

	@Test
	public void groundWithIOException() throws IOException {
		AspifIdentifier.reset();
		Predicate predicate = Expressions.makePredicate("p", 2);
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatements(new DataSourceDeclarationImpl(predicate, new CsvFileDataSource("src/test/data/input/binaryFacts.csv")));
		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		Reasoner reasoner = new VLogReasoner(aspReasoner.getDatalogKnowledgeBase());
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		Grounder grounder = new AspifGrounder(knowledgeBase, reasoner, bufferedWriter, Collections.emptySet());
		bufferedWriter.close();
		grounder.ground();
	}

	@Test
	public void visitRuleWithIOExceptionTest() throws IOException {
		Set<Predicate> approximatedPredicates = new HashSet<>();
		approximatedPredicates.add(Expressions.makePredicate("s", 1));

		AspifIdentifier.reset();
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		Fact fact = Expressions.makeFact("r", c);
		knowledgeBase.addStatements(fact);

		knowledgeBase.addStatement(Expressions.makeRule(
			Expressions.makePositiveLiteral("s", x),
			Expressions.makeNegativeLiteral("s", x),
			Expressions.makePositiveLiteral("r", x)
		));
		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		Reasoner reasoner = new VLogReasoner(aspReasoner.getDatalogKnowledgeBase());

		BufferedWriter mockedWriter = Mockito.mock(BufferedWriter.class);
		doNothing().doThrow(new IOException()).when(mockedWriter).write(anyString());
		Grounder grounder = new AspifGrounder(knowledgeBase, reasoner, mockedWriter, approximatedPredicates);
		assertFalse(grounder.ground());
	}

	@Test
	public void visitFactWithIOExceptionTest() throws IOException {
		AspifIdentifier.reset();

		Predicate predicate = Expressions.makePredicate("p", 2);
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatements(Expressions.makeFact("p", c, d));
		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		Reasoner reasoner = new VLogReasoner(aspReasoner.getDatalogKnowledgeBase());

		BufferedWriter mockedWriter = Mockito.mock(BufferedWriter.class);
		doNothing().doThrow(new IOException()).when(mockedWriter).write(anyString());
		Grounder grounder = new AspifGrounder(knowledgeBase, reasoner, mockedWriter, Collections.singleton(predicate));
		assertFalse(grounder.ground());
	}

	@Test
	public void visitDataSourceDeclarationWithIOExceptionTest() throws IOException {
		AspifIdentifier.reset();
		Predicate predicate = Expressions.makePredicate("p", 2);
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatements(new DataSourceDeclarationImpl(predicate, new CsvFileDataSource("src/test/data/input/binaryFacts.csv")));

		AspReasoner aspReasoner = new AspReasonerImpl(knowledgeBase);
		Reasoner reasoner = new VLogReasoner(aspReasoner.getDatalogKnowledgeBase());

		BufferedWriter mockedWriter = Mockito.mock(BufferedWriter.class);
		doNothing().doThrow(new IOException()).when(mockedWriter).write(anyString());
		Grounder grounder = new AspifGrounder(knowledgeBase, reasoner, mockedWriter, Collections.singleton(predicate));
		assertFalse(grounder.ground());
	}

	@Test(expected = NullPointerException.class)
	public void knowledgeBaseNotNull() {
		new AspifGrounder(null,
			new VLogReasoner(new KnowledgeBase()),
			new BufferedWriter(new StringWriter()),
			new HashSet<>());
	}

	@Test(expected = NullPointerException.class)
	public void reasonerNotNull() {
		new AspifGrounder(new KnowledgeBase(),
			null,
			new BufferedWriter(new StringWriter()),
			new HashSet<>());
	}

	@Test(expected = NullPointerException.class)
	public void writerNotNull() {
		new AspifGrounder(new KnowledgeBase(),
			new VLogReasoner(new KnowledgeBase()),
			null,
			new HashSet<>());
	}

	@Test(expected = NullPointerException.class)
	public void approximatedPredicatesNotNull() {
		new AspifGrounder(new KnowledgeBase(),
			new VLogReasoner(new KnowledgeBase()),
			new BufferedWriter(new StringWriter()),
			null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void approximatedPredicatesNoNullElements() {
		new AspifGrounder(new KnowledgeBase(),
			new VLogReasoner(new KnowledgeBase()),
			new BufferedWriter(new StringWriter()),
			new HashSet<>(Collections.singletonList(null)));
	}
}
