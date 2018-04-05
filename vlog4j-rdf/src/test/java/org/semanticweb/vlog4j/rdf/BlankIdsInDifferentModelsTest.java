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
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

public class BlankIdsInDifferentModelsTest {

	@Test
	public void testBlanksHaveDifferentIdsInDifferentModelContexts()
			throws RDFParseException, RDFHandlerException, IOException {

		final String blanksTurtleFile1 = TestingUtils.TURTLE_TEST_FILES_PATH + "blanks_context1.ttl";
		final Model model1 = TestingUtils.parseFile(new File(blanksTurtleFile1), RDFFormat.TURTLE);
		final Set<String> blankNodeIdsForModel1File1 = collectBlankNodeIds(model1);
		assertEquals(2, blankNodeIdsForModel1File1.size());

		final Model model2 = TestingUtils.parseFile(new File(blanksTurtleFile1), RDFFormat.TURTLE);
		final Set<String> blankNodeIdsForModel2File1 = collectBlankNodeIds(model2);
		assertEquals(2, blankNodeIdsForModel2File1.size());

		// assert that there is no common Blank in two different models (even if they
		// have been
		// loaded from the same file)
		final Set<String> intersectionModel1Model2 = new HashSet<>(blankNodeIdsForModel1File1);
		intersectionModel1Model2.retainAll(blankNodeIdsForModel2File1);
		assertTrue(intersectionModel1Model2.isEmpty());

		final String blanksTurtleFile2SameContentAsFile1 = TestingUtils.TURTLE_TEST_FILES_PATH + "blanks_context2.ttl";
		final Model model3 = TestingUtils.parseFile(new File(blanksTurtleFile2SameContentAsFile1), RDFFormat.TURTLE);
		final Set<String> blankNodeIdsForModel3File2 = collectBlankNodeIds(model3);
		assertEquals(2, blankNodeIdsForModel3File2.size());

		// assert that there is no common Blank in two different models, even if the
		// files contain the same blank names
		final Set<String> intersectionModel1Model3 = new HashSet<>(blankNodeIdsForModel1File1);
		intersectionModel1Model3.retainAll(blankNodeIdsForModel3File2);
		assertTrue(intersectionModel1Model3.isEmpty());

		// assert that there is no common Blank in two different models, even if the
		// files contain the same blank names
		final Set<String> intersectionModel2Model3 = new HashSet<>(blankNodeIdsForModel2File1);
		intersectionModel2Model3.retainAll(blankNodeIdsForModel3File2);
		assertTrue(intersectionModel2Model3.isEmpty());
	}

	private Set<String> collectBlankNodeIds(Model model) {
		final HashSet<String> blankNodeIds = new HashSet<>();
		model.forEach(statement -> {
			final Resource subject = statement.getSubject();
			if (subject instanceof BNode) {
				blankNodeIds.add(((BNode) subject).getID());
			}
			final Value object = statement.getObject();
			if (object instanceof BNode) {
				blankNodeIds.add(((BNode) object).getID());
			}
		});
		return blankNodeIds;
	}

}
