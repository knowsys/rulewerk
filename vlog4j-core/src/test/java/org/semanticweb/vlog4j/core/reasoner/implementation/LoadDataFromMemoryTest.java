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
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.BlankImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

import karmaresearch.vlog.EDBConfigurationException;

public class LoadDataFromMemoryTest {

	@Test(expected = EdbIdbSeparationException.class)
	public void loadEdbIdbNotSeparated()
			throws EDBConfigurationException, IOException, EdbIdbSeparationException, ReasonerStateException, IncompatiblePredicateArityException {
		final Variable vx = Expressions.makeVariable("x");
		final Rule rule = Expressions.makeRule(Expressions.makeAtom("q", vx), Expressions.makeAtom("p", vx));
		final Atom factIDBpredQ1 = Expressions.makeAtom("q", Expressions.makeConstant("c"));
		final Atom factEDBpredQ2 = Expressions.makeAtom("q", Expressions.makeConstant("d"),
				Expressions.makeConstant("d"));
		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addRules(rule);
			reasoner.addFacts(factIDBpredQ1, factEDBpredQ2);
			reasoner.load();
		}
	}

	@Test
	public void loadEdbIdbSeparated()
			throws EDBConfigurationException, IOException, EdbIdbSeparationException, ReasonerStateException, IncompatiblePredicateArityException {
		final Variable vx = Expressions.makeVariable("x");
		final Rule rule = Expressions.makeRule(Expressions.makeAtom("q", vx), Expressions.makeAtom("p", vx));
		final Atom factEDBpred = Expressions.makeAtom("q", Expressions.makeConstant("d"),
				Expressions.makeConstant("d"));

		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addRules(rule);
			reasoner.addFacts(factEDBpred);
			reasoner.load();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void addFactsWithVariableTerms() throws ReasonerStateException {
		final Atom factWithVariableTerms = Expressions.makeAtom("q", Expressions.makeConstant("d"),
				Expressions.makeVariable("x"));

		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addFacts(factWithVariableTerms);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void addFactsWithBlankTerms() throws ReasonerStateException {
		final Atom factWithBlankTerms = Expressions.makeAtom("q", Expressions.makeConstant("d"), new BlankImpl("b"));

		try (final VLogReasoner reasoner = new VLogReasoner()) {
			reasoner.addFacts(factWithBlankTerms);
		}
	}

}
