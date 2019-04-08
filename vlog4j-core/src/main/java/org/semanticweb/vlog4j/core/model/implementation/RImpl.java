package org.semanticweb.vlog4j.core.model.implementation;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Conj;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.R;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * Implementation for {@link Rule}. Represents rules with non-empty heads and
 * bodies.
 * 
 * @author Irina Dragoste
 *
 */
public class RImpl implements R {

	final Conj<Literal> body;
	final Conj<PositiveLiteral> head;

	/**
	 * Creates a Rule with a non-empty body and an non-empty head. All variables in
	 * the body are considered universally quantified; all variables in the head
	 * that do not occur in the body are considered existentially quantified.
	 *
	 * @param head
	 *            list of Atoms representing the rule body conjuncts.
	 * @param body
	 *            list of Atoms representing the rule head conjuncts.
	 */
	public RImpl(@NonNull final Conj<PositiveLiteral> head, @NonNull final Conj<Literal> body) {
		Validate.notNull(head);
		Validate.notNull(body);
		Validate.notEmpty(body.getLiterals());
		Validate.notEmpty(head.getLiterals());

		this.head = head;
		this.body = body;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.body.hashCode();
		result = prime * result + this.head.hashCode();
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
		if (!(obj instanceof R)) {
			return false;
		}
		final R other = (R) obj;

		return this.head.equals(other.getHead()) && this.body.equals(other.getBody());
	}

	@Override
	public String toString() {
		return this.head + " :- " + this.body;
	}

	@Override
	public Conj<PositiveLiteral> getHead() {
		return this.head;
	}

	@Override
	public Conj<Literal> getBody() {
		return this.body;
	}

	@Override
	public Set<Variable> getExistentiallyQuantifiedVariables() {
		final Set<Variable> result = new HashSet<>(this.head.getVariables());
		result.removeAll(this.body.getVariables());
		return result;
	}

	@Override
	public Set<Variable> getUniversallyQuantifiedVariables() {
		return this.body.getVariables();
	}

}
