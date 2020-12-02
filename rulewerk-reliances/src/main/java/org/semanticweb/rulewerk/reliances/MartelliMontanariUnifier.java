package org.semanticweb.rulewerk.reliances;

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

import java.util.HashMap;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class MartelliMontanariUnifier {
	// TODO: check if this way of unification can be transformed into another one.
	// s.t. we unify atoms into another ones, instead of using new things.
	final HashMap<Term, Term> unifier;
	boolean success;

	public String toString() {
		String result = success + ", {";

		for (Term t : unifier.keySet()) {
			result += t + ": " + unifier.get(t) + ", ";
		}

		result += "}";
		return result;
	}

	/**
	 * An implementation of the Martelli & Montanari unification algorithm. @note
	 * that this algorithm is commutative.
	 * 
	 * @param first      List of Literals to be unified
	 * @param second     List of Literals to be unified
	 * @param assignment of Literal positions where [i] is the location in the first
	 *                   list while assignment[i] is the location in the second
	 *                   list. @see AssignmentIterable.AssignmentIterarot.next
	 */
	public MartelliMontanariUnifier(List<Literal> first, List<Literal> second, Assignment assignment) {
		unifier = new HashMap<>();
		success = true;
		for (Match match : assignment.getMatches()) {
			unify(first.get(match.getOrigin()), second.get(match.getDestination()));
		}
	}

	private String getNewFreshVariableName() {
		return "NewFreshVariable-" + unifier.size();
	}

	private void unify(Variable var, Constant cons) {
		if (unifier.containsKey(var) && !cons.equals(unifier.get(var))) {
			success = false;
		} else {
			unifier.putIfAbsent(var, cons);
		}
	}

	// there may be errors here because of existential and universal variables
	private void unify(Variable var1, Variable var2) {
		if (unifier.containsKey(var1) && unifier.containsKey(var2)) {
			if (!unifier.get(var1).equals(unifier.get(var2))) {
				success = false;
			}
		} else if (!unifier.containsKey(var1) && unifier.containsKey(var2)) {
			unifier.put(var1, unifier.get(var2));
		} else if (unifier.containsKey(var1) && !unifier.containsKey(var2)) {
			unifier.put(var2, unifier.get(var1));
		} else {
			String newVarName = getNewFreshVariableName();
			if (var1.getType() == TermType.EXISTENTIAL_VARIABLE) {
				unifier.put(var1, Expressions.makeExistentialVariable(newVarName));
			} else {
				unifier.put(var1, Expressions.makeUniversalVariable(newVarName));
			}
			if (var2.getType() == TermType.EXISTENTIAL_VARIABLE) {
				unifier.put(var2, Expressions.makeExistentialVariable(newVarName));
			} else {
				unifier.put(var2, Expressions.makeUniversalVariable(newVarName));
			}
		}
	}

	private void unify(Term term1, Term term2) {
		if (term1.isConstant() && term2.isConstant()) {
			if (term1.equals(term2)) {
				return;
			} else {
				success = false;
			}
		} else if (term1.isConstant() && term2.isVariable()) {
			unify((Variable) term2, (Constant) term1);
		} else if (term1.isVariable() && term2.isConstant()) {
			unify((Variable) term1, (Constant) term2);
		} else {
			unify((Variable) term1, (Variable) term2);
		}
	}

	public void unify(Literal literal1, Literal literal2) {
		if (success) {
			if (!literal1.getPredicate().equals(literal2.getPredicate())) {
				success = false;
				return;
			}
			List<Term> terms1 = literal1.getArguments();
			List<Term> terms2 = literal2.getArguments();
			for (int i = 0; i < terms1.size(); i++) {
				unify(terms1.get(i), terms2.get(i));
			}
		}
	}

}
