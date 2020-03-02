package org.semanticweb.rulewerk.owlapi;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OwlToRulesConverterTest {
	
	static OWLDataFactory df = OWLManager.getOWLDataFactory();
	
	public static IRI getIri(final String localName) {
		return IRI.create("http://example.org/" + localName);
	}

	public static OWLClass getOwlClass(final String localName) {
		return df.getOWLClass(getIri(localName));
	}
	
	static final OWLClass cC = getOwlClass("C");
	static final OWLIndividual inda = df.getOWLNamedIndividual(getIri("a"));

	@Test
	public void testLoadOntologies() throws OWLOntologyCreationException {
		final OWLAnonymousIndividual bnode = df.getOWLAnonymousIndividual("abc");
		final OWLAxiom Cn = df.getOWLClassAssertionAxiom(cC, bnode);
		final OWLAxiom Ca = df.getOWLClassAssertionAxiom(cC, inda);
		
		final OWLOntology ontology =  OWLManager.createOWLOntologyManager().createOntology(Arrays.asList(Cn,Ca));

		final OwlToRulesConverter converter = new OwlToRulesConverter();
		converter.addOntology(ontology);
		converter.addOntology(ontology);

		assertEquals(3, converter.getFacts().size());
	}

}
