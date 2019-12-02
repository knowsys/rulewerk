package org.semanticweb.vlog4j.core.reasoner.implementation;

/*-
 * #%L
 * VLog4j Core Components
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
import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNull;

/**
 * An {@code RdfFileDataSource} stores facts in the RDF N-Triples format inside
 * a file of the extension {@code .nt}. These fact triples can be associated
 * with a single predicate of arity 3.
 * <p>
 * The required format is given in the <a
 * href=https://www.w3.org/TR/n-triples/>W3C specification</a>. A simple example
 * file could look like this:
 *
 * <pre>
 * {@code
 * <subject1> <predicate1> <object1> .
 * <subject2> <predicate2> <object2> .
 * }
 * </pre>
 *
 * Gzipped files of the extension {@code .nt.gz} are also supported.
 *
 * @author Christian Lewe
 *
 */
public class RdfFileDataSource extends FileDataSource {

	private final static Iterable<String> possibleExtensions = Arrays.asList(".nt", ".nt.gz");

	/**
	 * Constructor.
	 *
	 * @param rdfFile a file of a {@code .nt} or {@code .nt.gz} extension and a
	 *                valid N-Triples format.
	 * @throws IOException              if the path of the given {@code rdfFile} is
	 *                                  invalid.
	 * @throws IllegalArgumentException if the extension of the given
	 *                                  {@code rdfFile} does not occur in
	 *                                  {@link #possibleExtensions}.
	 */
	public RdfFileDataSource(@NonNull final File rdfFile) throws IOException {
		super(rdfFile, possibleExtensions);
	}

	@Override
	public String toString() {
		return "RdfFileDataSource [rdfFile=" + getFile() + "]";
	}

	@Override
	public String getSyntacticRepresentation() {
		return "load-rdf(\"" + getFile() + "\") .";
	}

}
