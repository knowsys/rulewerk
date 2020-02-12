package org.semanticweb.vlog4j.core.model.implementation;

/*-
 * #%L
 * vlog4j-parser
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.semanticweb.vlog4j.core.exceptions.PrefixDeclarationException;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;

/**
 * Implementation of {@link PrefixDeclarations} that is suitable for
 * incrementally parsing from multiple sources. When trying to merge in
 * conflicting prefix declarations, a fresh non-conflicting prefix is generated
 * instead.
 *
 * @author Maximilian Marx
 */
final public class MergeablePrefixDeclarations implements PrefixDeclarations {
	private Map<String, String> prefixes = new HashMap<>();

	private String baseUri = EMPTY_BASE_PREFIX;
	private long nextIndex = 0;

	private static final String EMPTY_BASE_PREFIX = "";
	private static final String GENERATED_PREFIX_PREFIX = "vlog4j_generated_";

	public MergeablePrefixDeclarations() {
	}

	public MergeablePrefixDeclarations(final PrefixDeclarations prefixDeclarations) {
		super();
		mergePrefixDeclarations(prefixDeclarations);
	}

	@Override
	public String getBase() {
		return baseUri;
	}

	@Override
	public void setBase(String base) {
		if (base != this.baseUri && this.baseUri != EMPTY_BASE_PREFIX) {
			prefixes.put(getFreshPrefix(), this.baseUri);
		}

		this.baseUri = base;
	}

	@Override
	public String getPrefix(String prefix) throws PrefixDeclarationException {
		if (!prefixes.containsKey(prefix)) {
			throw new PrefixDeclarationException("Prefix \"" + prefix + "\" cannot be resolved (not declared yet).");
		}
		return prefixes.get(prefix);
	}

	@Override
	public void setPrefix(String prefix, String iri) {
		String prefixName = prefixes.containsKey(prefix) ? getFreshPrefix() : prefix;
		prefixes.put(prefixName, iri);
	}

	@Override
	public String resolvePrefixedName(String prefixedName) throws PrefixDeclarationException {
		int colon = prefixedName.indexOf(":");
		String prefix = prefixedName.substring(0, colon + 1);
		String suffix = prefixedName.substring(colon + 1);

		return getPrefix(prefix) + suffix;
	}

	/**
	 * Turn an absolute Iri into a (possibly) prefixed name. Dual to
	 * {@link resolvePrefixedName}.
	 *
	 * @param iri an absolute Iri to abbreviate.
	 *
	 * @return an abbreviated form of {@code iri} if an appropriate prefix is known,
	 *         or {@code iri}.
	 */
	public String unresolveAbsoluteIri(String iri) {
		Map<String, Integer> matches = new HashMap<>();

		prefixes.forEach((prefixName, baseIri) -> {
			// only select proper prefixes here, since `eg:` is not a valid prefixed name.
			if (iri.startsWith(baseIri) && !iri.equals(baseIri)) {
				matches.put(iri.replaceFirst(baseIri, prefixName), baseIri.length());
			}
		});

		List<String> matchesByLength = new ArrayList<>(matches.keySet());
		matchesByLength.sort((left, right) -> {
			// inverse order, so we get the longest match first
			return matches.get(right).compareTo(matches.get(left));
		});

		if (matchesByLength.size() > 0) {
			return matchesByLength.get(0);
		} else {
			// no matching prefix
			return iri;
		}
	}

	@Override
	public String absolutize(String iri) {
		URI relative = URI.create(iri);

		if (relative.isAbsolute()) {
			return iri;
		} else {
			return getBase() + iri;
		}
	}

	@Override
	public Iterator<String> iterator() {
		return this.prefixes.keySet().iterator();
	}

	/**
	 * Merge another set of prefix declarations.
	 *
	 * @param other the set of prefix declarations to merge. Conflicting prefixes
	 *              will be renamed.
	 *
	 * @return this
	 */
	public MergeablePrefixDeclarations mergePrefixDeclarations(final PrefixDeclarations other) {
		for (String prefixName : other) {
			String iri;
			try {
				iri = other.getPrefix(prefixName);
			} catch (PrefixDeclarationException e) {
				// this shouldn't throw, since we already know that prefix is defined.
				throw new RuntimeException(e);
			}

			this.prefixes.put(prefixName, iri);
		}

		return this;
	}

	private String getFreshPrefix() {
		for (long idx = nextIndex; true; ++idx) {
			String freshPrefix = GENERATED_PREFIX_PREFIX + idx + ":";

			if (!prefixes.containsKey(freshPrefix)) {
				this.nextIndex = idx + 1;
				return freshPrefix;
			}
		}
	}
}
