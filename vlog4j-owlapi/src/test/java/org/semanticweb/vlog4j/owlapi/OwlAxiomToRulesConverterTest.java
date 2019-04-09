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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class OwlAxiomToRulesConverterTest {

	static OWLDataFactory df = OWLManager.getOWLDataFactory();

	public static IRI getIri(final String localName) {
		return IRI.create("http://example.org/" + localName);
	}

	public static OWLClass getOwlClass(final String localName) {
		return df.getOWLClass(getIri(localName));
	}

	public static OWLObjectProperty getOwlObjectProperty(final String localName) {
		return df.getOWLObjectProperty(getIri(localName));
	}

	public static Predicate getClassPredicate(final String localName) {
		return Expressions.makePredicate("http://example.org/" + localName, 1);
	}

	public static Predicate getPropertyPredicate(final String localName) {
		return Expressions.makePredicate("http://example.org/" + localName, 2);
	}

	static OWLClass cA = getOwlClass("A");
	static OWLClass cB = getOwlClass("B");
	static OWLClass cC = getOwlClass("C");
	static OWLClass cD = getOwlClass("D");
	static OWLClass cE = getOwlClass("E");
	static OWLObjectProperty pR = getOwlObjectProperty("Rule");
	static OWLObjectProperty pS = getOwlObjectProperty("S");
	static OWLObjectProperty pT = getOwlObjectProperty("T");
	static OWLObjectProperty pU = getOwlObjectProperty("U");

	static Predicate nA = getClassPredicate("A");
	static Predicate nB = getClassPredicate("B");
	static Predicate nC = getClassPredicate("C");
	static Predicate nD = getClassPredicate("D");
	static Predicate nE = getClassPredicate("E");
	static Predicate nR = getPropertyPredicate("Rule");
	static Predicate nS = getPropertyPredicate("S");
	static Predicate nT = getPropertyPredicate("T");
	static Predicate nU = getPropertyPredicate("U");

	static OWLIndividual inda = df.getOWLNamedIndividual(getIri("a"));
	static OWLIndividual indb = df.getOWLNamedIndividual(getIri("b"));
	static OWLIndividual indc = df.getOWLNamedIndividual(getIri("c"));

	@Test
	public void testSimpleRule() {
		final OWLObjectIntersectionOf body = df.getOWLObjectIntersectionOf(cA, cB, cC);
		final OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(cD, cE);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(body, head);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atC = Expressions.makePositiveLiteral(nC, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atD = Expressions.makePositiveLiteral(nD, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atE = Expressions.makePositiveLiteral(nE, Arrays.asList(converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makePositiveLiteralsConjunction(Arrays.asList(atD, atE)),
				Expressions.makeConjunction(Arrays.asList(atA, atB, atC)));

		assertEquals(Collections.singleton(rule), converter.rules);

	}

	@Test
	public void testTrueBody() {
		final OWLClassExpression body = df.getOWLObjectIntersectionOf(df.getOWLThing(), df.getOWLObjectAllValuesFrom(df.getOWLBottomObjectProperty(), cB));
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(body, cA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral top = OwlToRulesConversionHelper.getTop(converter.frontierVariable);
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)), Expressions.makeConjunction(Arrays.asList(top)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testConjunctionTruth() {
		final OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(cB, df.getOWLThing(), cC);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		final Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		final Atom atC = Expressions.makeAtom(nC, Arrays.asList(converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atB, atC)), Expressions.makeConjunction(Arrays.asList(atA)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testConjunctionTruthTruth() {
		final OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(df.getOWLThing(), df.getOWLThing());
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		assertEquals(0, converter.rules.size());
	}

	@Test
	public void testConjunctionFalsity() {
		final OWLClassExpression notSupported = df.getOWLObjectExactCardinality(10, pR);
		final OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(notSupported, df.getOWLNothing(), cC);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		final Atom bot = OwlToRulesConversionHelper.getBottom(converter.frontierVariable);
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(bot)), Expressions.makeConjunction(Arrays.asList(atA)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test(expected = OwlFeatureNotSupportedException.class)
	public void testConjunctionException() {
		final OWLClassExpression notSupported = df.getOWLObjectExactCardinality(10, pR);
		final OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(notSupported, cC);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);
	}

	@Test
	public void testConjunctionNegativeLiterals() {
		final OWLClassExpression notA = df.getOWLObjectComplementOf(cA);
		final OWLClassExpression notB = df.getOWLObjectComplementOf(cB);
		final OWLClassExpression notC = df.getOWLObjectComplementOf(cC);
		final OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(notB, notC);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(notA, head);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Predicate auxPredicate = OwlToRulesConversionHelper.getAuxiliaryClassPredicate(Arrays.asList(notB, notC));

		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		final Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		final Atom atC = Expressions.makeAtom(nC, Arrays.asList(converter.frontierVariable));
		final Atom atAux = Expressions.makeAtom(auxPredicate, Arrays.asList(converter.frontierVariable));

		final Rule rule1 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atAux)), Expressions.makeConjunction(Arrays.asList(atB)));
		final Rule rule2 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atAux)), Expressions.makeConjunction(Arrays.asList(atC)));
		final Rule rule3 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)), Expressions.makeConjunction(Arrays.asList(atAux)));

		assertEquals(Sets.newSet(rule1, rule2, rule3), converter.rules);
	}

	@Test
	public void testContrapositive() {
		final OWLClassExpression notA = df.getOWLObjectComplementOf(cA);
		final OWLClassExpression notB = df.getOWLObjectComplementOf(cB);
		final OWLClassExpression notC = df.getOWLObjectComplementOf(cC);
		final OWLClassExpression notBOrNotC = df.getOWLObjectUnionOf(notB, notC);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(notA, notBOrNotC);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		final Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		final Atom atC = Expressions.makeAtom(nC, Arrays.asList(converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)), Expressions.makeConjunction(Arrays.asList(atB, atC)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testPositiveUniversal() {
		final OWLClassExpression forallRA = df.getOWLObjectAllValuesFrom(pR, cA);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cB, forallRA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));
		final Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)), Expressions.makeConjunction(Arrays.asList(atR, atB)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testPositiveExistential() {
		final OWLClassExpression existsRA = df.getOWLObjectSomeValuesFrom(pR, cA);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cB, existsRA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));
		final Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR, atA)), Expressions.makeConjunction(Arrays.asList(atB)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testNegativeUniversal() {
		final OWLClassExpression forallRA = df.getOWLObjectAllValuesFrom(pR, cA);
		final OWLClassExpression notB = df.getOWLObjectComplementOf(cB);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(forallRA, notB);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Predicate auxPredicate = OwlToRulesConversionHelper.getAuxiliaryClassPredicate(Arrays.asList(cA));
		final Variable secondVariable = Expressions.makeVariable("Y1");

		final Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		final Atom atAux = Expressions.makeAtom(auxPredicate, Arrays.asList(secondVariable));
		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));
		final Atom bot = OwlToRulesConversionHelper.getBottom(secondVariable);

		final Rule rule1 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR, atAux)), Expressions.makeConjunction(Arrays.asList(atB)));
		final Rule rule2 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(bot)), Expressions.makeConjunction(Arrays.asList(atAux, atA)));

		assertEquals(Sets.newSet(rule1, rule2), converter.rules);
	}

	@Test
	public void testNegativeExistential() {
		final OWLClassExpression existRA = df.getOWLObjectSomeValuesFrom(pR, cA);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(existRA, cB);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));
		final Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atB)), Expressions.makeConjunction(Arrays.asList(atR, atA)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testSelf() {
		final OWLClassExpression selfR = df.getOWLObjectHasSelf(pR);
		final OWLClassExpression selfS = df.getOWLObjectHasSelf(pS);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(selfR, selfS);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, converter.frontierVariable));
		final Atom atS = Expressions.makeAtom(nS, Arrays.asList(converter.frontierVariable, converter.frontierVariable));

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)), Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testHasValue() {
		final OWLClassExpression hasRa = df.getOWLObjectHasValue(pR, inda);
		final OWLClassExpression hasSb = df.getOWLObjectHasValue(pS, indb);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(hasRa, hasSb);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Term consta = Expressions.makeConstant(getIri("a").toString());
		final Term constb = Expressions.makeConstant(getIri("b").toString());
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, consta));
		final Atom atS = Expressions.makeAtom(nS, Arrays.asList(converter.frontierVariable, constb));

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)), Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testObjectPropertyAssertions() {
		final OWLAxiom Rab = df.getOWLObjectPropertyAssertionAxiom(pR, inda, indb);
		final OWLAxiom invSab = df.getOWLObjectPropertyAssertionAxiom(df.getOWLObjectInverseOf(pS), inda, indb);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		Rab.accept(converter);
		invSab.accept(converter);

		final Term consta = Expressions.makeConstant(getIri("a").toString());
		final Term constb = Expressions.makeConstant(getIri("b").toString());
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(consta, constb));
		final Atom atS = Expressions.makeAtom(nS, Arrays.asList(constb, consta));

		assertEquals(Sets.newSet(atR, atS), converter.facts);
	}

	@Test
	public void testClassAssertions() {
		final OWLAxiom Ca = df.getOWLClassAssertionAxiom(cC, indc);
		final OWLClassExpression BandhasRb = df.getOWLObjectIntersectionOf(cB, df.getOWLObjectHasValue(pR, indb));
		final OWLAxiom BandhasRba = df.getOWLClassAssertionAxiom(BandhasRb, inda);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		Ca.accept(converter);
		BandhasRba.accept(converter);

		final Term consta = Expressions.makeConstant(getIri("a").toString());
		final Term constb = Expressions.makeConstant(getIri("b").toString());
		final Term constc = Expressions.makeConstant(getIri("c").toString());
		final Atom atC = Expressions.makeAtom(nC, Arrays.asList(constc));
		final Atom atB = Expressions.makeAtom(nB, Arrays.asList(consta));
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(consta, constb));

		assertEquals(Sets.newSet(atC, atB, atR), converter.facts);
	}

	@Test
	public void testNegativeObjectPropertyAssertions() {
		final OWLAxiom Rab = df.getOWLNegativeObjectPropertyAssertionAxiom(pR, inda, indb);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		Rab.accept(converter);

		final Term consta = Expressions.makeConstant(getIri("a").toString());
		final Term constb = Expressions.makeConstant(getIri("b").toString());
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(consta, constb));
		final Atom bot = OwlToRulesConversionHelper.getBottom(consta);

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(bot)), Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testSubObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLSubObjectPropertyOfAxiom(pR, df.getOWLObjectInverseOf(pS));

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		final Atom atS = Expressions.makeAtom(nS, Arrays.asList(secondVariable, converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)), Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testAsymmetricObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLAsymmetricObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		final Atom at2 = Expressions.makeAtom(nR, Arrays.asList(secondVariable, converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(OwlToRulesConversionHelper.getBottom(converter.frontierVariable))),
				Expressions.makeConjunction(Arrays.asList(at1, at2)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testSymmetricObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLSymmetricObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		final Atom at2 = Expressions.makeAtom(nR, Arrays.asList(secondVariable, converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(at2)), Expressions.makeConjunction(Arrays.asList(at1)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testIrreflexiveObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLIrreflexiveObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(OwlToRulesConversionHelper.getBottom(converter.frontierVariable))),
				Expressions.makeConjunction(Arrays.asList(at1)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testReflexiveObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLReflexiveObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(at1)),
				Expressions.makeConjunction(Arrays.asList(OwlToRulesConversionHelper.getTop(converter.frontierVariable))));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testInverseObjectProperties() {
		final OWLAxiom axiom = df.getOWLInverseObjectPropertiesAxiom(pR, pS);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		final Atom atS = Expressions.makeAtom(nS, Arrays.asList(secondVariable, converter.frontierVariable));
		final Rule rule1 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)), Expressions.makeConjunction(Arrays.asList(atR)));
		final Rule rule2 = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR)), Expressions.makeConjunction(Arrays.asList(atS)));

		assertEquals(Sets.newSet(rule1, rule2), converter.rules);
	}

	@Test
	public void testEquivalentObjectProperties() {
		final OWLAxiom axiom = df.getOWLEquivalentObjectPropertiesAxiom(pR, df.getOWLObjectInverseOf(pS), pT);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		final Atom atS = Expressions.makeAtom(nS, Arrays.asList(secondVariable, converter.frontierVariable));
		final Atom atT = Expressions.makeAtom(nT, Arrays.asList(converter.frontierVariable, secondVariable));
		final Rule ruleRS = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)), Expressions.makeConjunction(Arrays.asList(atR)));
		final Rule ruleST = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atT)), Expressions.makeConjunction(Arrays.asList(atS)));
		final Rule ruleTR = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR)), Expressions.makeConjunction(Arrays.asList(atT)));
		final Rule ruleRT = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atT)), Expressions.makeConjunction(Arrays.asList(atR)));
		final Rule ruleTS = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atS)), Expressions.makeConjunction(Arrays.asList(atT)));
		final Rule ruleSR = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR)), Expressions.makeConjunction(Arrays.asList(atS)));

		// We have to test against two possible iteration orders, which may occur
		// non-deterministically and affect the result: Rule S T or Rule T S
		// (other orders lead to the same outcome)
		assertTrue(converter.rules.equals(Sets.newSet(ruleRS, ruleST, ruleTR)) || converter.rules.equals(Sets.newSet(ruleRT, ruleTS, ruleSR)));
	}

	@Test
	public void testSubObjectPropertyChain() {
		final OWLAxiom axiom = df.getOWLSubPropertyChainOfAxiom(Arrays.asList(pR, df.getOWLObjectInverseOf(pS), pT), pU);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable var1 = Expressions.makeVariable("Y1");
		final Variable var2 = Expressions.makeVariable("Y2");
		final Variable var3 = Expressions.makeVariable("Y3");
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, var1));
		final Atom atS = Expressions.makeAtom(nS, Arrays.asList(var2, var1));
		final Atom atT = Expressions.makeAtom(nT, Arrays.asList(var2, var3));
		final Atom atU = Expressions.makeAtom(nU, Arrays.asList(converter.frontierVariable, var3));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atU)), Expressions.makeConjunction(Arrays.asList(atR, atS, atT)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	public void testTransitiveProperty() {
		final OWLAxiom axiom = df.getOWLTransitiveObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable var1 = Expressions.makeVariable("Y1");
		final Variable var2 = Expressions.makeVariable("Y2");
		final Atom at1 = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, var1));
		final Atom at2 = Expressions.makeAtom(nR, Arrays.asList(var1, var2));
		final Atom ath = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, var2));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(ath)), Expressions.makeConjunction(Arrays.asList(at1, at2)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testEquivalentClasses() {
		final OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(cA, cB, cC);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));
		final Atom atB = Expressions.makeAtom(nB, Arrays.asList(converter.frontierVariable));
		final Atom atC = Expressions.makeAtom(nC, Arrays.asList(converter.frontierVariable));
		final Rule ruleAB = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atB)), Expressions.makeConjunction(Arrays.asList(atA)));
		final Rule ruleBC = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atC)), Expressions.makeConjunction(Arrays.asList(atB)));
		final Rule ruleCA = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)), Expressions.makeConjunction(Arrays.asList(atC)));
		final Rule ruleAC = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atC)), Expressions.makeConjunction(Arrays.asList(atA)));
		final Rule ruleCB = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atB)), Expressions.makeConjunction(Arrays.asList(atC)));
		final Rule ruleBA = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)), Expressions.makeConjunction(Arrays.asList(atB)));

		// We have to test against two possible iteration orders, which may occur
		// non-deterministically and affect the result: A B C or A C B
		// (other orders lead to the same outcome)
		assertTrue(converter.rules.equals(Sets.newSet(ruleAB, ruleBC, ruleCA)) || converter.rules.equals(Sets.newSet(ruleAC, ruleCB, ruleBA)));
	}

	@Test
	public void testObjectPropertyDomain() {
		final OWLAxiom axiom = df.getOWLObjectPropertyDomainAxiom(pR, cA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(converter.frontierVariable));

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)), Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testObjectPropertyRange() {
		final OWLAxiom axiom = df.getOWLObjectPropertyRangeAxiom(pR, cA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final Atom atR = Expressions.makeAtom(nR, Arrays.asList(converter.frontierVariable, secondVariable));
		final Atom atA = Expressions.makeAtom(nA, Arrays.asList(secondVariable));

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)), Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Ignore
	public void test() {
		final OWLObjectPropertyExpression Sinv = df.getOWLObjectInverseOf(pS);
		final OWLObjectSomeValuesFrom SomeSinvE = df.getOWLObjectSomeValuesFrom(Sinv, cE);
		final OWLObjectSomeValuesFrom SomeRSomeSinvE = df.getOWLObjectSomeValuesFrom(pR, SomeSinvE);
		final OWLObjectUnionOf AorB = df.getOWLObjectUnionOf(cA, cB);
		final OWLObjectIntersectionOf AorBandCandSomeRSomeSinvE = df.getOWLObjectIntersectionOf(AorB, cC, SomeRSomeSinvE);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(AorBandCandSomeRSomeSinvE, cD);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		for (final Rule rule : converter.rules) {
			System.out.println(rule);
		}
	}

}
