package org.semanticweb.vlog4j.owlapi;

/*-
 * #%L
 * VLog4j OWL API Support
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

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.vlog4j.core.model.api.Rule;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyFactoryImpl;

public class OwlToRulesConverterTest {

	@Test
	public void test() {
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		IRI iA = IRI.create("http://example.org/A");
		IRI iB = IRI.create("http://example.org/B");
		IRI iC = IRI.create("http://example.org/C");
		IRI iD = IRI.create("http://example.org/D");
		IRI iE = IRI.create("http://example.org/E");
		IRI iR = IRI.create("http://example.org/R");
		IRI iS = IRI.create("http://example.org/S");
		OWLClass A = df.getOWLClass(iA);
		OWLClass B = df.getOWLClass(iB);
		OWLClass C = df.getOWLClass(iC);
		OWLClass D = df.getOWLClass(iD);
		OWLClass E = df.getOWLClass(iE);
		OWLObjectProperty R = df.getOWLObjectProperty(iR);
		OWLObjectProperty S = df.getOWLObjectProperty(iS);
		OWLObjectPropertyExpression Sinv = df.getOWLObjectInverseOf(S);
		OWLObjectSomeValuesFrom SomeSinvE = df.getOWLObjectSomeValuesFrom(Sinv, E);
		OWLObjectSomeValuesFrom SomeRSomeSinvE = df.getOWLObjectSomeValuesFrom(R, SomeSinvE);
		OWLObjectUnionOf AorB = df.getOWLObjectUnionOf(A, B);
		OWLObjectIntersectionOf AorBandCandSomeRSomeSinvE = df.getOWLObjectIntersectionOf(AorB, C, SomeRSomeSinvE);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(AorBandCandSomeRSomeSinvE, D);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		for (Rule rule : converter.rules) {
			System.out.println(rule);
		}

	}

}
