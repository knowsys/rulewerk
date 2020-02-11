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
import org.semanticweb.vlog4j.core.exceptions.PrefixDeclarationException;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.MergeablePrefixDeclarations;

public class KnowledgeBaseTest {

	private KnowledgeBase kb;
	private final Fact fact1 = Expressions.makeFact("P", Expressions.makeAbstractConstant("c"));
	private final Fact fact2 = Expressions.makeFact("P", Expressions.makeAbstractConstant("d"));
	private final Fact fact3 = Expressions.makeFact("Q", Expressions.makeAbstractConstant("c"));

	@Before
	public void initKB() {
		this.kb = new KnowledgeBase();
		this.kb.addStatements(this.fact1, this.fact2, this.fact3);
	}

	@Test
	public void testDoRemoveStatementExistent() {
		final boolean removed = this.kb.doRemoveStatement(this.fact1);

		assertTrue(removed);
		assertEquals(Arrays.asList(this.fact2, this.fact3), this.kb.getFacts());
		assertEquals(Sets.newSet(this.fact2), this.kb.getFactsByPredicate().get(this.fact1.getPredicate()));
	}

	@Test
	public void testDoRemoveStatementOnlyExistentWithPredicate() {
		final boolean removed = this.kb.doRemoveStatement(this.fact3);

		assertTrue(removed);
		assertEquals(Arrays.asList(this.fact1, this.fact2), this.kb.getFacts());
		assertEquals(null, this.kb.getFactsByPredicate().get(this.fact3.getPredicate()));
	}

	@Test
	public void testDoRemoveStatementInexistent() {
		final Fact fact = Expressions.makeFact("P", Expressions.makeAbstractConstant("e"));
		final boolean removed = this.kb.doRemoveStatement(fact);

		assertFalse(removed);
		assertEquals(Arrays.asList(this.fact1, this.fact2, this.fact3), this.kb.getFacts());
		assertEquals(Sets.newSet(this.fact1, this.fact2), this.kb.getFactsByPredicate().get(fact.getPredicate()));

		assertEquals(Sets.newSet(this.fact1, this.fact2), this.kb.getFactsByPredicate().get(this.fact1.getPredicate()));
		assertEquals(Sets.newSet(this.fact1, this.fact2), this.kb.getFactsByPredicate().get(this.fact2.getPredicate()));
		assertEquals(Sets.newSet(this.fact3), this.kb.getFactsByPredicate().get(this.fact3.getPredicate()));
	}

	@Test
	public void testDoRemoveStatementInexistentPredicate() {

		final Fact fact = Expressions.makeFact("R", Expressions.makeAbstractConstant("e"));
		final boolean removed = this.kb.doRemoveStatement(fact);

		assertFalse(removed);
		assertEquals(Arrays.asList(this.fact1, this.fact2, this.fact3), this.kb.getFacts());
		assertEquals(null, this.kb.getFactsByPredicate().get(fact.getPredicate()));

		assertEquals(Sets.newSet(this.fact1, this.fact2), this.kb.getFactsByPredicate().get(this.fact1.getPredicate()));
		assertEquals(Sets.newSet(this.fact1, this.fact2), this.kb.getFactsByPredicate().get(this.fact2.getPredicate()));
		assertEquals(Sets.newSet(this.fact3), this.kb.getFactsByPredicate().get(this.fact3.getPredicate()));

	}

	@Test
	public void getPrefixDeclarations_default_hasEmptyBase() {
		assertEquals(this.kb.getPrefixDeclarations().getBase(), "");
	}

	@Test(expected = PrefixDeclarationException.class)
	public void getPrefixDeclarations_defaultUndeclaredPrefix_throws() throws PrefixDeclarationException {
		this.kb.getPrefixDeclarations().getPrefix("ex");
	}

	@Test
	public void mergePrefixDeclarations_merge_succeeds() throws PrefixDeclarationException {
		String iri = "https://example.org";
		MergeablePrefixDeclarations prefixDeclarations = new MergeablePrefixDeclarations();
		prefixDeclarations.setPrefix("ex", iri);
		this.kb.mergePrefixDeclarations(prefixDeclarations);
		assertEquals(this.kb.getPrefixDeclarations().getPrefix("ex"), iri);
	}
}
