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

import java.io.File;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.reasoner.exceptions.FactsSourceConfigException;

public class CSVFileDataSource implements DataSource {
	public static final String CSV_FILE_EXTENSION = ".csv";

	private final String location;

	@Override
	public String getLocation() {
		return this.location;
	}

	public CSVFileDataSource(File csvFile) throws FactsSourceConfigException {
		Validate.notNull(csvFile);
		if (!csvFile.getName().endsWith(CSV_FILE_EXTENSION)) {
			throw new FactsSourceConfigException("Expected .csv extension for data source file [" + csvFile + "]!");
		}
		this.location = csvFile.getAbsolutePath();
	}

}
