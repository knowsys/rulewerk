package org.semanticweb.vlog4j.core.reasoner.implementation;

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

import org.junit.Test;
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.BlankImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import karmaresearch.vlog.EDBConfigurationException;

public class LoadDataFromMemoryTest {

	@Test(expected = EdbIdbSeparationException.class)
	public void loadEdbIdbNotSeparated() throws EDBConfigurationException, IOException, EdbIdbSeparationException,
			ReasonerStateException, IncompatiblePredicateArityException {
		final Variable vx = Expressions.makeVariable("x");
		final Rule rule = Expressions.makeRule(Expressions.makePositiveLiteral("q", vx),
				Expressions.makePositiveLiteral("p", vx));
		final PositiveLiteral factIDBpredQ1 = Expressions.makePositiveLiteral("q", Expressions.makeConstant("c"));
		final PositiveLiteral factEDBpredQ2 = Expressions.makePositiveLiteral("q", Expressions.makeConstant("d"),
				Expressions.makeConstant("d"));
		final VLogKnowledgeBase kb = new VLogKnowledgeBase();
		kb.addRules(rule);
		kb.addFacts(factIDBpredQ1, factEDBpredQ2);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
		}
	}

	@Test
	public void loadEdbIdbSeparated() throws EDBConfigurationException, IOException, EdbIdbSeparationException,
			ReasonerStateException, IncompatiblePredicateArityException {
		final Variable vx = Expressions.makeVariable("x");
		final Rule rule = Expressions.makeRule(Expressions.makePositiveLiteral("q", vx),
				Expressions.makePositiveLiteral("p", vx));
		final PositiveLiteral factEDBpred = Expressions.makePositiveLiteral("q", Expressions.makeConstant("d"),
				Expressions.makeConstant("d"));
		final VLogKnowledgeBase kb = new VLogKnowledgeBase();
		kb.addRules(rule);
		kb.addFacts(factEDBpred);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
		}
	}

	// TODO move to a test class for KnowledgeBase
	@Test(expected = IllegalArgumentException.class)
	public void addFactsWithVariableTerms() throws ReasonerStateException {
		final PositiveLiteral factWithVariableTerms = Expressions.makePositiveLiteral("q",
				Expressions.makeConstant("d"), Expressions.makeVariable("x"));
		final KnowledgeBase kb = new VLogKnowledgeBase();
		kb.addFacts(factWithVariableTerms);
	}

	// TODO move to a test class for KnowledgeBase
	@Test(expected = IllegalArgumentException.class)
	public void addFactsWithBlankTerms() throws ReasonerStateException {
		final PositiveLiteral factWithBlankTerms = Expressions.makePositiveLiteral("q", Expressions.makeConstant("d"),
				new BlankImpl("b"));
		final KnowledgeBase kb = new VLogKnowledgeBase();
		kb.addFacts(factWithBlankTerms);
	}

}
