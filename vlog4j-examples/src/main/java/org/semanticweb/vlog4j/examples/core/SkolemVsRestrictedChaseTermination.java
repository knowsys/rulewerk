package org.semanticweb.vlog4j.examples.core;

/*-
 * #%L
 * VLog4j Examples
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

import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

/**
 * This example shows non-termination of the Skolem Chase, versus termination of
 * the Restricted Chase on the same set of rules and facts. Note that the
 * Restricted Chase is the default reasoning algorithm, as it terminates in most
 * cases and generates a smaller number of facts.
 *
 * @author Irina Dragoste
 *
 */
public class SkolemVsRestrictedChaseTermination {

	public static void main(final String[] args) throws ReasonerStateException, EdbIdbSeparationException,
			IncompatiblePredicateArityException, IOException, ParsingException {

		ExamplesUtils.configureLogging();

		/* 1. Load data and prepare rules. */

		final String rules = "" // define some facts:
				+ "bicycleEDB(bicycle1) ." //
				+ "hasPartEDB(bicycle1, wheel1) ." //
				+ "wheelEDB(wheel1) ." //
				+ "bicycleEDB(bicycle2) ." //
				// rules to load all data from the file-based ("EDB") predicates:
				+ "bicycleIDB(?X) :- bicycleEDB(?X) ." //
				+ "wheelIDB(?X) :- wheelEDB(?X) ." //
				+ "hasPartIDB(?X, ?Y) :- hasPartEDB(?X, ?Y) ." //
				+ "isPartOfIDB(?X, ?Y) :- isPartOfEDB(?X, ?Y) ." //
				// every bicycle has some part that is a wheel:
				+ "hasPartIDB(?X, !Y), wheelIDB(!Y) :- bicycleIDB(?X) ." //
				// every wheel is part of some bicycle:
				+ "isPartOfIDB(?X, !Y), bicycleIDB(!Y) :- wheelIDB(?X) ." //
				// hasPart and isPartOf are mutually inverse relations:
				+ "hasPartIDB(?X, ?Y) :- isPartOfIDB(?Y, ?X) ." //
				+ "isPartOfIDB(?X, ?Y) :- hasPartIDB(?Y, ?X) .";

		RuleParser ruleParser = new RuleParser();
		ruleParser.parse(rules);

		/*
		 * 2. Loading, reasoning, and querying. Use try-with resources, or remember to
		 * call close() to free the reasoner resources.
		 */
		try (Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(ruleParser.getRules());
			reasoner.addFacts(ruleParser.getFacts());
			reasoner.load();

			PositiveLiteral queryHasPart = ruleParser.parsePositiveLiteral("hasPartIDB(?X, ?Y)");

			/* See that there is no fact HasPartIDB before reasoning. */
			System.out.println("Before reasoning is started, no inferrences have been computed yet.");
			ExamplesUtils.printOutQueryAnswers(queryHasPart, reasoner);

			/*
			 * As the Skolem Chase is known not to terminate for this set of rules and
			 * facts, it is interrupted after one second.
			 */
			reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
			reasoner.setReasoningTimeout(1);
			System.out.print("Starting Skolem Chase (a.k.a. semi-oblivious chase) with 1 second timeout ... ");
			final boolean skolemChaseFinished = reasoner.reason();
			System.out.println("done.");

			/* Verify that the Skolem Chase did not terminate before timeout. */
			System.out.println("Has Skolem Chase algorithm finished before 1 second timeout? " + skolemChaseFinished);
			/*
			 * See that the Skolem Chase generated a very large number of facts in 1 second,
			 * extensively introducing new unnamed individuals to satisfy existential
			 * restrictions.
			 */
			QueryResultIterator answers = reasoner.answerQuery(queryHasPart, true);
			System.out.println("Before the timeout, the Skolem chase had produced "
					+ ExamplesUtils.iteratorSize(answers) + " results for hasPartIDB(?X, ?Y).");

			/*
			 * We reset the reasoner and apply the Restricted Chase on the same set of rules
			 * and facts
			 */
			System.out.println("\nReseting reasoner; discarding facts generated during reasoning.");
			reasoner.resetReasoner();
			reasoner.load();

			/*
			 * See that there is no fact HasPartIDB before reasoning. All inferred facts
			 * have been discarded when the reasoner was reset.
			 */
			System.out.println("We can verify that there are no inferences for hasPartIDB(?X, ?Y) after reset.");
			ExamplesUtils.printOutQueryAnswers(queryHasPart, reasoner);

			/*
			 * As the Restricted Chase is known to terminate for this set of rules and
			 * facts, we will not interrupt it.
			 */
			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			reasoner.setReasoningTimeout(null);
			final long restrictedChaseStartTime = System.currentTimeMillis();
			System.out.print("Starting Restricted Chase (a.k.a. Standard Chase) without any timeout ... ");
			reasoner.reason();
			System.out.println("done.");

			/* The Restricted Chase terminates: */
			final long restrictedChaseDuration = System.currentTimeMillis() - restrictedChaseStartTime;
			System.out.println("The Restricted Chase finished in " + restrictedChaseDuration + " ms.");

			/*
			 * See that the Restricted Chase generated a small number of facts, reusing
			 * individuals that satisfy existential restrictions.
			 */
			ExamplesUtils.printOutQueryAnswers(queryHasPart, reasoner);
		}
	}

}
