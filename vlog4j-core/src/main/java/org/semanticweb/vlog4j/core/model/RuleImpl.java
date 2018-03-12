package org.semanticweb.vlog4j.core.model;

import java.util.Collection;
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

import org.semanticweb.vlog4j.core.validation.VLog4jRuleValidationException;

public class RuleImpl implements Rule {

	private final List<Atom> body;
	private final List<Atom> head;

	private final Set<Variable> existentiallyQuantifiedVariables;

	private final Set<Variable> bodyVariables;
	private final Set<Variable> headVariables;
	private final Set<Variable> variables;
	private final Set<Variable> universallyQuantifiedVariables;
	private final Set<Constant> bodyConstants;
	private final Set<Constant> headConstants;
	private final Set<Constant> constants;
	private final Set<Term> terms;

	/**
	 * Creates a Rule with a non-empty body and an non-empty head. The variables that occur only in the rule head (and not in the rule body) are considered to
	 * be existentially quantified. The variables that are not existentially quantified are considered to be universally quantified.
	 *
	 * @param body
	 *            list of Atoms representing the rule body conjuncts.
	 * @param head
	 *            list of Atoms representing the rule head conjuncts.
	 * @throws VLog4jRuleValidationException
	 *             if body or head are null or empty.
	 */
	public RuleImpl(final List<Atom> body, final List<Atom> head) throws VLog4jRuleValidationException {
		validateRuleInput(body, head);

		this.body = body;
		this.head = head;

		this.bodyVariables = collectVariables(body);
		this.bodyConstants = collectConstants(body);
		this.headVariables = collectVariables(head);
		this.headConstants = collectConstants(head);

		this.variables = collectVariables();
		this.constants = collectConstants();
		this.terms = collectTerms();

		this.existentiallyQuantifiedVariables = collectExistentiallyQuantifiedVariables();
		this.universallyQuantifiedVariables = collectUniversallyQuantifiedVariables();
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

	// TODO perhaps another format?
	@Override
	public String toString() {
		return "RuleImpl [body=" + this.body + ", head=" + this.head + "]";
	}

	@Override
	public List<Atom> getBody() {
		return Collections.unmodifiableList(this.body);
	}

	@Override
	public List<Atom> getHead() {
		return Collections.unmodifiableList(this.head);
	}

	@Override
	public Set<Variable> getExistentiallyQuantifiedVariables() {
		return Collections.unmodifiableSet(this.existentiallyQuantifiedVariables);
	}

	@Override
	public Set<Variable> getBodyVariables() {
		return Collections.unmodifiableSet(this.bodyVariables);
	}

	@Override
	public Set<Variable> getHeadVariables() {
		return Collections.unmodifiableSet(this.headVariables);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.unmodifiableSet(this.variables);
	}

	@Override
	public Set<Variable> getUniversallyQuantifiedVariables() {
		return Collections.unmodifiableSet(this.universallyQuantifiedVariables);
	}

	@Override
	public Set<Constant> getBodyConstants() {
		return Collections.unmodifiableSet(this.bodyConstants);
	}

	@Override
	public Set<Constant> getHeadConstants() {
		return Collections.unmodifiableSet(this.headConstants);
	}

	@Override
	public Set<Constant> getConstants() {
		return Collections.unmodifiableSet(this.constants);
	}

	@Override
	public Set<Term> getTerms() {
		return Collections.unmodifiableSet(this.terms);
	}

	private void validateRuleInput(final List<Atom> body, final List<Atom> head) throws VLog4jRuleValidationException {
		if (body == null) {
			throw new VLog4jRuleValidationException("Null rule body");
		}
		if (body.isEmpty()) {
			throw new VLog4jRuleValidationException("Empty rule body");
		}
		for (final Atom bodyAtom : body) {
			if (bodyAtom == null) {
				throw new VLog4jRuleValidationException("Rule body contains null atoms");
			}
		}

		if (head == null) {
			throw new VLog4jRuleValidationException("Null rule head");
		}
		if (head.isEmpty()) {
			throw new VLog4jRuleValidationException("Empty rule head");
		}
		for (final Atom headAtom : head) {
			if (headAtom == null) {
				throw new VLog4jRuleValidationException("Rule head contains null atoms");
			}
		}
	}

	private Set<Variable> collectVariables(final Collection<Atom> atoms) {
		final Set<Variable> variables = new HashSet<>();
		for (final Atom atom : atoms) {
			variables.addAll(atom.getVariables());
		}
		return variables;
	}

	private Set<Constant> collectConstants(final Collection<Atom> atoms) {
		final Set<Constant> constants = new HashSet<>();
		for (final Atom atom : atoms) {
			constants.addAll(atom.getConstants());
		}
		return constants;
	}

	private Set<Variable> collectVariables() {
		final Set<Variable> variables = new HashSet<>();
		variables.addAll(this.bodyVariables);
		variables.addAll(this.headVariables);
		return variables;
	}

	private Set<Constant> collectConstants() {
		final Set<Constant> constants = new HashSet<>();
		constants.addAll(this.bodyConstants);
		constants.addAll(this.headConstants);
		return constants;
	}

	private Set<Term> collectTerms() {
		final Set<Term> terms = new HashSet<>();
		terms.addAll(this.variables);
		terms.addAll(this.constants);
		return terms;
	}

	private Set<Variable> collectExistentiallyQuantifiedVariables() {
		final Set<Variable> existentiallyQuantifiedVariables = new HashSet<>();
		for (final Variable headVariable : this.headVariables) {
			if (!this.bodyVariables.contains(headVariable)) {
				existentiallyQuantifiedVariables.add(headVariable);
			}
		}
		return existentiallyQuantifiedVariables;
	}

	private Set<Variable> collectUniversallyQuantifiedVariables() {
		final Set<Variable> universallyQuantifiedVariables = new HashSet<>();
		for (final Variable variable : this.variables) {
			if (!this.existentiallyQuantifiedVariables.contains(variable)) {
				universallyQuantifiedVariables.add(variable);
			}
		}
		return universallyQuantifiedVariables;
	}

}
