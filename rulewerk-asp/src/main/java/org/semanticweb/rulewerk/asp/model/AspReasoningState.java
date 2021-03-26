package org.semanticweb.rulewerk.asp.model;

/*-
 * #%L
 * Rulewerk ASP Components
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
 * Enumeration of different result state for the reasoning about ASP programs.
 *
 * @author Philipp Hanisch
 */
public enum AspReasoningState {

	/**
	 * The corresponding ASP program is satisfiable.
	 */
	SATISFIABLE,

	/**
	 * The corresponding ASP program is unsatisfiable.
	 */
	UNSATISFIABLE,

	/**
	 * The reasoning process was interrupted.
	 */
	INTERRUPTED,

	/**
	 * An error occurred during the reasoning process.
	 */
	ERROR
}


