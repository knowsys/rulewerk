package org.semanticweb.rulewerk.core.model.api;

/*
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
 * Enumeration listing the different types of terms.
 *
 * @author david.carral@tu-dresden.de
 * @author Markus Kroetzsch
 *
 */
public enum TermType {
	/**
	 * An abstract constant is a term used to represent named domain elements that
	 * are not a value of any specific datatype.
	 */
	ABSTRACT_CONSTANT,
	/**
	 * A datatype constant is a term used to represent named domain elements that
	 * are the value of a specific datatype.
	 */
	DATATYPE_CONSTANT,
	/**
	 * A string constant with a language tag, used to represent values of type
	 * http://www.w3.org/1999/02/22-rdf-syntax-ns#langString in RDF, OWL, and
	 * related languages used with knowledge graphs.
	 */
	LANGSTRING_CONSTANT,
	/**
	 * A named null is an entity used to represent anonymous domain elements
	 * introduced during the reasoning process to satisfy existential restrictions.
	 */
	NAMED_NULL,
	/**
	 * A universal variable is a variable that can only be used in positions where
	 * it is universally quantified, or implicitly assumed to be.
	 */
	UNIVERSAL_VARIABLE,
	/**
	 * An existential variable is a variable that can only be used in positions
	 * where it is existentially quantified, or implicitly assumed to be.
	 */
	EXISTENTIAL_VARIABLE
}
