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
import org.semanticweb.vlog4j.core.reasoner.CsvFileUtils;

public class CsvFileDataSourceTest {

	@Test
	public void testToConfigString() throws IOException {
		final File csvFile = new File(CsvFileUtils.CSV_INPORT_FOLDER + "file.csv");
		final CsvFileDataSource csvFileDataSource = new CsvFileDataSource(csvFile);

		final String expectedConfigString = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=INMEMORY\n"
				+ "EDB%1$d_param0=" + new File(csvFile.getParent()).getCanonicalPath() + "\n" + "EDB%1$d_param1=file\n";

		final String actualConfigString = csvFileDataSource.toConfigString();
		assertEquals(expectedConfigString, actualConfigString);
	}

	@Test
	public void getFileNameWithoutExtension() throws IOException {
		final CsvFileDataSource csvFileDataSource = new CsvFileDataSource(
				new File(CsvFileUtils.CSV_INPORT_FOLDER + "file.csv"));
		assertEquals("file", csvFileDataSource.getFileNameWithoutExtension());
	}

	@Test(expected = NullPointerException.class)
	public void fileNameNotNull() throws IOException {
		new CsvFileDataSource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fileNameEndsWithCsv() throws IOException {
		new CsvFileDataSource(new File("invalid/file/name"));
	}

}
