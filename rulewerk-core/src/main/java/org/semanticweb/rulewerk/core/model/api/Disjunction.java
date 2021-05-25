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
 * Interface for representing disjunctions of {@link Conjunction}s.
 *
 * @author Lukas Gerlach
 *
 */
public interface Disjunction<T extends Conjunction<?>> extends SyntaxObject {

	/**
	 * Returns the list of conjunctions that are part of this disjunction.
	 *
	 * @return list of conjunctions
	 */
	List<T> getConjunctions();

}
