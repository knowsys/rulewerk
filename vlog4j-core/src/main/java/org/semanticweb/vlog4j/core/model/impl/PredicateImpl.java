package org.semanticweb.vlog4j.core.model.impl;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Predicate;

/**
 * Implementation for {@link Predicate}. Supports predicates of arity 1 or
 * higher.
 * 
 * @author Irina Dragoste
 *
 */
public class PredicateImpl implements Predicate {

	final private String name;

	final private int arity;

	/**
	 * Constructor for {@link Predicate}s of arity 1 or higher.
	 * 
	 * @param name
	 *            a non-blank String (not null, nor empty or whitespace).
	 * @param arity
	 *            an int value strictly greater than 0.
	 */
	public PredicateImpl(String name, int arity) {
		Validate.notBlank(name, "Predicates cannot be named by blank Strings.");
		Validate.isTrue(arity > 0, "Predicate arity must be greater than zero: %d", arity);

		this.name = name;
		this.arity = arity;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getArity() {
		return this.arity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.arity;
		result = prime * result + this.name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PredicateImpl)) {
			return false;
		}
		final PredicateImpl other = (PredicateImpl) obj;

		return this.arity == other.arity && this.name.equals(other.getName());
	}

	@Override
	public String toString() {
		return "PredicateImpl [name=" + this.name + ", arity=" + this.arity + "]";
	}

}
