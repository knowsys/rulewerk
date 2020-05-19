package org.semanticweb.rulewerk.core.reasoner.implementation;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
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

import org.junit.Test;

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
		new CsvFileDataSource(ntFile);
	}

	@Test
	public void testConstructor() throws IOException {
		final CsvFileDataSource unzippedCsvFileDataSource = new CsvFileDataSource(csvFile);
		final CsvFileDataSource zippedCsvFileDataSource = new CsvFileDataSource(gzFile);

		FileDataSourceTestUtils.testConstructor(unzippedCsvFileDataSource, new File(csvFile).getName());
		FileDataSourceTestUtils.testConstructor(zippedCsvFileDataSource, new File(gzFile).getName());
	}
}
