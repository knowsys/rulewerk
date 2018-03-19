package org.semanticweb.vlog4j.core.model.impl;

import java.util.ArrayList;
import java.util.Collections;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.validation.AtomValidationException;
import org.semanticweb.vlog4j.core.model.validation.BlankNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.ConstantNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.IllegalEntityNameException;
import org.semanticweb.vlog4j.core.model.validation.PredicateNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.RuleValidationException;
import org.semanticweb.vlog4j.core.model.validation.RuleValidator;
import org.semanticweb.vlog4j.core.model.validation.VariableNameValidationException;

public class RuleImpl implements Rule {

	private final List<Atom> body = new ArrayList<>();
	private final List<Atom> head = new ArrayList<>();

	private final Set<Term> terms = new HashSet<>();
	private final Set<Term> bodyTerms = new HashSet<>();
	private final Set<Term> headTerms = new HashSet<>();
	private final Set<Constant> constants = new HashSet<>();
	private final Set<Constant> bodyConstants = new HashSet<>();
	private final Set<Constant> headConstants = new HashSet<>();
	private final Set<Variable> variables = new HashSet<>();
	private final Set<Variable> bodyVariables = new HashSet<>();
	private final Set<Variable> headVariables = new HashSet<>();
	private final Set<Variable> existentiallyQuantifiedVariables = new HashSet<>();

	/**
	 * Creates a Rule with a non-empty body and an non-empty head. The variables that occur only in the rule head (and not in the rule body) are considered to
	 * be existentially quantified. The variables that are not existentially quantified are considered to be universally quantified.
	 *
	 * @param head
	 *            list of Atoms representing the rule body conjuncts.
	 * @param body
	 *            list of Atoms representing the rule head conjuncts.
	 * @throws RuleValidationException
	 * @throws AtomValidationException
	 * @throws PredicateNameValidationException
	 * @throws BlankNameValidationException
	 * @throws ConstantNameValidationException
	 * @throws VariableNameValidationException
	 * @throws IllegalEntityNameException
	 */
	public RuleImpl(final List<Atom> head, final List<Atom> body) throws RuleValidationException, AtomValidationException, PredicateNameValidationException,
			BlankNameValidationException, ConstantNameValidationException, VariableNameValidationException, IllegalEntityNameException {
		RuleValidator.ruleCheck(body, head);
		RuleValidator.bodyCheck(body);
		RuleValidator.headCheck(head);

//		for (final Atom bodyAtom : body) {
//			final AtomImpl copiedBodyAtom = new AtomImpl(bodyAtom);
//			this.body.add(copiedBodyAtom);
//			this.bodyConstants.addAll(new HashSet<>(copiedBodyAtom.getConstants()));
//			this.bodyVariables.addAll(new HashSet<>(copiedBodyAtom.getVariables()));
//			this.bodyTerms.addAll(new HashSet<>(copiedBodyAtom.getArguments()));
//		}
//
//		for (final Atom headAtom : head) {
//			final AtomImpl copiedHeadAtom = new AtomImpl(headAtom);
//			this.head.add(new AtomImpl(copiedHeadAtom));
//			this.headConstants.addAll(new HashSet<>(copiedHeadAtom.getConstants()));
//			this.headVariables.addAll(new HashSet<>(copiedHeadAtom.getVariables()));
//			this.headTerms.addAll(new HashSet<>(copiedHeadAtom.getArguments()));
//		}

		this.terms.addAll(new HashSet<>(this.bodyTerms));
		this.terms.addAll(new HashSet<>(this.headTerms));
		this.constants.addAll(new HashSet<>(this.bodyConstants));
		this.constants.addAll(new HashSet<>(this.headConstants));
		this.variables.addAll(new HashSet<>(this.bodyVariables));
		this.variables.addAll(new HashSet<>(this.headVariables));
		this.existentiallyQuantifiedVariables.addAll(new HashSet<>(this.variables));
		this.existentiallyQuantifiedVariables.removeAll(new HashSet<>(this.bodyVariables));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.body == null ? 0 : this.body.hashCode());
		result = prime * result + (this.head == null ? 0 : this.head.hashCode());
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
		final RuleImpl other = (RuleImpl) obj;
		if (this.body == null) {
			if (other.body != null) {
				return false;
			}
		} else if (!this.body.equals(other.body)) {
			return false;
		}
		if (this.head == null) {
			if (other.head != null) {
				return false;
			}
		} else if (!this.head.equals(other.head)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RuleImpl [body=" + this.body + ", head=" + this.head + "]";
	}

	@Override
	public List<Atom> getHead() {
		return Collections.unmodifiableList(this.head);
	}

	@Override
	public List<Atom> getBody() {
		return Collections.unmodifiableList(this.body);
	}

	@Override
	public Set<Term> getTerms() {
		return Collections.unmodifiableSet(this.terms);
	}

	@Override
	public Set<Term> getBodyTerms() {
		return Collections.unmodifiableSet(this.bodyTerms);
	}

	@Override
	public Set<Term> getHeadTerms() {
		return Collections.unmodifiableSet(this.headTerms);
	}

	@Override
	public Set<Constant> getConstants() {
		return Collections.unmodifiableSet(this.constants);
	}

	@Override
	public Set<Constant> getHeadConstants() {
		return Collections.unmodifiableSet(this.headConstants);
	}

	@Override
	public Set<Constant> getBodyConstants() {
		return Collections.unmodifiableSet(this.bodyConstants);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.unmodifiableSet(this.variables);
	}

	@Override
	public Set<Variable> getHeadVariables() {
		return Collections.unmodifiableSet(this.headVariables);
	}

	@Override
	public Set<Variable> getBodyVariables() {
		return Collections.unmodifiableSet(this.bodyVariables);
	}

	@Override
	public Set<Variable> getUniversallyQuantifiedVariables() {
		return Collections.unmodifiableSet(this.bodyVariables);
	}

	@Override
	public Set<Variable> getExistentiallyQuantifiedVariables() {
		return Collections.unmodifiableSet(this.existentiallyQuantifiedVariables);
	}

}
