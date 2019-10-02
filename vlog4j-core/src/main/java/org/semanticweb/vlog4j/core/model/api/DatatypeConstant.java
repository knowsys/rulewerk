package org.semanticweb.vlog4j.core.model.api;

/**
 * Interface for datatype constants, i.e. for constants that represent a
 * specific value of a concrete datatype). Such terms are of type
 * {@link TermType#DATATYPE_CONSTANT}.
 * 
 * Note that <i>datatype literal</i> is a common name of the representation of
 * specific values for a datatype. We mostly avoid this meaning of
 * <i>literal</i> since a literal in logic is typically a negated or non-negated
 * atom.
 * 
 * @author Markus Kroetzsch
 */
public interface DatatypeConstant extends Constant {

	@Override
	default TermType getType() {
		return TermType.DATATYPE_CONSTANT;
	}

	/**
	 * Returns the datatype of this term, which is typically an IRI that defines how
	 * to interpret the lexical value.
	 * 
	 * @return a non-blank String (not null, nor empty or whitespace).
	 */
	String getDatatype();

	/**
	 * Returns the lexical value of the literal, i.e. a string that encodes a
	 * specific value based on the datatype of this literal. Note that there can be
	 * several strings that represent the same value, depending on the rules of the
	 * datatype, and that there the value used here does not have to be a canonical
	 * representation.
	 * 
	 * @return a non-null string
	 */
	String getLexicalValue();
}
