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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

final class TestingUtils {

	private TestingUtils() {
	}

	static final String TURTLE_TEST_FILES_PATH = "src/test/data/";

	static Model parseFile(File file, RDFFormat rdfFormat) throws RDFParseException, RDFHandlerException, IOException {

		final URI baseURI = file.toURI();
		final InputStream inputStream = new FileInputStream(file);
		final RDFParser rdfParser = Rio.createParser(rdfFormat);

		final Model model = new LinkedHashModel();
		rdfParser.setRDFHandler(new StatementCollector(model));
		rdfParser.parse(inputStream, baseURI.toString());

		return model;
	}

}
