package org.semanticweb.rulewerk.core.reasoner.implementation;

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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.implementation.FileDataSourceTestUtils;

public class CsvFileDataSourceTest {

	private final String ntFile = FileDataSourceTestUtils.INPUT_FOLDER + "file.nt";
	private final String csvFile = FileDataSourceTestUtils.INPUT_FOLDER + "file.csv";
	private final String gzFile = csvFile + ".gz";

	@Test(expected = NullPointerException.class)
	public void testConstructorNullFile() throws IOException {
		new CsvFileDataSource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorFalseExtension() throws IOException {
		new CsvFileDataSource(new File(ntFile));
	}

	@Test
	public void testConstructor() throws IOException {
		final File unzippedCsvFile = new File(csvFile);
		final File zippedCsvFile = new File(gzFile);
		final String dirCanonicalPath = new File(FileDataSourceTestUtils.INPUT_FOLDER).getCanonicalPath();
		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(unzippedCsvFile);
		final CsvFileDataSource zippedCsvFileDataSource = new CsvFileDataSource(zippedCsvFile);

		FileDataSourceTestUtils.testConstructor(unzippedCsvFileDataSource, unzippedCsvFile, dirCanonicalPath, "file");
		FileDataSourceTestUtils.testConstructor(zippedCsvFileDataSource, zippedCsvFile, dirCanonicalPath, "file");
	}

	@Test
	public void testToConfigString() throws IOException {
		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(new File(csvFile));
		final CsvFileDataSource zippedCsvFileDataSource = new CsvFileDataSource(new File(gzFile));

		final String expectedDirCanonicalPath = new File(FileDataSourceTestUtils.INPUT_FOLDER).getCanonicalPath();
		final String expectedConfigString = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=INMEMORY\n" + "EDB%1$d_param0="
				+ expectedDirCanonicalPath + "\n" + "EDB%1$d_param1=file\n";

		assertEquals(expectedConfigString, unzippedCsvFileDataSource.toConfigString());
		assertEquals(expectedConfigString, zippedCsvFileDataSource.toConfigString());
	}

	@Test
	public void testNoParentDir() throws IOException {
		final File file = new File("file.csv");
		final FileDataSource fileDataSource = new CsvFileDataSource(file);
		final String dirCanonicalPath = fileDataSource.getDirCanonicalPath();
		final String currentFolder = new File(".").getCanonicalPath();
		assertEquals(currentFolder, dirCanonicalPath);
	}

	@Test
	public void testNotNormalisedParentDir() throws IOException {
		final File file = new File("./././file.csv");
		final FileDataSource fileDataSource = new CsvFileDataSource(file);
		final String dirCanonicalPath = fileDataSource.getDirCanonicalPath();
		final String currentFolder = new File(".").getCanonicalPath();
		assertEquals(currentFolder, dirCanonicalPath);
	}

}
