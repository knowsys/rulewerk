package org.semanticweb.vlog4j.rdf;

import static org.junit.Assert.assertTrue;

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

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

public class TestLoadFactsFromRDFToVLogReasoner {

	// TODO add rules, reason
	// TODO add data of each type

	@Test
	public void testLoadFactsFromRDF() throws RDFParseException, RDFHandlerException, IOException,
			ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final Model model = TestingUtils.parseFile(
				new File(TestingUtils.TURTLE_TEST_FILES_PATH + "exampleFactsNoBlanks.ttl"), RDFFormat.TURTLE);
		final Set<Atom> facts = RDFModelToAtomsConverter.rdfModelToAtoms(model);
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFacts(facts);
			reasoner.load();
			final QueryResultIterator answerQuery = reasoner.answerQuery(
					Expressions.makeAtom(RDFModelToAtomsConverter.RDF_TRIPLE_PREDICATE, Expressions.makeVariable("x"),
							Expressions.makeVariable("y"), Expressions.makeVariable("z")),
					true);
			assertTrue(answerQuery.hasNext());
			answerQuery.close();
		}

	}
}
