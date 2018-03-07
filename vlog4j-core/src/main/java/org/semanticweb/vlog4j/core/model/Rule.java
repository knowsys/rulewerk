package org.semanticweb.vlog4j.core.model;

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
import java.util.Set;

import org.semanticweb.vlog4j.core.validation.RuleValidationException;

public class Rule {

	private final Atom[] body;
	private final Atom[] head;

	// TODO should we use Collections.unmodifiableList(body)? no compile error
	private final Set<Variable> existentiallyQuantifiedVariables = new HashSet<Variable>();

	private final Set<Variable> bodyVariables = new HashSet<Variable>();
	private final Set<Variable> headVariables = new HashSet<Variable>();
	private final Set<Variable> variables = new HashSet<Variable>();
	private final Set<Variable> universallyQuantifiedVariables = new HashSet<Variable>();
	private final Set<Constant> bodyConstants = new HashSet<Constant>();
	private final Set<Constant> headConstants = new HashSet<Constant>();
	private final Set<Constant> constants = new HashSet<Constant>();
	private final Set<Term> terms = new HashSet<Term>();

	public Rule(Atom[] body, Atom[] head) throws RuleValidationException {

		validateRuleInput(body, head);

		this.body = body;
		this.head = head;

		collectTerms();
	}

	private void validateRuleInput(Atom[] body, Atom[] head) throws RuleValidationException {
		if (body == null) {
			throw new RuleValidationException("Null rule body");
		}
		if (body.length == 0) {
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
		if (head.length == 0) {
			throw new RuleValidationException("Empty rule head");
		}
		for (Atom headAtom : head) {
			if (headAtom == null) {
				throw new RuleValidationException("Rule head contains null atoms");
			}
		}
	}

	private void collectTerms() {
		for (Atom bodyAtom : this.body) {
			for (Term term : bodyAtom.getArguments()) {
				if (term.isVariable()) {
					bodyVariables.add((Variable) term);
				} else {
					bodyConstants.add((Constant) term);
				}
			}
		}
		for (Atom headAtom : this.head) {
			for (Term term : headAtom.getArguments()) {
				if (term.isVariable()) {
					headVariables.add((Variable) term);
				} else {
					headConstants.add((Constant) term);
				}
			}
		}
		variables.addAll(bodyVariables);
		variables.addAll(headVariables);

		constants.addAll(bodyConstants);
		constants.addAll(headConstants);

		terms.addAll(variables);
		terms.addAll(constants);

		for (Variable headVariable : headVariables) {
			if (!bodyVariables.contains(headVariable)) {
				existentiallyQuantifiedVariables.add(headVariable);
			}
		}

		for (Variable variable : variables) {
			if (!existentiallyQuantifiedVariables.contains(variable)) {
				universallyQuantifiedVariables.add(variable);
			}
		}
	}

	public Atom[] getBody() {
		return body;
	}

	public Atom[] getHead() {
		return head;

	}

	public Set<Variable> getExistentiallyQuantifiedVariables() {
		return existentiallyQuantifiedVariables;
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
