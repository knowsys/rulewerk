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
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.rdf.RdfModelToAtomsConverter.RDF_TRIPLE_PREDICATE_NAME;

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
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Blank;

public class TestTurtleToFacts {

	// TODO: test of collections and nested unlabelled blank nodes? ~> IDs of blank
	// nodes determined are dynamic, which makes testing hard
	// FIXME: the openrdf parser does neither support '\b' nor '\f' (from ASCII) nor
	// any unicode, and encodes all of that in hex

	private static final Set<Atom> expectedNormalizedAtoms = new HashSet<>(Arrays.asList(
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"), makeConstant("file:/a"),
					makeConstant(intoLexical("-1", "integer"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/2"), makeConstant("file:/a"),
					makeConstant(intoLexical("1", "integer"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/3"), makeConstant("file:/a"),
					makeConstant(intoLexical("-1.0", "decimal"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/4"), makeConstant("file:/a"),
					makeConstant(intoLexical("1.0", "decimal"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/5"), makeConstant("file:/a"),
					makeConstant(intoLexical("-1.1E1", "double"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/6"), makeConstant("file:/a"),
					makeConstant(intoLexical("1.1E1", "double"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/7"), makeConstant("file:/a"),
					makeConstant(intoLexical("true", "boolean")))));

	private static final Set<Atom> expectedLiteralAtoms = new HashSet<>(Arrays.asList(
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"), makeConstant("file:/a"),
					makeConstant(intoLexical("1", "integer"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/2"), makeConstant("file:/a"),
					makeConstant(intoLexical("1.0", "decimal"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/3"), makeConstant("file:/a"),
					makeConstant(intoLexical("1.0E1", "double"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/4"), makeConstant("file:/a"),
					makeConstant(intoLexical("true", "boolean"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/5"), makeConstant("file:/a"),
					makeConstant(intoLexical("false", "boolean"))),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/6"), makeConstant("file:/a"),
					makeConstant("\"test string\""))));

	private static final Set<Atom> expectedRelativeUriAtoms = new HashSet<>(Arrays.asList(
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("http://example.org/1"),
					makeConstant("http://example.org/a"), makeConstant("http://example.org/#1")),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("http://example.org/2"),
					makeConstant("http://example.org/a"), makeConstant("http://example.org/#1")),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("http://example.org/3"),
					makeConstant("http://example.org/a"), makeConstant("http://example.org/#1"))));

	private static final Set<Atom> expectedEscapedCharacterAtoms = new HashSet<>(
			Arrays.asList(makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"), makeConstant("file:/a"),
					makeConstant("\"\\t\\u0008\\n\\r\\u000C\\\"'\\\\\""))));

	private static final Set<Atom> expectedLanguageTagAtoms = new HashSet<>(Arrays.asList(
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"),
					makeConstant("file:/a"), makeConstant("\"This is a test.\"@en")),
			makeAtom(RDF_TRIPLE_PREDICATE_NAME, makeConstant("file:/1"),
					makeConstant("file:/a"), makeConstant("\"Das ist ein Test.\"@de"))));

	private static String intoLexical(final String abbreviated, final String type) {
		return "\"" + abbreviated + "\"^^<http://www.w3.org/2001/XMLSchema#" + type + ">";
	}

	@Test
	public void testDataTypesNormalized() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(
				new File(RdfTestUtils.INPUT_FOLDER + "unnormalizedLiteralValues.ttl"),
				RDFFormat.TURTLE);
		final Set<Atom> atomsFromModel = RdfModelToAtomsConverter.rdfModelToAtoms(model);
		assertEquals(expectedNormalizedAtoms, atomsFromModel);
	}

	@Test
	public void testLiteralValuesPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "literalValues.ttl"),
				RDFFormat.TURTLE);
		final Set<Atom> atomsFromModel = RdfModelToAtomsConverter.rdfModelToAtoms(model);
		assertEquals(expectedLiteralAtoms, atomsFromModel);
	}

	@Test
	public void testRelativeURIsMadeAbsolute() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "relativeURIs.ttl"),
				RDFFormat.TURTLE);
		final Set<Atom> atomsFromModel = RdfModelToAtomsConverter.rdfModelToAtoms(model);
		assertEquals(expectedRelativeUriAtoms, atomsFromModel);
	}

	@Test
	public void testEscapedCharactersPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "escapedCharacters.ttl"),
				RDFFormat.TURTLE);
		final Set<Atom> atomsFromModel = RdfModelToAtomsConverter.rdfModelToAtoms(model);
		assertEquals(expectedEscapedCharacterAtoms, atomsFromModel);
	}

	@Test
	public void testLanguageTagsPreserved() throws RDFHandlerException, RDFParseException, IOException {
		final Model model = RdfTestUtils.parseFile(new File(RdfTestUtils.INPUT_FOLDER + "languageTags.ttl"),
				RDFFormat.TURTLE);
		final Set<Atom> atomsFromModel = RdfModelToAtomsConverter.rdfModelToAtoms(model);
		assertEquals(expectedLanguageTagAtoms, atomsFromModel);
	}

	@Test
	public void testNumberOfBlankNodesCorrect() throws RDFParseException, RDFHandlerException, IOException {
		final File labelledFile = new File(RdfTestUtils.INPUT_FOLDER + "labelledBNodes.ttl");
		final File unlabelledFile = new File(RdfTestUtils.INPUT_FOLDER + "unlabelledBNodes.ttl");
		final Set<Blank> labelledBlanks = getBlanksFromTurtleFile(labelledFile);
		final Set<Blank> unlabelledBlanks = getBlanksFromTurtleFile(unlabelledFile);

		assertEquals(2, labelledBlanks.size());
		assertEquals(2, unlabelledBlanks.size());
	}

	@Test
	public void testBlankNodesWithSameLabelAreDifferentInDifferentModels()
			throws RDFParseException, RDFHandlerException, IOException {
		final File file = new File(RdfTestUtils.INPUT_FOLDER + "labelledBNodes.ttl");
		final Set<Blank> blanks1 = getBlanksFromTurtleFile(file);
		final Set<Blank> blanks2 = getBlanksFromTurtleFile(file);

		assertTrue(CollectionUtils.intersection(blanks1, blanks2).isEmpty());
	}

	private Set<Blank> getBlanksFromTurtleFile(final File file)
			throws RDFParseException, RDFHandlerException, IOException {
		final Model model = RdfTestUtils.parseFile(file, RDFFormat.TURTLE);
		final Set<Atom> atoms = RdfModelToAtomsConverter.rdfModelToAtoms(model);

		final Set<Blank> blanks = new HashSet<>();
		atoms.forEach(atom -> blanks.addAll(atom.getBlanks()));
		return blanks;
	}

}
