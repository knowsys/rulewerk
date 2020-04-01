package org.semanticweb.rulewerk.examples.graal;

/*-
 * #%L
 * Rulewerk Examples
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.graal.GraalConjunctiveQueryToRule;
import org.semanticweb.rulewerk.graal.GraalToRulewerkModelConverter;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * This example shows how facts and rules can be imported from objects of the
 * <a href="http://graphik-team.github.io/graal/">Graal</a> library. Special
 * care must be taken with the import of Graal {@link ConjunctiveQuery}-objects,
 * since unlike with Rulewerk, they represent both the query atom and the
 * corresponding rule.
 * <p>
 * In Rulewerk, the reasoner is queried by a query Atom and the results are all
 * facts matching this query Atom.<br>
 * Answering a Graal {@link ConjunctiveQuery} over a certain knowledge base is
 * equivalent to adding a {@link Rule} to the knowledge base, <em> prior to
 * reasoning</em>. The rule consists of the query Atoms as the Rule body and a
 * single Atom with a fresh predicate containing all the answer variables of the
 * {@link ConjunctiveQuery} as the Rule head. After the reasoning process, in
 * which the rule is materialised, is completed, this Rule head can then be used
 * as a a query Atom to obtain the results of the Graal
 * {@link ConjunctiveQuery}.
 * </p>
 *
 * @author Adrian Bielefeldt
 *
 */
public class AddDataFromGraal {

	public static void main(final String[] args) throws IOException {
		/*
		 * 1. Instantiating rules
		 */
		final List<fr.lirmm.graphik.graal.api.core.Rule> graalRules = new ArrayList<>();

		/*
		 * 1.1 Rules to map external database (EDB) predicates to internal database
		 * predicates (IDB). Necessary because Rulewerk requires separation between input
		 * predicates and predicates for which additional facts can be derived.
		 */
		graalRules.add(DlgpParser.parseRule("bicycleIDB(X) :- bicycleEDB(X)."));
		graalRules.add(DlgpParser.parseRule("wheelIDB(X) :- wheelEDB(X)."));
		graalRules.add(DlgpParser.parseRule("hasPartIDB(X, Y) :- hasPartEDB(X, Y)."));
		graalRules.add(DlgpParser.parseRule("isPartOfIDB(X, Y) :- isPartOfEDB(X, Y)."));

		/*
		 * 1.2 Rules modelling that every bicycle has wheels and that the has part
		 * relation is inverse to the is part of relation.
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
		final GraalConjunctiveQueryToRule convertedGraalConjunctiveQuery = GraalToRulewerkModelConverter.convertQuery(
				"graalQuery", DlgpParser.parseQuery("?(B, W) :- bicycleIDB(B), wheelIDB(W), isPartOfIDB(W, B)."));

		/*
		 * 4. Loading, reasoning, and querying while using try-with-resources to close
		 * the reasoner automatically.
		 */
		final KnowledgeBase kb = new KnowledgeBase();

		try (Reasoner reasoner = new VLogReasoner(kb)) {

			/*
			 * Add facts to the reasoner knowledge base
			 */
			kb.addStatements(GraalToRulewerkModelConverter.convertAtomsToFacts(graalAtoms));
			/*
			 * Load the knowledge base into the reasoner
			 */
			reasoner.reason();

			/*
			 * Query the loaded facts
			 */
			System.out.println("Before materialisation:");
			ExamplesUtils.printOutQueryAnswers(convertedGraalConjunctiveQuery.getQuery(), reasoner);

			/*
			 * Add rules to the reasoner knowledge base
			 */
			kb.addStatements(GraalToRulewerkModelConverter.convertRules(graalRules));
			kb.addStatements(convertedGraalConjunctiveQuery.getRule());

			/*
			 * Materialise facts using rules
			 */
			reasoner.reason();

			System.out.println("After materialisation:");
			ExamplesUtils.printOutQueryAnswers(convertedGraalConjunctiveQuery.getQuery(), reasoner);

		}

	}

}
