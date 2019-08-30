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
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class TestConvertRdfFileToFacts {

	// FIXME: The openrdf parser does neither support '\b' nor '\f' (from ASCII) and
	// encodes such characters as "\u0008" and "\u000C", respectively (the
	// corresponding Unicode hex code).

	private final static Constant file1 = Expressions.makeConstant("file:/1");
	private final static Constant file2 = Expressions.makeConstant("file:/2");
	private final static Constant file3 = Expressions.makeConstant("file:/3");
	private final static Constant file4 = Expressions.makeConstant("file:/4");
	private final static Constant file5 = Expressions.makeConstant("file:/5");
	private final static Constant file6 = Expressions.makeConstant("file:/6");
	private final static Constant file7 = Expressions.makeConstant("file:/7");
	private final static Constant fileA = Expressions.makeConstant("file:/a");

	private final static Constant booleanTrue = Expressions.makeDatatypeConstant("true",
			"http://www.w3.org/2001/XMLSchema#boolean");
	private final static Constant booleanFalse = Expressions.makeDatatypeConstant("false",
			"http://www.w3.org/2001/XMLSchema#boolean");

	private final static Constant decimalOne = Expressions.makeDatatypeConstant("1.0",
			"http://www.w3.org/2001/XMLSchema#decimal");
	private final static Constant decimalMinusOne = Expressions.makeDatatypeConstant("-1.0",
			"http://www.w3.org/2001/XMLSchema#decimal");

	private final static Constant integerOne = Expressions.makeDatatypeConstant("1",
			"http://www.w3.org/2001/XMLSchema#integer");
	private final static Constant integerMinusOne = Expressions.makeDatatypeConstant("-1",
			"http://www.w3.org/2001/XMLSchema#integer");

	private final static Constant doubleOnePoitZero = Expressions.makeDatatypeConstant("1.0E1",
			"http://www.w3.org/2001/XMLSchema#double");
	private final static Constant doubleOnePoitOne = Expressions.makeDatatypeConstant("1.1E1",
			"http://www.w3.org/2001/XMLSchema#double");
	private final static Constant doubleMinusOnePoitOne = Expressions.makeDatatypeConstant("-1.1E1",
			"http://www.w3.org/2001/XMLSchema#double");

	private static final Set<Fact> expectedNormalizedFacts = new HashSet<>(
			Arrays.asList(Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file1, fileA, integerMinusOne)),
					Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file2, fileA, integerOne)),
					Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file3, fileA, decimalMinusOne)),
					Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file4, fileA, decimalOne)),
					Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file5, fileA, doubleMinusOnePoitOne)),
					Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file6, fileA, doubleOnePoitOne)),
					Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file7, fileA, booleanTrue))));

	private static final Set<Fact> expectedLiteralFacts = new HashSet<>(Arrays.asList(
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file1, fileA, integerOne)),
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file2, fileA, decimalOne)),
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file3, fileA, doubleOnePoitZero)),
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file4, fileA, booleanTrue)),
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file5, fileA, booleanFalse)),
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file6, fileA,
					Expressions.makeDatatypeConstant("test string", "http://www.w3.org/2001/XMLSchema#string")))));

	private final static Constant exampleA = Expressions.makeConstant("http://example.org/a");
	private final static Constant example1 = Expressions.makeConstant("http://example.org/1");
	private final static Constant example2 = Expressions.makeConstant("http://example.org/2");
	private final static Constant example3 = Expressions.makeConstant("http://example.org/3");
	private final static Constant exampleHash1 = Expressions.makeConstant("http://example.org/#1");

	private static final Set<Fact> expectedRelativeUriFacts = new HashSet<>(Arrays.asList(
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(example1, exampleA, exampleHash1)),
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(example2, exampleA, exampleHash1)),
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(example3, exampleA, exampleHash1))));

	private static final Set<Fact> expectedEscapedCharacterFacts = new HashSet<>(
			Arrays.asList(Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(file1, fileA, Expressions.makeDatatypeConstant("\\t\\u0008\\n\\r\\u000C\\\"'\\\\",
							"http://www.w3.org/2001/XMLSchema#string")))));

	private static final Set<Fact> expectedLanguageTagFacts = new HashSet<>(Arrays.asList(
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(file1, fileA, Expressions.makeConstant("\"This is a test.\"@en"))),
			Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME,
					Arrays.asList(file1, fileA, Expressions.makeConstant("\"Das ist ein Test.\"@de")))));

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

		final Term blank1 = RdfTestUtils.getObjectOfFirstMatchedTriple(file2, fileA, factsFromModel);
		final Term blank2 = RdfTestUtils.getObjectOfFirstMatchedTriple(file3, fileA, factsFromModel);
		final Term blank3 = RdfTestUtils.getObjectOfFirstMatchedTriple(blank2, RDF_REST, factsFromModel);

		final Set<Fact> expectedSetFacts = new HashSet<>(
				Arrays.asList(Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file1, fileA, RDF_NIL)),
						Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file2, fileA, blank1)),
						Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME,
								Arrays.asList(blank1, RDF_FIRST, Expressions.makeConstant(intoLexical("1", "integer")))),
						Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank1, RDF_REST, RDF_NIL)),
						Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(file3, fileA, blank2)),
						Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank2, RDF_FIRST, Expressions.makeConstant("file:/#1"))),
						Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank2, RDF_REST, blank3)),
						Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank3, RDF_FIRST, Expressions.makeConstant("file:/#2"))),
						Expressions.makeFact(RDF_TRIPLE_PREDICATE_NAME, Arrays.asList(blank3, RDF_REST, RDF_NIL))));

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
