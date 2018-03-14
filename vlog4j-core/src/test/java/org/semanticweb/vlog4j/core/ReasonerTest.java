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

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.AtomImpl;
import org.semanticweb.vlog4j.core.model.ConstantImpl;
import org.semanticweb.vlog4j.core.model.Rule;
import org.semanticweb.vlog4j.core.model.RuleImpl;
import org.semanticweb.vlog4j.core.model.Term;
import org.semanticweb.vlog4j.core.model.VariableImpl;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.ReasonerImpl;
import org.semanticweb.vlog4j.core.validation.VLog4jAtomValidationException;
import org.semanticweb.vlog4j.core.validation.VLog4jRuleValidationException;
import org.semanticweb.vlog4j.core.validation.VLog4jTermValidationException;

import junit.framework.TestCase;
import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.StringQueryResultEnumeration;

public class ReasonerTest extends TestCase {

	public void testSimpleInference() throws VLog4jTermValidationException, VLog4jAtomValidationException, VLog4jRuleValidationException,
			AlreadyStartedException, EDBConfigurationException, IOException, NotStartedException {

		// Loading rules
		final List<Rule> rules = new ArrayList<>();

		final List<Term> bodyAtomArgs1 = new ArrayList<>();
		bodyAtomArgs1.add(new VariableImpl("X"));
		final Atom bodyAtom1 = new AtomImpl("A", bodyAtomArgs1);
		final List<Atom> body1 = new ArrayList<>();
		body1.add(bodyAtom1);
		final List<Term> headAtomArgs1 = new ArrayList<>();
		headAtomArgs1.add(new VariableImpl("X"));
		final Atom headAtom1 = new AtomImpl("B", bodyAtomArgs1);
		final List<Atom> head1 = new ArrayList<>();
		head1.add(headAtom1);
		final Rule rule1 = new RuleImpl(body1, head1);
		rules.add(rule1);

		final List<Term> bodyAtomArgs2 = new ArrayList<>();
		bodyAtomArgs2.add(new VariableImpl("X"));
		final Atom bodyAtom2 = new AtomImpl("B", bodyAtomArgs2);
		final List<Atom> body2 = new ArrayList<>();
		body2.add(bodyAtom2);
		final List<Term> headAtomArgs2 = new ArrayList<>();
		headAtomArgs2.add(new VariableImpl("X"));
		final Atom headAtom2 = new AtomImpl("C", bodyAtomArgs2);
		final List<Atom> head2 = new ArrayList<>();
		head2.add(headAtom2);
		final Rule rule2 = new RuleImpl(body2, head2);
		rules.add(rule2);

		// // Facts
		// // final String csvFilePath = File.pathSeparator + "Users" + File.pathSeparator + "carralma" + File.pathSeparator + "eclipse-workspace"
		// // + File.pathSeparator + "vlog4j-parent" + File.pathSeparator + "vlog4j-core" + File.pathSeparator + "data" + File.pathSeparator
		// // + "unaryFacts.csv";
		// // final Set<String[]> edbConfig = new HashSet<>();
		// // edbConfig.add(new String[] { csvFilePath, "B" });
		//
		// final VLog reasoner
		final Reasoner reasoner = new ReasonerImpl();

		// Loading facts
		final List<Term> firstFactArgs = new ArrayList<>();
		firstFactArgs.add(new ConstantImpl("a"));
		reasoner.getFacts().add(new AtomImpl("A", firstFactArgs));
		final List<Term> secondFactArgs = new ArrayList<>();
		secondFactArgs.add(new ConstantImpl("b"));
		reasoner.getFacts().add(new AtomImpl("A", secondFactArgs));
		// final List<Term> fact3Args = new ArrayList<>();
		// fact3Args.add(new ConstantImpl("c"));
		// reasoner.getFacts().add(new AtomImpl("B", fact3Args));

		// Reasoning
		reasoner.applyReasoning();

		// Querying
		// final StringQueryResultEnumeration resultEnumeration = reasoner.query(bodyAtom1);
		final StringQueryResultEnumeration resultEnumeration = reasoner.query(headAtom1);
		// final Iterator<String[]> iterator = resultEnumeration.asIterator();
		// while (iterator.hasNext()) {
		// final String[] answer = iterator.next();
		// System.out.println(answer[0]);
		// }

	}
}
