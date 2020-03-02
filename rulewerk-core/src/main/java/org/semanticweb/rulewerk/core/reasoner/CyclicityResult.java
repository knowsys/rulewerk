package org.semanticweb.rulewerk.core.reasoner;

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

/**
 * Enumeration for the cyclicity property of a set of rules and predicates. The
 * cyclicity property determines whether the Restricted Chase
 * ({@link Algorithm#RESTRICTED_CHASE}) is guaranteed to terminate for given
 * rules and any set of facts over given EDB predicates.
 *
 * @author Irina Dragoste
 *
 */
public enum CyclicityResult {
	/**
	 * Reasoning with restricted chase algorithm
	 * ({@link Algorithm#RESTRICTED_CHASE}) is guaranteed to terminate for given set
	 * of rules and any facts over given EDB predicates.
	 */
	CYCLIC,
	/**
	 * There exists a set of facts over given EDB predicates for which reasoning
	 * with restricted chase algorithm ({@link Algorithm#RESTRICTED_CHASE})
	 * guaranteed to not terminate for given set of rules.
	 */
	ACYCLIC,
	/**
	 * (A)cyclicity cannot be determined.
	 */
	UNDETERMINED
}
