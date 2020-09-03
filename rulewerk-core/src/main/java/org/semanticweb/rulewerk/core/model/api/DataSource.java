package org.semanticweb.rulewerk.core.model.api;

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

import java.util.Optional;

/**
 * Interfaces various types of data sources for storing facts.
 *
 * @author Irina Dragoste
 *
 */
public interface DataSource extends Entity {

	/**
	 * Retrieve the required arity of target predicates for the data source.
	 *
	 * @return the required arity for the data source, or Optional.empty() if there
	 *         is none.
	 */
	public default Optional<Integer> getRequiredArity() {
		return Optional.empty();
	}

	/**
	 * Returns a fact that represents the declaration of this {@link DataSource}.
	 * Rulewerk syntax uses facts to specify the relevant parameters for data source
	 * declarations.
	 * 
	 * @return {@link Fact} that contains the parameters of this data source
	 */
	public Fact getDeclarationFact();

}
