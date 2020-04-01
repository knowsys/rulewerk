package org.semanticweb.rulewerk.reasoner.vlog;

import static org.junit.Assert.assertArrayEquals;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NonExistingPredicateException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Rule;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

/**
 * Tests that reasoning and querying with predicates of large arities is
 * allowed.
 * 
 * @author Irina Dragoste
 *
 */
public class LargeAritiesTest {

	final static int PREDICATE_ARITY_LIMIT = 255;
	final static int VARIABLES_PER_RULE_LIMIT = 255;

	@Test
	public void testLargeNumberOfVariablesPerRule() throws NotStartedException, EDBConfigurationException, NonExistingPredicateException {
		testNumberOfVariablesPerRule(VARIABLES_PER_RULE_LIMIT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNumberOfVariablesPerRuleExceedsLimit() throws NotStartedException, EDBConfigurationException, NonExistingPredicateException {
		testNumberOfVariablesPerRule(VARIABLES_PER_RULE_LIMIT + 1);
	}

	@Test
	public void testLargePredicateArities() throws NotStartedException, EDBConfigurationException, NonExistingPredicateException {
		testPredicateArity(PREDICATE_ARITY_LIMIT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPredicateAritiesExceedLimit() throws NotStartedException, EDBConfigurationException, NonExistingPredicateException {
		testPredicateArity(PREDICATE_ARITY_LIMIT + 1);
	}

	private void testNumberOfVariablesPerRule(int variablesPerRuleLimit)
			throws EDBConfigurationException, NotStartedException, NonExistingPredicateException {
		final VLog vLog = new VLog();

		final String[][] pFactArguments = { { "c" } };

		final List<Atom> body = new ArrayList<>();
		for (int i = 1; i <= variablesPerRuleLimit; i++) {
			final String predicateName = "P" + i;
			// Pi(xi)
			body.add(VLogExpressions.makeAtom(predicateName, VLogExpressions.makeVariable("x" + i)));
			// Pi(c)
			vLog.addData(predicateName, pFactArguments);
		}
		final Atom head = VLogExpressions.makeAtom("q", VLogExpressions.makeVariable("x1"));

		// q(x1) :- P1(x1),...,Pn(xn)
		final Rule rule = VLogExpressions.makeRule(head, body.toArray(new Atom[body.size()]));

		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		vLog.materialize(true);
		try (final TermQueryResultIterator queryResultIterator = vLog.query(head, true, false)) {
			assertTrue(queryResultIterator.hasNext());
			final Term[] queryResult = queryResultIterator.next();
			assertArrayEquals(new Term[] { VLogExpressions.makeConstant("c") }, queryResult);

			assertFalse(queryResultIterator.hasNext());
		}
		vLog.stop();
	}

	private void testPredicateArity(final int predicateArityLimit)
			throws EDBConfigurationException, NotStartedException, NonExistingPredicateException {
		final List<String> constants = new ArrayList<>();
		for (int i = 0; i < predicateArityLimit; i++) {
			constants.add("c" + i);
		}
		final String[][] pFactArguments = { constants.toArray(new String[predicateArityLimit]) };

		final List<Term> variables = new ArrayList<>();
		for (int i = 0; i < predicateArityLimit; i++) {
			variables.add(VLogExpressions.makeVariable("x" + i));
		}

		final Term[] terms = variables.toArray(new Term[variables.size()]);
		final Rule rule = VLogExpressions.makeRule(VLogExpressions.makeAtom("q", terms),
				VLogExpressions.makeAtom("p", terms));
		final Atom queryAtomQPredicate = VLogExpressions.makeAtom("q", terms);

		final VLog vLog = new VLog();
		vLog.addData("p", pFactArguments);

		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		vLog.materialize(true);
		try (final TermQueryResultIterator queryResultIterator = vLog.query(queryAtomQPredicate, true, false)) {
			assertTrue(queryResultIterator.hasNext());
			final Term[] queryResult = queryResultIterator.next();
			assertTrue(queryResult.length == predicateArityLimit);

			assertFalse(queryResultIterator.hasNext());
		}
		vLog.stop();
	}

}
