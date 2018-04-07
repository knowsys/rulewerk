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

/**
 * Interfaces various types of data sources for storing facts.
 * 
 * @author Irina Dragoste
 *
 */
public interface DataSource {

	public static final String PREDICATE_NAME_CONFIG_LINE = "EDB%1$d_predname=%2$s\n";
	public static final String DATASOURCE_TYPE_CONFIG_PARAM = "EDB%1$d_type";

	/**
	 * Constructs a String representation of the data source.
	 * 
	 * @return a String representation of the data source configuration for a
	 *         certain predicate.
	 */
	public String toConfigString();

}
