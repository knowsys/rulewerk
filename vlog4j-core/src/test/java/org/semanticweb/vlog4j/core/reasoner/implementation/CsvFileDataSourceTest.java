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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.vlog4j.core.reasoner.FileDataSourceUtils;

public class CsvFileDataSourceTest {

	@Test(expected = NullPointerException.class)
	public void testConstructorNullFile() throws IOException {
		new CsvFileDataSource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorFalseExtension() throws IOException {
		new CsvFileDataSource(new File(FileDataSourceUtils.INPUT_FOLDER + "file.nt"));
	}

	@Test
	public void testConstructor() throws IOException {
		final File unzippedCsvFile = new File(FileDataSourceUtils.INPUT_FOLDER + "file.csv");
		final String expectedDirCanonicalPath = unzippedCsvFile.getParentFile().getCanonicalPath();
		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(unzippedCsvFile);
		assertEquals(unzippedCsvFile, unzippedCsvFileDataSource.getFile());
		assertEquals(expectedDirCanonicalPath, unzippedCsvFileDataSource.getDirCanonicalPath());
		assertEquals("file", unzippedCsvFileDataSource.getFileNameWithoutExtension());

		final File zippedCsvFile = new File(FileDataSourceUtils.INPUT_FOLDER + "file.csv.gz");
		final CsvFileDataSource zippedCsvFileDataSource = new CsvFileDataSource(zippedCsvFile);
		assertEquals(zippedCsvFile, zippedCsvFileDataSource.getFile());
		assertEquals(expectedDirCanonicalPath, zippedCsvFileDataSource.getDirCanonicalPath());
		assertEquals("file", zippedCsvFileDataSource.getFileNameWithoutExtension());
	}

	@Test
	public void testToConfigString() throws IOException {
		final File unzippedCsvFile = new File(FileDataSourceUtils.INPUT_FOLDER + "file.csv");
		final File zippedCsvFile = new File(FileDataSourceUtils.INPUT_FOLDER + "file.csv.gz");
		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(unzippedCsvFile);
		final CsvFileDataSource zippedCsvFileDataSource = new CsvFileDataSource(zippedCsvFile);

		final String expectedDirCanonicalPath = unzippedCsvFile.getParentFile().getCanonicalPath();
		final String expectedConfigString = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=INMEMORY\n" + "EDB%1$d_param0="
				+ expectedDirCanonicalPath + "\n" + "EDB%1$d_param1=file\n";

		assertEquals(expectedConfigString, zippedCsvFileDataSource.toConfigString());
		assertEquals(expectedConfigString, unzippedCsvFileDataSource.toConfigString());
	}

}
