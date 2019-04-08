package org.semanticweb.vlog4j.core.model.implementation;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * Implements {@link Literal} objects. A literal is a formula of the form
 * +P(t1,...,tn) or -P(t1,...,tn) for P a {@link Predicate} name, and t1,...,tn some
 * {@link Term}s. The number of terms corresponds to the {@link Predicate}
 * arity.
 *
 * @author david.carral@tu-dresden.de
 * @author Markus Krötzsch
 */
public abstract class AbstractLiteral implements Literal {
	
	private final Predicate predicate;
	private final List<Term> terms;

	/**
	 * Creates a {@link Literal} of the form "{@code predicate}({@code terms})".
	 *
	 * @param predicate
	 *            non-blank predicate name
	 * @param terms
	 *            non-empty list of non-null terms. List size must be the same as
	 *            the <b>predicate</b> arity.
	 */
	public AbstractLiteral(@NonNull final Predicate predicate, @NonNull final List<Term> terms) {
		Validate.notNull(predicate, "Literal predicates cannot be null.");
		Validate.noNullElements(terms, "Null terms cannot appear in literals. The list contains a null at position [%d].");
		Validate.notEmpty(terms, "Literals of arity zero are not supported: please specify at least one term.");

		Validate.isTrue(terms.size() == predicate.getArity(), "Terms size [%d] does not match predicate arity [%d].",
				terms.size(), predicate.getArity());

		this.predicate = predicate;
		this.terms = terms;
	}
	
	@Override
	public Predicate getPredicate() {
		return this.predicate;
	}

	@Override
	public List<Term> getTerms() {
		return Collections.unmodifiableList(this.terms);
	}

	@Override
	public Set<Variable> getVariables() {
		final TermFilter termFilter = new TermFilter(TermType.VARIABLE);
		for (final Term term : this.terms) {
			term.accept(termFilter);
		}
		return termFilter.getVariables();
	}

	@Override
	public Set<Constant> getConstants() {
		final TermFilter termFilter = new TermFilter(TermType.CONSTANT);
		for (final Term term : this.terms) {
			term.accept(termFilter);
		}
		return termFilter.getConstants();
	}
	
	@Override
	public Set<Blank> getBlanks() {
		final TermFilter termFilter = new TermFilter(TermType.BLANK);
		for (final Term term : this.terms) {
			term.accept(termFilter);
		}
		return termFilter.getBlanks();
	}
	

}
