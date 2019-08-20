package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
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

import org.junit.Test;
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

import karmaresearch.vlog.EDBConfigurationException;

public class ReasonerTest {

	final String constantNameC = "c";
	final String constantNameD = "d";

	final Constant constantC = Expressions.makeConstant(constantNameC);
	final Constant constantD = Expressions.makeConstant(constantNameD);
	final Variable x = Expressions.makeVariable("x");
	final PositiveLiteral factAc = Expressions.makePositiveLiteral("A", constantC);
	final PositiveLiteral factAd = Expressions.makePositiveLiteral("A", constantD);
	final PositiveLiteral atomAx = Expressions.makePositiveLiteral("A", x);
	final PositiveLiteral atomBx = Expressions.makePositiveLiteral("B", x);
	final PositiveLiteral atomCx = Expressions.makePositiveLiteral("C", x);
	final Rule ruleBxAx = Expressions.makeRule(atomBx, atomAx);
	final Rule ruleCxBx = Expressions.makeRule(atomCx, atomBx);

	@Test
	public void testCloseRepeatedly()
			throws EdbIdbSeparationException, IOException, IncompatiblePredicateArityException, ReasonerStateException {
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.close();
		}

		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.load();
			reasoner.close();
			reasoner.close();
		}
	}

	@Test
	public void testLoadRules()
			throws EdbIdbSeparationException, IOException, IncompatiblePredicateArityException, ReasonerStateException {
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addRules(ruleBxAx, ruleCxBx);
			reasoner.addRules(ruleBxAx);
			assertEquals(reasoner.getRules(), Arrays.asList(ruleBxAx, ruleCxBx, ruleBxAx));
		}
	}

	@Test
	public void testSimpleInference() throws EDBConfigurationException, IOException, ReasonerStateException,
			EdbIdbSeparationException, IncompatiblePredicateArityException {

		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addFacts(factAc, factAd);
			reasoner.addRules(ruleBxAx, ruleCxBx);
			reasoner.load();

			final QueryResultIterator cxQueryResultEnumBeforeReasoning = reasoner.answerQuery(atomCx, true);
			assertFalse(cxQueryResultEnumBeforeReasoning.hasNext());

			reasoner.reason();

			final QueryResultIterator cxQueryResultEnumAfterReasoning = reasoner.answerQuery(atomCx, true);
			final Set<List<Term>> actualResults = QueryResultsUtils
					.collectQueryResults(cxQueryResultEnumAfterReasoning);

			final Set<List<Constant>> expectedResults = new HashSet<>(
					Arrays.asList(Arrays.asList(constantC), Arrays.asList(constantD)));

			assertEquals(expectedResults, actualResults);
		}
	}

	@Test
	public void testGenerateDataSourcesConfigEmpty() throws ReasonerStateException, IOException {
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			final String dataSourcesConfig = reasoner.generateDataSourcesConfig();
			assertTrue(dataSourcesConfig.isEmpty());
		}
	}

}
