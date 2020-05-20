package org.semanticweb.rulewerk.reasoner.vlog;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NonExistingPredicateException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Rule;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.Term.TermType;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

public class VLogQueryTest {

	private final Term variableX = VLogExpressions.makeVariable("x");
	private final Term variableY = VLogExpressions.makeVariable("y");
	private final Term variableZ = VLogExpressions.makeVariable("z");
	private final String[][] pFactArguments = { { "c" } };
	// P(x) -> Q(y)
	private final Rule ruleWithExistentials = VLogExpressions.makeRule(VLogExpressions.makeAtom("q", variableY),
			VLogExpressions.makeAtom("p", variableX));
	private final Atom queryAtomQPredicate = VLogExpressions.makeAtom("q", variableZ);

	@Test
	public void queryResultWithBlanksExcludeBlanks() throws EDBConfigurationException, NotStartedException, NonExistingPredicateException {
		final VLog vLog = new VLog();

		vLog.addData("p", pFactArguments);
		vLog.setRules(new Rule[] { ruleWithExistentials }, RuleRewriteStrategy.NONE);
		vLog.materialize(true);
		try (final TermQueryResultIterator queryResultIterator = vLog.query(queryAtomQPredicate, true, true)) {
			assertFalse(queryResultIterator.hasNext());
		}
		vLog.stop();
	}

	@Test
	public void queryResultWithBlanksInludeBlanks() throws EDBConfigurationException, NotStartedException, NonExistingPredicateException {
		final VLog vLog = new VLog();
		vLog.addData("p", pFactArguments);
		vLog.setRules(new Rule[] { ruleWithExistentials }, RuleRewriteStrategy.NONE);
		vLog.materialize(true);
		try (final TermQueryResultIterator queryResultIterator = vLog.query(queryAtomQPredicate, true, false)) {
			// tests that the only query result is a BLANK
			assertTrue(queryResultIterator.hasNext());
			final Term[] queryResult = queryResultIterator.next();
			assertTrue(queryResult.length == 1);
			final Term term = queryResult[0];
			assertEquals(TermType.BLANK, term.getTermType());
			assertFalse(queryResultIterator.hasNext());
		}
		vLog.stop();
	}

}
