package org.semanticweb.vlog4j.core.reasoner;

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

public class EDBPredConfig {
	private final int id;
	private final String predName;
	private final EDBSourceType sourceType;
	private final String sourceDirName;
	private final String sourceFileName;

	public EDBPredConfig(final int id, final String predName, final EDBSourceType sourceType, final String sourceDirName, final String sourceFileName) {
		this.id = id;
		this.predName = predName;
		this.sourceType = sourceType;
		this.sourceDirName = sourceDirName;
		this.sourceFileName = sourceFileName;
	}

	public String toVLogEDBConfigFileFormat() {
		return "EDB" + this.id + "_predname = " + this.predName + "\n" + "EDB" + this.id + "_type = " + this.sourceType + "\n" + "EDB" + this.id + "_param0 = "
				+ this.sourceDirName + "\n" + "EDB" + this.id + "_param1 = " + this.sourceFileName + "\n\n";
	}
}
