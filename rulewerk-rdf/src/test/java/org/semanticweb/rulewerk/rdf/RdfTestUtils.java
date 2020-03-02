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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public final class RdfTestUtils {

	static final String INPUT_FOLDER = "src/test/data/input/";
	static final String OUTPUT_FOLDER = "src/test/data/output/";

	static final Constant RDF_FIRST = Expressions
			.makeAbstractConstant("http://www.w3.org/1999/02/22-rdf-syntax-ns#first");
	static final Constant RDF_REST = Expressions
			.makeAbstractConstant("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest");
	static final Constant RDF_NIL = Expressions.makeAbstractConstant("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil");

	/*
	 * This is a utility class. Therefore, it is best practice to do the following:
	 * (1) Make the class final, (2) make its constructor private, (3) make all its
	 * fields and methods static. This prevents the classes instantiation and
	 * inheritance.
	 */
	private RdfTestUtils() {

	}

	static Model parseFile(final File file, final RDFFormat rdfFormat)
			throws RDFParseException, RDFHandlerException, IOException {
		final URI baseURI = file.toURI();
		final InputStream inputStream = new FileInputStream(file);
		final RDFParser rdfParser = Rio.createParser(rdfFormat);

		final Model model = new LinkedHashModel();
		rdfParser.setRDFHandler(new StatementCollector(model));
		rdfParser.parse(inputStream, baseURI.toString());

		return model;
	}

	static Term getSubjectFromTriple(final PositiveLiteral triple) {
		return triple.getArguments().get(0);
	}

	static Term getPredicateFromTriple(final PositiveLiteral triple) {
		return triple.getArguments().get(1);
	}

	static Term getObjectFromTriple(final PositiveLiteral triple) {
		return triple.getArguments().get(2);
	}

	static Term getObjectOfFirstMatchedTriple(final Term subject, final Term predicate, final Set<Fact> facts) {
		return facts.stream()
				.filter(triple -> getSubjectFromTriple(triple).equals(subject)
						&& getPredicateFromTriple(triple).equals(predicate))
				.findFirst().map(triple -> getObjectFromTriple(triple)).get();
	}

}
