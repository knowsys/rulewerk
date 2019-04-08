package org.semanticweb.vlog4j.core.model.api;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author Irina Dragoste
 *
 */
/**
 * Interface for literals. An atom is predicate applied to a tuple of terms; that
 * is, an atomic formula is a formula of the form P(t1,...,tn) for P a
 * {@link Predicate} name, and t1,...,tn some {@link Term}s. The number of terms
 * in the tuple corresponds to the {@link Predicate} arity.
 *
 * @author david.carral@tu-dresden.de
 */
public interface Literal {
	
	boolean isNegated();
	
	/**
	 * The atom predicate.
	 * 
	 * @return the atom predicate.
	 */
	Predicate getPredicate();

	/**
	 * The list of terms representing the tuple arguments.
	 *
	 * @return an unmodifiable list of terms with the same size as the
	 *         {@link Predicate} arity.
	 */
	List<Term> getTerms();

	/**
	 * Returns the {@link Variable}s that occur among the atom terms.
	 *
	 * @return the set of atom variables
	 */
	Set<Variable> getVariables();

	/**
	 * Returns the {@link Constant}s that occur among the atom terms.
	 * 
	 * @return the set of atom constants
	 */
	Set<Constant> getConstants();

	/**
	 * Returns the {@link Blank}s that occur among the atom terms.
	 * 
	 * @return the set of atom blanks
	 */
	Set<Blank> getBlanks();

}
