package org.semanticweb.vlog4j.core.model;

import java.util.ArrayList;
import java.util.Arrays;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.vlog4j.core.model.validation.AtomValidationException;

public class AtomImpl implements Atom {
	private final String predicate;
	private final List<Term> arguments;
	private final Set<Variable> variables;
	private final Set<Constant> constants;

	public AtomImpl(final String predicate, final List<Term> arguments) throws AtomValidationException {
		validatePredicateName(predicate);
		validateArguments(arguments);
		this.predicate = predicate;
		this.arguments = arguments;
		this.variables = collectVariables();
		this.constants = collectConstants();
	}

	public AtomImpl(final String predicateName, final Term... arguments) throws AtomValidationException {
		validatePredicateName(predicateName);
		validateArguments(Arrays.asList(arguments));
		this.predicate = predicateName;
		this.arguments = Arrays.asList(arguments);
		this.variables = collectVariables();
		this.constants = collectConstants();
	}

	public AtomImpl(final Atom atom) {
		this.predicate = new String(atom.getPredicate());

		this.arguments = new ArrayList<>(atom.getArguments());
		this.variables = new HashSet<>(atom.getVariables());
		this.constants = new HashSet<>(atom.getConstants());
	}

	private void validatePredicateName(final String predicateName) throws AtomValidationException {
		if (StringUtils.isBlank(predicateName)) {
			// TODO use string formatter
			throw new AtomValidationException("Invalid blank Atom predicate name: " + predicateName);
		}

		// TODO other Predicate name validations

	}

	private void validateArguments(final List<Term> arguments) throws AtomValidationException {
		// FIXME do we allow empty predicates?
		if (arguments == null) {
			throw new AtomValidationException("Null Atom argument list");
		}
		if (arguments.isEmpty()) {
			throw new AtomValidationException("Empty Atom argument list");
		}
		for (final Term term : arguments) {
			if (term == null) {
				throw new AtomValidationException("Atom argument list contains null terms");
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.predicate == null ? 0 : this.predicate.hashCode());
		result = prime * result + (this.arguments == null ? 0 : this.arguments.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AtomImpl other = (AtomImpl) obj;
		if (this.predicate == null) {
			if (other.predicate != null) {
				return false;
			}
		} else if (!this.predicate.equals(other.predicate)) {
			return false;
		}
		if (this.arguments == null) {
			if (other.arguments != null) {
				return false;
			}
		} else if (!this.arguments.equals(other.arguments)) {
			return false;
		}
		return true;
	}

	// TODO: perhaps another format
	@Override
	public String toString() {
		return "AtomImpl [predicateName=" + this.predicate + ", arguments=" + this.arguments + "]";
	}

	@Override
	public String getPredicate() {
		return this.predicate;
	}

	@Override
	public List<Term> getArguments() {
		return Collections.unmodifiableList(this.arguments);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.unmodifiableSet(this.variables);
	}

	@Override
	public Set<Constant> getConstants() {
		return Collections.unmodifiableSet(this.constants);
	}

	private Set<Variable> collectVariables() {
		final Set<Variable> variables = new HashSet<>();
		for (final Term term : this.arguments) {
			if (TermType.VARIABLE.equals(term.getType())) {
				variables.add((Variable) term);
			}
		}
		return variables;
	}

	private Set<Constant> collectConstants() {
		final Set<Constant> constants = new HashSet<>();
		for (final Term term : this.arguments) {
			if (TermType.CONSTANT.equals(term.getType())) {
				constants.add((Constant) term);
			}
		}
		return constants;
	}

}
