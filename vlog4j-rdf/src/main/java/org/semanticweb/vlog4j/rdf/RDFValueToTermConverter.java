package org.semanticweb.vlog4j.rdf;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
		// TODO should we get string value or id? it should be the same value
		System.out.println("bNode.getID() " + bNode.getID());
		System.out.println("bNode.stringValue() " + bNode.stringValue());

		return new BlankImpl(bNode.stringValue());
	}

	public static Term rdfURItoConstant(URI uri) {
		// TODO remove sysos
		System.out.println("uri.getNamespace() " + uri.getNamespace());
		System.out.println("uri.getLocalName() " + uri.getLocalName());
		System.out.println(uri.stringValue());
		return new ConstantImpl(uri.stringValue());
	}

	public static Term rdfLiteralToConstant(Literal literal) {
		// TODO canonical form (1 instead of 01)
		// TODO datatype long prefix

		System.out.println("literal.getLabel()" + literal.getLabel());
		System.out.println("literal.getLanguage()" + literal.getLanguage());
		System.out.println(" literal.getDatatype()" + literal.getDatatype());
		System.out.println("literal.stringValue()" + literal.stringValue());
		return new ConstantImpl(literal.stringValue());
	}

}
