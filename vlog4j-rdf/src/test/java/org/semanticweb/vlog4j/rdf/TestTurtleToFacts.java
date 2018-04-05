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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Blank;

public class TestTurtleToFacts {

	@Test
	public void testRDFFileToAtomsConverter() throws RDFParseException, RDFHandlerException, IOException {

		final Model model = TestingUtils.parseFile(new File(TestingUtils.TURTLE_TEST_FILES_PATH + "exampleFacts.ttl"),
				RDFFormat.TURTLE);
		final Set<Atom> facts = RDFModelToAtomsConverter.rdfModelToAtoms(model);

		System.out.println(facts);
		// TODO asserts: url long name for constants and literal datatypes
		// TODO asserts: normalized literal label
		// TODO asserts: escaped " characters in literal
		// FIXME test builtin datatypes?
		// TODO test literal of all literal datatypes
		// TODO test with/without language
		// TODO test if reasoning is possible with this predicate / fact names
	}

	@Test
	public void testBlanksWithSameRDFNameAreDifferentInDifferentModelContexts()
			throws RDFParseException, RDFHandlerException, IOException {
		final String blanksTurtleFile1 = TestingUtils.TURTLE_TEST_FILES_PATH + "blanks_context1.ttl";

		final Model model1File1 = TestingUtils.parseFile(new File(blanksTurtleFile1), RDFFormat.TURTLE);
		final Set<Atom> atomsFromModel1 = RDFModelToAtomsConverter.rdfModelToAtoms(model1File1);
		final Set<Blank> blanksFromModel1 = extractBlanks(atomsFromModel1);
		assertEquals(2, blanksFromModel1.size());

		final Model model2File1 = TestingUtils.parseFile(new File(blanksTurtleFile1), RDFFormat.TURTLE);
		final Set<Atom> atomsFromModel2 = RDFModelToAtomsConverter.rdfModelToAtoms(model2File1);
		final Set<Blank> blanksFromModel2 = extractBlanks(atomsFromModel2);
		assertEquals(2, blanksFromModel2.size());

		// assert that there is no common Blank in two different models (even if they
		// have been
		// loaded from the same file)
		final Set<Blank> intersection = new HashSet<>(blanksFromModel1);
		intersection.retainAll(blanksFromModel2);
		assertTrue(intersection.isEmpty());

	}

	private Set<Blank> extractBlanks(Collection<Atom> atoms) {
		final Set<Blank> blanks = new HashSet<>();
		atoms.forEach(atom -> blanks.addAll(atom.getBlanks()));
		return blanks;

	}

}
