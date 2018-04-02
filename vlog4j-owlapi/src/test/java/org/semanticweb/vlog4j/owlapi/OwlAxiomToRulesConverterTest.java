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

public class OwlAxiomToRulesConverterTest {

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
	static OWLObjectProperty pU = getOwlObjectProperty("U");

	static Predicate nA = getClassPredicate("A");
	static Predicate nB = getClassPredicate("B");
	static Predicate nC = getClassPredicate("C");
	static Predicate nD = getClassPredicate("D");
	static Predicate nE = getClassPredicate("E");
	static Predicate nR = getPropertyPredicate("R");
	static Predicate nS = getPropertyPredicate("S");
	static Predicate nT = getPropertyPredicate("T");
	static Predicate nU = getPropertyPredicate("U");

	static OWLIndividual inda = df.getOWLNamedIndividual(getIri("a"));
	static OWLIndividual indb = df.getOWLNamedIndividual(getIri("b"));
	static OWLIndividual indc = df.getOWLNamedIndividual(getIri("c"));

	@Test
	public void testSimpleRule() {
		OWLObjectIntersectionOf body = df.getOWLObjectIntersectionOf(cA, cB, cC);
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(cD, cE);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(body, head);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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
	public void testTrueBody() {
		OWLClassExpression body = df.getOWLObjectIntersectionOf(df.getOWLThing(),
				df.getOWLObjectAllValuesFrom(df.getOWLBottomObjectProperty(), cB));
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(body, cA);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		Atom top = OwlToRulesConversionHelper.getTop(converter.frontierVariable);
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(top)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testConjunctionTruth() {
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(cB, df.getOWLThing(), cC);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		assertEquals(0, converter.rules.size());
	}

	@Test
	public void testConjunctionFalsity() {
		OWLClassExpression notSupported = df.getOWLObjectExactCardinality(10, pR);
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(notSupported, df.getOWLNothing(), cC);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);
	}

	@Test
	public void testConjunctionNegativeLiterals() {
		OWLClassExpression notA = df.getOWLObjectComplementOf(cA);
		OWLClassExpression notB = df.getOWLObjectComplementOf(cB);
		OWLClassExpression notC = df.getOWLObjectComplementOf(cC);
		OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(notB, notC);
		OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(notA, head);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		Rab.accept(converter);
		invSab.accept(converter);

		Term consta = Expressions.makeConstant(getIri("a").toString());
		Term constb = Expressions.makeConstant(getIri("b").toString());
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(consta, constb));
		Atom atS = Expressions.makeAtom(nS, Arrays.asList(constb, consta));

		assertEquals(Sets.newSet(atR, atS), converter.facts);
	}

	@Test
	public void testClassAssertions() {
		OWLAxiom Ca = df.getOWLClassAssertionAxiom(cC, indc);
		OWLClassExpression BandhasRb = df.getOWLObjectIntersectionOf(cB, df.getOWLObjectHasValue(pR, indb));
		OWLAxiom BandhasRba = df.getOWLClassAssertionAxiom(BandhasRb, inda);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		Ca.accept(converter);
		BandhasRba.accept(converter);

		Term consta = Expressions.makeConstant(getIri("a").toString());
		Term constb = Expressions.makeConstant(getIri("b").toString());
		Term constc = Expressions.makeConstant(getIri("c").toString());
		Atom atC = Expressions.makeAtom(nC, Arrays.asList(constc));
		Atom atB = Expressions.makeAtom(nB, Arrays.asList(consta));
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(consta, constb));

