package org.semanticweb.rulewerk.reliances;

import java.util.ArrayList;
import java.util.List;

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

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class SelfRestraint {

	// TODO create class to instantiate query
	static private Literal instantiate(Literal literal) {
		assert !literal.isNegated();
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			newTerms.add(Expressions.makeAbstractConstant(term.getName()));
		}
		return Expressions.makePositiveLiteral(literal.getPredicate(), newTerms);
	}

	static private Literal instantiateQuery(Literal literal) {
		assert !literal.isNegated();
		List<Term> newTerms = new ArrayList<>();
		for (Term term : literal.getArguments()) {
			if (term.getType() == TermType.EXISTENTIAL_VARIABLE) {
				newTerms.add(term);
			} else {
				newTerms.add(Expressions.makeAbstractConstant(term.getName()));
			}

		}
		return Expressions.makePositiveLiteral(literal.getPredicate(), newTerms);
	}

	// TODO create util class for the combination
	static private int[] complement(int[] combination) {
		int[] result = new int[combination.length];
		for (int i = 0; i < combination.length; i++) {
			if (combination[i] == 0) {
				result[i] = 1;
			} else {
				result[i] = 0;
			}
		}
		return result;
	}

	// TODO create util class for the filtering
	static <T> List<T> filter(List<T> original, int[] combination) {
		List<T> result = new ArrayList<>();
		for (int i = 0; i < combination.length; i++) {
			if (combination[i] == 1) {
				result.add(original.get(i));
			}
		}
		return result;
	}

	/**
	 * 
	 * @param rule
	 * @return True if the rule restraints itself.
	 */
	static public boolean restraint(Rule rule) {
		if (rule.containsUnconnectedPieces()) {
			return true;
		}

		List<PositiveLiteral> headAtoms = rule.getHead().getLiterals();
		int headSize = headAtoms.size();

		PowerSet powerSet = new PowerSet(headSize);

		while (powerSet.hasNext()) {
			int[] toAssignIdx = powerSet.next();
			int[] assigneeIdx = complement(toAssignIdx);

			List<PositiveLiteral> headAtomsToAssign = filter(headAtoms, toAssignIdx);
			List<PositiveLiteral> headAtomsAssignee = filter(headAtoms, assigneeIdx);

			if (headAtomsToAssign.size() > 0 && headAtomsAssignee.size() > 0) {
				List<Literal> instance = new ArrayList<>();
				List<Literal> query = new ArrayList<>();

				headAtomsAssignee.forEach(literal -> instance.add(instantiate(literal)));
				headAtomsToAssign.forEach(literal -> query.add(instantiateQuery(literal)));

				if (SBCQ.query(instance, query)) {
					return true;
				}
			}

		}
		return false;

	}

}
