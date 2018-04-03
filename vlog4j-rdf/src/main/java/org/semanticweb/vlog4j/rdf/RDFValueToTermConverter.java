package org.semanticweb.vlog4j.rdf;

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

class RDFValueToTermConverter {

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

	// NTriplesUtil append format
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
