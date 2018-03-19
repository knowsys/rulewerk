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
 * predicate, and t1,...,tn some terms.
 *
 * @author david.carral@tu-dresden.de
 */

public interface Atom {

	/**
	 * @return this method may not return a blank String (null, " ", empty
	 *         string...).
	 */
	public String getPredicate();

	/**
	 * The atom arguments as an unmodifiableList. An
	 * {@link UnsupportedOperationException} is thrown, when an attempt to modify
	 * the list occurs.
	 *
	 * @return a non-empty unmodifiableList representing the predicate arguments
	 */
	public List<Term> getArguments();

	/**
	 * Returns the {@link Variable}s that occur among the predicate arguments.
	 *
	 * @return a set of variables
	 */
	public Set<Variable> getVariables();

}