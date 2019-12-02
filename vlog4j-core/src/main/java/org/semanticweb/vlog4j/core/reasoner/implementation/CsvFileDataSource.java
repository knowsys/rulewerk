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
 * An {@code CsvFileDataSource} stores facts in the CSV format inside a file of
 * the extension {@code .csv}. These fact tuples can be associated with a single
 * predicate of the same arity as the length of these tuples.
 * <p>
 * The required format looks like this:
 *
 * <pre>
 * {@code
 * term11, term12, term13, ... term1n
 * term21, term22, term23, ... term2n
 * ...
 * termM1, termM2, termM3, ... termMn
 * }
 * </pre>
 *
 * where {@code n} is the arity of the predicate and {@code M} is the number of
 * facts. Gzipped files of the extension {@code .csv.gz} are also supported.
 *
 * @author Christian Lewe
 * @author Irina Dragoste
 *
 */
public class CsvFileDataSource extends FileDataSource {

	private static final Iterable<String> possibleExtensions = Arrays.asList(".csv", ".csv.gz");

	/**
	 * Constructor.
	 *
	 * @param csvFile a file of a {@code .csv} or {@code .csv.gz} extension and a
	 *                valid CSV format.
	 * @throws IOException              if the path of the given {@code csvFile} is
	 *                                  invalid.
	 * @throws IllegalArgumentException if the extension of the given
	 *                                  {@code csvFile} does not occur in
	 *                                  {@link #possibleExtensions}.
	 */
	public CsvFileDataSource(@NonNull final File csvFile) throws IOException {
		super(csvFile, possibleExtensions);
	}

	@Override
	public String toString() {
		return "CsvFileDataSource [csvFile=" + getFile() + "]";
	}

	@Override
	public String getSyntacticRepresentation() {
		return "load-csv(\"" + getFile() + "\") .";
	}

}
