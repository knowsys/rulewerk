package org.semanticweb.vlog4j.core.model.impl;

/*
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * Implements {@link Atom} objects. An atom is an atomic formula is a formula of
 * the form P(t1,...,tn) for P a predicate and t1,...,tn some {@link Term}s.
 *
 * @author david.carral@tu-dresden.de
 * @author Markus Krötzsch
 */
public class AtomImpl implements Atom {

	private final String predicateName;
	private final List<Term> arguments;

	/**
	 * Creates an {@link Atom} of the form
	 * "{@code predicateName}({@code arguments})".
	 *
	 * @param predicateName
	 *            non-blank predicate name
	 * @param arguments
	 *            non-empty list of non-null terms
	 */
	public AtomImpl(final String predicateName, final List<Term> arguments) {
		Validate.notBlank(predicateName, "Predicates cannot be named by blank strings");
		Validate.notEmpty(arguments, "Atoms of arity zero are not supported: please specify at least one parameter.");
		Validate.noNullElements(arguments, "Null cannot be used as a parameter in atoms");

		this.predicateName = predicateName;
		this.arguments = arguments;
	}

	@Override
	public String getPredicate() {
		return this.predicateName;
	}

	@Override
	public List<Term> getArguments() {
		return Collections.unmodifiableList(this.arguments);
	}

	@Override
	public Set<Variable> getVariables() {
		TermFilter termFilter = new TermFilter(TermType.VARIABLE);
		for (Term term : this.arguments) {
			term.accept(termFilter);
		}
		return termFilter.getVariables();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.predicateName.hashCode();
		result = prime * result + this.arguments.hashCode();
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
		if (!(obj instanceof Atom)) {
			return false;
		}
		final Atom other = (Atom) obj;

		return (this.predicateName.equals(other.getPredicate())) && this.arguments.equals(other.getArguments());
	}

	@Override
	public String toString() {
		return "AtomImpl [predicateName=" + this.predicateName + ", arguments=" + this.arguments + "]";
	}

}
