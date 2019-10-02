package org.semanticweb.vlog4j.core.model.api;

import java.util.stream.Stream;

/**
 * Collection of utility methods for handling terms.
 * 
 * @author Markus Kroetzsch
 *
 */
public class Terms {

	/**
	 * Returns a stream of variables found in the given stream of terms. Ordering
	 * and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<Variable> getVariables(Stream<? extends Term> terms) {
		return terms.filter(term -> term.getType() == TermType.UNIVERSAL_VARIABLE
				|| term.getType() == TermType.EXISTENTIAL_VARIABLE).map(Variable.class::cast);
	}

	/**
	 * Returns a stream of constants found in the given stream of terms. Ordering
	 * and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<Constant> getConstants(Stream<? extends Term> terms) {
		return terms.filter(
				term -> term.getType() == TermType.ABSTRACT_CONSTANT || term.getType() == TermType.DATATYPE_CONSTANT)
				.map(Constant.class::cast);
	}

	/**
	 * Returns a stream of named nulls found in the given stream of terms. Ordering
	 * and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<NamedNull> getNamedNulls(Stream<? extends Term> terms) {
		return terms.filter(term -> term.getType() == TermType.NAMED_NULL).map(NamedNull.class::cast);
	}

	/**
	 * Returns a stream of universal variables found in the given stream of terms.
	 * Ordering and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<UniversalVariable> getUniversalVariables(Stream<? extends Term> terms) {
		return terms.filter(term -> term.getType() == TermType.UNIVERSAL_VARIABLE).map(UniversalVariable.class::cast);
	}

	/**
	 * Returns a stream of existential variables found in the given stream of terms.
	 * Ordering and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<ExistentialVariable> getExistentialVariables(Stream<? extends Term> terms) {
		return terms.filter(term -> term.getType() == TermType.EXISTENTIAL_VARIABLE)
				.map(ExistentialVariable.class::cast);
	}

	/**
	 * Returns a stream of abstract constants found in the given stream of terms.
	 * Ordering and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<AbstractConstant> getAbstractConstants(Stream<Term> terms) {
		return terms.filter(term -> term.getType() == TermType.ABSTRACT_CONSTANT).map(AbstractConstant.class::cast);
	}

	/**
	 * Returns a stream of datatype constants found in the given stream of terms.
	 * Ordering and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<DatatypeConstant> getDatatypeConstants(Stream<Term> terms) {
		return terms.filter(term -> term.getType() == TermType.DATATYPE_CONSTANT).map(DatatypeConstant.class::cast);
	}

}
