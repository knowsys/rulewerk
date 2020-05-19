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

public class RdfFileDataSourceTest {

	private final String unzippedRdfFile = FileDataSourceTestUtils.INPUT_FOLDER + "file.nt";
	private final String zippedRdfFile = FileDataSourceTestUtils.INPUT_FOLDER + "file.nt.gz";

	@Test(expected = NullPointerException.class)
	public void testConstructorNullFile() throws IOException {
		new RdfFileDataSource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorFalseExtension() throws IOException {
		new RdfFileDataSource(FileDataSourceTestUtils.INPUT_FOLDER + "file.csv");
	}

	@Test
	public void testConstructor() throws IOException {
		final RdfFileDataSource unzippedRdfFileDataSource = new RdfFileDataSource(unzippedRdfFile);
		final RdfFileDataSource zippedRdfFileDataSource = new RdfFileDataSource(zippedRdfFile);

		FileDataSourceTestUtils.testConstructor(unzippedRdfFileDataSource, new File(unzippedRdfFile).getName());
		FileDataSourceTestUtils.testConstructor(zippedRdfFileDataSource, new File(zippedRdfFile).getName());
	}
}
