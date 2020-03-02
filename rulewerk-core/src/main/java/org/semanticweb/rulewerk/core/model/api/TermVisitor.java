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

/**
 * A visitor for the various types of {@link Term}s in the data model. Should be
 * used to avoid any type casting or {@code instanceof} checks when processing
 * terms.
 * 
 * @author Markus Krötzsch
 */
public interface TermVisitor<T> {

	/**
	 * Visits an {@link AbstractConstant} and returns a result.
	 * 
	 * @param term the term to visit
	 * @return some result
	 */
	T visit(AbstractConstant term);

	/**
	 * Visits a {@link DatatypeConstant} and returns a result.
	 * 
	 * @param term the term to visit
	 * @return some result
	 */
	T visit(DatatypeConstant term);

	/**
	 * Visits a {@link LanguageStringConstant} and returns a result.
	 * 
	 * @param term the term to visit
	 * @return some result
	 */
	T visit(LanguageStringConstant term);

	/**
	 * Visits a {@link UniversalVariable} and returns a result
	 * 
	 * @param term the term to visit
	 * @return some result
	 */
	T visit(UniversalVariable term);

	/**
	 * Visits a {@link ExistentialVariable} and returns a result
	 * 
	 * @param term the term to visit
	 * @return some result
	 */
	T visit(ExistentialVariable term);

	/**
	 * Visits a {@link NamedNull} and returns a result
	 * 
	 * @param term the term to visit
	 * @return some result
	 */
	T visit(NamedNull term);
}
