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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

/**
 * Utility class for reading from and writing to data source files.
 *
 * @author Christian Lewe
 * @author Irina Dragoste
 *
 */
public final class FileDataSourceTestUtils {

	public static final String INPUT_FOLDER = "src/test/data/input/";

	/*
	 * This is a utility class. Therefore, it is best practice to do the following:
	 * (1) Make the class final, (2) make its constructor private, (3) make all its
	 * fields and methods static. This prevents the classes instantiation and
	 * inheritance.
	 */
	private FileDataSourceTestUtils() {

	}

	public static void testConstructor(final FileDataSource fileDataSource, final String expectedFileName)
			throws IOException {
		assertEquals(expectedFileName, fileDataSource.getName());
	}
}
