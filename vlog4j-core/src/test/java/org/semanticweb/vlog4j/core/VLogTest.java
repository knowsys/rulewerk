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

import org.semanticweb.vlog4j.core.validation.VLog4jAtomValidationException;
import org.semanticweb.vlog4j.core.validation.VLog4jRuleValidationException;
import org.semanticweb.vlog4j.core.validation.VLog4jTermValidationException;

import junit.framework.TestCase;
import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Rule;
import karmaresearch.vlog.StringQueryResultEnumeration;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.Term.TermType;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

public class VLogTest extends TestCase {

	public void testVLogSimpleInference() throws AlreadyStartedException, EDBConfigurationException, IOException, VLog4jAtomValidationException,
			VLog4jTermValidationException, VLog4jRuleValidationException, NotStartedException {

		// Initialize and start VLog
		final VLog vlog = new VLog();
		vlog.start("", false);

		// Loading Facts: A(a), A(b)
		final String predA = "A";
		final String[][] argsAMatrix = { { "a" }, { "b" } };
		vlog.addData(predA, argsAMatrix);

		final String predB = "B";
		// final String[][] argsBMatrix = { { "c" }, { "d" } };
		// vlog.addData(predB, argsBMatrix);

		// Loading Rule: B(X) :- A(X) .
		final Term[] args = { new Term(TermType.VARIABLE, "X") };
		final Atom atomBx = new Atom(predB, args);
		final Atom[] headAtoms = { atomBx };
		final Atom atomAx = new Atom(predA, args);
		final Atom[] bodyAtoms = { atomAx };
		final Rule[] rules = { new Rule(headAtoms, bodyAtoms) };
		vlog.setRules(rules, RuleRewriteStrategy.NONE);

		// Materialization
		vlog.materialize(true);

		// Querying
		System.out.println("Querying predicate: " + atomBx.getPredicate());
		final StringQueryResultEnumeration answers = vlog.query(atomBx);
		while (answers.hasMoreElements()) {
			final String[] answer = answers.nextElement();
			System.out.println(answer[0]);
		}

		// TODO do we want to use this method from Java 9.
		// final Iterator<String[]> answerIterator = answers.asIterator();
		// while (answerIterator.hasNext()) {
		// final String[] answer = answerIterator.next();
		// System.out.println(answer[0]);
		// }

		// Loading rule: B(X) :- A(X) .
		// final List<Rule> rules = new ArrayList<>();
		// final List<Term> bodyAtomArgs1 = new ArrayList<>();
		// bodyAtomArgs1.add(new VariableImpl("X"));
		// final Atom bodyAtom1 = new AtomImpl("A", bodyAtomArgs1);
		// final List<Atom> body1 = new ArrayList<>();
		// body1.add(bodyAtom1);
		// final List<Term> headAtomArgs1 = new ArrayList<>();
		// headAtomArgs1.add(new VariableImpl("X"));
		// final Atom headAtom1 = new AtomImpl("B", bodyAtomArgs1);
		// final List<Atom> head1 = new ArrayList<>();
		// head1.add(headAtom1);
		// final Rule rule1 = new RuleImpl(body1, head1);
		// rules.add(rule1);
		// vlog.setRules(ModelToVLogConverter.toVLogRuleArray(rules), RuleRewriteStrategy.NONE);

		// Loading facts
		// final List<Term> firstFactArgs = new ArrayList<>();
		// firstFactArgs.add(new ConstantImpl("a"));
		// reasoner.getFacts().add(new AtomImpl("C", firstFactArgs));
		// final List<Term> secondFactArgs = new ArrayList<>();
		// secondFactArgs.add(new ConstantImpl("b"));
		// reasoner.getFacts().add(new AtomImpl("C", secondFactArgs));
		// vlog.addData(predName, tuplesMatrix);

		// this.vlog.setRules(ModelToVLogConverter.toVLogRuleArray(this.rules), RuleRewriteStrategy.NONE);
	}
}
