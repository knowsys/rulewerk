package org.semanticweb.vlog4j.rdf;

/*-
 * #%L
 * VLog4j RDF Support
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
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeDatatypeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeFact;
import static org.semanticweb.vlog4j.rdf.RdfModelConverter.RDF_TRIPLE_PREDICATE_NAME;
import static org.semanticweb.vlog4j.rdf.RdfTestUtils.RDF_FIRST;
import static org.semanticweb.vlog4j.rdf.RdfTestUtils.RDF_NIL;
import static org.semanticweb.vlog4j.rdf.RdfTestUtils.RDF_REST;
import static org.semanticweb.vlog4j.rdf.RdfTestUtils.intoLexical;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Term;

public class TestConvertRdfFileToFacts {

	// FIXME: The openrdf parser does neither support '\b' nor '\f' (from ASCII) and
	// encodes such characters as "\u0008" and "\u000C", respectively (the
	// corresponding Unicode hex code).

	private static final Set<Fact> expectedNormalizedFacts = new HashSet<>(Arrays.asList(
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/1"), makeConstant("file:/a"),
							makeDatatypeConstant("-1", "http://www.w3.org/2001/XMLSchema#integer"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/2"), makeConstant("file:/a"),
							makeDatatypeConstant("1", "http://www.w3.org/2001/XMLSchema#integer"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/3"), makeConstant("file:/a"),
							makeDatatypeConstant("-1.0", "http://www.w3.org/2001/XMLSchema#decimal"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/4"), makeConstant("file:/a"),
							makeDatatypeConstant("1.0", "http://www.w3.org/2001/XMLSchema#decimal"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/5"), makeConstant("file:/a"),
							makeDatatypeConstant("-1.1E1", "http://www.w3.org/2001/XMLSchema#double"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/6"), makeConstant("file:/a"),
							makeDatatypeConstant("1.1E1", "http://www.w3.org/2001/XMLSchema#double"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(makeConstant("file:/7"), makeConstant("file:/a"),
					makeDatatypeConstant("true", "http://www.w3.org/2001/XMLSchema#boolean")))));

	private static final Set<Fact> expectedLiteralFacts = new HashSet<>(Arrays.asList(
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/1"), makeConstant("file:/a"),
							makeDatatypeConstant("1", "http://www.w3.org/2001/XMLSchema#integer"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/2"), makeConstant("file:/a"),
							makeDatatypeConstant("1.0", "http://www.w3.org/2001/XMLSchema#decimal"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/3"), makeConstant("file:/a"),
							makeDatatypeConstant("1.0E1", "http://www.w3.org/2001/XMLSchema#double"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/4"), makeConstant("file:/a"),
							makeDatatypeConstant("true", "http://www.w3.org/2001/XMLSchema#boolean"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/5"), makeConstant("file:/a"),
							makeDatatypeConstant("false", "http://www.w3.org/2001/XMLSchema#boolean"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(makeConstant("file:/6"), makeConstant("file:/a"),
					makeDatatypeConstant("test string", "http://www.w3.org/2001/XMLSchema#string")))));

	private static final Set<Fact> expectedRelativeUriFacts = new HashSet<>(Arrays.asList(
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("http://example.org/1"), makeConstant("http://example.org/a"),
							makeConstant("http://example.org/#1"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("http://example.org/2"), makeConstant("http://example.org/a"),
							makeConstant("http://example.org/#1"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(makeConstant("http://example.org/3"),
					makeConstant("http://example.org/a"), makeConstant("http://example.org/#1")))));

	private static final Set<Fact> expectedEscapedCharacterFacts = new HashSet<>(
			Arrays.asList(makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/1"), makeConstant("file:/a"), makeDatatypeConstant(
							"\\t\\u0008\\n\\r\\u000C\\\"'\\\\", "http://www.w3.org/2001/XMLSchema#string")))));

	private static final Set<Fact> expectedLanguageTagFacts = new HashSet<>(Arrays.asList(
			makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(makeConstant("file:/1"), makeConstant("file:/a"),
							makeConstant("\"This is a test.\"@en"))),
			makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(makeConstant("file:/1"), makeConstant("file:/a"),
					makeConstant("\"Das ist ein Test.\"@de")))));

	@Test
	public void testDataTypesNormalized() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils
				.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "unnormalizedLiteralValues.ttl"), RDFFormat.TURTLE);
		final Set<Fact> facts = RdfModelConverter.rdfModelToFacts(model);
		assertEquals(expectedNormalizedFacts, facts);
	}

	@Test
	public void testLiteralValuesPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "literalValues.ttl"),
				RDFFormat.TURTLE);
		final Set<Fact> facts = RdfModelConverter.rdfModelToFacts(model);
		assertEquals(expectedLiteralFacts, facts);
	}

	@Test
	public void testRelativeURIsMadeAbsolute() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "relativeURIs.ttl"),
				RDFFormat.TURTLE);
		final Set<Fact> facts = RdfModelConverter.rdfModelToFacts(model);
		assertEquals(expectedRelativeUriFacts, facts);
	}

	@Test
	public void testEscapedCharactersPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "escapedCharacters.ttl"),
				RDFFormat.TURTLE);
		final Set<Fact> facts = RdfModelConverter.rdfModelToFacts(model);
		assertEquals(expectedEscapedCharacterFacts, facts);
	}

	@Test
	public void testLanguageTagsPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "languageTags.ttl"),
				RDFFormat.TURTLE);
		final Set<Fact> facts = RdfModelConverter.rdfModelToFacts(model);
		assertEquals(expectedLanguageTagFacts, facts);
	}

	@Test
	public void testCollectionsPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "collections.ttl"),
				RDFFormat.TURTLE);
		final Set<Fact> factsFromModel = RdfModelConverter.rdfModelToFacts(model);

		final Term blank1 = RdfTestUtils.getObjectOfFirstMatchedTriple(makeConstant("file:/2"), makeConstant("file:/a"),
				factsFromModel);
		final Term blank2 = RdfTestUtils.getObjectOfFirstMatchedTriple(makeConstant("file:/3"), makeConstant("file:/a"),
				factsFromModel);
		final Term blank3 = RdfTestUtils.getObjectOfFirstMatchedTriple(blank2, RDF_REST, factsFromModel);

		final Set<Fact> expectedSetFacts = new HashSet<>(Arrays.asList(
				makeFact(RDF_TRIPLE_PREDICATE_NAME,
						Arrays.asList(makeConstant("file:/1"), makeConstant("file:/a"), RDF_NIL)),
				makeFact(RDF_TRIPLE_PREDICATE_NAME,
						Arrays.asList(makeConstant("file:/2"), makeConstant("file:/a"), blank1)),
				makeFact(RDF_TRIPLE_PREDICATE_NAME,
						Arrays.asList(blank1, RDF_FIRST, makeConstant(intoLexical("1", "integer")))),
				makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank1, RDF_REST, RDF_NIL)),
				makeFact(RDF_TRIPLE_PREDICATE_NAME,
						Arrays.asList(makeConstant("file:/3"), makeConstant("file:/a"), blank2)),
				makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank2, RDF_FIRST, makeConstant("file:/#1"))),
				makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank2, RDF_REST, blank3)),
				makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank3, RDF_FIRST, makeConstant("file:/#2"))),
				makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank3, RDF_REST, RDF_NIL))));

		assertEquals(expectedSetFacts, factsFromModel);
	}

	@Test
	public void testNumberOfBlankNodesCorrect() throws RDFParseException, RDFHandlerException, IOException {
		final File labelledFile = new File(RdfTestUtils.INPUT_FOLDER + "labelledBNodes.ttl");
		final File unlabelledFile = new File(RdfTestUtils.INPUT_FOLDER + "unlabelledBNodes.ttl");
		final Set<Blank> labelledBlanks = this.getBlanksFromTurtleFile(labelledFile);
		final Set<Blank> unlabelledBlanks = this.getBlanksFromTurtleFile(unlabelledFile);

		assertEquals(2, labelledBlanks.size());
		assertEquals(2, unlabelledBlanks.size());
	}

	@Test
	public void testBlankNodesWithSameLabelAreDifferentInDifferentModels()
			throws RDFParseException, RDFHandlerException, IOException {
		final File file = new File(RdfTestUtils.INPUT_FOLDER + "labelledBNodes.ttl");
		final Set<Blank> blanks1 = this.getBlanksFromTurtleFile(file);
		final Set<Blank> blanks2 = this.getBlanksFromTurtleFile(file);

		assertTrue(CollectionUtils.intersection(blanks1, blanks2).isEmpty());
	}

	private Set<Blank> getBlanksFromTurtleFile(final File file)
			throws RDFParseException, RDFHandlerException, IOException {
		final Model model = RdfTestUtils.parseFile(file, RDFFormat.TURTLE);
		final Set<Fact> facts = RdfModelConverter.rdfModelToFacts(model);

		final Set<Blank> blanks = new HashSet<>();
		facts.forEach(fact -> blanks.addAll(fact.getBlanks()));
		return blanks;
	}

}
