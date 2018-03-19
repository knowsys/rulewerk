package org.semanticweb.vlog4j.core.model.impl;

import java.util.HashSet;

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

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.validation.AtomValidationException;
import org.semanticweb.vlog4j.core.model.validation.BlankNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.ConstantNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.IllegalEntityNameException;
import org.semanticweb.vlog4j.core.model.validation.PredicateNameValidationException;
import org.semanticweb.vlog4j.core.model.validation.RuleValidationException;
import org.semanticweb.vlog4j.core.model.validation.VariableNameValidationException;

public class RuleImpl implements Rule {

	final Conjunction body;
	final Conjunction head;

	/**
	 * Creates a Rule with a non-empty body and an non-empty head. The variables
	 * that occur only in the rule head (and not in the rule body) are considered to
	 * be existentially quantified. The variables that are not existentially
	 * quantified are considered to be universally quantified.
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
	public RuleImpl(final Conjunction head, final Conjunction body) {
		Validate.notNull(head);
		Validate.notNull(body);
		Validate.notEmpty(body.getAtoms());
		Validate.notEmpty(head.getAtoms());

		this.head = head;
		this.body = body;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.body.hashCode();
		result = prime * result + this.head.hashCode();
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
		if (!(obj instanceof Rule)) {
			return false;
		}
		final Rule other = (Rule) obj;

		return this.head.equals(other.getHead()) && this.body.equals(other.getBody());
	}

	@Override
	public String toString() {
		return "RuleImpl [body=" + this.body + ", head=" + this.head + "]";
	}

	@Override
	public Conjunction getHead() {
		return this.head;
	}

	@Override
	public Conjunction getBody() {
		return this.body;
	}

	@Override
	public Set<Variable> getUniversallyQuantifiedVariables() {
		return this.body.getVariables();
	}

	@Override
	public Set<Variable> getExistentiallyQuantifiedVariables() {
		Set<Variable> result = new HashSet<Variable>(this.head.getVariables());
		result.removeAll(this.body.getVariables());
		return result;
	}

}
