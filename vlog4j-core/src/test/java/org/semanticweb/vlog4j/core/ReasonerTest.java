package org.semanticweb.vlog4j.core;

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
import java.util.List;

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.AtomImpl;
import org.semanticweb.vlog4j.core.model.ConstantImpl;
import org.semanticweb.vlog4j.core.model.Rule;
import org.semanticweb.vlog4j.core.model.RuleImpl;
import org.semanticweb.vlog4j.core.model.VariableImpl;
import org.semanticweb.vlog4j.core.model.validation.VLog4jAtomValidationException;
import org.semanticweb.vlog4j.core.model.validation.VLog4jRuleValidationException;
import org.semanticweb.vlog4j.core.model.validation.VLog4jTermValidationException;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.ReasonerImpl;

import junit.framework.TestCase;
import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;

public class ReasonerTest extends TestCase {

	public void testSimpleInference() throws VLog4jTermValidationException, VLog4jAtomValidationException, VLog4jRuleValidationException,
			AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {
		final Atom factAc = new AtomImpl("A", new ConstantImpl("c"));
		final Atom factAd = new AtomImpl("A", new ConstantImpl("d"));
		final Atom atomAx = new AtomImpl("A", new VariableImpl("X"));
		final Atom atomBx = new AtomImpl("B", new VariableImpl("X"));
		final Atom atomCx = new AtomImpl("C", new VariableImpl("X"));
		final Rule ruleBxAx = new RuleImpl(Arrays.asList(atomBx), Arrays.asList(atomAx));
		final Rule ruleCxBx = new RuleImpl(Arrays.asList(atomCx), Arrays.asList(atomBx));

		final Reasoner reasoner = new ReasonerImpl();
		reasoner.addFacts(factAc, factAd);
		reasoner.addRules(ruleBxAx, ruleCxBx);
		reasoner.load();
		reasoner.reason();
		final List<List<String>> answers = reasoner.compileQuerySet(atomCx);

		for (final List<String> answer : answers) {
			System.out.println(answer);
		}

	}
}
