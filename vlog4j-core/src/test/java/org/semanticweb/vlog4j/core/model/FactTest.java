package org.semanticweb.vlog4j.core.model;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.FactImpl;

public class FactTest {

	@Test
	public void factsConstructor() {
		Predicate p = Expressions.makePredicate("p", 2);
		Constant c = Expressions.makeAbstractConstant("c");
		Constant d = Expressions.makeAbstractConstant("d");
		Fact f1 = Expressions.makeFact(p, Arrays.asList(c, d));
		Fact f2 = Expressions.makeFact("p", Arrays.asList(c, d));
		Fact f3 = new FactImpl(p, Arrays.asList(c, d));
		assertEquals(f1, f2);
		assertEquals(f1, f3);
		assertEquals(f2, f3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void factsOnlyContainConstants() {
		Predicate p = Expressions.makePredicate("p", 1);
		Variable x = Expressions.makeUniversalVariable("X");
		new FactImpl(p, Arrays.asList(x));
	}

	@Test
	public void factToStringTest() {
		final Predicate p = Expressions.makePredicate("p", 2);
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		final Fact f1 = Expressions.makeFact(p, Arrays.asList(c, d));
		assertEquals("p(c, d).", f1.toString());
	}

}
