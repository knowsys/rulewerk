package org.semanticweb.vlog4j.core.model.implementation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Conj;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * Simple implementation of {@link Conjunction}.
 * 
 * @author Markus Krötzsch
 */
public class ConjImpl<T extends Literal> implements Conj<T> {
	
	final List<T> literals;

	/**
	 * Constructor.
	 * 
	 * @param literals
	 *            a non-null list of literals, that cannot contain null elements.
	 */
	public ConjImpl(@NonNull List<T> literals) {
		Validate.noNullElements(literals);
		this.literals = literals;
	}



	@Override
	public List<T> getLiterals() {
		return Collections.unmodifiableList(this.literals);
	}

	/**
	 * Returns a term filter object that has visited all terms in this conjunction
	 * for the given type.
	 * 
	 * @param termType
	 *            specifies the type of term to look for
	 * @return term filter
	 */
	TermFilter getTermFilter(TermType termType) {
		final TermFilter termFilter = new TermFilter(termType);
		for (final T literal : this.literals) {
			for (final Term term : literal.getTerms()) {
				term.accept(termFilter);
			}
		}
		return termFilter;
	}

	@Override
	public Set<Term> getTerms() {
		return getTermFilter(null).getTerms();
	}

	@Override
	public Set<Term> getTerms(TermType termType) {
		return getTermFilter(termType).getTerms();
	}

	@Override
	public Set<Variable> getVariables() {
		return getTermFilter(TermType.VARIABLE).getVariables();
	}

	@Override
	public int hashCode() {
		return this.literals.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Conjunction)) {
			return false;
		}
		final Conjunction other = (Conjunction) obj;
		return this.literals.equals(other.getAtoms());
	}

	@Override
	public Iterator<T> iterator() {
		return this.literals.iterator();
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;
		for (final T literal : this.literals) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(literal.toString());
		}
		return stringBuilder.toString();
	}


}
