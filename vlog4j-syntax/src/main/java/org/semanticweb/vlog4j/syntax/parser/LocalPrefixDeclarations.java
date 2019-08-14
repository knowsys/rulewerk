package org.semanticweb.vlog4j.syntax.parser;

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
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.vlog4j.syntax.common.PrefixDeclarations;
import org.semanticweb.vlog4j.syntax.common.PrefixDeclarationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link PrefixDeclarations} that is used when parsing data
 * from a single source. In this case, attempts to re-declare prefixes or the
 * base IRI will lead to errors.
 * 
 * @author Markus Kroetzsch
 *
 */
final public class LocalPrefixDeclarations implements PrefixDeclarations {

	final static Logger logger = LoggerFactory.getLogger(LocalPrefixDeclarations.class.getName());

	Map<String, String> prefixes = new HashMap<>();
	String baseUri;

	public String getBase() {
		if (this.baseUri == null) {
			this.baseUri = PrefixDeclarations.DEFAULT_BASE;
		}
		return baseUri.toString();
	}

	public String getPrefix(String prefix) throws PrefixDeclarationException {
		if (!prefixes.containsKey(prefix)) {
			throw new PrefixDeclarationException("@prefix " + prefix + " not defined");
		}
		return prefixes.get(prefix).toString();
	}

	public void setPrefix(String prefix, String uri) throws PrefixDeclarationException {
		if (prefixes.containsKey(prefix)) {
			throw new PrefixDeclarationException("Prefix " + prefix + " is already defined as <" + prefixes.get(prefix)
					+ ">. It cannot be redefined to mean <" + uri + ">.");
		}

		logger.info("Setting new prefix: " + prefix + ", " + uri);
		prefixes.put(prefix, uri);
	}

	public void setBase(String baseUri) throws PrefixDeclarationException {
		if (this.baseUri != null)
			throw new PrefixDeclarationException(
					"Base is already defined as <" + this.baseUri + "> and cannot be re-defined as " + baseUri);
		logger.info("Setting base URI: " + baseUri);
		this.baseUri = baseUri;
	}

	public String resolvePrefixedName(String prefixedName) throws PrefixDeclarationException {
		// from the parser we know that prefixedName is of the form:
		// prefix:something
		// remember that the prefixes are stored with the colon symbol
		// This does not return the surrounding angle brackes <>

		int idx = prefixedName.indexOf(":") + 1;
		String prefix = prefixedName.substring(0, idx);
		String suffix = prefixedName.substring(idx);

		if (prefixes.containsKey(prefix)) {
			return this.prefixes.get(prefix) + suffix;
		} else {
			throw new PrefixDeclarationException("Prefix " + prefixedName + " cannot be resolved (not declared yet).");
		}
	}

	public String absolutize(String iri) throws PrefixDeclarationException {
		URI relative = URI.create(iri);
		if (relative.isAbsolute()) {
			return iri;
		} else {
			return getBase() + iri;
		}
	}

}
