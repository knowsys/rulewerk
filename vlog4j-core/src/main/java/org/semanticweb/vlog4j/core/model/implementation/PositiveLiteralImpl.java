package org.semanticweb.vlog4j.core.model.implementation;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;

public class PositiveLiteralImpl extends AbstractLiteral implements PositiveLiteral {

	public PositiveLiteralImpl(@NonNull Predicate predicate, @NonNull List<Term> terms) {
		super(predicate, terms);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.isNegated() ? 1231 : 1237);
		result = prime * result + this.getPredicate().hashCode();
		result = prime * result + this.getTerms().hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PositiveLiteral)) {
			return false;
		}
		final PositiveLiteral other = (PositiveLiteral) obj;

		return this.getPredicate().equals(other.getPredicate()) && this.getTerms().equals(other.getTerms());
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getPredicate().getName()).append("(");
		boolean first = true;
		for (final Term term : this.getTerms()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(term);
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
