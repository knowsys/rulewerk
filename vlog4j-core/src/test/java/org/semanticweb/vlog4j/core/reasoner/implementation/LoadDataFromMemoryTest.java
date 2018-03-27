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
import org.semanticweb.vlog4j.core.reasoner.ReasonerInterface;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.Reasoner;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;

public class LoadDataFromMemoryTest {

	@Test(expected = EdbIdbSeparationException.class)
	public void loadEdbIdbNotSeparated() throws AlreadyStartedException, EDBConfigurationException, IOException,
			NotStartedException, EdbIdbSeparationException, ReasonerStateException {
		Variable vx = Expressions.makeVariable("x");
		Rule rule = Expressions.makeRule(Expressions.makeAtom("q", vx), Expressions.makeAtom("p", vx));
		Atom factIDBpred = Expressions.makeAtom("q", Expressions.makeConstant("c"));
		Atom factEDBpred = Expressions.makeAtom("q", Expressions.makeConstant("d"), Expressions.makeConstant("d"));
		ReasonerInterface reasoner = new Reasoner();
		reasoner.addRules(rule);
		reasoner.addFacts(factIDBpred, factEDBpred);
		try {
			reasoner.load();
		} finally {
			reasoner.dispose();
		}
	}

	@Test
	public void loadEdbIdbSeparated() throws AlreadyStartedException, EDBConfigurationException, IOException,
			NotStartedException, EdbIdbSeparationException, ReasonerStateException {
		Variable vx = Expressions.makeVariable("x");
		Rule rule = Expressions.makeRule(Expressions.makeAtom("q", vx), Expressions.makeAtom("p", vx));
		Atom factEDBpred = Expressions.makeAtom("q", Expressions.makeConstant("d"), Expressions.makeConstant("d"));

		ReasonerInterface reasoner = new Reasoner();
		reasoner.addRules(rule);
		reasoner.addFacts(factEDBpred);
		try {
			reasoner.load();
		} finally {
			reasoner.dispose();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void addFactsWithVariableTerms() throws ReasonerStateException {
		Atom factWithVariableTerms = Expressions.makeAtom("q", Expressions.makeConstant("d"),
				Expressions.makeVariable("x"));

		ReasonerInterface reasoner = new Reasoner();
		try {
			reasoner.addFacts(factWithVariableTerms);
		} finally {
			reasoner.dispose();
		}
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void addFactsWithBlankTerms() throws ReasonerStateException {
		Atom factWithBlankTerms = Expressions.makeAtom("q", Expressions.makeConstant("d"), new BlankImpl("b"));

		ReasonerInterface reasoner = new Reasoner();
		try {
			reasoner.addFacts(factWithBlankTerms);
		} finally {
			reasoner.dispose();
		}
	}

}
