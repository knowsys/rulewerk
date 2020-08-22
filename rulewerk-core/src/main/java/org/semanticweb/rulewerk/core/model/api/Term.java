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
 * Interface for terms. A term is characterized by a string name and a
 * {@link TermType}.
 *
 * @author david.carral@tu-dresden.de
 * @author Markus Krötzsch
 */
public interface Term extends Entity {

	/**
	 * Returns the name this term. The name uniquely identifies terms of the same
	 * {@link TermType}.
	 *
	 * @return a non-blank String (not null, nor empty or whitespace).
	 */
	String getName();

	/**
	 * Return the type of this term.
	 *
	 * @return the type of this term
	 */
	TermType getType();

	/**
	 * Returns true if the term represents some kind of constant.
	 *
	 * @return true if term is constant
	 */
	default boolean isConstant() {
		return this.getType() == TermType.ABSTRACT_CONSTANT || this.getType() == TermType.DATATYPE_CONSTANT
				|| this.getType() == TermType.LANGSTRING_CONSTANT;
	}

	/**
	 * Returns true if the term represents some kind of variable.
	 *
	 * @return true if term is variable
	 */
	default boolean isVariable() {
		return this.getType() == TermType.UNIVERSAL_VARIABLE || this.getType() == TermType.EXISTENTIAL_VARIABLE;
	}

	/**
	 * Accept a {@link TermVisitor} and return its output.
	 *
	 * @param termVisitor the TermVisitor
	 * @return output of the visitor
	 */
	<T> T accept(TermVisitor<T> termVisitor);

}
