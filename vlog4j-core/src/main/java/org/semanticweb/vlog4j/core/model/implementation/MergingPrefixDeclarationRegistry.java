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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.semanticweb.vlog4j.core.model.api.PrefixDeclarationRegistry;

/**
 * Implementation of {@link PrefixDeclarationRegistry} that is suitable for
 * incrementally parsing from multiple sources. When trying to merge in
 * conflicting prefix declarations, a fresh non-conflicting prefix is generated
 * instead.
 *
 * @author Maximilian Marx
 */
final public class MergingPrefixDeclarationRegistry extends AbstractPrefixDeclarationRegistry {
	/**
	 * Next index to use for generated prefix names.
	 */
	private long nextIndex = 0;

	/**
	 * Prefix string to use for generated prefix name
	 */
	private static final String GENERATED_PREFIX_PREFIX_STRING = "vlog4j_generated_";

	public MergingPrefixDeclarationRegistry() {
		super();
	}

	public MergingPrefixDeclarationRegistry(final PrefixDeclarationRegistry prefixDeclarations) {
		super();
		mergePrefixDeclarations(prefixDeclarations);
	}

	/**
	 * Sets the base namespace to the given value. If a base Iri has already been
	 * set, it will be added as a prefix declaration with a fresh prefixName.
	 *
	 * @param baseIri the new base namespace.
	 */
	@Override
	public void setBaseIri(String baseIri) {
		if (baseIri != this.baseUri && this.baseUri != PrefixDeclarationRegistry.EMPTY_BASE) {
			prefixes.put(getFreshPrefix(), this.baseUri);
		}

		this.baseUri = baseIri;
	}

	/**
	 * Registers a prefix declaration. If prefixName is already registered, a
	 * freshly generated name will be used instead.
	 *
	 * @param prefixName the name of the prefix.
	 * @param prefixIri  the IRI of the prefix.
	 */
	@Override
	public void setPrefixIri(String prefixName, String prefixIri) {
		String name = prefixes.containsKey(prefixName) ? getFreshPrefix() : prefixName;
		prefixes.put(name, prefixIri);
	}

	/**
	 * Turn an absolute Iri into a (possibly) prefixed name. Dual to
	 * {@link AbstractPrefixDeclarationRegistry#resolvePrefixedName}.
	 *
	 * @param iri an absolute Iri to abbreviate.
	 *
	 * @return an abbreviated form of {@code iri} if an appropriate prefix is known,
	 *         or {@code iri}.
	 */
	public String unresolveAbsoluteIri(String iri) {
		Map<String, Integer> matches = new HashMap<>();

		if (baseUri != PrefixDeclarationRegistry.EMPTY_BASE && iri.startsWith(baseUri) && !iri.equals(baseUri)) {
			matches.put(iri.replaceFirst(baseUri, PrefixDeclarationRegistry.EMPTY_BASE), baseUri.length());
		}

		prefixes.forEach((prefixName, prefixIri) -> {
			// only select proper prefixes here, since `eg:` is not a valid prefixed name.
			if (iri.startsWith(prefixIri) && !iri.equals(prefixIri)) {
				matches.put(iri.replaceFirst(prefixIri, prefixName), prefixIri.length());
			}
		});

		List<String> matchesByLength = new ArrayList<>(matches.keySet());
		// reverse order, so we get the longest match first
		matchesByLength.sort(Comparator.comparing(matches::get).reversed());

		if (matchesByLength.size() > 0) {
			return matchesByLength.get(0);
		} else {
			// no matching prefix
			return iri;
		}
	}

	/**
	 * Merge another set of prefix declarations.
	 *
	 * @param other the set of prefix declarations to merge. Conflicting prefixes
	 *              from {@code other} will be renamed.
	 *
	 * @return this
	 */
	public MergingPrefixDeclarationRegistry mergePrefixDeclarations(final PrefixDeclarationRegistry other) {
		this.setBaseIri(other.getBaseIri());

		for (Entry<String, String> prefix : other) {
			setPrefixIri(prefix.getKey(), prefix.getValue());
		}

		return this;
	}

	private String getFreshPrefix() {
		for (long idx = nextIndex; true; ++idx) {
			String freshPrefix = GENERATED_PREFIX_PREFIX_STRING + idx + PrefixDeclarationRegistry.PREFIX_NAME_SEPARATOR;

			if (!prefixes.containsKey(freshPrefix)) {
				this.nextIndex = idx + 1;
				return freshPrefix;
			}
		}
	}
}
