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

import java.util.List;
import java.util.Set;

/**
 * Interface for representing conjunctions of {@link Atom}s, i.e., lists of
 * atomic formulas that are connected with logical AND. Conjunctions may have
 * free variables, since they contain no quantifiers.
 * 
 * @author Markus Krötzsch
 *
 */
public interface Conjunction extends Iterable<Atom> {

	/**
	 * Returns the list of atoms that are part of this conjunction.
	 * 
	 * @return list of atoms
	 */
	public List<Atom> getAtoms();

	/**
	 * Returns the set of terms of a certain type that occur in this conjunction.
	 * 
	 * @param termType
	 *            the type of the term requested
	 * @return set of matching terms used in this conjunction
	 */
	public Set<? extends Term> getTerms(TermType termType);
	
	/**
	 * Returns the set of all terms  that occur in this conjunction.
	 * 
	 * @return set of terms used in this conjunction
	 */
	public Set<Term> getTerms();

	/**
	 * Returns the {@link Variable}s that occur in this conjunction.
	 *
	 * @return a set of variables
	 */
	public Set<Variable> getVariables();
}
