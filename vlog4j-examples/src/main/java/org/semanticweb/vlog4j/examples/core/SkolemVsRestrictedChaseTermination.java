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

import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.examples.ExamplesUtils;

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

	public static void main(final String[] args)
			throws ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException, IOException {
		/* 1. Instantiating entities, rules and facts */
		final Predicate bicycleIDB = Expressions.makePredicate("BicycleIDB", 1);
		final Predicate bicycleEDB = Expressions.makePredicate("BicycleEDB", 1);
		final Predicate wheelIDB = Expressions.makePredicate("WheelIDB", 1);
		final Predicate wheelEDB = Expressions.makePredicate("WheelEDB", 1);
		final Predicate hasPartIDB = Expressions.makePredicate("HasPartIDB", 2);
		final Predicate hasPartEDB = Expressions.makePredicate("HasPartEDB", 2);
		final Predicate isPartOfIDB = Expressions.makePredicate("IsPartOfIDB", 2);
		final Predicate isPartOfEDB = Expressions.makePredicate("IsPartOfEDB", 2);
		final Constant bicycle1 = Expressions.makeConstant("bicycle1");
		final Constant bicycle2 = Expressions.makeConstant("bicycle2");
		final Constant wheel1 = Expressions.makeConstant("wheel1");
		final Variable x = Expressions.makeVariable("x");
		final Variable y = Expressions.makeVariable("y");

		/* BicycleIDB(?x) :- BicycleEDB(?x) . */
		final PositiveLiteral bicycleIDBX = Expressions.makePositiveLiteral(bicycleIDB, x);
		final PositiveLiteral bicycleEDBX = Expressions.makePositiveLiteral(bicycleEDB, x);
		final Rule rule1 = Expressions.makeRule(bicycleIDBX, bicycleEDBX);

		/* WheelIDB(?x) :- WheelEDB(?x) . */
		final PositiveLiteral wheelIDBX = Expressions.makePositiveLiteral(wheelIDB, x);
		final PositiveLiteral wheelEDBX = Expressions.makePositiveLiteral(wheelEDB, x);
		final Rule rule2 = Expressions.makeRule(wheelIDBX, wheelEDBX);

		/* hasPartIDB(?x, ?y) :- hasPartEDB(?x, ?y) . */
		final PositiveLiteral hasPartIDBXY = Expressions.makePositiveLiteral(hasPartIDB, x, y);
		final PositiveLiteral hasPartEDBXY = Expressions.makePositiveLiteral(hasPartEDB, x, y);
		final Rule rule3 = Expressions.makeRule(hasPartIDBXY, hasPartEDBXY);

		/* isPartOfIDB(?x, ?y) :- isPartOfEDB(?x, ?y) . */
		final PositiveLiteral isPartOfIDBXY = Expressions.makePositiveLiteral(isPartOfIDB, x, y);
		final PositiveLiteral isPartOfEDBXY = Expressions.makePositiveLiteral(isPartOfEDB, x, y);
		final Rule rule4 = Expressions.makeRule(isPartOfIDBXY, isPartOfEDBXY);

		/*
		 * exists y. HasPartIDB(?x, !y), WheelIDB(!y) :- BicycleIDB(?x) .
		 */
		final PositiveLiteral wheelIDBY = Expressions.makePositiveLiteral(wheelIDB, y);
		final Rule rule5 = Expressions.makeRule(Expressions.makePositiveConjunction(hasPartIDBXY, wheelIDBY),
				Expressions.makeConjunction(bicycleIDBX));

		/*
		 * exists y. IsPartOfIDB(?x, !y), BicycleIDB(!y) :- WheelIDB(?x) .
		 */
		final PositiveLiteral bycicleIDBY = Expressions.makePositiveLiteral(bicycleIDB, y);
		final Rule rule6 = Expressions.makeRule(Expressions.makePositiveConjunction(isPartOfIDBXY, bycicleIDBY),
				Expressions.makeConjunction(wheelIDBX));

		/* IsPartOfIDB(?x, ?y) :- HasPartIDB(?y, ?x) . */
		final PositiveLiteral hasPartIDBYX = Expressions.makePositiveLiteral(hasPartIDB, y, x);
		final Rule rule7 = Expressions.makeRule(isPartOfIDBXY, hasPartIDBYX);

		/* HasPartIDB(?x, ?y) :- IsPartOfIDB(?y, ?x) . */
		final PositiveLiteral isPartOfIDBYX = Expressions.makePositiveLiteral(isPartOfIDB, y, x);
		final Rule rule8 = Expressions.makeRule(hasPartIDBXY, isPartOfIDBYX);

		/* BicycleEDB(bicycle1) . */
		final PositiveLiteral fact1 = Expressions.makePositiveLiteral(bicycleEDB, bicycle1);

		/* HasPartEDB(bicycle1, wheel1) . */
		final PositiveLiteral fact2 = Expressions.makePositiveLiteral(hasPartEDB, bicycle1, wheel1);

		/* Wheel(wheel1) . */
		final PositiveLiteral fact3 = Expressions.makePositiveLiteral(wheelEDB, wheel1);

		/* BicycleEDB(b) . */
		final PositiveLiteral fact4 = Expressions.makePositiveLiteral(bicycleEDB, bicycle2);

		/*
		 * 2. Loading, reasoning, and querying. Use try-with resources, or remember to
		 * call close() to free the reasoner resources.
		 */
		try (Reasoner reasoner = Reasoner.getInstance(new KnowledgeBase())) {

			reasoner.addRules(rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8);
			reasoner.addFacts(fact1, fact2, fact3, fact4);
			reasoner.load();

			/* See that there is no fact HasPartIDB before reasoning. */
			System.out.println("Answers to query " + hasPartIDBXY + " before reasoning:");
			ExamplesUtils.printOutQueryAnswers(hasPartIDBXY, reasoner);

			/*
			 * As the Skolem Chase is known not to terminate for this set of rules and
			 * facts, it is interrupted after one second.
			 */
			reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
			reasoner.setReasoningTimeout(1);
			System.out.println("Starting Skolem Chase with 1 second timeout.");

			/* Indeed, the Skolem Chase did not terminate before timeout. */
			final boolean skolemChaseFinished = reasoner.reason();
			System.out.println("Has Skolem Chase algorithm finished before 1 second timeout? " + skolemChaseFinished);

			/*
			 * See that the Skolem Chase generated a very large number of facts in 1 second,
			 * extensively introducing new unnamed individuals to satisfy existential
			 * restrictions.
			 */
			System.out.println(
					"Answers to query " + hasPartIDBXY + " after reasoning with the Skolem Chase for 1 second:");
			ExamplesUtils.printOutQueryAnswers(hasPartIDBXY, reasoner);

			/*
			 * We reset the reasoner and apply the Restricted Chase on the same set of rules
			 * and facts
			 */
			System.out.println();
			System.out.println("Reseting reasoner; discarding facts generated during reasoning.");
			reasoner.resetReasoner();
			reasoner.load();

			/*
			 * See that there is no fact HasPartIDB before reasoning. All inferred facts
			 * have been discarded when the reasoner was reset.
			 */
			System.out.println("Answers to query " + hasPartIDBXY + " before reasoning:");
			ExamplesUtils.printOutQueryAnswers(hasPartIDBXY, reasoner);

			/*
			 * As the Restricted Chase is known to terminate for this set of rules and
			 * facts, we will not interrupt it.
			 */
			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			reasoner.setReasoningTimeout(null);
			final long restrictedChaseStartTime = System.currentTimeMillis();
			System.out.println("Starting Restricted Chase with no timeout.");

			/* Indeed, the Restricted Chase did terminate (in less than 1 second) */
			final boolean restrictedChaseFinished = reasoner.reason();
			final long restrictedChaseDuration = System.currentTimeMillis() - restrictedChaseStartTime;
			System.out.println("Has Restricted Chase algorithm finished? " + restrictedChaseFinished + ". (Duration: "
					+ restrictedChaseDuration + " ms)");

			/*
			 * See that the Restricted Chase generated a small number of facts, reusing
			 * individuals that satisfy existential restrictions.
			 */
			System.out.println("Answers to query " + hasPartIDBXY + " after reasoning with the Restricted Chase:");
			ExamplesUtils.printOutQueryAnswers(hasPartIDBXY, reasoner);
		}
	}

}
