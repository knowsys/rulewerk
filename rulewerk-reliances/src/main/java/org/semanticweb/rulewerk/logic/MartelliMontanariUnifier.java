package org.semanticweb.rulewerk.logic;

/*-
 * #%L
 * Rulewerk Reliances
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

import java.util.List;
import java.util.Map;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;

/**
 * An implementation of the Martelli & Montanari unification algorithm for
 * predicate logic without function symbols.
 * 
 * @author Larry González
 *
 */
public class MartelliMontanariUnifier implements Unifier {
	final private Substitution substitution;
	private boolean success;

	/**
	 * An implementation of the Martelli & Montanari unification algorithm for
	 * predicate logic without function symbols.
	 * 
	 * It will unify pair-wise every pair key-value in {@code mapping}
	 */
	public MartelliMontanariUnifier(Map<? extends Literal, ? extends Literal> mapping) {
		substitution = new Substitution();
		success = true;
		mapping.forEach((key, value) -> {
			if (success) {
				unify(key, value);
			}
		});
	}

	private void unify(Literal first, Literal second) {
		if (!first.getPredicate().equals(second.getPredicate()) || first.isNegated() != second.isNegated()) {

			success = false;
		} else {
			List<Term> terms1 = first.getArguments();
			List<Term> terms2 = second.getArguments();
			for (int i = 0; i < terms1.size(); i++) {
				unify(terms1.get(i), terms2.get(i));
			}
		}
	}

	private void unify(Term first, Term second) {
		Term rep1 = substitution.getValue(first);
		Term rep2 = substitution.getValue(second);
		if (first.isExistentialVariable() || second.isExistentialVariable() || rep1.isExistentialVariable()
				|| rep2.isExistentialVariable()) {
			throw new IllegalArgumentException("Unification is not defined for Existential Variables");
		}

		if (rep1.isConstant()) {
			if (rep2.isConstant()) {
				success &= rep1.equals(rep2);
			} else if (rep2.isNull()) {
				success = false;
			} else {
				substitution.add((UniversalVariable) rep2, rep1);
			}
		} else if (rep1.isNull()) {
			if (rep2.isConstant()) {
				success = false;
			} else if (rep2.isNull()) {
				success &= rep1.equals(rep2);
			} else {
				substitution.add((UniversalVariable) rep2, rep1);
			}
		} else {
			if (!rep1.equals(rep2)) {
				substitution.add((UniversalVariable) rep1, rep2);
			}
		}
	}
	

	@Override
	public boolean isSuccessful() {
		return success;
	}

	@Override
	public Substitution getSubstitution() {
		return substitution;
	}

	@Override
	public String toString() {
		return "{"+success+", "+substitution+"}";
	}
}
