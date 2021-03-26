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

import org.junit.Test;
import org.semanticweb.rulewerk.asp.implementation.AspifIdentifier;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.NegativeLiteral;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class AspifIdentifierTest {

	final Variable x = Expressions.makeUniversalVariable("X");

	final Constant c = Expressions.makeAbstractConstant("c");
	final Constant d = Expressions.makeAbstractConstant("d");

	final PositiveLiteral atom = Expressions.makePositiveLiteral("p", x);
	final PositiveLiteral atom2 = Expressions.makePositiveLiteral("q", x, c);
	final NegativeLiteral negLiteral = Expressions.makeNegativeLiteral("p", x);

	@Test
	public void getTermNamesTest() {
		AspifIdentifier aspifIdentifier = new AspifIdentifier(atom2, Arrays.asList(d, c));
		String[] termNames = aspifIdentifier.getTermNames();
		assertEquals(2, termNames.length);
		assertEquals("d", termNames[0]);
		assertEquals("c", termNames[1]);
	}

	@Test
	public void getPredicateNameTest() {
		AspifIdentifier aspifIdentifier = new AspifIdentifier(atom2, Arrays.asList(d, c));
		assertEquals("q", aspifIdentifier.getPredicateName());
	}

	@Test
	public void getPositiveLiteralTest() {
		AspifIdentifier aspifIdentifier = new AspifIdentifier(negLiteral, Collections.singletonList(d));
		assertEquals(Expressions.makePositiveLiteral("p", d), aspifIdentifier.getPositiveLiteral());
	}

	@Test
	public void equalsTest() {
		AspifIdentifier.reset();
		AspifIdentifier identifier = new AspifIdentifier(atom, Collections.singletonList(c));
		AspifIdentifier identifier2 = new AspifIdentifier(atom, Collections.singletonList(d));
		AspifIdentifier identifier3 = new AspifIdentifier(negLiteral, Collections.singletonList(d));
		AspifIdentifier identifier4 = new AspifIdentifier(atom2, Arrays.asList(c, d));

		assertEquals(identifier, identifier);
		assertEquals(identifier2, identifier3);

		assertNotEquals(identifier, identifier2);
		assertNotEquals(identifier, null);
		assertNotEquals(identifier, identifier4);
		assertNotEquals(identifier, 1);
	}

	@Test
	public void getAspifValueTest() {
		AspifIdentifier.reset();
		assertEquals(1, AspifIdentifier.getAspifValue(atom, Collections.singletonList(c)));
		assertEquals(2, AspifIdentifier.getAspifValue(atom, Collections.singletonList(d)));
		assertEquals(3, AspifIdentifier.getAspifValue());
		assertEquals(-2, AspifIdentifier.getAspifValue(negLiteral, Collections.singletonList(d)));
		assertEquals(4, AspifIdentifier.getAspifValue(atom2, Arrays.asList(c, d)));
	}
}
