package org.semanticweb.vlog4j.core.model.api;

/**
 * A Predicate represents a relation between terms. Is uniquely identified by
 * its name and arity. The arity determines the number of terms allowed in the
 * relation. For example, a Predicate with name {@code P} and arity {@code n}
 * allows atomic formulae of the form {@code P(t1,...,tn)}.
 * 
 * @author Irina Dragoste
 *
 */
public interface Predicate {

	/**
	 * The name of the Predicate.
	 * 
	 * @return the name of the Predicate.
	 */
	String getName();

	/**
	 * The arity represents the number of terms allowed as relation arguments for
	 * this Predicate. For example, a Predicate with name {@code P} and arity
	 * {@code n} allows atomic formulae of the form {@code P(t1,...,tn)}.
	 * 
	 * @return the arity of the Predicate.
	 */
	int getArity();

}
