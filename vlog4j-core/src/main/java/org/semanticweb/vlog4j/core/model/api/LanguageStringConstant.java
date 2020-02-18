package org.semanticweb.vlog4j.core.model.api;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import java.util.function.Function;

import org.semanticweb.vlog4j.core.model.implementation.Serializer;

/**
 * Interface for string constants with a language tag, used to represent values
 * of type http://www.w3.org/1999/02/22-rdf-syntax-ns#langString in RDF, OWL,
 * and related languages used with knowledge graphs. Such terms are of type
 * {@link TermType#LANGSTRING_CONSTANT}.
 *
 * @author Markus Kroetzsch
 */
public interface LanguageStringConstant extends Constant {

	@Override
	default TermType getType() {
		return TermType.LANGSTRING_CONSTANT;
	}

	/**
	 * Returns the datatype of this term, which is always
	 * http://www.w3.org/1999/02/22-rdf-syntax-ns#langString.
	 *
	 * @return a IRI of RDF langString datatype
	 */
	default String getDatatype() {
		return PrefixDeclarations.RDF_LANGSTRING;
	}

	/**
	 * Returns the string value of the literal without the language tag.
	 *
	 * @return a non-null string
	 */
	String getString();

	/**
	 * Returns the language tag of the literal, which should be a lowercase string
	 * that conforms to the <a href="http://tools.ietf.org/html/bcp47">BCP 47</a>
	 * specification.
	 *
	 * @return a non-empty string
	 */
	String getLanguageTag();

	@Override
	default String getSyntacticRepresentation() {
		return Serializer.getConstantName(this);
	}

	@Override
	default String getSyntacticRepresentation(Function<String, String> iriTransformer) {
		return getSyntacticRepresentation();
	}
}
