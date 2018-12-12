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

import java.io.IOException;

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

	static Term rdfValueToTerm(Value value) {
		if (value instanceof BNode) {
			return rdfBlankNodeToBlank((BNode) value);
		} else if (value instanceof Literal) {
			return rdfLiteralToConstant((Literal) value);
		} else
			return rdfURItoConstant((URI) value);
	}

	static Term rdfBlankNodeToBlank(BNode bNode) {
		// IDs are unique per Model.
		return new BlankImpl(bNode.getID());
	}

	static Term rdfURItoConstant(URI uri) {
		return new ConstantImpl(uri.toString());
	}

	static Term rdfLiteralToConstant(Literal literal) {
		if (literal.getDatatype() != null) {
			final String normalizedLabel = XMLDatatypeUtil.normalize(literal.getLabel(), literal.getDatatype());
			System.out.println("normalized label: " + normalizedLabel);
		}
		final String normalizedStringValueLiteral = buildNormalizedStringValue(literal);
		return new ConstantImpl(normalizedStringValueLiteral);
	}

	// method inspired from NTriplesUtil #append(Literal literal)
	/**
	 * Serializes the given {@code literal} to the the NTriples format for
	 * {@link Literal}s, using a canonical representation.
	 * 
	 * @param literal
	 * @return a unique string representation of given {@code literal} in canonical
	 *         form.
	 */
	static String buildNormalizedStringValue(Literal literal) {
		final StringBuilder sb = new StringBuilder();
		final URI datatype = literal.getDatatype();
		// Do some character escaping on the label:
		sb.append("\"");
		final String normalizedLabel = datatype != null ? XMLDatatypeUtil.normalize(literal.getLabel(), datatype)
				: literal.getLabel();
		try {
			NTriplesUtil.escapeString(normalizedLabel, sb);
		} catch (final IOException e) {
			throw new RuntimeException("I/O exception unexpected when appending to a StringBuilder.", e);
		}
		sb.append("\"");

		if (literal.getLanguage() != null) {
			// Append the literal's language
			sb.append("@");
			sb.append(literal.getLanguage());
		} else {

			if (datatype != null) {
				// Append the literal's datatype
				// FIXME make datatype is not an abbreviated URI.
				sb.append("^^");
				try {
					NTriplesUtil.append(datatype, sb);
				} catch (final IOException e) {
					throw new RuntimeException("I/O exception unexpected when appending to a StringBuilder.", e);
				}
			}
		}
		return sb.toString();
	}

}
