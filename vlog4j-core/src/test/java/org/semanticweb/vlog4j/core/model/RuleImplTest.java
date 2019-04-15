package org.semanticweb.vlog4j.core.model;

/*-
 * #%L
 * VLog4j Core Components
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Collections;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.RuleImpl;

public class RuleImplTest {

	@Test
	public void testGetters() {
		final Variable x = Expressions.makeVariable("X");
		final Variable y = Expressions.makeVariable("Y");
		final Variable z = Expressions.makeVariable("Z");
		final Constant c = Expressions.makeConstant("c");
		final Constant d = Expressions.makeConstant("d");
		final Literal atom1 = Expressions.makePositiveLiteral("p", x, c);
		final Literal atom2 = Expressions.makePositiveLiteral("p", x, z);
		final PositiveLiteral atom3 = Expressions.makePositiveLiteral("q", x, y);
		final PositiveLiteral atom4 = Expressions.makePositiveLiteral("r", x, d);
		final Conjunction<Literal> body = Expressions.makeConjunction(atom1, atom2);
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom3, atom4);
		final Rule rule = Expressions.makeRule(head, body);

		assertEquals(body, rule.getBody());
		assertEquals(head, rule.getHead());
		assertEquals(Collections.singleton(y), rule.getExistentiallyQuantifiedVariables());
		assertEquals(Sets.newSet(x, z), rule.getUniversallyQuantifiedVariables());
	}

	@Test
	public void testEquals() {
		final Variable x = Expressions.makeVariable("X");
		final Variable y = Expressions.makeVariable("Y");
		final Variable z = Expressions.makeVariable("Z");
		final Constant c = Expressions.makeConstant("c");
		final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", x, c);
		final PositiveLiteral atom2 = Expressions.makePositiveLiteral("p", x, z);
		final PositiveLiteral atom3 = Expressions.makePositiveLiteral("q", x, y);
		final Conjunction<Literal> body1 = Expressions.makeConjunction(atom1, atom2);
		final Conjunction<PositiveLiteral> head1 = Expressions.makePositiveConjunction(atom3);
		final Conjunction<PositiveLiteral> body2 = Expressions.makePositiveConjunction(atom1, atom2);
		final Conjunction<Literal> head2 = Expressions.makeConjunction(atom3);
		
		final Rule rule1 = new RuleImpl(head1, body1);
		final Rule rule2 = Expressions.makeRule(atom3, atom1, atom2);
		final Rule rule3 = new RuleImpl(head1, head2);
		final Rule rule4 = new RuleImpl(body2, body1);
		final Rule rule5 = new RuleImpl(body2, head2);

		assertEquals(rule1, rule1);
		assertEquals(rule2, rule1);
		assertEquals(rule2.hashCode(), rule1.hashCode());
		assertNotEquals(rule3, rule1);
		assertNotEquals(rule3.hashCode(), rule1.hashCode());
		assertNotEquals(rule4, rule1);
		assertNotEquals(rule4.hashCode(), rule1.hashCode());
		assertNotEquals(rule5, rule1);
		assertFalse(rule1.equals(null));
		assertFalse(rule1.equals(c));
	}

	@Test(expected = IllegalArgumentException.class)
	public void bodyNonEmpty() {
		Expressions.makeRule(Expressions.makePositiveLiteral("p", Expressions.makeVariable("X")));
	}

	@Test(expected = NullPointerException.class)
	public void bodyNotNull() {
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(Expressions.makePositiveLiteral("p", Expressions.makeVariable("X")));
		Expressions.makeRule(head, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void headNonEmpty() {
		final Literal literal = Expressions.makePositiveLiteral("p", Expressions.makeVariable("X"));
		final Conjunction<Literal> body = Expressions.makeConjunction(literal);
		Expressions.makeRule(Expressions.makePositiveConjunction(), body);
	}

	@Test(expected = NullPointerException.class)
	public void headNotNull() {
		final Literal literal = Expressions.makePositiveLiteral("p", Expressions.makeVariable("X"));
		final Conjunction<Literal> body = Expressions.makeConjunction(literal);
		Expressions.makeRule(null, body);
	}

}
