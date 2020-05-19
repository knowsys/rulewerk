package org.semanticweb.rulewerk.parser;

import org.apache.commons.lang3.Validate;

/*-
 * #%L
 * Rulewerk Parser
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

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.implementation.AbstractPrefixDeclarationRegistry;

/**
 * Implementation of {@link PrefixDeclarationRegistry} that is used when parsing
 * data from a single source. In this case, attempts to re-declare prefixes or
 * the base IRI will lead to errors.
 *
 * @author Markus Kroetzsch
 *
 */
final public class LocalPrefixDeclarationRegistry extends AbstractPrefixDeclarationRegistry {

	/**
	 * Fallback IRI to use as base IRI if none is set.
	 */
	private String fallbackIri;

	/**
	 * Construct a Prefix declaration registry without an inherited base IRI. In
	 * this case, we default to {@value org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry#EMPTY_BASE}.
	 */
	public LocalPrefixDeclarationRegistry() {
		this(PrefixDeclarationRegistry.EMPTY_BASE); // empty string encodes: "no base" (use relative IRIs)
	}

	/**
	 * Construct a Prefix declaration registry with a base IRI inherited from the
	 * importing file.
	 *
	 * @param fallbackIri the IRI to use as a base if none is set by the imported
	 *                    file itself (i.e., if {@link #setBaseIri} is not called).
	 */
	public LocalPrefixDeclarationRegistry(String fallbackIri) {
		super();
		Validate.notNull(fallbackIri, "fallbackIri must not be null");
		this.fallbackIri = fallbackIri;
	}

	/**
	 * Returns the relevant base namespace. Returns the fallback IRI if no base
	 * namespace has been set yet, and sets that as the base IRI.
	 *
	 * @return string of an absolute base IRI
	 */
	@Override
	public String getBaseIri() {
		if (this.baseIri == null) {
			this.baseIri = this.fallbackIri;
		}
		return baseIri;
	}

	@Override
	public void setPrefixIri(String prefixName, String prefixIri) throws PrefixDeclarationException {
		if (prefixes.containsKey(prefixName)) {
			throw new PrefixDeclarationException("Prefix \"" + prefixName + "\" is already defined as <"
					+ prefixes.get(prefixName) + ">. It cannot be redefined to mean <" + prefixIri + ">.");
		}

		prefixes.put(prefixName, prefixIri);
	}

	/**
	 * Sets the base namespace to the given value. This should only be done once,
	 * and not after the base namespace was assumed to be an implicit default value.
	 *
	 * @param baseIri the new base namespace
	 * @throws PrefixDeclarationException if base was already defined
	 */

	@Override
	public void setBaseIri(String baseIri) throws PrefixDeclarationException {
		Validate.notNull(baseIri, "baseIri must not be null");
		if (this.baseIri != null)
			throw new PrefixDeclarationException(
					"Base is already defined as <" + this.baseIri + "> and cannot be re-defined as " + baseIri);
		this.baseIri = baseIri;
	}
}
