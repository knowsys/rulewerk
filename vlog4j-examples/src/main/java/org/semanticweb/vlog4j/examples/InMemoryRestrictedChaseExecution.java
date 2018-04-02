package org.semanticweb.vlog4j.examples;

import java.io.IOException;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

/*-
 * #%L
 * examples
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

public class InMemoryRestrictedChaseExecution {
	public static void main(String[] args) throws EdbIdbSeparationException, IOException, ReasonerStateException {

		// Instantiating entities
		Predicate bicycleIDB = Expressions.makePredicate("BicycleIDB", 1);
		Predicate bicycleEDB = Expressions.makePredicate("BicycleEDB", 1);
		Predicate wheelIDB = Expressions.makePredicate("WheelIDB", 1);
		Predicate wheelEDB = Expressions.makePredicate("WheelEDB", 1);
		Predicate hasPartIDB = Expressions.makePredicate("HasPartIDB", 2);
		Predicate hasPartEDB = Expressions.makePredicate("HasPartEDB", 2);
		Predicate isPartOfIDB = Expressions.makePredicate("IsPartOfIDB", 2);
		Predicate isPartOfEDB = Expressions.makePredicate("IsPartOfEDB", 2);
		Constant bicycle1 = Expressions.makeConstant("bicycle1");
		Constant bicycle2 = Expressions.makeConstant("bicycle2");
		Constant wheel1 = Expressions.makeConstant("wheel1");
		Variable x = Expressions.makeVariable("x");
		Variable y = Expressions.makeVariable("y");

		// Instantiating rules
		// BicycleIDB(?x) :- BicycleEDB(?x) .
		Atom bicycleIDBX = Expressions.makeAtom(bicycleIDB, x);
		Atom bicycleEDBX = Expressions.makeAtom(bicycleEDB, x);
		Rule rule1 = Expressions.makeRule(bicycleIDBX, bicycleEDBX);
		// WheelIDB(?x) :- WheelEDB(?x) .
		Atom wheelIDBX = Expressions.makeAtom(wheelIDB, x);
		Atom wheelEDBX = Expressions.makeAtom(wheelEDB, x);
		Rule rule2 = Expressions.makeRule(wheelIDBX, wheelEDBX);
		// hasPartIDB(?x, ?y) :- hasPartEDB(?x, ?y) .
		Atom hasPartIDBXY = Expressions.makeAtom(hasPartIDB, x, y);
		Atom hasPartEDBXY = Expressions.makeAtom(hasPartEDB, x, y);
		Rule rule3 = Expressions.makeRule(hasPartIDBXY, hasPartEDBXY);
		// isPartOfIDB(?x, ?y) :- isPartOfEDB(?x, ?y) .
		Atom isPartOfIDBXY = Expressions.makeAtom(isPartOfIDB, x, y);
		Atom isPartOfEDBXY = Expressions.makeAtom(isPartOfEDB, x, y);
		Rule rule4 = Expressions.makeRule(isPartOfIDBXY, isPartOfEDBXY);
		// HasPartIDB(?x, !y), WheelIDB(!y) :- BicycleIDB(?x) .
		Atom wheelIDBY = Expressions.makeAtom(wheelIDB, y);
		Rule rule5 = Expressions.makeRule(Expressions.makeConjunction(hasPartIDBXY, wheelIDBY),
				Expressions.makeConjunction(bicycleIDBX));
		// IsPartOfIDB(?x, !y), BicycleIDB(!y) :- WheelIDB(?x) .
		Atom bycicleIDBY = Expressions.makeAtom(bicycleIDB, y);
		Rule rule6 = Expressions.makeRule(Expressions.makeConjunction(isPartOfIDBXY, bycicleIDBY),
				Expressions.makeConjunction(wheelIDBX));
		// IsPartOfIDB(?x, ?y) :- HasPartIDB(?y, ?x) .
		Atom hasPartIDBYX = Expressions.makeAtom(hasPartIDB, y, x);
		Rule rule7 = Expressions.makeRule(isPartOfIDBXY, hasPartIDBYX);
		// HasPartIDB(?x, ?y) :- IsPartOfIDB(?y, ?x) .
		Atom isPartOfIDBYX = Expressions.makeAtom(isPartOfIDB, y, x);
		Rule rule8 = Expressions.makeRule(hasPartIDBXY, isPartOfIDBYX);

		// Instantiating facts
		// BicycleEDB(bicycle1) .
		Atom fact1 = Expressions.makeAtom(bicycleEDB, bicycle1);
		// HasPartEDB(bicycle1, wheel1) .
		Atom fact2 = Expressions.makeAtom(hasPartEDB, bicycle1, wheel1);
		// Wheel(wheel1) .
		Atom fact3 = Expressions.makeAtom(wheelEDB, wheel1);
		// BicycleEDB(b) .
		Atom fact4 = Expressions.makeAtom(bicycleEDB, bicycle2);

		// Loading, reasoning, and querying.
		Reasoner reasoner = Reasoner.getInstance();
		reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
		reasoner.addRules(rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8);
		reasoner.addFacts(fact1, fact2, fact3, fact4);
		reasoner.load();
		System.out.println("\n" + "Answers to query " + hasPartEDBXY + " before materialisation:");
		QueryResultIterator answersBeforeMaterialisation = reasoner.answerQuery(hasPartEDBXY, true);
		while (answersBeforeMaterialisation.hasNext())
			System.out.println(answersBeforeMaterialisation.next());
		System.out.println();
		reasoner.reason();
		System.out.println("\n" + "Answers to query " + hasPartIDBXY + " after materialisation:");
		QueryResultIterator answersAfterMaterialisation = reasoner.answerQuery(hasPartIDBXY, true);
		while (answersAfterMaterialisation.hasNext())
			System.out.println(answersAfterMaterialisation.next());
		System.out.println();
	}
}
