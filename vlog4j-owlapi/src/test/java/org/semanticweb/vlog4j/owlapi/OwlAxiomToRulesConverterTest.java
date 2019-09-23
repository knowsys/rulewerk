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

		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, converter.frontierVariable);
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, converter.frontierVariable);
		final PositiveLiteral atC = Expressions.makePositiveLiteral(nC, converter.frontierVariable);
		final PositiveLiteral atD = Expressions.makePositiveLiteral(nD, converter.frontierVariable);
		final PositiveLiteral atE = Expressions.makePositiveLiteral(nE, converter.frontierVariable);
		final Rule rule = Expressions.makeRule(Expressions.makePositiveConjunction(atD, atE),
				Expressions.makeConjunction(atA, atB, atC));

		assertEquals(Collections.singleton(rule), converter.rules);

	}

	@Test
	public void testTrueBody() {
		final OWLClassExpression body = df.getOWLObjectIntersectionOf(df.getOWLThing(),
				df.getOWLObjectAllValuesFrom(df.getOWLBottomObjectProperty(), cB));
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(body, cA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral top = OwlToRulesConversionHelper.getTop(converter.frontierVariable);
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(top)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testConjunctionTruth() {
		final OWLObjectIntersectionOf head = df.getOWLObjectIntersectionOf(cB, df.getOWLThing(), cC);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cA, head);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atC = Expressions.makePositiveLiteral(nC, Arrays.asList(converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makePositiveConjunction(atB, atC),
				Expressions.makeConjunction(atA));

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

		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral bot = OwlToRulesConversionHelper.getBottom(converter.frontierVariable);
		final Rule rule = Expressions.makeRule(bot, atA);

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

		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atC = Expressions.makePositiveLiteral(nC, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atAux = Expressions.makePositiveLiteral(auxPredicate,
				Arrays.asList(converter.frontierVariable));

		final Rule rule1 = Expressions.makeRule(atAux, atB);
		final Rule rule2 = Expressions.makeRule(atAux, atC);
		final Rule rule3 = Expressions.makeRule(atA, atAux);

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

		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atC = Expressions.makePositiveLiteral(nC, Arrays.asList(converter.frontierVariable));
		final Rule rule = Expressions.makeRule(Expressions.makePositiveConjunction(atA),
				Expressions.makeConjunction(atB, atC));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testPositiveUniversal() {
		final OWLClassExpression forallRA = df.getOWLObjectAllValuesFrom(pR, cA);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cB, forallRA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(secondVariable));
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR,
				Arrays.asList(converter.frontierVariable, secondVariable));

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(atR, atB)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testPositiveExistential() {
		final OWLClassExpression existsRA = df.getOWLObjectSomeValuesFrom(pR, cA);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(cB, existsRA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(secondVariable));
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR,
				Arrays.asList(converter.frontierVariable, secondVariable));

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atR, atA)),
				Expressions.makeConjunction(Arrays.asList(atB)));

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

		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR,
				Arrays.asList(converter.frontierVariable, secondVariable));
		final PositiveLiteral atAux = Expressions.makePositiveLiteral(auxPredicate, Arrays.asList(secondVariable));
		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(secondVariable));
		final PositiveLiteral bot = OwlToRulesConversionHelper.getBottom(secondVariable);

		final Rule rule1 = Expressions.makeRule(Expressions.makePositiveConjunction(atR, atAux),
				Expressions.makeConjunction(atB));
		final Rule rule2 = Expressions.makeRule(Expressions.makePositiveConjunction(bot),
				Expressions.makeConjunction(atAux, atA));

		assertEquals(Sets.newSet(rule1, rule2), converter.rules);
	}

	@Test
	public void testNegativeExistential() {
		final OWLClassExpression existRA = df.getOWLObjectSomeValuesFrom(pR, cA);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(existRA, cB);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, converter.frontierVariable, secondVariable);
		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, secondVariable);
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, converter.frontierVariable);

		final Rule rule = Expressions.makeRule(atB, atR, atA);

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testSelf() {
		final OWLClassExpression selfR = df.getOWLObjectHasSelf(pR);
		final OWLClassExpression selfS = df.getOWLObjectHasSelf(pS);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(selfR, selfS);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, converter.frontierVariable,
				converter.frontierVariable);
		final PositiveLiteral atS = Expressions.makePositiveLiteral(nS, converter.frontierVariable,
				converter.frontierVariable);

		final Rule rule = Expressions.makeRule(atS, atR);

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
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, converter.frontierVariable, consta);
		final PositiveLiteral atS = Expressions.makePositiveLiteral(nS, converter.frontierVariable, constb);

		final Rule rule = Expressions.makeRule(atS, atR);

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
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, Arrays.asList(consta, constb));
		final PositiveLiteral atS = Expressions.makePositiveLiteral(nS, Arrays.asList(constb, consta));

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
		final PositiveLiteral atC = Expressions.makePositiveLiteral(nC, constc);
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, consta);
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, consta, constb);

		assertEquals(Sets.newSet(atC, atB, atR), converter.facts);
	}

	@Test
	public void testNegativeObjectPropertyAssertions() {
		final OWLAxiom Rab = df.getOWLNegativeObjectPropertyAssertionAxiom(pR, inda, indb);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		Rab.accept(converter);

		final Term consta = Expressions.makeConstant(getIri("a").toString());
		final Term constb = Expressions.makeConstant(getIri("b").toString());
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, Arrays.asList(consta, constb));
		final PositiveLiteral bot = OwlToRulesConversionHelper.getBottom(consta);

		final Rule rule = Expressions.makeRule(bot, atR);

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testSubObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLSubObjectPropertyOfAxiom(pR, df.getOWLObjectInverseOf(pS));

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, converter.frontierVariable, secondVariable);
		final PositiveLiteral atS = Expressions.makePositiveLiteral(nS, secondVariable, converter.frontierVariable);
		final Rule rule = Expressions.makeRule(atS, atR);

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testAsymmetricObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLAsymmetricObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral at1 = Expressions.makePositiveLiteral(nR, converter.frontierVariable, secondVariable);
		final PositiveLiteral at2 = Expressions.makePositiveLiteral(nR, secondVariable, converter.frontierVariable);
		final Rule rule = Expressions.makeRule(OwlToRulesConversionHelper.getBottom(converter.frontierVariable), at1,
				at2);

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testSymmetricObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLSymmetricObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral at1 = Expressions.makePositiveLiteral(nR, converter.frontierVariable, secondVariable);
		final PositiveLiteral at2 = Expressions.makePositiveLiteral(nR, secondVariable, converter.frontierVariable);
		final Rule rule = Expressions.makeRule(at2, at1);

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testIrreflexiveObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLIrreflexiveObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final PositiveLiteral at1 = Expressions.makePositiveLiteral(nR, converter.frontierVariable,
				converter.frontierVariable);
		final Rule rule = Expressions.makeRule(OwlToRulesConversionHelper.getBottom(converter.frontierVariable), at1);

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testReflexiveObjectPropertyOf() {
		final OWLAxiom axiom = df.getOWLReflexiveObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final PositiveLiteral at1 = Expressions.makePositiveLiteral(nR, converter.frontierVariable,
				converter.frontierVariable);
		final Rule rule = Expressions.makeRule(at1, OwlToRulesConversionHelper.getTop(converter.frontierVariable));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testInverseObjectProperties() {
		final OWLAxiom axiom = df.getOWLInverseObjectPropertiesAxiom(pR, pS);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, converter.frontierVariable, secondVariable);
		final PositiveLiteral atS = Expressions.makePositiveLiteral(nS, secondVariable, converter.frontierVariable);
		final Rule rule1 = Expressions.makeRule(atS, atR);
		final Rule rule2 = Expressions.makeRule(atR, atS);

		assertEquals(Sets.newSet(rule1, rule2), converter.rules);
	}

	@Test
	public void testEquivalentObjectProperties() {
		final OWLAxiom axiom = df.getOWLEquivalentObjectPropertiesAxiom(pR, df.getOWLObjectInverseOf(pS), pT);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR,
				Arrays.asList(converter.frontierVariable, secondVariable));
		final PositiveLiteral atS = Expressions.makePositiveLiteral(nS,
				Arrays.asList(secondVariable, converter.frontierVariable));
		final PositiveLiteral atT = Expressions.makePositiveLiteral(nT,
				Arrays.asList(converter.frontierVariable, secondVariable));
		final Rule ruleRS = Expressions.makeRule(atS, atR);
		final Rule ruleST = Expressions.makeRule(atT, atS);
		final Rule ruleTR = Expressions.makeRule(atR, atT);
		final Rule ruleRT = Expressions.makeRule(atT, atR);
		final Rule ruleTS = Expressions.makeRule(atS, atT);
		final Rule ruleSR = Expressions.makeRule(atR, atS);

		// We have to test against two possible iteration orders, which may occur
		// non-deterministically and affect the result: Rule S T or Rule T S
		// (other orders lead to the same outcome)
		assertTrue(converter.rules.equals(Sets.newSet(ruleRS, ruleST, ruleTR))
				|| converter.rules.equals(Sets.newSet(ruleRT, ruleTS, ruleSR)));
	}

	@Test
	public void testSubObjectPropertyChain() {
		final OWLAxiom axiom = df.getOWLSubPropertyChainOfAxiom(Arrays.asList(pR, df.getOWLObjectInverseOf(pS), pT),
				pU);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable var1 = Expressions.makeVariable("Y1");
		final Variable var2 = Expressions.makeVariable("Y2");
		final Variable var3 = Expressions.makeVariable("Y3");
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR,
				Arrays.asList(converter.frontierVariable, var1));
		final PositiveLiteral atS = Expressions.makePositiveLiteral(nS, Arrays.asList(var2, var1));
		final PositiveLiteral atT = Expressions.makePositiveLiteral(nT, Arrays.asList(var2, var3));
		final PositiveLiteral atU = Expressions.makePositiveLiteral(nU,
				Arrays.asList(converter.frontierVariable, var3));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atU)),
				Expressions.makeConjunction(Arrays.asList(atR, atS, atT)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	public void testTransitiveProperty() {
		final OWLAxiom axiom = df.getOWLTransitiveObjectPropertyAxiom(pR);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable var1 = Expressions.makeVariable("Y1");
		final Variable var2 = Expressions.makeVariable("Y2");
		final PositiveLiteral at1 = Expressions.makePositiveLiteral(nR,
				Arrays.asList(converter.frontierVariable, var1));
		final PositiveLiteral at2 = Expressions.makePositiveLiteral(nR, Arrays.asList(var1, var2));
		final PositiveLiteral ath = Expressions.makePositiveLiteral(nR,
				Arrays.asList(converter.frontierVariable, var2));
		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(ath)),
				Expressions.makeConjunction(Arrays.asList(at1, at2)));

		assertEquals(Sets.newSet(rule), converter.rules);
	}

	@Test
	public void testEquivalentClasses() {
		final OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(cA, cB, cC);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atB = Expressions.makePositiveLiteral(nB, Arrays.asList(converter.frontierVariable));
		final PositiveLiteral atC = Expressions.makePositiveLiteral(nC, Arrays.asList(converter.frontierVariable));
		final Rule ruleAB = Expressions.makeRule(atB, atA);
		final Rule ruleBC = Expressions.makeRule(atC, atB);
		final Rule ruleCA = Expressions.makeRule(atA, atC);
		final Rule ruleAC = Expressions.makeRule(atC, atA);
		final Rule ruleCB = Expressions.makeRule(atB, atC);
		final Rule ruleBA = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(atB)));

		// We have to test against two possible iteration orders, which may occur
		// non-deterministically and affect the result: A B C or A C B
		// (other orders lead to the same outcome)
		assertTrue(converter.rules.equals(Sets.newSet(ruleAB, ruleBC, ruleCA))
				|| converter.rules.equals(Sets.newSet(ruleAC, ruleCB, ruleBA)));
	}

	@Test
	public void testObjectPropertyDomain() {
		final OWLAxiom axiom = df.getOWLObjectPropertyDomainAxiom(pR, cA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, converter.frontierVariable, secondVariable);
		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, converter.frontierVariable);

		final Rule rule = Expressions.makeRule(atA, atR);

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Test
	public void testObjectPropertyRange() {
		final OWLAxiom axiom = df.getOWLObjectPropertyRangeAxiom(pR, cA);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		final Variable secondVariable = Expressions.makeVariable("Y1");
		final PositiveLiteral atR = Expressions.makePositiveLiteral(nR, converter.frontierVariable, secondVariable);
		final PositiveLiteral atA = Expressions.makePositiveLiteral(nA, secondVariable);

		final Rule rule = Expressions.makeRule(Expressions.makeConjunction(Arrays.asList(atA)),
				Expressions.makeConjunction(Arrays.asList(atR)));

		assertEquals(Collections.singleton(rule), converter.rules);
	}

	@Ignore
	public void test() {
		final OWLObjectPropertyExpression Sinv = df.getOWLObjectInverseOf(pS);
		final OWLObjectSomeValuesFrom SomeSinvE = df.getOWLObjectSomeValuesFrom(Sinv, cE);
		final OWLObjectSomeValuesFrom SomeRSomeSinvE = df.getOWLObjectSomeValuesFrom(pR, SomeSinvE);
		final OWLObjectUnionOf AorB = df.getOWLObjectUnionOf(cA, cB);
		final OWLObjectIntersectionOf AorBandCandSomeRSomeSinvE = df.getOWLObjectIntersectionOf(AorB, cC,
				SomeRSomeSinvE);
		final OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(AorBandCandSomeRSomeSinvE, cD);

		final OwlAxiomToRulesConverter converter = new OwlAxiomToRulesConverter();
		axiom.accept(converter);

		for (final Rule rule : converter.rules) {
			System.out.println(rule);
		}
	}

}
