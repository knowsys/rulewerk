package org.semanticweb.vlog4j.core.model.api;

/**
 * Interface for string constants with a language tag, used to represent values
 * of type http://www.w3.org/1999/02/22-rdf-syntax-ns#langString in RDF, OWL,
 * and related languages used with knowledge graphs. Such terms are of type
 * {@link TermType#LANGSTRING_CONSTANT}.
 * 
 * @author Markus Kroetzsch
 */
public interface LanguageStringConstant extends Constant {

	@Override
	default TermType getType() {
		return TermType.LANGSTRING_CONSTANT;
	}

	/**
	 * Returns the datatype of this term, which is always
	 * http://www.w3.org/1999/02/22-rdf-syntax-ns#langString.
	 * 
	 * @return a IRI of RDF langString datatype
	 */
	default String getDatatype() {
		return "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";
	}

	/**
	 * Returns the string value of the literal without the language tag.
	 * 
	 * @return a non-null string
	 */
	String getString();

	/**
	 * Returns the language tag of the literal, which should be a lowercase string
	 * that conforms to the <a href="http://tools.ietf.org/html/bcp47">BCP 47</a>
	 * specification.
	 * 
	 * @return a non-empty string
	 */
	String getLanguageTag();
}
