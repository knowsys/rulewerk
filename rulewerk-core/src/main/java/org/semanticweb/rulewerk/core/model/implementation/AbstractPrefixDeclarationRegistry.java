package org.semanticweb.rulewerk.core.model.implementation;

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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;

/**
 * Implementation of the common logic for prefix declaration registries.
 *
 * @author Maximilian Marx
 */
public abstract class AbstractPrefixDeclarationRegistry implements PrefixDeclarationRegistry {

	/**
	 * Pattern for strings that are permissible as local names in abbreviated forms.
	 */
	static public final String REGEXP_LOCNAME = "^[a-zA-Z]([/a-zA-Z0-9_-])*$";

	/**
	 * Map associating each prefixName with the full prefixIri.
	 */
	protected Map<String, String> prefixes = new HashMap<>();

	/**
	 * Iri holding the base namespace.
	 */
	protected String baseIri = null;

	@Override
	public void clear() {
		baseIri = null;
		prefixes = new HashMap<>();
	}

	@Override
	public String getBaseIri() {
		if (baseIri == null) {
			baseIri = PrefixDeclarationRegistry.EMPTY_BASE;
		}

		return baseIri;
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
	public void unsetPrefix(String prefixName) {
		prefixes.remove(prefixName);
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
	public String unresolveAbsoluteIri(String iri, boolean addIriBrackets) {
		String shortestIri;
		if (addIriBrackets) {
			if (!iri.contains(":") && iri.matches(REGEXP_LOCNAME)) {
				shortestIri = iri;
				if (!PrefixDeclarationRegistry.EMPTY_BASE.equals(baseIri)) {
					throw new RulewerkRuntimeException("Relative IRIs cannot be serialized when a base is declared.");
				}
			} else {
				shortestIri = "<" + iri + ">";
			}
		} else {
			shortestIri = iri;
		}

		String baseIri = getBaseIri();

		if (!PrefixDeclarationRegistry.EMPTY_BASE.equals(baseIri) && iri.length() > baseIri.length()
				&& iri.startsWith(baseIri)) {
			String shorterIri = iri.substring(baseIri.length());
			// Only allow very simple names of this form, to avoid confusion, e.g., with
			// numbers or boolean literals:
			if (shorterIri.matches(REGEXP_LOCNAME) && !"true".equals(shorterIri) || !"false".equals(shorterIri)) {
				shortestIri = shorterIri;
			}
		}

		for (Map.Entry<String, String> entry : prefixes.entrySet()) {
			int localNameLength = iri.length() - entry.getValue().length();
			if (localNameLength > 0 && shortestIri.length() > localNameLength + entry.getKey().length()
					&& iri.startsWith(entry.getValue())) {
				shortestIri = entry.getKey() + iri.substring(entry.getValue().length());
			}
		}

		return shortestIri;
	}

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return this.prefixes.entrySet().iterator();
	}
}
