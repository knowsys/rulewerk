package org.semanticweb.rulewerk.asp;

/*-
 * #%L
 * Rulewerk ASP Components
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

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.rulewerk.asp.implementation.AspifIdentifier;
import org.semanticweb.rulewerk.asp.implementation.RuleAspifTemplate;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class RuleAspifTemplateTest {

	final Variable x = Expressions.makeUniversalVariable("X");
	final Variable y = Expressions.makeUniversalVariable("Y");
	final Variable z = Expressions.makeUniversalVariable("Z");

	final Constant c = Expressions.makeAbstractConstant("c");
	final Constant d = Expressions.makeAbstractConstant("d");
	final Constant e = Expressions.makeAbstractConstant("d");

	final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x);
	final PositiveLiteral atom2 = Expressions.makePositiveLiteral("q", x, y, z);
	final PositiveLiteral atom3 = Expressions.makePositiveLiteral("r", y);
	final PositiveLiteral atomWithConstant = Expressions.makePositiveLiteral("q", c, z);
	final NegativeLiteral negativeLiteral = Expressions.makeNegativeLiteral("r", x, d);

	@Before
	public void setUp() {
		AspifIdentifier.reset();
	}

	@Test
	public void writeInstances() throws IOException {
		Set<Predicate> approximatedPredicates = new HashSet<>();
		approximatedPredicates.add(Expressions.makePredicate("p", 1));
		approximatedPredicates.add(Expressions.makePredicate("q", 3));
		approximatedPredicates.add(Expressions.makePredicate("r", 1));
		approximatedPredicates.add(Expressions.makePredicate("r", 2));

		Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom1, atom3);
		Conjunction<Literal> body = Expressions.makeConjunction(atom2, negativeLiteral);
		Rule rule = Expressions.makeRule(head, body);
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		PositiveLiteral query = Expressions.makePositiveLiteral("s", x, y, z);

		RuleAspifTemplate template = new RuleAspifTemplate(rule, bufferedWriter, query, approximatedPredicates);
		template.writeGroundInstances(Arrays.asList(c, d, e));
		bufferedWriter.flush();
		assertEquals("1 0 1 3 0 2 1 -2\n" +
			"1 0 1 4 0 2 1 -2\n", writer.toString());
		Map<Integer, AspifIdentifier> map = AspifIdentifier.getIntegerAspifIdentifierMap();
		assertEquals(new AspifIdentifier(atom2, Arrays.asList(c, d, e)), map.get(1));
		assertEquals(new AspifIdentifier(negativeLiteral, Arrays.asList(c, d)), map.get(2));
		assertEquals(new AspifIdentifier(atom1, Collections.singletonList(c)), map.get(3));
		assertEquals(new AspifIdentifier(atom3, Collections.singletonList(d)), map.get(4));
	}

	@Test
	public void writeInstancesWithConstantInRule() throws IOException {
		Set<Predicate> approximatedPredicates = new HashSet<>();
		approximatedPredicates.add(Expressions.makePredicate("q", 3));

		Rule rule = Expressions.makeRule(atomWithConstant, atom2);
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		PositiveLiteral query = Expressions.makePositiveLiteral("s", x, y, z);

		RuleAspifTemplate template = new RuleAspifTemplate(rule, bufferedWriter, query, approximatedPredicates);
		template.writeGroundInstances(Arrays.asList(c, d, e));
		bufferedWriter.flush();
		assertEquals("1 0 1 2 0 1 1\n", writer.toString());
		Map<Integer, AspifIdentifier> map = AspifIdentifier.getIntegerAspifIdentifierMap();
		assertEquals(new AspifIdentifier(atom2, Arrays.asList(c, d, e)), map.get(1));
		assertEquals(new AspifIdentifier(atomWithConstant, Arrays.asList(c, e)), map.get(2));
	}

	@Test
	public void writeInstancesWithConstantInQuery() throws IOException {
		Set<Predicate> approximatedPredicates = new HashSet<>();
		approximatedPredicates.add(Expressions.makePredicate("q", 3));

		Rule rule = Expressions.makeRule(atomWithConstant, atom2);
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		PositiveLiteral query = Expressions.makePositiveLiteral("s", x, y, z, c);
		RuleAspifTemplate template = new RuleAspifTemplate(rule, bufferedWriter, query, approximatedPredicates);
		template.writeGroundInstances(Arrays.asList(c, d, e));
		bufferedWriter.flush();

		assertEquals("1 0 1 2 0 1 1\n", writer.toString());
		Map<Integer, AspifIdentifier> map = AspifIdentifier.getIntegerAspifIdentifierMap();
		assertEquals(new AspifIdentifier(atom2, Arrays.asList(c, d, e)), map.get(1));
		assertEquals(new AspifIdentifier(atomWithConstant, Arrays.asList(c, e)), map.get(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void templateWithNotCapturedVariable() throws IOException {
		Set<Predicate> approximatedPredicates = new HashSet<>();
		approximatedPredicates.add(Expressions.makePredicate("q", 3));

		Rule rule = Expressions.makeRule(atomWithConstant, atom2);
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		PositiveLiteral query = Expressions.makePositiveLiteral("s", x, d, z);
		new RuleAspifTemplate(rule, bufferedWriter, query, approximatedPredicates);
	}

	@Test
	public void approximatedAndNonApproximatedPredicatesTest() throws IOException {
		Set<Predicate> approximatedPredicates = new HashSet<>();
		approximatedPredicates.add(Expressions.makePredicate("q", 3));

		Rule rule = Expressions.makeRule(atomWithConstant, atom2, atom1);
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		PositiveLiteral query = Expressions.makePositiveLiteral("s", x, y, z, c);
		RuleAspifTemplate template = new RuleAspifTemplate(rule, bufferedWriter, query, approximatedPredicates);
		template.writeGroundInstances(Arrays.asList(c, d, e));
		bufferedWriter.flush();

		assertEquals("1 0 1 2 0 1 1\n", writer.toString());
		Map<Integer, AspifIdentifier> map = AspifIdentifier.getIntegerAspifIdentifierMap();
		assertEquals(new AspifIdentifier(atom2, Arrays.asList(c, d, e)), map.get(1));
		assertEquals(new AspifIdentifier(atomWithConstant, Arrays.asList(c, e)), map.get(2));
	}

}
