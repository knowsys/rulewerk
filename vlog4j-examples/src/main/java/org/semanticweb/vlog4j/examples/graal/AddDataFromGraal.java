/**
 * 
 */
package org.semanticweb.vlog4j.examples.graal;

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
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.graal.GraalConjunctiveQueryToRule;
import org.semanticweb.vlog4j.graal.GraalToVLog4JModelConverter;

import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * This example shows how facts and rules can be imported from objects of the
 * Graal library. Special care must be taken with the import of Graal
 * ConjunctiveQuery-objects, since unlike with VLog4J, they represent both the
 * query atom and the corresponding rule.
 * <p>
 * In VLog4J, the reasoner is queried by an Atom and the results are all facts
 * matching this Atom.<br>
 * In Graal, a query is a conjunction of atoms and a set of queried variables
 * from those atoms. The results are then all variable sets for which Atoms
 * matching the conjunction Atoms are found.
 * </p>
 * <p>
 * To apply a Graaal query to VLog4J, an additional Rule must be added to the
 * reasoner. This rule is part of the {@link ImportedGraalQuery}, which also
 * holds the query to use in {@link Reasoner#answerQuery(Atom, boolean)}.
 * </p>
 * 
 * @author adrian
 *
 */
public class AddDataFromGraal {

	public static void main(final String[] args) throws ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException, IOException {
		/* 
		 * 1. Instantiating rules 
		 */
		final List<fr.lirmm.graphik.graal.api.core.Rule> graalRules = new ArrayList<>();
		
		/* 
		 * 1.1 Rules to map external database (EDB) predicates to internal database predicates (IDB).
		 * Necessary because VLog4J requires separation between input predicates and predicates
		 * for which additional facts can be derived.
		 */
		graalRules.add(DlgpParser.parseRule("bicycleIDB(X) :- bicycleEDB(X)."));
		graalRules.add(DlgpParser.parseRule("wheelIDB(X) :- wheelEDB(X)."));
		graalRules.add(DlgpParser.parseRule("hasPartIDB(X, Y) :- hasPartEDB(X, Y)."));
		graalRules.add(DlgpParser.parseRule("isPartOfIDB(X, Y) :- isPartOfEDB(X, Y)."));
		
		/*
		 * 1.2 Rules modelling that every bicycle has wheels and that the
		 * has part relation is inverse to the is part of relation.
		 */
		graalRules.add(DlgpParser.parseRule("hasPartIDB(X, Y), wheelIDB(Y) :- bicycleIDB(X)."));
		graalRules.add(DlgpParser.parseRule("isPartOfIDB(X, Y) :- wheelIDB(X)."));
		graalRules.add(DlgpParser.parseRule("isPartOfIDB(X, Y) :- hasPartIDB(Y, X)."));
		graalRules.add(DlgpParser.parseRule("hasPartIDB(X, Y) :- isPartOfIDB(Y, X)."));
		
		/**
		 * 2. Instantiating Atoms representing the data to reason on (EDB).
		 */
		final List<fr.lirmm.graphik.graal.api.core.Atom> graalAtoms = new ArrayList<>();
		
		/*
		 * bicycleEDB
		 */
		graalAtoms.add(DlgpParser.parseAtom("bicycleEDB(redBike)."));
		graalAtoms.add(DlgpParser.parseAtom("bicycleEDB(blueBike)."));
		graalAtoms.add(DlgpParser.parseAtom("bicycleEDB(blackBike)."));
		
		/*
		 * wheelEDB
		 */
		graalAtoms.add(DlgpParser.parseAtom("wheelEDB(redWheel)."));
		graalAtoms.add(DlgpParser.parseAtom("wheelEDB(blueWheel)."));
		
		/*
		 * hasPartEDB
		 */
		graalAtoms.add(DlgpParser.parseAtom("hasPartEDB(redBike, redWheel)."));
		graalAtoms.add(DlgpParser.parseAtom("hasPartEDB(blueBike, blueWheel)."));
		
		/*
		 * 3. Instantiating a Graal conjunctive query. This is equivalent to adding the
		 * rule query(?b, ?w) :- bicycleIDB(?b), wheelIDB(?w), isPartOfIDB(?w, ?b) and
		 * then querying with query(?b, ?w) The rule from convertedGraalConjunctiveQuery
		 * needs to be added to the reasoner.
		 */
		final GraalConjunctiveQueryToRule convertedGraalConjunctiveQuery = GraalToVLog4JModelConverter.convertQuery(
				"graalQuery",
				DlgpParser.parseQuery("?(B, W) :- bicycleIDB(B), wheelIDB(W), isPartOfIDB(W, B)."));
		
		/* 
		 * 4. Loading, reasoning, and querying while using try-with-resources to close the reasoner automatically. 
		 */
		try (Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(GraalToVLog4JModelConverter.convertRules(graalRules));
			reasoner.addRules(convertedGraalConjunctiveQuery.getRule());
			reasoner.addFacts(GraalToVLog4JModelConverter.convertAtoms(graalAtoms));
			
			reasoner.load();
			System.out.println("Before materialisation:");
			ExamplesUtils.printOutQueryAnswers(convertedGraalConjunctiveQuery.getQueryAtom(), reasoner);

			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();
			System.out.println("After materialisation:");
			ExamplesUtils.printOutQueryAnswers(convertedGraalConjunctiveQuery.getQueryAtom(), reasoner);
			
		}
		
	}

}
