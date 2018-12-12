package org.semanticweb.vlog4j.core.model.api;

import java.util.List;
import java.util.Set;

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

/**
 * Interface for atoms. An atom is predicate applied to a tuple of terms; that
 * is, an atomic formula is a formula of the form P(t1,...,tn) for P a
 * {@link Predicate} name, and t1,...,tn some {@link Term}s. The number of terms
 * in the tuple corresponds to the {@link Predicate} arity.
 *
 * @author david.carral@tu-dresden.de
 */

public interface Atom {

	/**
	 * The atom predicate.
	 * 
	 * @return the atom predicate.
	 */
	public Predicate getPredicate();

	/**
	 * The list of terms representing the tuple arguments.
	 *
	 * @return an unmodifiable list of terms with the same size as the
	 *         {@link Predicate} arity.
	 */
	public List<Term> getTerms();

	/**
	 * Returns the {@link Variable}s that occur among the atom terms.
	 *
	 * @return the set of atom variables
	 */
	public Set<Variable> getVariables();

	/**
	 * Returns the {@link Constant}s that occur among the atom terms.
	 * 
	 * @return the set of atom constants
	 */
	public Set<Constant> getConstants();

	/**
	 * Returns the {@link Blank}s that occur among the atom terms.
	 * 
	 * @return the set of atom blanks
	 */
	public Set<Blank> getBlanks();
}
