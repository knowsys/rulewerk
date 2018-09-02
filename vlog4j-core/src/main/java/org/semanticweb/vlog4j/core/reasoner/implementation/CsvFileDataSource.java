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
 * A CsvFileDataSource stores fact terms (tuples) as lines in a ".csv" format
 * file, each column being a predicate argument.
 *
 * @author Irina Dragoste
 *
 */
public class CsvFileDataSource extends FileDataSource {

	final static Iterable<String> possibleExtensions = Arrays.asList(".csv", ".csv.gz");

	/**
	 * Constructor.
	 *
	 * @param csvFile a file of ".csv" or ".csv.gz" extension and valid CSV format.
	 *                The content of the file represents fact tuples, where each
	 *                line corresponds to a fact, each column being a predicate
	 *                argument.
	 * @throws IOException              if the path of the given {@code file} is
	 *                                  invalid.
	 * @throws IllegalArgumentException if the extension of the given {@code file}
	 *                                  does not occur in
	 *                                  {@code possibleExtensions}.
	 */
	public CsvFileDataSource(@NonNull final File csvFile) throws IOException {
		super(csvFile, possibleExtensions);
	}

	@Override
	public String toString() {
		return "CsvFileDataSource [csvFile=" + getFile() + "]";
	}

}
