package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

import karmaresearch.vlog.EDBConfigurationException;

public class QueryDataFromMemoryTest {

	@Ignore
	@Test
	public void queryResultWithBlanks() throws ReasonerStateException, EdbIdbSeparationException, IOException {
		final Variable vx = Expressions.makeVariable("x");
		final Variable vy = Expressions.makeVariable("y");
		// P(x) -> Q(y)
		final Rule existentialRule = Expressions.makeRule(Expressions.makeAtom("q", vy), Expressions.makeAtom("p", vx));
		assertEquals(Sets.newSet(vy), existentialRule.getExistentiallyQuantifiedVariables());
		final Constant constantC = Expressions.makeConstant("c");
		final Atom fact = Expressions.makeAtom("P", constantC);
		final Atom queryAtom = Expressions.makeAtom("P", Expressions.makeVariable("?x"));

		final List<Term> expectedQueryAnswerConstant = Arrays.asList(constantC);
		final Set<List<Term>> expectedQueryResultsExcludingBlanks = new HashSet<>();
		expectedQueryResultsExcludingBlanks.add(expectedQueryAnswerConstant);

		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addFacts(fact);
			reasoner.addRules(existentialRule);
			reasoner.load();
			reasoner.reason();
			final QueryResultIterator queryResultIteratorIncludeBlanks = reasoner.answerQuery(queryAtom, true);
			final Set<List<Term>> queryResultsIncludingBlanks = QueryResultUtils
					.gatherQueryResults(queryResultIteratorIncludeBlanks);
			assertTrue(queryResultsIncludingBlanks.size() == 2);
			assertTrue(queryResultsIncludingBlanks.contains(expectedQueryAnswerConstant));
			
			queryResultsIncludingBlanks.remove(expectedQueryAnswerConstant);
			assertTrue(queryResultsIncludingBlanks.size() == 1);
			// the two querry results are Constant "c" and one Blank
			queryResultsIncludingBlanks.forEach(queryResultTerms -> {
				assertTrue(queryResultTerms.size() == 1);
				queryResultTerms.forEach(term -> assertEquals(TermType.BLANK, term.getType()));
			});

			final QueryResultIterator queryResultIteratorExcludeBlanks = reasoner.answerQuery(queryAtom, false);
			final Set<List<Term>> queryResultsExcludingBlanks = QueryResultUtils
					.gatherQueryResults(queryResultIteratorExcludeBlanks);
			assertEquals(expectedQueryResultsExcludingBlanks, queryResultsExcludingBlanks);
		}

	}

	@Test
	public void queryEmptyKnowledgeBase() throws IOException, EdbIdbSeparationException, ReasonerStateException {
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.load();

			final Atom queryAtom = Expressions.makeAtom("P", Expressions.makeVariable("?x"));
			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true);
			Assert.assertFalse(queryResultIterator.hasNext());
			queryResultIterator.close();

			reasoner.reason();

			final QueryResultIterator queryResultIteratorAfterReason = reasoner.answerQuery(queryAtom, true);
			assertFalse(queryResultIteratorAfterReason.hasNext());
			queryResultIteratorAfterReason.close();
		}
	}

	@Test
	public void queryEmptyRules() throws IOException, EdbIdbSeparationException, ReasonerStateException {
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			final Atom fact = Expressions.makeAtom("P", Expressions.makeConstant("c"));
			reasoner.addFacts(fact);
			reasoner.load();

			final Atom queryAtom = Expressions.makeAtom("P", Expressions.makeVariable("?x"));

			reasoner.reason();

			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true);
			final Set<List<Term>> queryResults = QueryResultUtils.gatherQueryResults(queryResultIterator);
			@SuppressWarnings("unchecked")
			final Set<List<Term>> expectedQueryResults = Sets.newSet(Arrays.asList(Expressions.makeConstant("c")));
			assertEquals(expectedQueryResults, queryResults);
		}
	}

	@Test
	public void queryEmptyFacts()
			throws EDBConfigurationException, IOException, EdbIdbSeparationException, ReasonerStateException {
		final Variable vx = Expressions.makeVariable("x");
		final Rule rule = Expressions.makeRule(Expressions.makeAtom("q", vx), Expressions.makeAtom("p", vx));

		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addRules(rule);
			reasoner.load();

			final Atom queryAtom = Expressions.makeAtom("P", Expressions.makeVariable("?x"));
			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true);
			Assert.assertFalse(queryResultIterator.hasNext());
			queryResultIterator.close();

			reasoner.reason();

			final QueryResultIterator queryResultIteratorAfterReason = reasoner.answerQuery(queryAtom, true);
			assertFalse(queryResultIteratorAfterReason.hasNext());
			queryResultIteratorAfterReason.close();

		}
	}

}
