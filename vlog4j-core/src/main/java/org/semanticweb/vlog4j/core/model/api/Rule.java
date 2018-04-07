package org.semanticweb.vlog4j.core.model.api;

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

import java.util.Set;

/**
 * Interface for classes representing a rule. This implementation assumes that
 * rules are defined by their head and body atoms, without explicitly specifying
 * quantifiers. All variables in the body are considered universally quantified;
 * all variables in the head that do not occur in the body are considered
 * existentially quantified.
 * 
 * @author Markus Krötzsch
 *
 */
public interface Rule {

	/**
	 * Returns the conjunction of head atoms (the consequence of the rule).
	 *
	 * @return conjunction of atoms
	 */
	public Conjunction getHead();

	/**
	 * Returns the conjunction of body atoms (the premise of the rule).
	 *
	 * @return conjunction of atoms
	 */
	public Conjunction getBody();

	/**
	 * Returns the existentially quantified head variables of this rule.
	 *
	 * @return a set of variables
	 */
	public Set<Variable> getExistentiallyQuantifiedVariables();

	/**
	 * Returns the universally quantified variables of this rule.
	 *
	 * @return a set of variables
	 */
	public Set<Variable> getUniversallyQuantifiedVariables();

}
