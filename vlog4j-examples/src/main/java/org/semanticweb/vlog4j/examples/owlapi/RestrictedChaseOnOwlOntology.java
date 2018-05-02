package org.semanticweb.vlog4j.examples.owlapi;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.owlapi.OwlToRulesConverter;

public class RestrictedChaseOnOwlOntology {

	public static void main(String[] args) throws OWLOntologyCreationException, ReasonerStateException,
			EdbIdbSeparationException, IncompatiblePredicateArityException, IOException {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(new File("src/main/data/owl/bike.owl"));

		OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter();
		owlToRulesConverter.addOntology(ontology);

		// Print out rules
		Set<Rule> rules = owlToRulesConverter.getRules();
		for (Rule rule : rules)
			System.out.println(rule);
		System.out.println();

		// Print out facts
		Set<Atom> facts = owlToRulesConverter.getFacts();
		for (Atom fact : facts)
			System.out.println(fact);
		System.out.println();

		try (Reasoner reasoner = Reasoner.getInstance()) {
			// Load and reason
			reasoner.addRules(new ArrayList<Rule>(owlToRulesConverter.getRules()));
			reasoner.addFacts(owlToRulesConverter.getFacts());
			reasoner.load();
			reasoner.reason();

			// Print out Query Answers
			Predicate pred = Expressions.makePredicate("http://www.bike.org#isPartOf", 2);
			Variable vx = Expressions.makeVariable("x");
			Variable vy = Expressions.makeVariable("y");
			QueryResultIterator answers = reasoner.answerQuery(Expressions.makeAtom(pred, vx, vy), true);
			while (answers.hasNext()) {
				System.out.println(answers.next());
			}
		}
	}
}
