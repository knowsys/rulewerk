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

import org.semanticweb.vlog4j.core.model.validation.AtomValidationException;
import org.semanticweb.vlog4j.core.model.validation.AtomValidator;
import org.semanticweb.vlog4j.core.model.validation.BlankNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.ConstantNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.EntityNameValidator;
import org.semanticweb.vlog4j.core.model.validation.PredicateNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.VariableNameValidationException;

public class AtomImpl implements Atom {
	private final String predicateName;
	private final List<Term> arguments = new ArrayList<>();
	private final Set<Blank> blanks = new HashSet<>();
	private final Set<Constant> constants = new HashSet<>();
	private final Set<Variable> variables = new HashSet<>();

	public AtomImpl(final String predicateName, final List<Term> arguments) throws AtomValidationException, PredicateNameValidationException,
			BlankNameValidationException, ConstantNameValidationException, VariableNameValidationException {
		EntityNameValidator.oredicateNameCheck(predicateName);
		this.predicateName = new String(predicateName);
		AtomValidator.validArgumentsCheck(arguments);
		for (final Term argument : arguments) {
			switch (argument.getType()) {
				case BLANK:
					this.arguments.add(new BlankImpl((Blank) argument));
					this.blanks.add(new BlankImpl((Blank) argument));
					break;
				case CONSTANT:
					this.arguments.add(new ConstantImpl((Constant) argument));
					this.constants.add(new ConstantImpl((Constant) argument));
					break;
				case VARIABLE:
					this.arguments.add(new VariableImpl((Variable) argument));
					this.variables.add(new VariableImpl((Variable) argument));
					break;
			}
		}
	}

	public AtomImpl(final String predicateName, final Term firstArgument, final Term... remainingArguments) throws AtomValidationException,
			PredicateNameValidationException, BlankNameValidationException, ConstantNameValidationException, VariableNameValidationException {
		this(predicateName, append(firstArgument, remainingArguments));
	}

	private static List<Term> append(final Term firstArgument, final Term... remainingArguments) {
		final List<Term> arguments = new ArrayList<>();
		arguments.add(firstArgument);
		arguments.addAll(Arrays.asList(remainingArguments));
		return arguments;
	}

	public AtomImpl(final Atom atom) throws AtomValidationException, PredicateNameValidationException, BlankNameValidationException,
			ConstantNameValidationException, VariableNameValidationException {
		this(atom.getPredicate(), atom.getArguments());
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
	public Set<Blank> getBlanks() {
		return Collections.unmodifiableSet(this.blanks);
	}

	@Override
	public Set<Constant> getConstants() {
		return Collections.unmodifiableSet(this.constants);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.unmodifiableSet(this.variables);
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
