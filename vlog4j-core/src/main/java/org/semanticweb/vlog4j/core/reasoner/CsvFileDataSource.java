package org.semanticweb.vlog4j.core.reasoner;

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

import org.apache.commons.lang3.Validate;

/**
 * A CsvFileDataSource stores fact terms (tuples) as lines in a ".csv" format
 * file, each column being a predicate argument.
 * 
 * @author Irina Dragoste
 *
 */
public class CsvFileDataSource implements DataSource {
	public static final String CSV_FILE_EXTENSION = ".csv";

	private final File csvFile;

	/**
	 * A ".csv" format file, where each line corresponds to a fact, each column
	 * being a predicate argument.
	 * 
	 * @return
	 */
	public File getCsvFile() {
		return this.csvFile;
	}

	/**
	 * Constructor.
	 * 
	 * @param csvFile
	 *            must be a file of ".csv" extension and valid CSV format. The
	 *            content of the file represents fact tuples, where each line
	 *            corresponds to a fact, each column being a predicate argument.
	 */
	public CsvFileDataSource(final File csvFile) {
		Validate.notNull(csvFile, "Data source file cannot be null!");
		Validate.isTrue(csvFile.getName().endsWith(CSV_FILE_EXTENSION),
				"Expected .csv extension for data source file [%s]!", csvFile);
		this.csvFile = csvFile;
	}

}
