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

import java.util.Map.Entry;

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;

/**
 * Registry that manages prefixes and base namespace declarations as used for
 * parsing and serialising inputs.
 *
 * @author Markus Kroetzsch
 */
public interface PrefixDeclarationRegistry extends Iterable<Entry<String, String>> {

	static final String XSD = "http://www.w3.org/2001/XMLSchema#";
	static final String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
	static final String XSD_DECIMAL = "http://www.w3.org/2001/XMLSchema#decimal";
	static final String XSD_DOUBLE = "http://www.w3.org/2001/XMLSchema#double";
	static final String XSD_FLOAT = "http://www.w3.org/2001/XMLSchema#float";
	static final String XSD_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";
	static final String XSD_INT = "http://www.w3.org/2001/XMLSchema#int";
	static final String XSD_LONG = "http://www.w3.org/2001/XMLSchema#long";
	static final String XSD_SHORT = "http://www.w3.org/2001/XMLSchema#short";
	static final String XSD_BYTE = "http://www.w3.org/2001/XMLSchema#byte";
	static final String XSD_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
	static final String RDF_LANGSTRING = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";

	static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	static final String EMPTY_BASE = "";
	static final String PREFIX_NAME_SEPARATOR = ":";

	/**
	 * Resets the registry to an empty state, without a base or any prefixes.
	 */
	void clear();

	/**
	 * Returns the relevant base namespace. This should always return a result,
	 * possibly using a local default value if no base was declared.
	 *
	 * @return string of an absolute base IRI
	 */
	String getBaseIri();

	/**
	 * Sets the base namespace to the given value. This should only be done once,
	 * and not after the base namespace was assumed to be an implicit default value.
	 *
	 * @param baseIri the new base namespace
	 * @throws PrefixDeclarationException if base was already defined
	 */
	void setBaseIri(String baseIri) throws PrefixDeclarationException;

	/**
	 * Returns the IRI associated with a given prefix name.
	 *
	 * @param prefixName the name of the prefix.
	 * @throws PrefixDeclarationException if prefixName was not defined.
	 */
	String getPrefixIri(String prefixName) throws PrefixDeclarationException;

	/**
	 * Registers a prefix declaration. Behaviour is implementation-defined if
	 * prefixName has already been registered.
	 *
	 * @param prefixName the name of the prefix.
	 * @param prefixIri  the IRI of the prefix.
	 *
	 * @throws PrefixDeclarationException when prefixName is already registered, at
	 *                                    the discretion of the implementation.
	 */
	void setPrefixIri(String prefixName, String prefixIri) throws PrefixDeclarationException;

	/**
	 * Un-registers a prefix declaration if present.
	 *
	 * @param prefixName the name of the prefix.
	 */
	void unsetPrefix(String prefixName);

	/**
	 * Turn a <a href="https://www.w3.org/TR/turtle/#prefixed-name">prefixed
	 * name</a> into an absolute IRI.
	 *
	 * @param prefixedName a prefixed name of the form prefixName:localName.
	 *
	 * @throws PrefixDeclarationException when the prefixName has not been declared.
	 * @return an absolute IRI corresponding to prefixedName.
	 */
	String resolvePrefixedName(String prefixedName) throws PrefixDeclarationException;

	/**
	 * Turn a potentially relative IRI into an absolute IRI.
	 *
	 * @param relativeOrAbsoluteIri an IRI that may be relative or absolute.
	 * @throws PrefixDeclarationException when relativeOrAbsoluteIri is not a valid
	 *                                    IRI.
	 *
	 * @return when relativeOrAbsoluteIri is an absolute IRI, it is returned as-is.
	 *         Otherwise, the current base IRI is prepended.
	 */
	String absolutizeIri(String relativeOrAbsoluteIri) throws PrefixDeclarationException;

	/**
	 * Turn an absolute IRI into a (possibly) prefixed name. Dual to
	 * {@link PrefixDeclarationRegistry#resolvePrefixedName}.
	 *
	 * @param iri            an absolute IRI to abbreviate
	 * @param addIriBrackets if true, unabbreviated IRIs will be enclosed in &lt;
	 *                       &gt;
	 *
	 * @return an abbreviated form of {@code iri} if an appropriate prefix is known,
	 *         or {@code iri}.
	 */
	String unresolveAbsoluteIri(String iri, boolean addIriBrackets);
}
