package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.assertEquals;

import java.io.File;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.QueryResultIterator;

public class VLogReasonerCombinedInputs {

	final Variable vx = Expressions.makeVariable("x");
	final Predicate q = Expressions.makePredicate("q", 1);
	final Rule rulePimpliesQ = Expressions.makeRule(Expressions.makePositiveLiteral("q", vx),
			Expressions.makePositiveLiteral("p", vx));

	final Fact factQc = Expressions.makeFact(q, Arrays.asList(Expressions.makeConstant("c")));
	final Fact factQc1 = Expressions.makeFact(q, Arrays.asList(Expressions.makeConstant("c1")));
	final Fact factQc2 = Expressions.makeFact(q, Arrays.asList(Expressions.makeConstant("c2")));
	final Fact factQd = Expressions.makeFact(q, Arrays.asList(Expressions.makeConstant("d")));
	final Fact factPd = Expressions.makeFact("p", Arrays.asList(Expressions.makeConstant("d")));
	final PositiveLiteral queryQx = Expressions.makePositiveLiteral(q, Arrays.asList(Expressions.makeVariable("x")));

	final Set<List<Term>> resultsCC1C2D = new HashSet<>(
			Arrays.asList(Collections.singletonList(Expressions.makeConstant("c")),
					Collections.singletonList(Expressions.makeConstant("c1")),
					Collections.singletonList(Expressions.makeConstant("c2")),
					Collections.singletonList(Expressions.makeConstant("d"))));

	final DataSourceDeclaration qFromCsv;
	final DataSourceDeclaration qCDFromCsv;

	public VLogReasonerCombinedInputs() throws IOException {
		qFromCsv = new DataSourceDeclarationImpl(q, new CsvFileDataSource(new File(
				FileDataSourceTestUtils.INPUT_FOLDER + FileDataSourceTestUtils.unzippedUnaryCsvFileRoot + ".csv")));
		qCDFromCsv = new DataSourceDeclarationImpl(q,
				new CsvFileDataSource(new File(FileDataSourceTestUtils.INPUT_FOLDER + "unaryFactsCD.csv")));
	}

	@Test
	public void samePredicateSourceFactRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(qFromCsv, factQc, factPd, rulePimpliesQ);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryQx, true);
			final Set<List<Term>> queryResult = QueryResultsUtils.collectQueryResults(queryResultIterator);
			assertEquals(resultsCC1C2D, queryResult);
		}
	}

	@Test
	public void samePredicateFactSourceRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factQc, factPd, qFromCsv, rulePimpliesQ);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryQx, true);
			final Set<List<Term>> queryResult = QueryResultsUtils.collectQueryResults(queryResultIterator);
			assertEquals(resultsCC1C2D, queryResult);
		}
	}

	@Test
	public void samePredicateRuleFactSource() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(rulePimpliesQ, factQc, factPd, qFromCsv);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryQx, true);
			final Set<List<Term>> queryResult = QueryResultsUtils.collectQueryResults(queryResultIterator);
			assertEquals(resultsCC1C2D, queryResult);
		}
	}

	@Test
	public void samePredicateSourceSource() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(qFromCsv, qCDFromCsv);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryQx, true);
			final Set<List<Term>> queryResult = QueryResultsUtils.collectQueryResults(queryResultIterator);
			assertEquals(resultsCC1C2D, queryResult);
		}
	}

	@Test
	public void samePredicateSourceFactFact() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(qFromCsv, factQc, factQd);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryQx, true);
			final Set<List<Term>> queryResult = QueryResultsUtils.collectQueryResults(queryResultIterator);
			assertEquals(resultsCC1C2D, queryResult);
		}
	}

	@Test
	public void samePredicateFactsRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factPd, factQc, factQc1, factQc2, rulePimpliesQ);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();
			final QueryResultIterator queryResultIterator = reasoner.answerQuery(queryQx, true);
			final Set<List<Term>> queryResult = QueryResultsUtils.collectQueryResults(queryResultIterator);
			assertEquals(resultsCC1C2D, queryResult);
		}
	}

}
