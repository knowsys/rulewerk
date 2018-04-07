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

/**
 * A visitor for the various types of {@link Term}s in the data model. Should be
 * used to avoid any type casting or {@code instanceof} checks when processing terms.
 * 
 * @author Markus Krötzsch
 */
public interface TermVisitor<T> {

	/**
	 * Visits a {@link Constant} and returns a result
	 * 
	 * @param term
	 *            the term to visit
	 * @return some result
	 */
	T visit(Constant term);

	/**
	 * Visits a {@link Variable} and returns a result
	 * 
	 * @param term
	 *            the term to visit
	 * @return some result
	 */
	T visit(Variable term);

	/**
	 * Visits a {@link Blank} and returns a result
	 * 
	 * @param term
	 *            the term to visit
	 * @return some result
	 */
	T visit(Blank term);
}
