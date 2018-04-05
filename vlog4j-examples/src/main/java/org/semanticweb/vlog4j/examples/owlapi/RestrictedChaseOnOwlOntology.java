package org.semanticweb.vlog4j.examples.owlapi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.owlapi.OwlToRulesConverter;

public class RestrictedChaseOnOwlOntology {

	public static void main(String[] args) throws OWLOntologyCreationException, ReasonerStateException,
			EdbIdbSeparationException, IncompatiblePredicateArityException, IOException {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(
				new File("src\\main\\data\\owl\\4ecd5765-8ee2-4f18-a7e7-4daf64288dc4_Plant.owl.owl.xml"));

		OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter();
		owlToRulesConverter.addOntology(ontology);

		try (Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(new ArrayList<Rule>(owlToRulesConverter.getRules()));
			reasoner.addFacts(owlToRulesConverter.getFacts());
			reasoner.load();
		}

		// TODO this might fail because of EDB/IDB
		// TODO query, reason

	}

}
