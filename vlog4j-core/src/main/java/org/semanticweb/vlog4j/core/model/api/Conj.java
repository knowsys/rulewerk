package org.semanticweb.vlog4j.core.model.api;

import java.util.List;
import java.util.Set;

/**
 * Interface for representing conjunctions of {@link Atom}s, i.e., lists of
 * atomic formulas that are connected with logical AND. Conjunctions may have
 * free variables, since they contain no quantifiers.
 * 
 * @author Markus Krötzsch
 *
 */
public interface Conj<T extends Literal> extends Iterable<T> {
	
	/**
	 * Returns the list of literals that are part of this conjunction.
	 * 
	 * @return list of literals
	 */
	List<T> getLiterals();

	/**
	 * Returns the set of terms of a certain type that occur in this conjunction.
	 * 
	 * @param termType
	 *            the type of the term requested
	 * @return set of matching terms used in this conjunction
	 */
	Set<? extends Term> getTerms(TermType termType);

	/**
	 * Returns the set of all terms that occur in this conjunction.
	 * 
	 * @return set of terms used in this conjunction
	 */
	Set<Term> getTerms();

	/**
	 * Returns the {@link Variable}s that occur in this conjunction.
	 *
	 * @return a set of variables
	 */
	Set<Variable> getVariables();

}
