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
import org.semanticweb.rulewerk.asp.implementation.AnswerSetImpl;
import org.semanticweb.rulewerk.asp.model.AnswerSet;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AnswerSetImplTest {

	final Variable x = Expressions.makeUniversalVariable("X");
	final Variable y = Expressions.makeUniversalVariable("Z");

	final Constant c = Expressions.makeAbstractConstant("c");

	final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
	final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, y);
	final PositiveLiteral atom3 = Expressions.makePositiveLiteral("q", x, c);

	@Test(expected = NullPointerException.class)
	public void answerSetRepresentationNotNull() {
		new AnswerSetImpl(null, Collections.emptyMap());
	}

	@Test(expected = NullPointerException.class)
	public void integerLiteralMapNotNull() {
		new AnswerSetImpl("1", null);
	}

	@Test(expected = NullPointerException.class)
	public void noUnknownLiteralInteger() {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);
		new AnswerSetImpl("1 2 3 4", integerLiteralMap);
	}

	@Test(expected = NumberFormatException.class)
	public void onlyIntegersInAnswerSetRepresentation() {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);
		new AnswerSetImpl("1 2 3 a", integerLiteralMap);
	}

	@Test
	public void getQueryResultTest() {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);
		AnswerSet answerSet = new AnswerSetImpl("1 2 3", integerLiteralMap);
		QueryResultIterator queryResultIterator = answerSet.getQueryResults(Expressions.makePredicate("p", 2));
		Set<List<Term>> expectedQueryResults = new HashSet<>(Arrays.asList(atom1.getArguments(), atom2.getArguments()));
		int answerCounter = 0;
		while (queryResultIterator.hasNext()) {
			answerCounter++;
			assertTrue(expectedQueryResults.contains(queryResultIterator.next().getTerms()));
		}
		assertEquals(2, answerCounter);
	}

	@Test
	public void getLiteralsTest() {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);
		AnswerSet answerSet = new AnswerSetImpl("2 1 3", integerLiteralMap);
		Set<Literal> expectedLiterals = new HashSet<>(Arrays.asList(atom1, atom2, atom3));
		Set<Literal> actualLiterals = answerSet.getLiterals();
		int answerCounter = 0;
		for (Literal literal : actualLiterals) {
			answerCounter++;
			assertTrue(expectedLiterals.contains(literal));
		}
		assertEquals(3, answerCounter);
	}

	@Test
	public void getLiteralsByPredicateTest() {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);
		AnswerSet answerSet = new AnswerSetImpl("1 2 3", integerLiteralMap);
		Set<Literal> expectedLiterals = new HashSet<>(Collections.singletonList(atom3));
		Set<Literal> actualLiterals = answerSet.getLiterals(Expressions.makePredicate("q", 2));
		int answerCounter = 0;
		for (Literal literal : actualLiterals) {
			answerCounter++;
			assertTrue(expectedLiterals.contains(literal));
		}
		assertEquals(1, answerCounter);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void unmodifiableAnswers() {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);
		AnswerSet answerSet = new AnswerSetImpl("1 2 3", integerLiteralMap);
		answerSet.getLiterals(Expressions.makePredicate("q", 2)).add(atom1);
	}
}
