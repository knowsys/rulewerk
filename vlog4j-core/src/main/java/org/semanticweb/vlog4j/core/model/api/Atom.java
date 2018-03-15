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

public interface Atom {

	public String getPredicate();

	/**
	 * The predicate arguments as an unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an attempt to modify the list occurs.
	 *
	 * @return an unmodifiableList representing the predicate arguments
	 */
	public List<Term> getArguments();

	/**
	 * The Variables that occur in the predicate arguments as an unmodifiableSet. An {@link UnsupportedOperationException} is thrown, when an attempt to modify
	 * the set occurs.
	 *
	 * @return an unmodifiableSet representing Variables in the the predicate arguments.
	 */
	public Set<Variable> getVariables();

	/**
	 * The Constants that occur in the predicate arguments as an unmodifiableSet. An {@link UnsupportedOperationException} is thrown, when an attempt to modify
	 * the set occurs.
	 *
	 * @return an unmodifiableSet representing Constants in the the predicate arguments.
	 */
	public Set<Constant> getConstants();

	Set<Blank> getBlanks();

}
