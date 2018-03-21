package org.semanticweb.vlog4j.core.reasoner;

import java.io.File;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.reasoner.exceptions.FactsSourceConfigException;

/*
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

public class FactsSourceConfigCSVImpl implements FactsSourceConfig {
	private static final String CSV_FILE_EXTENSION = ".csv";
	private final String predicate;
	private final File sourceFile;

	public FactsSourceConfigCSVImpl(final String predName, final File sourceFile) throws FactsSourceConfigException {
		Validate.notNull(sourceFile);
		if (!predName.endsWith(CSV_FILE_EXTENSION)) {
			throw new FactsSourceConfigException("Expected .csv extension for source file [" + sourceFile + "]!");
		}
		Validate.notBlank(predName, "Predicate cannot be blank");

		this.predicate = predName;
		this.sourceFile = sourceFile;
	}

	@Override
	public String getPredicate() {
		return this.predicate;
	}

	@Override
	public File getSourceFile() {
		return this.sourceFile;
	}

	// String example = "EDB0_predname=hospital\r\n" +
	// "EDB0_type=INMEMORY\r\n" +
	// "EDB0_param0=/Users/dragoste/Documents/vlog/VLOG_chase_test/db-1m\r\n" +
	// "EDB0_param1=hospital\r\n";
	//
	// String format = "EDBB%1$_predname=hospital\r\n" +
	// "EDB%1$_type=INMEMORY\r\n" +
	// "EDB%1$_param0=/Users/dragoste/Documents/vlog/VLOG_chase_test/db-1m\r\n" + "EDB%1$_param1=hospital\r\n";


}
