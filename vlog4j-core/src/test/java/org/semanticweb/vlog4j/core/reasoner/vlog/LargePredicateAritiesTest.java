package org.semanticweb.vlog4j.core.reasoner.vlog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import karmaresearch.vlog.Atom;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.Rule;
import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;
import karmaresearch.vlog.VLog.RuleRewriteStrategy;

/**
 * Tests that reasoning and querying with predicates of large arities is allowed.
 * 
 * @author Irina Dragoste
 *
 */
public class LargePredicateAritiesTest {

	final static int PREDICATE_ARITY_LIMIT = 255;

	@Test
	public void testLargePredicateArities() throws NotStartedException, EDBConfigurationException {

		final List<String> constants = new ArrayList<>();
		for (int i = 0; i < PREDICATE_ARITY_LIMIT; i++) {
			constants.add("c" + i);
		}
		final String[][] pFactArguments = { constants.toArray(new String[PREDICATE_ARITY_LIMIT]) };

		final List<Term> variables = new ArrayList<>();
		for (int i = 0; i < PREDICATE_ARITY_LIMIT; i++) {
			variables.add(VLogExpressions.makeVariable("x" + i));
		}

		final Term[] terms = variables.toArray(new Term[PREDICATE_ARITY_LIMIT]);
		final Rule rule = VLogExpressions.makeRule(VLogExpressions.makeAtom("q", terms), VLogExpressions.makeAtom("p", terms));
		final Atom queryAtomQPredicate = VLogExpressions.makeAtom("q", terms);

		final VLog vLog = new VLog();
		vLog.addData("p", pFactArguments);

		vLog.setRules(new Rule[] { rule }, RuleRewriteStrategy.NONE);
		vLog.materialize(true);
		try (final TermQueryResultIterator queryResultIterator = vLog.query(queryAtomQPredicate, true, false)) {
			assertTrue(queryResultIterator.hasNext());
			final Term[] queryResult = queryResultIterator.next();
			assertTrue(queryResult.length == PREDICATE_ARITY_LIMIT);

			assertFalse(queryResultIterator.hasNext());
		}
		vLog.stop();

	}
}
