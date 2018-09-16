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

public class RdfFileDataSourceTest {

	@Test
	public void testToConfigString() throws IOException {
		File rdfFile = new File(FileDataSourceUtils.INPUT_FOLDER + "file.nt");
		RdfFileDataSource rdfFileDataSource = new RdfFileDataSource(rdfFile);

		final String expectedConfigString = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=INMEMORY\n" + "EDB%1$d_param0="
				+ new File(rdfFile.getParent()).getCanonicalPath() + "\n" + "EDB%1$d_param1=file\n";
		assertEquals(expectedConfigString, rdfFileDataSource.toConfigString());

		rdfFile = new File(FileDataSourceUtils.INPUT_FOLDER + "file.nt.gz");
		rdfFileDataSource = new RdfFileDataSource(rdfFile);
		assertEquals(expectedConfigString, rdfFileDataSource.toConfigString());
	}

	@Test
	public void getFileNameWithoutExtension() throws IOException {
		RdfFileDataSource rdfFileDataSource = new RdfFileDataSource(
				new File(FileDataSourceUtils.INPUT_FOLDER + "file.nt"));
		assertEquals("file", rdfFileDataSource.getFileNameWithoutExtension());

		rdfFileDataSource = new RdfFileDataSource(new File(FileDataSourceUtils.INPUT_FOLDER + "file.nt.gz"));
		assertEquals("file", rdfFileDataSource.getFileNameWithoutExtension());
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
