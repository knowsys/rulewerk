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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * Implementation for {@link Rule}. Represents rules with non-empty heads and
 * bodies.
 * 
 * @author Irina Dragoste
 *
 */
public class RuleImpl implements Rule {

	final Conjunction<Literal> body;
	final Conjunction<PositiveLiteral> head;

	/**
	 * Creates a Rule with a non-empty body and an non-empty head. All variables in
	 * the body are considered universally quantified; all variables in the head
	 * that do not occur in the body are considered existentially quantified.
	 *
	 * @param head
	 *            list of Literals (negated or non-negated) representing the rule
	 *            body conjuncts.
	 * @param body
	 *            list of positive (non-negated) Literals representing the rule head
	 *            conjuncts.
	 */
	public RuleImpl(@NonNull final Conjunction<PositiveLiteral> head, @NonNull final Conjunction<Literal> body) {
		Validate.notNull(head);
		Validate.notNull(body);
		Validate.notEmpty(body.getLiterals());
		Validate.notEmpty(head.getLiterals());

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
		return this.head + " :- " + this.body;
	}

	@Override
	public Conjunction<PositiveLiteral> getHead() {
		return this.head;
	}

	@Override
	public Conjunction<Literal> getBody() {
		return this.body;
	}

	@Override
	public Set<Variable> getExistentiallyQuantifiedVariables() {
		final Set<Variable> result = new HashSet<>(this.head.getVariables());
		result.removeAll(this.body.getVariables());
		return result;
	}

	@Override
	public Set<Variable> getUniversallyQuantifiedVariables() {
		return this.body.getVariables();
	}

}
