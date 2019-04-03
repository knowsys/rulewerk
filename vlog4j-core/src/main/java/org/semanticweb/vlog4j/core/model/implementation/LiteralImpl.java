package org.semanticweb.vlog4j.core.model.implementation;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;

public class LiteralImpl extends AtomImpl implements Literal {

	private final boolean positive;

	public LiteralImpl(@NonNull Predicate predicate, @NonNull List<Term> terms, boolean positive) {
		super(predicate, terms);
		this.positive = positive;
	}

	@Override
	public boolean isPositive() {
		return positive;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
