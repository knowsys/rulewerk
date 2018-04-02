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

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

/**
 * Utility class for collecting the content of a .csv file, or writing fact
 * terms to a csv file.
 * 
 * @author Irina Dragoste
 *
 */
public final class CsvFileUtils {
	public static final String CSV_EXPORT_FOLDER = "src/test/data/output/";
	public static final String CSV_INPORT_FOLDER = "src/test/data/input/";

	private CsvFileUtils() {
	}

	/**
	 * Collects the content of given {@code csvFile} into a List of lines, where
	 * each line is represented as a List of String entries.
	 * 
	 * @param csvFile
	 *            file to be read
	 * @return content of given {@code csvFile} as a List of lines, where each line
	 *         is represented as a List of String entries.
	 * @throws IOException
	 *             if an I/O error occurs regarding given {@code csvFile}
	 */
	public static List<List<String>> getCSVContent(final String csvFile) throws IOException {
		final List<List<String>> content = new ArrayList<>();

		final Reader in = new FileReader(csvFile);
		final CSVParser parse = CSVFormat.DEFAULT.parse(in);
		parse.forEach(csvRecord -> {
			final List<String> line = new ArrayList<>();
			csvRecord.forEach(line::add);
			content.add(line);
		});
		return content;
	}

}
