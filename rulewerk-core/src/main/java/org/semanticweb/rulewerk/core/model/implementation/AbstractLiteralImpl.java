package org.semanticweb.rulewerk.core.model.implementation;

/*-
 * #%L
 * Rulewerk Core Components
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collections;
import java.util.List;

import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;

/**
 * Implements {@link Literal} objects. A literal is a formula of the form
 * +P(t1,...,tn) or -P(t1,...,tn) for P a {@link Predicate} name, and t1,...,tn
 * some {@link Term}s. The number of terms corresponds to the {@link Predicate}
 * arity.
 *
 * @author david.carral@tu-dresden.de
 * @author Markus Krötzsch
 */
public abstract class AbstractLiteralImpl implements Literal {

	private final Predicate predicate;
	private final List<Term> terms;

	/**
	 * Creates a {@link Literal} of the form "{@code predicate}({@code terms})".
	 *
	 * @param predicate non-blank predicate name
	 * @param terms     non-empty list of non-null terms. List size must be the same
	 *                  as the <b>predicate</b> arity.
	 */
	public AbstractLiteralImpl(final Predicate predicate, final List<Term> terms) {
		Validate.notNull(predicate, "Literal predicates cannot be null.");
		Validate.noNullElements(terms,
				"Null terms cannot appear in literals. The list contains a null at position [%d].");
		Validate.notEmpty(terms, "Literals of arity zero are not supported: please specify at least one term.");

		Validate.isTrue(terms.size() == predicate.getArity(), "Terms size [%d] does not match predicate arity [%d].",
				terms.size(), predicate.getArity());

		this.predicate = predicate;
		this.terms = terms;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.isNegated() ? 1231 : 1237);
		result = prime * result + this.getPredicate().hashCode();
		result = prime * result + this.getArguments().hashCode();
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
		if (!(obj instanceof Literal)) {
			return false;
		}
		final Literal other = (Literal) obj;

		return this.isNegated() == other.isNegated() && this.getPredicate().equals(other.getPredicate())
				&& this.getArguments().equals(other.getArguments());
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeLiteral(this));
	}

	@Override
	public Predicate getPredicate() {
		return this.predicate;
	}

	@Override
	public List<Term> getArguments() {
		return Collections.unmodifiableList(this.terms);
	}

	@Override
	public Stream<Term> getTerms() {
		return getArguments().stream().distinct();
	}

}
