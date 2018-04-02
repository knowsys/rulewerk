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

import java.util.Arrays;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class OwlToRulesConverterTest {

	static OWLDataFactory df = OWLManager.getOWLDataFactory();

	public static IRI getIri(String localName) {
		return IRI.create("http://example.org/" + localName);
	}

	public static OWLClass getOwlClass(String localName) {
		return df.getOWLClass(getIri(localName));
	}

	public static OWLObjectProperty getOwlObjectProperty(String localName) {
		return df.getOWLObjectProperty(getIri(localName));
	}

	public static Predicate getClassPredicate(String localName) {
		return Expressions.makePredicate("http://example.org/" + localName, 1);
	}

	public static Predicate getPropertyPredicate(String localName) {
		return Expressions.makePredicate("http://example.org/" + localName, 2);
	}

	static OWLClass cA = getOwlClass("A");
	static OWLClass cB = getOwlClass("B");
	static OWLClass cC = getOwlClass("C");
	static OWLClass cD = getOwlClass("D");
	static OWLClass cE = getOwlClass("E");
	static OWLObjectProperty pR = getOwlObjectProperty("R");
	static OWLObjectProperty pS = getOwlObjectProperty("S");
	static OWLObjectProperty pT = getOwlObjectProperty("T");

	static Predicate nA = getClassPredicate("A");
	static Predicate nB = getClassPredicate("B");
	static Predicate nC = getClassPredicate("C");
	static Predicate nD = getClassPredicate("D");
	static Predicate nE = getClassPredicate("E");
	static Predicate nR = getPropertyPredicate("R");
	static Predicate nS = getPropertyPredicate("S");
	static Predicate nT = getPropertyPredicate("T");

	static OWLIndividual inda = df.getOWLNamedIndividual(getIri("a"));
	static OWLIndividual indb = df.getOWLNamedIndividual(getIri("b"));

	@Test
	public void testSimpleRule() {
		OWLObjectIntersectionOf body = df.getOWLObjectIntersectionOf(cA, cB, cC);
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(cD, cE);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(body, head);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		Atom atC = Expressions.makeAtom(nC, Arrays.asList(converter.frontierVariable));
		Atom atD = Expressions.makeAtom(nD, Arrays.asList(converter.frontierVariable));
		Atom atE = Expressions.makeAtom(nE, Arrays.asList(converter.frontierVariable));
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atD, atE)),
				Expressions.makeConjunction(Arrays.asList(atA, atB, atC)));

		assertEquals(Collections.singleton(rule), converter.rules);

	}

	@Test
	public void testConjunctionTruth() {
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(cB, df.getOWLThing(), cC);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		Atom atC = Expressions.makeAtom(nC, Arrays.asList(converter.frontierVariable));
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atB, atC)),
				Expressions.makeConjunction(Arrays.asList(atA)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testConjunctionTruthTruth() {
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(df.getOWLThing(), df.getOWLThing());
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		assertEquals(0, converter.rules.size());
	}

	@Test
	public void testConjunctionFalsity() {
		OWLClassExpression notSupported = df.getOWLObjectExactCardinality(10, pR);
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(notSupported, df.getOWLNothing(), cC);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		Atom bot = OwlToRulesConversionHelper.getBottom(converter.frontierVariable);
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(bot)),
				Expressions.makeConjunction(Arrays.asList(atA)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test(expected = OwlFeatureNotSupportedException.class)
	public void testConjunctionException() {
		OWLClassExpression notSupported = df.getOWLObjectExactCardinality(10, pR);
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(notSupported, cC);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);
	}

	@Test
	public void testConjunctionNegativeLiterals() {
		OWLClassExpression notA = df.getOWLObjectComplementOf(cA);
		OWLClassExpression notB = df.getOWLObjectComplementOf(cB);
		OWLClassExpression notC = df.getOWLObjectComplementOf(cC);
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(notB, notC);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(notA, head);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Predicate auxPredicate = OwlToRulesConversionHelper.getAuxiliaryClassPredicate(Arrays.asList(notB, notC));

		Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		Atom atC = Expressions.makeAtom(nC, Arrays.asList(converter.frontierVariable));
		Atom atAux = Expressions.makeAtom(auxPredicate, Arrays.asList(converter.frontierVariable));

		Rule rule1 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atAux)),
				Expressions.makeConjunction(Arrays.asList(atB)));
		Rule rule2 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atAux)),
				Expressions.makeConjunction(Arrays.asList(atC)));
		Rule rule3 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(atAux)));

		assertEquals(Sets.newSet(rule1, rule2, rule3), converter.rules);
	}

	@Test
	public void testContrapositive() {
		OWLClassExpression notA = df.getOWLObjectComplementOf(cA);
		OWLClassExpression notB = df.getOWLObjectComplementOf(cB);
		OWLClassExpression notC = df.getOWLObjectComplementOf(cC);
		OWLClassExpression notBOrNotC = df.getOWLObjectUnionOf(notB, notC);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(notA, notBOrNotC);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		Atom atC = Expressions.makeAtom(nC, Arrays.asList(converter.frontierVariable));
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(atB, atC)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testPositiveUniversal() {
		OWLClassExpression forallRA = df.getOWLObjectAllValuesFrom(pR, cA);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cB, forallRA);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));
		Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));

		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(atR, atB)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testPositiveExistential() {
		OWLClassExpression existsRA = df.getOWLObjectSomeValuesFrom(pR, cA);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cB, existsRA);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));
		Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));

		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR, atA)),
				Expressions.makeConjunction(Arrays.asList(atB)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testNegativeUniversal() {
		OWLClassExpression forallRA = df.getOWLObjectAllValuesFrom(pR, cA);
		OWLClassExpression notB = df.getOWLObjectComplementOf(cB);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(forallRA, notB);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Predicate auxPredicate = OwlToRulesConversionHelper.getAuxiliaryClassPredicate(Arrays.asList(cA));
		Variable secondVariable = Expressions.makeVariable("Y1");

		Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		Atom atAux = Expressions.makeAtom(auxPredicate, Arrays.asList(secondVariable));
		Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));
		Atom bot = OwlToRulesConversionHelper.getBottom(secondVariable);

		Rule rule1 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR, atAux)),
				Expressions.makeConjunction(Arrays.asList(atB)));
		Rule rule2 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(bot)),
				Expressions.makeConjunction(Arrays.asList(atAux, atA)));

		assertEquals(Sets.newSet(rule1, rule2), converter.rules);
	}

	@Test
	public void testNegativeExistential() {
		OWLClassExpression existRA = df.getOWLObjectSomeValuesFrom(pR, cA);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(existRA, cB);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));
		Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));

		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atB)),
				Expressions.makeConjunction(Arrays.asList(atR, atA)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testSelf() {
		OWLClassExpression selfR = df.getOWLObjectHasSelf(pR);
		OWLClassExpression selfS = df.getOWLObjectHasSelf(pS);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(selfR, selfS);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, converter.frontierVariable));
		Atom atS = Expressions.makeAtom(nS, Arrays.asList(converter.frontierVariable, converter.frontierVariable));

		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)),
				Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testHasValue() {
		OWLClassExpression hasRa = df.getOWLObjectHasValue(pR, inda);
		OWLClassExpression hasSb = df.getOWLObjectHasValue(pS, indb);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(hasRa, hasSb);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		Term consta = Expressions.makeConstant(getIri("a").toString());
		Term constb = Expressions.makeConstant(getIri("b").toString());
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, consta));
		Atom atS = Expressions.makeAtom(nS, Arrays.asList(converter.frontierVariable, constb));

		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)),
				Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testObjectPropertyAssertions() {
		OWLAxiom Rab = df.getOWLObjectPropertyAssertionAxiom(pR, inda, indb);
		OWLAxiom invSab = df.getOWLObjectPropertyAssertionAxiom(df.getOWLObjectInverseOf(pS), inda, indb);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		Rab.accept(converter);
		invSab.accept(converter);

		Term consta = Expressions.makeConstant(getIri("a").toString());
		Term constb = Expressions.makeConstant(getIri("b").toString());
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(consta, constb));
		Atom atS = Expressions.makeAtom(nS, Arrays.asList(constb, consta));

		assertEquals(Sets.newSet(atR, atS), converter.facts);
	}
	
	@Test
	public void testNegativeObjectPropertyAssertions() {
		OWLAxiom Rab = df.getOWLNegativeObjectPropertyAssertionAxiom(pR, inda, indb);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		Rab.accept(converter);

		Term consta = Expressions.makeConstant(getIri("a").toString());
		Term constb = Expressions.makeConstant(getIri("b").toString());
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(consta, constb));
		Atom bot = OwlToRulesConversionHelper.getBottom(consta);
		
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(bot)),
				Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Ignore
	public void test() {
		OWLObjectPropertyExpression Sinv = df.getOWLObjectInverseOf(pS);
		OWLObjectSomeValuesFrom SomeSinvE = df.getOWLObjectSomeValuesFrom(Sinv, cE);
		OWLObjectSomeValuesFrom SomeRSomeSinvE = df.getOWLObjectSomeValuesFrom(pR, SomeSinvE);
		OWLObjectUnionOf AorB = df.getOWLObjectUnionOf(cA, cB);
		OWLObjectIntersectionOf AorBandCandSomeRSomeSinvE = df.getOWLObjectIntersectionOf(AorB, cC, SomeRSomeSinvE);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(AorBandCandSomeRSomeSinvE, cD);

		OwlToRulesConverter converter = new OwlToRulesConverter();
		axiom.accept(converter);

		for (Rule rule : converter.rules) {
			System.out.println(rule);
		}

	}

}
