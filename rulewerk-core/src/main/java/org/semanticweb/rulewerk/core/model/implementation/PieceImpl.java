package org.semanticweb.rulewerk.core.model.implementation;

/*-
 * #%L
 * Rulewerk Core Components
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
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

import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Piece;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Term;

/**
 * Implementation for {@link Piece}.
 * 
 * @author Larry Gonzalez
 *
 */
public class PieceImpl implements Piece {

	final Conjunction<PositiveLiteral> literals;

	/**
	 * Creates a Rule with a non-empty body and an non-empty head. All variables in
	 * the body must be universally quantified; all variables in the head that do
	 * not occur in the body must be existentially quantified.
	 *
	 * @param head list of Literals (negated or non-negated) representing the rule
	 *             body conjuncts.
	 * @param body list of positive (non-negated) Literals representing the rule
	 *             head conjuncts.
	 */
	public PieceImpl(final Conjunction<PositiveLiteral> literals) {
		Validate.notNull(literals);
		Validate.notEmpty(literals.getLiterals());

		this.literals = literals;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((literals == null) ? 0 : literals.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Piece)) {
			return false;
		}

		final Piece other = (Piece) obj;

		return this.literals.equals(other.getLiterals());
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeLiteralConjunction(this.literals));
	}

	@Override
	public Conjunction<PositiveLiteral> getLiterals() {
		return this.literals;
	}

	@Override
	public Stream<Term> getTerms() {
		return this.literals.getTerms().distinct();
	}

	@Override
	public boolean isUnconnected() {
		for (PositiveLiteral literal : this.literals) {
			if (literal.getUniversalVariables().count() > 0) {
				return false;
			}
		}
		// TODO Auto-generated method stub
		return true;
	}

}
