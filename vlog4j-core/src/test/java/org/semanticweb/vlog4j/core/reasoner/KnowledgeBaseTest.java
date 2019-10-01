package org.semanticweb.vlog4j.core.reasoner;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class KnowledgeBaseTest {

	private KnowledgeBase kb;
	private final Fact fact1 = Expressions.makeFact("P", Expressions.makeConstant("c"));
	private final Fact fact2 = Expressions.makeFact("P", Expressions.makeConstant("d"));
	private final Fact fact3 = Expressions.makeFact("Q", Expressions.makeConstant("c"));

	@Before
	public void initKB() {
		kb = new KnowledgeBase();
		kb.addStatements(fact1, fact2, fact3);
	}

	@Test
	public void testDoRemoveStatementExistent() {
		final boolean removed = kb.doRemoveStatement(fact1);

		assertTrue(removed);
		assertEquals(Arrays.asList(fact2, fact3), kb.getFacts());
		assertEquals(Sets.newSet(fact2), kb.getFactsByPredicate().get(fact1.getPredicate()));
	}

	@Test
	public void testDoRemoveStatementOnlyExistentWithPredicate() {
		final boolean removed = kb.doRemoveStatement(fact3);

		assertTrue(removed);
		assertEquals(Arrays.asList(fact1, fact2), kb.getFacts());
		assertEquals(null, kb.getFactsByPredicate().get(fact3.getPredicate()));
	}

	@Test
	public void testDoRemoveStatementInexistent() {
		final Fact fact = Expressions.makeFact("P", Expressions.makeConstant("e"));
		final boolean removed = kb.doRemoveStatement(fact);

		assertFalse(removed);
		assertEquals(Arrays.asList(fact1, fact2, fact3), kb.getFacts());
		assertEquals(Sets.newSet(fact1, fact2), kb.getFactsByPredicate().get(fact.getPredicate()));
		
		assertEquals(Sets.newSet(fact1, fact2), kb.getFactsByPredicate().get(fact1.getPredicate()));
		assertEquals(Sets.newSet(fact1, fact2), kb.getFactsByPredicate().get(fact2.getPredicate()));
		assertEquals(Sets.newSet(fact3), kb.getFactsByPredicate().get(fact3.getPredicate()));
	}

	@Test
	public void testDoRemoveStatementInexistentPredicate() {
		
		final Fact fact = Expressions.makeFact("R", Expressions.makeConstant("e"));
		final boolean removed = kb.doRemoveStatement(fact);

		assertFalse(removed);
		assertEquals(Arrays.asList(fact1, fact2, fact3), kb.getFacts());
		assertEquals(null, kb.getFactsByPredicate().get(fact.getPredicate()));
		
		assertEquals(Sets.newSet(fact1, fact2), kb.getFactsByPredicate().get(fact1.getPredicate()));
		assertEquals(Sets.newSet(fact1, fact2), kb.getFactsByPredicate().get(fact2.getPredicate()));
		assertEquals(Sets.newSet(fact3), kb.getFactsByPredicate().get(fact3.getPredicate()));

	}

}
