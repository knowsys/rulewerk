package org.semanticweb.vlog4j.core.model.api;

import org.semanticweb.vlog4j.core.exceptions.PrefixDeclarationException;

/*-
 * #%L
 * vlog4j-syntax
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

/**
 * Registry that manages prefixes and base namespace declarations as used for
 * parsing and serialising inputs.
 *
 * @author Markus Kroetzsch
 */
public interface PrefixDeclarations extends Iterable<String> {

	static final String XSD = "http://www.w3.org/2001/XMLSchema#";
	static final String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
	static final String XSD_DECIMAL = "http://www.w3.org/2001/XMLSchema#decimal";
	static final String XSD_DOUBLE = "http://www.w3.org/2001/XMLSchema#double";
	static final String XSD_FLOAT = "http://www.w3.org/2001/XMLSchema#float";
	static final String XSD_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";
	static final String XSD_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
	static final String RDF_LANGSTRING = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";

	/**
	 * Returns the relevant base namespace. This should always return a result,
	 * possibly using a local default value if no base was declared.
	 *
	 * @return string of an absolute base IRI
	 */
	String getBase();

	/**
	 * Sets the base namespace to the given value. This should only be done once,
	 * and not after the base namespace was assumed to be an implicit default value.
	 *
	 * @param base the new base namespace
	 * @throws PrefixDeclarationException if base was already defined
	 */
	void setBase(String base) throws PrefixDeclarationException;

	String getPrefix(String prefix) throws PrefixDeclarationException;

	void setPrefix(String prefix, String iri) throws PrefixDeclarationException;

	String resolvePrefixedName(String prefixedName) throws PrefixDeclarationException;

	String absolutize(String prefixedName) throws PrefixDeclarationException;
}
