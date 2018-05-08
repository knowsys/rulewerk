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
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.owlapi.OwlToRulesConverter;

/**
 * This example shows how <b>vlog4j-owlapi</b> library (class {@link OwlToRulesConverter}) can be used to transform
 * an OWL ontology into <b>vlog4j-core</b> {@link Rule}s and {@link Atom}s.
 * 
 * @author Irina Dragoste
 *
 */
public class RestrictedChaseOnOwlOntology {

	// FIXME change www.bike.org name

	public static void main(String[] args) throws OWLOntologyCreationException, ReasonerStateException,
			EdbIdbSeparationException, IncompatiblePredicateArityException, IOException {

		/* Bike ontology is loaded from a Bike file using OWL API */
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(new File("src/main/data/owl/bike.owl"));

		/*
		 * vlog4j.owlapi.OwlToRulesConverter can be used to convert the OWL axiom in
		 * source ontology to target Rule and Atom objects
		 */
		OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter();
		owlToRulesConverter.addOntology(ontology);

		/* Print out the Rules extracted from bike ontology. */
		System.out.println("Rules extracted from Bike ontology:");
		Set<Rule> rules = owlToRulesConverter.getRules();
		for (Rule rule : rules) {
			System.out.println(" - rule: " + rule);
		}
		System.out.println();

		/* Print out Facts extracted from bike ontology */
		System.out.println("Facts extracted from Bike ontology:");
		Set<Atom> facts = owlToRulesConverter.getFacts();
		for (Atom fact : facts) {
			System.out.println(" - fact: " + fact);
		}
		System.out.println();

		try (Reasoner reasoner = Reasoner.getInstance()) {
			/* Load rules and facts obtained from the ontology */
			reasoner.addRules(new ArrayList<Rule>(owlToRulesConverter.getRules()));
			reasoner.addFacts(owlToRulesConverter.getFacts());
			reasoner.load();
			/* Reason over loaded ontology */
			reasoner.reason();

			/* Query for the parts of bike constant "b2". */
			Variable vx = Expressions.makeVariable("x");
			Constant b2 = Expressions.makeConstant("http://www.bike-example.ontology#b2");
			Atom isPartOfPairs = Expressions.makeAtom("http://www.bike-example.ontology#isPartOf", vx, b2);

			/*
			 * See that an unnamed individual has been introduced to satisfy
			 * owl:someValuesFrom restriction:
			 * 
			 * http://www.semanticweb.org/carralma/ontologies/2018/3/untitled-ontology-2#
			 * Bike :Bike rdf:type owl:Class ; rdfs:subClassOf [ rdf:type owl:Restriction ;
			 * owl:onProperty :hasPart ; owl:someValuesFrom :Wheel ] .
			 */
			try (QueryResultIterator answers = reasoner.answerQuery(isPartOfPairs, true);) {
				answers.forEachRemaining(answer -> System.out
						.println(answer.getTerms().get(0) + " isPartOf " + answer.getTerms().get(1)));
			}
		}
	}
}