		assertEquals(Sets.newSet(atC, atB, atR), converter.facts);
	}

	@Test
	public void testNegativeObjectPropertyAssertions() {
		OWLAxiom Rab = df.getOWLNegativeObjectPropertyAssertionAxiom(pR, inda, indb);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		Rab.accept(converter);

		Term consta = Expressions.makeConstant(getIri("a").toString());
		Term constb = Expressions.makeConstant(getIri("b").toString());
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(consta, constb));
		Atom bot = OwlToRulesConversionHelper.getBottom(consta);

		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(bot)),
				Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testSubObjectPropertyOf() {
		OWLAxiom axiom = df.getOWLSubObjectPropertyOfAxiom(pR, df.getOWLObjectInverseOf(pS));

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		Atom atS = Expressions.makeAtom(nS, Arrays.asList(secondVariable, converter.frontierVariable));
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)),
				Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testAsymmetricObjectPropertyOf() {
		OWLAxiom axiom = df.getOWLAsymmetricObjectPropertyAxiom(pR);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		Atom at2 = Expressions.makeAtom(nR, Arrays.asList(secondVariable, converter.frontierVariable));
		Rule rule = Expressions.makeRule(
				Expressions.makeConjunction(
						Arrays.asList(OwlToRulesConversionHelper.getBottom(converter.frontierVariable))),
				Expressions.makeConjunction(Arrays.asList(at1, at2)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testSymmetricObjectPropertyOf() {
		OWLAxiom axiom = df.getOWLSymmetricObjectPropertyAxiom(pR);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		Atom at2 = Expressions.makeAtom(nR, Arrays.asList(secondVariable, converter.frontierVariable));
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(at2)),
				Expressions.makeConjunction(Arrays.asList(at1)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testIrreflexiveObjectPropertyOf() {
		OWLAxiom axiom = df.getOWLIrreflexiveObjectPropertyAxiom(pR);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, converter.frontierVariable));
		Rule rule = Expressions.makeRule(
				Expressions.makeConjunction(
						Arrays.asList(OwlToRulesConversionHelper.getBottom(converter.frontierVariable))),
				Expressions.makeConjunction(Arrays.asList(at1)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testReflexiveObjectPropertyOf() {
		OWLAxiom axiom = df.getOWLReflexiveObjectPropertyAxiom(pR);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, converter.frontierVariable));
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(at1)), Expressions
				.makeConjunction(Arrays.asList(OwlToRulesConversionHelper.getTop(converter.frontierVariable))));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testInverseObjectProperties() {
		OWLAxiom axiom = df.getOWLInverseObjectPropertiesAxiom(pR, pS);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		Atom atS = Expressions.makeAtom(nS, Arrays.asList(secondVariable, converter.frontierVariable));
		Rule rule1 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)),
				Expressions.makeConjunction(Arrays.asList(atR)));
		Rule rule2 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR)),
				Expressions.makeConjunction(Arrays.asList(atS)));

		assertEquals(Sets.newSet(rule1, rule2), converter.rules);
	}

	@Test
	public void testEquivalentObjectProperties() {
		OWLAxiom axiom = df.getOWLEquivalentObjectPropertiesAxiom(pR, df.getOWLObjectInverseOf(pS), pT);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		Atom atS = Expressions.makeAtom(nS, Arrays.asList(secondVariable, converter.frontierVariable));
		Atom atT = Expressions.makeAtom(nT, Arrays.asList(converter.frontierVariable, secondVariable));
		Rule ruleRS = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)),
				Expressions.makeConjunction(Arrays.asList(atR)));
		Rule ruleST = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atT)),
				Expressions.makeConjunction(Arrays.asList(atS)));
		Rule ruleTR = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR)),
				Expressions.makeConjunction(Arrays.asList(atT)));
		Rule ruleRT = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atT)),
				Expressions.makeConjunction(Arrays.asList(atR)));
		Rule ruleTS = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)),
				Expressions.makeConjunction(Arrays.asList(atT)));
		Rule ruleSR = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR)),
				Expressions.makeConjunction(Arrays.asList(atS)));

		// We have to test against two possible iteration orders, which may occur
		// non-deterministically and affect the result: R S T or R T S
		// (other orders lead to the same outcome)
		assertTrue(converter.rules.equals(Sets.newSet(ruleRS, ruleST, ruleTR))
				|| converter.rules.equals(Sets.newSet(ruleRT, ruleTS, ruleSR)));
	}

	@Test
	public void testSubObjectPropertyChain() {
		OWLAxiom axiom = df.getOWLSubPropertyChainOfAxiom(Arrays.asList(pR, df.getOWLObjectInverseOf(pS), pT), pU);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Variable var1 = Expressions.makeVariable("Y1");
		Variable var2 = Expressions.makeVariable("Y2");
		Variable var3 = Expressions.makeVariable("Y3");
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, var1));
		Atom atS = Expressions.makeAtom(nS, Arrays.asList(var2, var1));
		Atom atT = Expressions.makeAtom(nT, Arrays.asList(var2, var3));
		Atom atU = Expressions.makeAtom(nU, Arrays.asList(converter.frontierVariable, var3));
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atU)),
				Expressions.makeConjunction(Arrays.asList(atR, atS, atT)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	public void testTransitiveProperty() {
		OWLAxiom axiom = df.getOWLTransitiveObjectPropertyAxiom(pR);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Variable var1 = Expressions.makeVariable("Y1");
		Variable var2 = Expressions.makeVariable("Y2");
		Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, var1));
		Atom at2 = Expressions.makeAtom(nR, Arrays.asList(var1, var2));
		Atom ath = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, var2));
		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(ath)),
				Expressions.makeConjunction(Arrays.asList(at1, at2)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testEquivalentClasses() {
		OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(cA, cB, cC);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		Atom atC = Expressions.makeAtom(nC, Arrays.asList(converter.frontierVariable));
		Rule ruleAB = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atB)),
				Expressions.makeConjunction(Arrays.asList(atA)));
		Rule ruleBC = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atC)),
				Expressions.makeConjunction(Arrays.asList(atB)));
		Rule ruleCA = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(atC)));
		Rule ruleAC = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atC)),
				Expressions.makeConjunction(Arrays.asList(atA)));
		Rule ruleCB = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atB)),
				Expressions.makeConjunction(Arrays.asList(atC)));
		Rule ruleBA = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(atB)));

		// We have to test against two possible iteration orders, which may occur
		// non-deterministically and affect the result: A B C or A C B
		// (other orders lead to the same outcome)
		assertTrue(converter.rules.equals(Sets.newSet(ruleAB, ruleBC, ruleCA))
				|| converter.rules.equals(Sets.newSet(ruleAC, ruleCB, ruleBA)));
	}

	@Test
	public void testObjectPropertyDomain() {
		OWLAxiom axiom = df.getOWLObjectPropertyDomainAxiom(pR, cA);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));

		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testObjectPropertyRange() {
		OWLAxiom axiom = df.getOWLObjectPropertyRangeAxiom(pR, cA);

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		Variable secondVariable = Expressions.makeVariable("Y1");
		Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));

		Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
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

		OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		for (Rule rule : converter.rules) {
			System.out.println(rule);
		}
	}

}
