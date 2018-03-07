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

import org.semanticweb.vlog4j.core.validation.RuleValidationException;

public class Rule {

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

	public Rule(List<Atom> body, List<Atom> head) throws RuleValidationException {
		validateRuleInput(body, head);

		this.body = Collections.unmodifiableList(body);
		this.head = Collections.unmodifiableList(head);

		this.bodyVariables = Collections.unmodifiableSet(collectVariables(body));
		this.bodyConstants = Collections.unmodifiableSet(collectConstants(body));
		this.headVariables = Collections.unmodifiableSet(collectVariables(head));
		this.headConstants = Collections.unmodifiableSet(collectConstants(head));

		this.variables = Collections.unmodifiableSet(collectVariables());
		this.constants = Collections.unmodifiableSet(collectConstants());
		this.terms = Collections.unmodifiableSet(collectTerms());

		this.existentiallyQuantifiedVariables = Collections.unmodifiableSet(collectExistentiallyQuantifiedVariables());
		this.universallyQuantifiedVariables = Collections.unmodifiableSet(collectUniversallyQuantifiedVariables());
	}

	// TODO: to String
	// TODO: hasCode, equals

	private void validateRuleInput(List<Atom> body, List<Atom> head) throws RuleValidationException {
		if (body == null) {
			throw new RuleValidationException("Null rule body");
		}
		if (body.isEmpty()) {
			throw new RuleValidationException("Empty rule body");
		}
		for (Atom bodyAtom : body) {
			if (bodyAtom == null) {
				throw new RuleValidationException("Rule body contains null atoms");
			}
		}

		if (head == null) {
			throw new RuleValidationException("Null rule head");
		}
		if (head.isEmpty()) {
			throw new RuleValidationException("Empty rule head");
		}
		for (Atom headAtom : head) {
			if (headAtom == null) {
				throw new RuleValidationException("Rule head contains null atoms");
			}
		}
	}

	private Set<Variable> collectVariables(Collection<Atom> atoms) {
		final Set<Variable> variables = new HashSet<>();
		for (Atom atom : atoms) {
			for (Term term : atom.getArguments()) {
				if (term.isVariable()) {
					variables.add((Variable) term);
				}
			}
		}
		return variables;
	}

	private Set<Constant> collectConstants(Collection<Atom> atoms) {
		final Set<Constant> constants = new HashSet<>();
		for (Atom atom : atoms) {
			for (Term term : atom.getArguments()) {
				if (term.isConstant()) {
					constants.add((Constant) term);
				}
			}
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
		for (Variable headVariable : this.headVariables) {
			if (!this.bodyVariables.contains(headVariable)) {
				existentiallyQuantifiedVariables.add(headVariable);
			}
		}
		return existentiallyQuantifiedVariables;
	}

	private Set<Variable> collectUniversallyQuantifiedVariables() {
		final Set<Variable> universallyQuantifiedVariables = new HashSet<>();
		for (Variable variable : this.variables) {
			if (!this.existentiallyQuantifiedVariables.contains(variable)) {
				universallyQuantifiedVariables.add(variable);
			}
		}
		return universallyQuantifiedVariables;
	}

	public List<Atom> getBody() {
		return this.body;
	}

	public List<Atom> getHead() {
		return this.head;
	}

	public Set<Variable> getExistentiallyQuantifiedVariables() {
		return this.existentiallyQuantifiedVariables;
	}

	public Set<Variable> getBodyVariables() {
		return this.bodyVariables;
	}

	public Set<Variable> getHeadVariables() {
		return this.headVariables;
	}

	public Set<Variable> getVariables() {
		return this.variables;
	}

	public Set<Variable> getUniversallyQuantifiedVariables() {
		return this.universallyQuantifiedVariables;
	}

	public Set<Constant> getBodyConstants() {
		return this.bodyConstants;
	}

	public Set<Constant> getHeadConstants() {
		return this.headConstants;
	}

	public Set<Constant> getConstants() {
		return this.constants;
	}

	public Set<Term> getTerms() {
		return this.terms;
	}

}
