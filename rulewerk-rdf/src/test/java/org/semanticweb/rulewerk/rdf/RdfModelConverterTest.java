package org.semanticweb.rulewerk.rdf;

/*-
 * #%L
 * Rulewerk RDF Support
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;

public class RdfModelConverterTest {

	@Test
	public void addToKnowledgeBase_succeeds()
			throws RDFParseException, RDFHandlerException, IOException, PrefixDeclarationException {
		RdfModelConverter rdfModelConverter = new RdfModelConverter();
		Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "test-turtle.ttl"), RDFFormat.TURTLE);
		KnowledgeBase knowledgeBase = new KnowledgeBase();

		Predicate predicate = Expressions.makePredicate("TRIPLE", 3);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termb = Expressions.makeAbstractConstant("http://example.org/b");
		Term termc = Expressions.makeAbstractConstant("http://example.org/c");
		Fact fact = Expressions.makeFact(predicate, terma, termb, termc);

		rdfModelConverter.addAll(knowledgeBase, model);

		assertEquals(Arrays.asList(fact), knowledgeBase.getFacts());
		assertEquals("http://example.org/", knowledgeBase.getPrefixIri(":"));
	}

	@Test
	public void getFactSet_succeeds()
			throws RDFParseException, RDFHandlerException, IOException, PrefixDeclarationException {
		RdfModelConverter rdfModelConverter = new RdfModelConverter();
		Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "test-turtle.ttl"), RDFFormat.TURTLE);

		Predicate predicate = Expressions.makePredicate("TRIPLE", 3);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termb = Expressions.makeAbstractConstant("http://example.org/b");
		Term termc = Expressions.makeAbstractConstant("http://example.org/c");
		Fact fact = Expressions.makeFact(predicate, terma, termb, termc);
		Set<Fact> expected = new HashSet<Fact>();
		expected.add(fact);

		Set<Fact> facts = rdfModelConverter.rdfModelToFacts(model);

		assertEquals(expected, facts);
	}

	@Test
	public void addFactsCustomTriplePredicate_succeeds()
			throws RDFParseException, RDFHandlerException, IOException, PrefixDeclarationException {
		RdfModelConverter rdfModelConverter = new RdfModelConverter(true, "mytriple");
		Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "test-turtle.ttl"), RDFFormat.TURTLE);
		KnowledgeBase knowledgeBase = new KnowledgeBase();

		Predicate predicate = Expressions.makePredicate("mytriple", 3);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termb = Expressions.makeAbstractConstant("http://example.org/b");
		Term termc = Expressions.makeAbstractConstant("http://example.org/c");
		Fact fact = Expressions.makeFact(predicate, terma, termb, termc);

		rdfModelConverter.addFacts(knowledgeBase, model);

		assertEquals(Arrays.asList(fact), knowledgeBase.getFacts());
	}

	@Test
	public void addFactsNoTriplePredicate_succeeds()
			throws RDFParseException, RDFHandlerException, IOException, PrefixDeclarationException {
		RdfModelConverter rdfModelConverter = new RdfModelConverter(true, null);
		Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "test-turtle.ttl"), RDFFormat.TURTLE);
		KnowledgeBase knowledgeBase = new KnowledgeBase();

		Predicate predicate = Expressions.makePredicate("http://example.org/b", 2);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termc = Expressions.makeAbstractConstant("http://example.org/c");
		Fact fact = Expressions.makeFact(predicate, terma, termc);

		rdfModelConverter.addFacts(knowledgeBase, model);

		assertEquals(Arrays.asList(fact), knowledgeBase.getFacts());
	}

	@Test
	public void addFactsNoTriplePredicateType_succeeds()
			throws RDFParseException, RDFHandlerException, IOException, PrefixDeclarationException {
		RdfModelConverter rdfModelConverter = new RdfModelConverter(true, null);
		Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "test-turtle-type.ttl"),
				RDFFormat.TURTLE);
		KnowledgeBase knowledgeBase = new KnowledgeBase();

		Predicate predicate = Expressions.makePredicate("http://example.org/c", 1);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Fact fact = Expressions.makeFact(predicate, terma);

		rdfModelConverter.addFacts(knowledgeBase, model);

		assertEquals(Arrays.asList(fact), knowledgeBase.getFacts());
	}

	@Test
	public void addFactsNoTriplePredicateTypeWeird_succeeds()
			throws RDFParseException, RDFHandlerException, IOException, PrefixDeclarationException {
		RdfModelConverter rdfModelConverter = new RdfModelConverter(true, null);
		Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "test-turtle-type-weird.ttl"), RDFFormat.TURTLE);
		KnowledgeBase knowledgeBase = new KnowledgeBase();

		Predicate predicate = Expressions.makePredicate(PrefixDeclarationRegistry.RDF_TYPE, 2);
		Term terma = Expressions.makeAbstractConstant("http://example.org/a");
		Term termc = Expressions.makeLanguageStringConstant("test", "de");
		Fact fact = Expressions.makeFact(predicate, terma, termc);

		rdfModelConverter.addFacts(knowledgeBase, model);

		assertEquals(Arrays.asList(fact), knowledgeBase.getFacts());
	}

}
