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
 * Interface for datatype constants, i.e. for constants that represent a
 * specific value of a concrete datatype). Such terms are of type
 * {@link TermType#DATATYPE_CONSTANT}.
 *
 * Note that <i>datatype literal</i> is a common name of the representation of
 * specific values for a datatype. We mostly avoid this meaning of
 * <i>literal</i> since a literal in logic is typically a negated or non-negated
 * atom.
 *
 * @author Markus Kroetzsch
 */
public interface DatatypeConstant extends Constant {

	@Override
	default TermType getType() {
		return TermType.DATATYPE_CONSTANT;
	}

	/**
	 * Returns the datatype of this term, which is typically an IRI that defines how
	 * to interpret the lexical value.
	 *
	 * @return a non-blank String (not null, nor empty or whitespace).
	 */
	String getDatatype();

	/**
	 * Returns the lexical value of the data value, i.e. a string that encodes a
	 * specific value based on the value's datatype. Note that there can be several
	 * strings that represent the same value, depending on the rules of the
	 * datatype, and that there the value used here does not have to be a canonical
	 * representation.
	 *
	 * @return a non-null string
	 */
	String getLexicalValue();

}
