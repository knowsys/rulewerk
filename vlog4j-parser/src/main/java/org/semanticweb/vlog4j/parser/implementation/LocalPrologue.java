package org.semanticweb.vlog4j.parser.implementation;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.semanticweb.vlog4j.parser.api.Prologue;

final public class LocalPrologue implements Prologue {

	// ??? Can I use default logguer
	final static Logger logger = LoggerFactory.getLogger(LocalPrologue.class.getName());

	private static Prologue prologue;

	Map<String, URI> prefixes;
	URI baseURI;

	private LocalPrologue() {
		prefixes = new HashMap<String, URI>();
		baseURI = null;
	}

	public static synchronized Prologue getPrologue() {
		// Lazy initialization
		if (prologue == null) {
			prologue = new LocalPrologue();
			logger.info("Creating new prologue");
		} else {
			logger.info("Prologue previously defined");
		}
		return prologue;
	}

	public String getBase() throws PrologueException {
		if (baseURI == null)
			throw new PrologueException("@base not defined");
		return baseURI.toString();
	}

	public String getPrefix(String prefix) throws PrologueException {
		if (!prefixes.containsKey(prefix))
			throw new PrologueException("@prefix " + prefix + " not defined");
		return prefixes.get(prefix).toString();
	}

	public void setPrefix(String prefix, String uri) throws PrologueException {
		if (prefixes.containsKey(prefix)) {
			throw new PrologueException("Can not re define @prefix: " + prefix);
		}
		URI newUri = URI.create(uri);
		if (!newUri.isAbsolute()) {
			newUri = baseURI.resolve(newUri);
		}
		logger.info("Setting new prefix: " + prefix + ", " + newUri.toString());
		prefixes.putIfAbsent(prefix, newUri);
	}

	public void setBase(String baseString) throws PrologueException {
		if (baseURI != null)
			throw new PrologueException("Can not re define @base: " + baseURI.toString() + ", " + baseString);
		URI newBase = URI.create(baseString);
		if (!newBase.isAbsolute()) {
			throw new PrologueException("Base must be ab absolute IRI: " + baseString);
		}
		baseURI = newBase;
		System.out.println(baseString);
		System.out.println(baseURI.toString());
	}

	public String resolvePName(String prefixedName) throws PrologueException {
		// from the parser we know that prefixedName is of the form:
		// prefix:something
		// remember that the prefixes are stored with the colon symbol
		// This does not return the surrounding angle brackes <>

		int idx = prefixedName.indexOf(":") + 1;
		String prefix = prefixedName.substring(0, idx);
		String sufix = prefixedName.substring(idx);

		if (prefixes.containsKey(prefix))
			localResolver(prefixes.get(prefix), sufix);
		throw new PrologueException("@prefix not found: " + prefixedName);
	}

	public String absolutize(String iri) throws PrologueException {
		URI relative = URI.create(iri);
		if (relative.isAbsolute())
			return iri;
		if (baseURI == null)
			throw new PrologueException("@base not defined");
		return localResolver(baseURI, iri);
	}

	private String localResolver(URI uri, String relative) {
		// if the last character of the uri is '#', the resolve method of
		// java.net.URI does not work well
		String uriString = uri.toString();
		if (uriString.charAt(uriString.length() - 1) == '#')
			return uriString + relative;
		else
			return uri.resolve(relative).toString();
	}

}
