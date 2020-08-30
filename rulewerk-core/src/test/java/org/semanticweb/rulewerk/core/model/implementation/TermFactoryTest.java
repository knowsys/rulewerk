package org.semanticweb.rulewerk.core.model.implementation;

/*-
 * #%L
 * Rulewerk Core Components
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

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.DatatypeConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.ExistentialVariableImpl;
import org.semanticweb.rulewerk.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.TermFactory;
import org.semanticweb.rulewerk.core.model.implementation.UniversalVariableImpl;

public class TermFactoryTest {

	@Test
	public void universalVariable_reused() {
		TermFactory termFactory = new TermFactory();
		Term term1 = termFactory.makeUniversalVariable("X");
		Term term2 = termFactory.makeUniversalVariable("Y");
		Term term3 = termFactory.makeUniversalVariable("X");
		Term term4 = new UniversalVariableImpl("X");

		assertNotEquals(term1, term2);
		assertTrue(term1 == term3);
		assertEquals(term1, term4);
	}

	@Test
	public void existentialVariable_reused() {
		TermFactory termFactory = new TermFactory();
		Term term1 = termFactory.makeExistentialVariable("X");
		Term term2 = termFactory.makeExistentialVariable("Y");
		Term term3 = termFactory.makeExistentialVariable("X");
		Term term4 = new ExistentialVariableImpl("X");

		assertNotEquals(term1, term2);
		assertTrue(term1 == term3);
		assertEquals(term1, term4);
	}

	@Test
	public void abstractConstant_reused() {
		TermFactory termFactory = new TermFactory();
		Term term1 = termFactory.makeAbstractConstant("X");
		Term term2 = termFactory.makeAbstractConstant("Y");
		Term term3 = termFactory.makeAbstractConstant("X");
		Term term4 = new AbstractConstantImpl("X");

		assertNotEquals(term1, term2);
		assertTrue(term1 == term3);
		assertEquals(term1, term4);
	}

	@Test
	public void predicate_reused() {
		TermFactory termFactory = new TermFactory();
		Predicate pred1 = termFactory.makePredicate("p", 1);
		Predicate pred2 = termFactory.makePredicate("q", 1);
		Predicate pred3 = termFactory.makePredicate("p", 2);
		Predicate pred4 = termFactory.makePredicate("p", 1);

		assertNotEquals(pred1, pred2);
		assertNotEquals(pred1, pred3);
		assertTrue(pred1 == pred4);
	}

	@Test
	public void datatypeConstant_succeeds() {
		TermFactory termFactory = new TermFactory();
		Term term1 = termFactory.makeDatatypeConstant("abc", "http://test");
		Term term2 = new DatatypeConstantImpl("abc", "http://test");

		assertEquals(term1, term2);
	}

	@Test
	public void languageConstant_succeeds() {
		TermFactory termFactory = new TermFactory();
		Term term1 = termFactory.makeLanguageStringConstant("abc", "de");
		Term term2 = new LanguageStringConstantImpl("abc", "de");

		assertEquals(term1, term2);
	}

	@Test
	public void lruCache_works() {
		TermFactory.SimpleLruMap<String, String> map = new TermFactory.SimpleLruMap<>(1, 3);
		map.put("a", "test");
		map.put("b", "test");
		map.put("c", "test");
		map.put("c", "test2");

		assertTrue(map.containsKey("b"));
		assertTrue(map.containsKey("c"));
		assertFalse(map.containsKey("a"));
	}

}
