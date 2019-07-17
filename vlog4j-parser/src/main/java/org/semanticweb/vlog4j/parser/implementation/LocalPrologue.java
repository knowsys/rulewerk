package org.semanticweb.vlog4j.parser.implementation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.semanticweb.vlog4j.parser.api.Prologue;

final public class LocalPrologue implements Prologue {

	//??? Can I use default logguer
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
	}

	public String resolvePName(String prefixedName) throws PrologueException {
		// from the parser we know that prefixedName is of the form:
		// prefix:something
		// remember that the prefixes are stored with the colon symbol
		// This does not return the surrounding <>

		int idx = prefixedName.indexOf(":") + 1;
		String prefix = prefixedName.substring(0, idx);
		String sufix = prefixedName.substring(idx);

		if (prefixes.containsKey(prefix)) {
			// if the last character of the fullUri is '#', the resolve method of
			// java.net.URI does not work well
			String fullUri = prefixes.get(prefix).toString();
			if (fullUri.charAt(fullUri.length() - 1) == '#')
				return fullUri + sufix;
			// if it is different, then it works
			return prefixes.get(prefix).resolve(sufix).toString();
		}
		throw new PrologueException("@prefix not found: " + prefixedName);
	}

}