package org.semanticweb.vlog4j.core.reasoner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

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
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.FileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

/**
 * Utility class for reading from and writing to data source files.
 *
 * @author Christian Lewe
 * @author Irina Dragoste
 *
 */
public final class FileDataSourceUtils {
	public static final String OUTPUT_FOLDER = "src/test/data/output/";
	public static final String INPUT_FOLDER = "src/test/data/input/";

	public static final String unzippedUnaryCsvFileRoot = "unaryFacts";
	public static final String zippedUnaryCsvFileRoot = "unaryFactsZipped";
	public static final String unzippedNtFileRoot = "ternaryFacts";
	public static final String zippedNtFileRoot = "ternaryFactsZipped";
	public static final String binaryCsvFileNameRoot = "binaryFacts";
	public static final String wrongArityNtFileNameRoot = "illegalArityNtFacts";

	private FileDataSourceUtils() {
	}

	/**
	 * Collects the content of given {@code csvFile} into a List of lines, where
	 * each line is represented as a List of String entries.
	 *
	 * @param csvFile file to be read
	 * @return content of given {@code csvFile} as a List of lines, where each line
	 *         is represented as a List of String entries.
	 * @throws IOException if an I/O error occurs regarding given {@code csvFile}
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

	public static void testConstructor(final FileDataSource unzippedFileDataSource,
			final FileDataSource zippedFileDataSource)
					throws IOException {
		final String expectedDirCanonicalPath = new File(INPUT_FOLDER).getCanonicalPath();
		final File unzippedFile = unzippedFileDataSource.getFile();
		final File zippedFile = zippedFileDataSource.getFile();

		assertEquals(unzippedFile, unzippedFileDataSource.getFile());
		assertEquals(expectedDirCanonicalPath, unzippedFileDataSource.getDirCanonicalPath());
		assertEquals("file", unzippedFileDataSource.getFileNameWithoutExtension());

		assertEquals(zippedFile, zippedFileDataSource.getFile());
		assertEquals(expectedDirCanonicalPath, zippedFileDataSource.getDirCanonicalPath());
		assertEquals("file", zippedFileDataSource.getFileNameWithoutExtension());
	}

	public static void testToConfigString(final FileDataSource unzippedFileDataSource,
			final FileDataSource zippedFileDataSource) throws IOException {
		final String expectedDirCanonicalPath = new File(INPUT_FOLDER).getCanonicalPath();
		final String expectedConfigString = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=INMEMORY\n" + "EDB%1$d_param0="
				+ expectedDirCanonicalPath + "\n" + "EDB%1$d_param1=file\n";

		assertEquals(expectedConfigString, zippedFileDataSource.toConfigString());
		assertEquals(expectedConfigString, unzippedFileDataSource.toConfigString());
	}

	public static void testLoadEmptyFile(final Predicate predicate, final Atom queryAtom,
			final FileDataSource emptyFileDataSource)
					throws IOException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFactsFromDataSource(predicate, emptyFileDataSource);
			reasoner.load();
			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			reasoner.reason();

			try (final QueryResultIterator answerQuery = reasoner.answerQuery(queryAtom, true)) {
				assertFalse(answerQuery.hasNext());
			}
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(queryAtom, false)) {
				assertFalse(answerQuery.hasNext());
			}

			reasoner.resetReasoner();
			reasoner.load();
			reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
			reasoner.reason();

			try (final QueryResultIterator answerQuery = reasoner.answerQuery(queryAtom, true)) {
				assertFalse(answerQuery.hasNext());
			}
			try (final QueryResultIterator answerQuery = reasoner.answerQuery(queryAtom, false)) {
				assertFalse(answerQuery.hasNext());
			}
		}
	}

}
