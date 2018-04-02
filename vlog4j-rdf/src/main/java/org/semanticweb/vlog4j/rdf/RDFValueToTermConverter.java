package org.semanticweb.vlog4j.rdf;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.BlankImpl;
import org.semanticweb.vlog4j.core.model.implementation.ConstantImpl;

public class RDFValueToTermConverter {

	public static Term rdfValueToTerm(Value value) {
		if (value instanceof BNode) {
			return rdfBlankNodeToBlank((BNode) value);
		} else if (value instanceof Literal) {
			return rdfLiteralToConstant((Literal) value);
		} else
			return rdfURItoConstant((URI) value);
	}

	public static Term rdfBlankNodeToBlank(BNode bNode) {
		return new BlankImpl(bNode.stringValue());
	}

	public static Term rdfURItoConstant(URI uri) {
		// TODO perhaps enclose URI in "<" ">"
		return new ConstantImpl(uri.stringValue());
	}

	public static Term rdfLiteralToConstant(Literal literal) {
		// TODO canonical form (1 instead of 01)
		// TODO datatype long prefix

		System.out.println("literal.getLabel() " + literal.getLabel());
		System.out.println("literal.getLanguage() " + literal.getLanguage());
		System.out.println(" literal.getDatatype() " + literal.getDatatype());
		System.out.println("literal.stringValue() " + literal.stringValue());

		if (literal.getDatatype() != null) {
			final String normalizedLabel = XMLDatatypeUtil.normalize(literal.getLabel(), literal.getDatatype());
			System.out.println("normalized label: " + normalizedLabel);
		}
		// FIXME how t treat builtin datatypes?
		// XMLDatatypeUtil.isBuiltInDatatype(literal.getDatatype());

		final String normalizedStringValueLiteral = getNormalizedStringValueLiteral(literal);
		System.out.println(normalizedStringValueLiteral);
		return new ConstantImpl(normalizedStringValueLiteral);
	}

	protected static String getNormalizedStringValueLiteral(Literal literal) {
		final StringBuilder sb = new StringBuilder();
		// FIXME do these characters need to be escaped?
		sb.append('"');
		if (literal.getDatatype() != null) {
			final String normalizedLabel = XMLDatatypeUtil.normalize(literal.getLabel(), literal.getDatatype());
			System.out.println("normalized label: " + normalizedLabel);
			sb.append(normalizedLabel);
		} else {
			sb.append(literal.getLabel());
		}
		sb.append('"');
		if (literal.getLanguage() != null) {
			// Append the literal's language
			sb.append("@");
			sb.append(literal.getLanguage());
		} else if (literal.getDatatype() != null) {
			// Append the literal's datatype (possibly written as an abbreviated
			// URI)
			sb.append("^^");
			// FIXME get full datatype name
			// FIXME should datatype be enclosed in "<" ">" ?
			sb.append(literal.getDatatype());
		}
		return sb.toString();
	}

}
