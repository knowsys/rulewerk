package org.semanticweb.rulewerk.owlapi;

/*-
 * #%L
 * Rulewerk OWL API Support
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
