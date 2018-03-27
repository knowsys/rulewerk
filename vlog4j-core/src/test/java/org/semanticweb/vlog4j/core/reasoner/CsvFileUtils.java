package org.semanticweb.vlog4j.core.reasoner;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

/**
 * Utility class for collecting the content of a .csv file.
 * 
 * @author Irina Dragoste
 *
 */
public final class CsvFileUtils {

	private CsvFileUtils() {
	}

	/**
	 * 
	 * @param csvFile
	 *            file to be read
	 * @return
	 * @throws IOException
	 *             if an I/O error occurs regarding given {@code csvFile}
	 */
	public static List<List<String>> getCSVContent(final String csvFile) throws IOException {
		final List<List<String>> content = new ArrayList<>();
		// FIXME which format does vLog expect and generate?
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
