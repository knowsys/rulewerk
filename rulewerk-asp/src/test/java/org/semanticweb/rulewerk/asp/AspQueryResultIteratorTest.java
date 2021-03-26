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
import org.semanticweb.rulewerk.asp.implementation.AspQueryResultIterator;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AspQueryResultIteratorTest {

	final Variable x = Expressions.makeUniversalVariable("X");
	final Variable y = Expressions.makeUniversalVariable("Z");

	final Constant c = Expressions.makeAbstractConstant("c");

	final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
	final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, y);
	final PositiveLiteral atom3 = Expressions.makePositiveLiteral("q", x, c);

	@Test(expected = NullPointerException.class)
	public void noNullSetOfAnswers() {
		new AspQueryResultIterator(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noNullAnswers() {
		new AspQueryResultIterator(new HashSet<>(Arrays.asList(atom1, null, atom2)));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void correctnessCheckNotSupported() {
		Set<Literal> literalSet = new HashSet<>(Arrays.asList(atom1, atom2, atom3));
		QueryResultIterator queryResultIterator = new AspQueryResultIterator(literalSet);
		queryResultIterator.getCorrectness();
	}

	@Test
	public void returnCorrectQueryResultsTest() {
		Set<Literal> literalSet = new HashSet<>(Arrays.asList(atom1, atom2, atom3));
		Set<List<Term>> expectedAnswers = new HashSet<>();
		for (Literal literal : literalSet) {
			expectedAnswers.add(literal.getArguments());
		}

		QueryResultIterator queryResultIterator = new AspQueryResultIterator(literalSet);
		int answerCounter = 0;
		while (queryResultIterator.hasNext()) {
			answerCounter++;
			assertTrue(expectedAnswers.contains(queryResultIterator.next().getTerms()));
		}
		assertEquals(3, answerCounter);
		queryResultIterator.close();
	}

	@Test
	public void uniqueResultsTest() {
		Set<Literal> literalSet = new HashSet<>(Arrays.asList(atom1, atom2, atom3, atom1));
		QueryResultIterator queryResultIterator = new AspQueryResultIterator(literalSet);
		int answerCounter = 0;
		while (queryResultIterator.hasNext()) {
			answerCounter++;
			queryResultIterator.next();
		}
		assertEquals(3, answerCounter);
		queryResultIterator.close();
	}
}
