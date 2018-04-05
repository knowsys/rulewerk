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

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.reasoner.DataSource;

/**
 * A CsvFileDataSource stores fact terms (tuples) as lines in a ".csv" format
 * file, each column being a predicate argument.
 * 
 * @author Irina Dragoste
 *
 */
public class CsvFileDataSource implements DataSource {
	public static final String CSV_FILE_EXTENSION = ".csv";
	private static final String DATASOURCE_TYPE_CONFIG_VALUE = "INMEMORY";

	private final File csvFile;
	private final String dirCanonicalPath;

	/**
	 * A <i><b>.csv</b></i> format file, where each line corresponds to a fact of
	 * {@link TermType#CONSTANT} {@link Term}s, each column being the fact term
	 * name.
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
	 * @throws IOException
	 *             if the given {@code csvFile} path is and invalid file path.
	 * @throws IllegalArgumentException
	 *             if the given {@code csvFilePath} does not end with
	 *             <i><b>.csv</b></i> extension.
	 */
	public CsvFileDataSource(@NonNull final File csvFile) throws IOException {
		Validate.notNull(csvFile, "Data source file cannot be null!");
		Validate.isTrue(csvFile.getName().endsWith(CSV_FILE_EXTENSION),
				"Expected .csv extension for data source file [%s]!", csvFile);
		this.dirCanonicalPath = csvFile.getAbsoluteFile().getParentFile().getCanonicalPath();
		this.csvFile = csvFile;
	}

	@Override
	public final String toConfigString() {
		final String configStringPattern =

				DataSource.PREDICATE_NAME_CONFIG_LINE +

						DATASOURCE_TYPE_CONFIG_PARAM + "=" + DATASOURCE_TYPE_CONFIG_VALUE + "\n" +

						"EDB%1$d_param0=" + dirCanonicalPath + "\n" +

						"EDB%1$d_param1=" + getFileNameWithoutExtension() + "\n";

		return configStringPattern;
	}

	String getDirCanonicalPath() throws IOException {
		return dirCanonicalPath;
	}

	String getFileNameWithoutExtension() {
		final String fileName = this.csvFile.getName();
		return fileName.substring(0, fileName.lastIndexOf(CSV_FILE_EXTENSION));
	}

	@Override
	public int hashCode() {
		return this.csvFile.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CsvFileDataSource))
			return false;
		final CsvFileDataSource other = (CsvFileDataSource) obj;
		return csvFile.equals(other.getCsvFile());
	}

	@Override
	public String toString() {
		return "CsvFileDataSource [csvFile=" + csvFile + "]";

	}

}
