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
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.impl.Expressions;
import org.semanticweb.vlog4j.core.model.validation.AtomValidationException;
import org.semanticweb.vlog4j.core.model.validation.BlankNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.ConstantNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.IllegalEntityNameException;
import org.semanticweb.vlog4j.core.model.validation.PredicateNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.RuleValidationException;
import org.semanticweb.vlog4j.core.model.validation.VariableNameValidationException;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.ReasonerImpl;

import junit.framework.TestCase;
import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.StringQueryResultEnumeration;

public class ReasonerTest extends TestCase {

	public void testSimpleInference() throws AtomValidationException, IllegalEntityNameException, RuleValidationException, PredicateNameValidationException,
			BlankNameValidationException, ConstantNameValidationException, VariableNameValidationException, AlreadyStartedException, EDBConfigurationException,
			IOException, NotStartedException {
		final String constantNameC = "c";
		final String constantNameD = "d";

		final Variable x = Expressions.makeVariable("x");
		final Atom factAc = Expressions.makeAtom("A", Expressions.makeConstant(constantNameC));
		final Atom factAd = Expressions.makeAtom("A", Expressions.makeConstant(constantNameD));
		final Atom atomAx = Expressions.makeAtom("A", x);
		final Atom atomBx = Expressions.makeAtom("B", x);
		final Atom atomCx = Expressions.makeAtom("C", x);
		final Rule ruleBxAx = Expressions.makeRule(atomBx, atomAx);
		final Rule ruleCxBx = Expressions.makeRule(atomCx, atomBx);

		final Reasoner reasoner = new ReasonerImpl();
		reasoner.addFacts(factAc, factAd);
		reasoner.addRules(ruleBxAx, ruleCxBx);
		reasoner.load();

		final StringQueryResultEnumeration cxQueryResultEnumBeforeReasoning = reasoner.compileQueryIterator(atomCx);
		assertFalse(cxQueryResultEnumBeforeReasoning.hasMoreElements());

		reasoner.reason();

		final StringQueryResultEnumeration cxQueryResultEnumAfterReasoning = reasoner.compileQueryIterator(atomCx);
		final List<List<String>> actualQueryResults = VLogTest.getAnswers(cxQueryResultEnumAfterReasoning);

		final List<List<String>> expectedQueryResults = new ArrayList<>();
		final List<String> answer1 = new ArrayList<>();
		answer1.add(constantNameC);
		expectedQueryResults.add(answer1);
		final List<String> answer2 = new ArrayList<>();
		answer2.add(constantNameD);
		expectedQueryResults.add(answer2);

		assertEquals(expectedQueryResults, actualQueryResults);

		reasoner.dispose();
	}

}