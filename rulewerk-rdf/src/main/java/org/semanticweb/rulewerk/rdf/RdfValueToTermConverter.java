package org.semanticweb.rulewerk.rdf;

/*-
 * #%L
 * Rulewerk RDF Support
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

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.rio.ntriples.NTriplesUtil;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.implementation.Skolemization;
import org.semanticweb.rulewerk.core.model.implementation.TermFactory;

/**
 * Helper class to convert RDF ters to Rulewerk {@link Term} objects.
 * 
 * @author Markus Kroetzsch
 *
 */
final class RdfValueToTermConverter {

	final boolean skolemize;
	final Skolemization skolemization = new Skolemization();
	final TermFactory termFactory = new TermFactory();

	/**
	 * Constructor.
	 * 
	 * @param skolemize if true, blank nodes are translated to constants with
	 *                  generated IRIs; otherwise they are replanced by named nulls
	 *                  with generated ids
	 */
	public RdfValueToTermConverter(boolean skolemize) {
		this.skolemize = skolemize;
	}

	public Term convertValue(final Value value) {
		if (value instanceof BNode) {
			return convertBlankNode((BNode) value);
		} else if (value instanceof Literal) {
			return convertLiteral((Literal) value);
		} else if (value instanceof URI) {
			return convertUri((URI) value);
		} else {
			throw new RulewerkRuntimeException("Unknown value type: " + value.getClass());
		}
	}

	public Term convertBlankNode(final BNode bNode) {
		// Note: IDs are generated to be unique in every Model, so our renaming might be
		// redundant. But we want a RenamedNamedNull here, and a consistent name format
		// is nice too.
		if (skolemize) {
			return skolemization.getSkolemConstant(bNode.getID(), termFactory);
		} else {
			return skolemization.getRenamedNamedNull(bNode.getID());
		}
	}

	public Term convertUri(final URI uri) {
		final String escapedURIString = NTriplesUtil.escapeString(uri.toString());
		return termFactory.makeAbstractConstant(escapedURIString);
	}

	public Term convertLiteral(final Literal literal) {
		final URI datatype = literal.getDatatype();
		if (datatype != null) {
			return termFactory.makeDatatypeConstant(XMLDatatypeUtil.normalize(literal.getLabel(), datatype),
					datatype.toString());
		} else if (literal.getLanguage() != null) {
			return termFactory.makeLanguageStringConstant(literal.getLabel(), literal.getLanguage());
		} else {
			return termFactory.makeDatatypeConstant(literal.getLabel(), PrefixDeclarationRegistry.XSD_STRING);
		}
	}

	public Predicate convertUriToPredicate(final URI uri, int arity) {
		final String escapedURIString = NTriplesUtil.escapeString(uri.toString());
		return termFactory.makePredicate(escapedURIString, arity);
	}

}
