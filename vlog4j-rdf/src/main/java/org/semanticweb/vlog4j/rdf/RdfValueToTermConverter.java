package org.semanticweb.vlog4j.rdf;

/*-
 * #%L
 * VLog4j RDF Support
 * %%
 * Copyright (C) 2018 VLog4j Developers
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
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.BlankImpl;
import org.semanticweb.vlog4j.core.model.implementation.ConstantImpl;

final class RdfValueToTermConverter {

	private RdfValueToTermConverter() {
	}

	static Term rdfValueToTerm(final Value value) {
		if (value instanceof BNode) {
			return rdfBlankNodeToBlank((BNode) value);
		} else if (value instanceof Literal) {
			return rdfLiteralToConstant((Literal) value);
		} else if (value instanceof URI) {
			return rdfURItoConstant((URI) value);
		} else {
			throw new RuntimeException("Unknown value type: " + value.getClass());
		}
	}

	static Term rdfBlankNodeToBlank(final BNode bNode) {
		// IDs are generated to be unique in every Model.
		return new BlankImpl(bNode.getID());
	}

	static Term rdfURItoConstant(final URI uri) {
		final String escapedURIString = NTriplesUtil.escapeString(uri.toString());
		return new ConstantImpl(escapedURIString);
	}

	static Term rdfLiteralToConstant(final Literal literal) {
		final String normalizedStringValueLiteral = buildNormalizedStringValue(literal);
		return new ConstantImpl(normalizedStringValueLiteral);
	}

	/**
	 * Serializes the given {@code literal} to the NTriples format for
	 * {@link Literal}s, using a canonical representation.
	 *
	 * @param literal
	 * @return a unique string representation of given {@code literal} in canonical
	 *         form
	 */
	static String buildNormalizedStringValue(final Literal literal) {
		final URI datatype = literal.getDatatype();

		final StringBuilder sb = new StringBuilder();
		// Do some character escaping on the label:
		sb.append("\"");
		final String normalizedLabel = (datatype != null) ? XMLDatatypeUtil.normalize(literal.getLabel(), datatype)
				: literal.getLabel();
		sb.append(NTriplesUtil.escapeString(normalizedLabel));
		sb.append("\"");

		if (literal.getLanguage() != null) {
			// Append the literal's language
			sb.append("@");
			sb.append(literal.getLanguage());
		} else {
			if (datatype != null) { // Append the literal's datatype
				sb.append("^^");
				sb.append(NTriplesUtil.toNTriplesString(datatype));
			} else { // Default to string for untyped literals:
				sb.append("^^<http://www.w3.org/2001/XMLSchema#string>");
			}
		}

		return sb.toString();
	}

}
