package org.semanticweb.vlog4j.core.model.implementation;

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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.semanticweb.vlog4j.core.exceptions.PrefixDeclarationException;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarationRegistry;

/**
 * Implementation of the common logic for prefix declaration registries.
 *
 * @author Maximilian Marx
 */
public abstract class AbstractPrefixDeclarationRegistry implements PrefixDeclarationRegistry {
	/**
	 * Map associating each prefixName with the full prefixIri.
	 */
	protected Map<String, String> prefixes = new HashMap<>();

	/**
	 * Iri holding the base namespace.
	 */
	protected String baseUri = PrefixDeclarationRegistry.EMPTY_BASE;

	@Override
	public String getBaseIri() {
		return baseUri;
	}

	@Override
	public String getPrefixIri(String prefixName) throws PrefixDeclarationException {
		if (!prefixes.containsKey(prefixName)) {
			throw new PrefixDeclarationException(
					"Prefix \"" + prefixName + "\" cannot be resolved (not declared yet).");
		}

		return prefixes.get(prefixName);
	}

	@Override
	public String resolvePrefixedName(String prefixedName) throws PrefixDeclarationException {
		int colon = prefixedName.indexOf(":");
		String prefix = prefixedName.substring(0, colon + 1);
		String suffix = prefixedName.substring(colon + 1);

		return getPrefixIri(prefix) + suffix;
	}

	@Override
	public String absolutizeIri(String potentiallyRelativeIri) throws PrefixDeclarationException {
		URI relative;

		try {
			relative = new URI(potentiallyRelativeIri);
		} catch (URISyntaxException e) {
			throw new PrefixDeclarationException("Failed to parse IRI", e);
		}

		if (relative.isAbsolute()) {
			return potentiallyRelativeIri;
		} else {
			return getBaseIri() + potentiallyRelativeIri;
		}
	}

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return this.prefixes.entrySet().iterator();
	}
}
