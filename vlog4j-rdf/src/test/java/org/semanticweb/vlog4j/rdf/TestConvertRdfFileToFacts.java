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
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteral;
import static org.semanticweb.vlog4j.rdf.RdfModelToPositiveLiteralsConverter.RDF_TRIPLE_PREDICATE_NAME;
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
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Term;

public class TestConvertRdfFileToFacts {

	// FIXME: The openrdf parser does neither support '\b' nor '\f' (from ASCII) and
	// encodes such characters as "\u0008" and "\u000C", respectively (the
	// corresponding Unicode hex code).

	private static final Set<PositiveLiteral> expectedNormalizedPositiveLiterals = new HashSet<>(Arrays.asList(
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"), makeConstant("file:/a"),
					makeConstant(intoLexical("-1", "integer"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/2"), makeConstant("file:/a"),
					makeConstant(intoLexical("1", "integer"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/3"), makeConstant("file:/a"),
					makeConstant(intoLexical("-1.0", "decimal"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/4"), makeConstant("file:/a"),
					makeConstant(intoLexical("1.0", "decimal"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/5"), makeConstant("file:/a"),
					makeConstant(intoLexical("-1.1E1", "double"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/6"), makeConstant("file:/a"),
					makeConstant(intoLexical("1.1E1", "double"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/7"), makeConstant("file:/a"),
					makeConstant(intoLexical("true", "boolean")))));

	private static final Set<PositiveLiteral> expectedLiteralPositiveLiterals = new HashSet<>(Arrays.asList(
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"), makeConstant("file:/a"),
					makeConstant(intoLexical("1", "integer"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/2"), makeConstant("file:/a"),
					makeConstant(intoLexical("1.0", "decimal"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/3"), makeConstant("file:/a"),
					makeConstant(intoLexical("1.0E1", "double"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/4"), makeConstant("file:/a"),
					makeConstant(intoLexical("true", "boolean"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/5"), makeConstant("file:/a"),
					makeConstant(intoLexical("false", "boolean"))),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/6"), makeConstant("file:/a"),
					makeConstant("\"test string\""))));

	private static final Set<PositiveLiteral> expectedRelativeUriPositiveLiterals = new HashSet<>(Arrays.asList(
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("http://example.org/1"),
					makeConstant("http://example.org/a"), makeConstant("http://example.org/#1")),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("http://example.org/2"),
					makeConstant("http://example.org/a"), makeConstant("http://example.org/#1")),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("http://example.org/3"),
					makeConstant("http://example.org/a"), makeConstant("http://example.org/#1"))));

	private static final Set<PositiveLiteral> expectedEscapedCharacterPositiveLiterals = new HashSet<>(
			Arrays.asList(makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"),
					makeConstant("file:/a"), makeConstant("\"\\t\\u0008\\n\\r\\u000C\\\"'\\\\\""))));

	private static final Set<PositiveLiteral> expectedLanguageTagPositiveLiterals = new HashSet<>(Arrays.asList(
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"), makeConstant("file:/a"),
					makeConstant("\"This is a test.\"@en")),
			makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"), makeConstant("file:/a"),
					makeConstant("\"Das ist ein Test.\"@de"))));

	@Test
	public void testDataTypesNormalized() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils
				.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "unnormalizedLiteralValues.ttl"), RDFFormat.TURTLE);
		final Set<PositiveLiteral> PositiveLiteralsFromModel = RdfModelToPositiveLiteralsConverter
				.rdfModelToPositiveLiterals(model);
		assertEquals(expectedNormalizedPositiveLiterals, PositiveLiteralsFromModel);
	}

	@Test
	public void testLiteralValuesPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "literalValues.ttl"),
				RDFFormat.TURTLE);
		final Set<PositiveLiteral> PositiveLiteralsFromModel = RdfModelToPositiveLiteralsConverter
				.rdfModelToPositiveLiterals(model);
		assertEquals(expectedLiteralPositiveLiterals, PositiveLiteralsFromModel);
	}

	@Test
	public void testRelativeURIsMadeAbsolute() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "relativeURIs.ttl"),
				RDFFormat.TURTLE);
		final Set<PositiveLiteral> PositiveLiteralsFromModel = RdfModelToPositiveLiteralsConverter
				.rdfModelToPositiveLiterals(model);
		assertEquals(expectedRelativeUriPositiveLiterals, PositiveLiteralsFromModel);
	}

	@Test
	public void testEscapedCharactersPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "escapedCharacters.ttl"),
				RDFFormat.TURTLE);
		final Set<PositiveLiteral> PositiveLiteralsFromModel = RdfModelToPositiveLiteralsConverter
				.rdfModelToPositiveLiterals(model);
		assertEquals(expectedEscapedCharacterPositiveLiterals, PositiveLiteralsFromModel);
	}

	@Test
	public void testLanguageTagsPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "languageTags.ttl"),
				RDFFormat.TURTLE);
		final Set<PositiveLiteral> PositiveLiteralsFromModel = RdfModelToPositiveLiteralsConverter
				.rdfModelToPositiveLiterals(model);
		assertEquals(expectedLanguageTagPositiveLiterals, PositiveLiteralsFromModel);
	}

	@Test
	public void testCollectionsPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "collections.ttl"),
				RDFFormat.TURTLE);
		final Set<PositiveLiteral> PositiveLiteralsFromModel = RdfModelToPositiveLiteralsConverter
				.rdfModelToPositiveLiterals(model);

		final Term blank1 = RdfTestUtils.getObjectOfFirstMatchedTriple(makeConstant("file:/2"), makeConstant("file:/a"),
				PositiveLiteralsFromModel);
		final Term blank2 = RdfTestUtils.getObjectOfFirstMatchedTriple(makeConstant("file:/3"), makeConstant("file:/a"),
				PositiveLiteralsFromModel);
		final Term blank3 = RdfTestUtils.getObjectOfFirstMatchedTriple(blank2, RDF_REST, PositiveLiteralsFromModel);

		final Set<PositiveLiteral> expectedSetPositiveLiterals = new HashSet<>(Arrays.asList(
				makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"), makeConstant("file:/a"),
						RDF_NIL),
				makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/2"), makeConstant("file:/a"),
						blank1),
				makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, blank1, RDF_FIRST,
						makeConstant(intoLexical("1", "integer"))),
				makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, blank1, RDF_REST, RDF_NIL),
				makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/3"), makeConstant("file:/a"),
						blank2),
				makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, blank2, RDF_FIRST, makeConstant("file:/#1")),
				makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, blank2, RDF_REST, blank3),
				makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, blank3, RDF_FIRST, makeConstant("file:/#2")),
				makePositiveLiteral(RDF_TRIPLE_PREDICATE_NAME, blank3, RDF_REST, RDF_NIL)));

		assertEquals(expectedSetPositiveLiterals, PositiveLiteralsFromModel);
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
		final Set<PositiveLiteral> PositiveLiterals = RdfModelToPositiveLiteralsConverter
				.rdfModelToPositiveLiterals(model);

		final Set<Blank> blanks = new HashSet<>();
		PositiveLiterals.forEach(positiveLiteral -> blanks.addAll(positiveLiteral.getBlanks()));
		return blanks;
	}

}
