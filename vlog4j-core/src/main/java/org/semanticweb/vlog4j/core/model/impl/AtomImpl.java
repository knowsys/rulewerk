package org.semanticweb.vlog4j.core.model.impl;

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

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.validation.AtomValidationException;
import org.semanticweb.vlog4j.core.model.validation.AtomValidator;
import org.semanticweb.vlog4j.core.model.validation.EntityNameValidator;
import org.semanticweb.vlog4j.core.model.validation.IllegalEntityNameException;

/**
 * Implements {@link Atom} objects. An atom is an atomic formula is a formula of
 * the form P(t1,...,tn) for P a predicate and t1,...,tn some {@link Term}s.
 *
 * @author david.carral@tu-dresden.de
 */
public class AtomImpl implements Atom {
	private final String predicateName;
	private final List<Term> arguments = new ArrayList<>();

	/**
	 * Instantiates an <b>{@code Atom}</b> object of the form
	 * <b>{@code predicateName}</b>(<b>{@code arguments}</b>).
	 *
	 * @param predicateName
	 *            cannot be a blank String (null, " ", empty string...).
	 * @param arguments
	 *            cannot be null or contain some null value.
	 * @throws AtomValidationException
	 *             if the {@code arguments} is empty or null, or contains some null
	 *             value.
	 * @throws IllegalEntityNameException
	 *             if the <b>{@code predicateName}</b> or the name of some term is a
	 *             blank String.
	 */
	public AtomImpl(final String predicateName, final List<Term> arguments)
			throws IllegalEntityNameException, AtomValidationException {
		EntityNameValidator.validateNonEmptyString(predicateName);
		this.predicateName = new String(predicateName);
		AtomValidator.validateArguments(arguments);
	}

	/**
	 * Instantiates an <b>{@code Atom}</b> object of the form
	 * <b>{@code predicateName}</b>(<b>{@code firstArgument}</b>,
	 * <b>{@code remainingArguments}</b>).
	 *
	 * @param predicateName
	 *            cannot be a blank String (null, " ", empty string...).
	 * @param firstArgument
	 *            cannot be a blank String (null, " ", empty string...).
	 * @param remainingArguments
	 *            cannot be null or contain some null value.
	 * @throws AtomValidationException
	 *             if the {@code firstArgument} is null, {@code remainingArguments}
	 *             is null, or {@code remainingArguments} contains some null value.
	 * @throws IllegalEntityNameException
	 *             if the <b>{@code predicateName}</b> or the name of some term is a
	 *             blank String.
	 *
	 */
	public AtomImpl(final String predicateName, final Term firstArgument, final Term... remainingArguments)
			throws AtomValidationException, IllegalEntityNameException {
		this(predicateName, append(firstArgument, remainingArguments));
	}

	private static List<Term> append(final Term firstArgument, final Term... remainingArguments) {
		final List<Term> arguments = new ArrayList<>();
		arguments.add(firstArgument);
		arguments.addAll(Arrays.asList(remainingArguments));
		return arguments;
	}

	/**
	 * Deep copy constructor (the newly instantiated object does not contain any
	 * reference to original fields in the copied object).
	 */
	public AtomImpl(final Atom copyAtom) throws AtomValidationException, IllegalEntityNameException {
		this(copyAtom.getPredicate(), copyAtom.getArguments());
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
		//return Collections.unmodifiableSet(this.variables);
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.predicateName == null ? 0 : this.predicateName.hashCode());
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
		if (this.predicateName == null) {
			if (other.predicateName != null) {
				return false;
			}
		} else if (!this.predicateName.equals(other.predicateName)) {
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

	@Override
	public String toString() {
		return "AtomImpl [predicateName=" + this.predicateName + ", arguments=" + this.arguments + "]";
	}

}
