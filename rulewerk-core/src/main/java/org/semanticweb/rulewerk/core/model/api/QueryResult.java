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

import java.util.List;

/**
 * A Query Result represents a list of terms that match the terms of the asked
 * query. The terms can be named individuals (constants) and anonymous
 * individuals (blanks).
 * 
 * @author Irina Dragoste
 *
 */
public interface QueryResult {

	/**
	 * Getter for the terms that represent a query answer.
	 * 
	 * @return the terms that represent a query answer. They can be named
	 *         individuals (constants) and anonymous individuals (blanks).
	 */
	List<Term> getTerms();

}
