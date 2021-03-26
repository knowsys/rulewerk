package org.semanticweb.rulewerk.core.reasoner;

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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.MergingPrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;

public class KnowledgeBaseTest {

	private KnowledgeBase kb;
	private final Fact fact1 = Expressions.makeFact("P", Expressions.makeAbstractConstant("c"));
	private final Fact fact2 = Expressions.makeFact("P", Expressions.makeAbstractConstant("d"));
	private final Fact fact3 = Expressions.makeFact("Q", Expressions.makeAbstractConstant("c"));
	private final PositiveLiteral literal1 = Expressions.makePositiveLiteral("P",
			Expressions.makeUniversalVariable("X"));
	private final PositiveLiteral literal2 = Expressions.makePositiveLiteral("Q",
			Expressions.makeUniversalVariable("X"));
	private final Rule rule = Expressions.makeRule(literal1, literal2);

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
	public void getBase_default_hasEmptyBase() {
		assertEquals("", this.kb.getBaseIri());
	}

	@Test(expected = PrefixDeclarationException.class)
	public void getPrefix_defaultUndeclaredPrefix_throws() throws PrefixDeclarationException {
		this.kb.getPrefixIri("ex:");
	}

	@Test(expected = PrefixDeclarationException.class)
	public void resolvePrefixedName_defaultUndeclaredPrefix_throws() throws PrefixDeclarationException {
		this.kb.resolvePrefixedName("ex:test");
	}

	@Test
	public void mergePrefixDeclarations_merge_succeeds() throws PrefixDeclarationException {
		String iri = "https://example.org/";
		MergingPrefixDeclarationRegistry prefixDeclarations = new MergingPrefixDeclarationRegistry();
		prefixDeclarations.setPrefixIri("ex:", iri);
		this.kb.mergePrefixDeclarations(prefixDeclarations);
		assertEquals(this.kb.getPrefixIri("ex:"), iri);
		assertEquals(this.kb.resolvePrefixedName("ex:test"), iri + "test");
		assertEquals(this.kb.unresolveAbsoluteIri(iri + "test"), "ex:test");
	}

	@Test
	public void writeKnowledgeBase_justFacts_succeeds() throws IOException {
		StringWriter writer = new StringWriter();
		this.kb.writeKnowledgeBase(writer);
		assertEquals("P(c) .\nP(d) .\nQ(c) .\n", writer.toString());
	}

	@Test(expected = RulewerkRuntimeException.class)
	public void writeKnowledgeBase_withBase_fails() throws IOException {
		String baseIri = "https://example.org/";
		MergingPrefixDeclarationRegistry prefixDeclarations = new MergingPrefixDeclarationRegistry();
		prefixDeclarations.setBaseIri(baseIri);
		this.kb.mergePrefixDeclarations(prefixDeclarations);
		StringWriter writer = new StringWriter();
		this.kb.writeKnowledgeBase(writer);
		//// This would be incorrect, since parsing this would lead to another KB
		//// that uses IRIs like <https://example.org/P>:
		// assertEquals("@base <" + baseIri + "> .\nP(c) .\nP(d) .\nQ(c) .\n",
		// writer.toString());
	}

	@Test
	public void writeKnowledgeBase_alsoRuleAndDataSource_succeeds() throws IOException {
		String sparqlIri = "https://example.org/sparql";
		String sparqlBgp = "?X ?p []";
		this.kb.addStatement(rule);
		this.kb.addStatement(new DataSourceDeclarationImpl(Expressions.makePredicate("S", 1),
				new SparqlQueryResultDataSource(new URL(sparqlIri), "?X", sparqlBgp)));

		StringWriter writer = new StringWriter();
		this.kb.writeKnowledgeBase(writer);
		assertEquals("@source S[1]: sparql(<" + sparqlIri + ">, \"?X\", \"" + sparqlBgp
				+ "\") .\n\nP(c) .\nP(d) .\nQ(c) .\n\nP(?X) :- Q(?X) .\n", writer.toString());
	}

	@Test
	public void getPredicatesTest() throws IOException {
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addStatement(rule);
		knowledgeBase.addStatement(Expressions.makeFact("R", Expressions.makeAbstractConstant("c")));
		knowledgeBase.addStatement(new DataSourceDeclarationImpl(
			Expressions.makePredicate("S", 2),
			new CsvFileDataSource("dummy.csv")
		));

		Set<Predicate> expectedPredicates = new HashSet<>(Arrays.asList(
			Expressions.makePredicate("P", 1),
			Expressions.makePredicate("Q", 1),
			Expressions.makePredicate("R", 1),
			Expressions.makePredicate("S", 2)));
		assertEquals(expectedPredicates, knowledgeBase.getPredicates());
	}
}
