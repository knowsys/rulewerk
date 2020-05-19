package org.semanticweb.rulewerk.core.reasoner.implementation;

/*-
 * #%L
 * Rulewerk Core Components
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

import java.io.IOException;

import org.semanticweb.rulewerk.core.model.api.DataSource;

/**
 * An interface for DataSources that can be used with a Reasoner.
 */
public interface ReasonerDataSource extends DataSource {
	/**
	 * Accept a {@link DataSourceConfigurationVisitor} to configure a
	 * reasoner to load this data source.
	 *
	 * @param visitor the visitor.
	 */
	public void accept(DataSourceConfigurationVisitor visitor) throws IOException;
}
