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

/**
 * An implementation of the Martelli & Montanari unification algorithm.
 * 
 * @note check for other unification algorithms.
 * @author Larry Gonzalez
 *
 */
public class MartelliMontanariUnifier {
	final HashMap<Term, Term> unifier;
	boolean success;

	public Term getUnifiedTerm(Term t) {
		if (unifier.containsKey(t)) {
			return getUnifiedTerm(unifier.get(t));
		} else {
			return t;
		}
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

	public MartelliMontanariUnifier(Literal first, Literal second) {
		unifier = new HashMap<>();
		success = true;
		unify(first, second);
	}

	private String getNewFreshVariableName() {
		return "FN-" + unifier.size();
	}

	private void unify(Variable var, Constant cons) {
		if (unifier.containsKey(var)) {
			Term rep = getUnifiedTerm(var);
			if (rep.getType() == TermType.EXISTENTIAL_VARIABLE || rep.getType() == TermType.UNIVERSAL_VARIABLE) {
				// rep is at the end of the chain in the unifier
				unifier.putIfAbsent(rep, cons);
			} else {
				// both should be the same
				if (!rep.equals(cons)) {
					success = false;
				}
			}
		} else {
			unifier.put(var, cons);
		}

	}

	// var1 and var2 are new
	private void unify(Variable var1, Variable var2) {

		Term rep1 = null;
		Term rep2 = null;
		if (unifier.containsKey(var1)) {
			rep1 = getUnifiedTerm(var1);
		}
		if (unifier.containsKey(var2)) {
			rep2 = getUnifiedTerm(var2);
		}
		// both variables have a representative
		if (rep1 != null && rep2 != null) {
			if (rep1.isVariable() && rep2.isVariable()) {
				if (!rep1.getName().equals(rep2.getName())) {
					insertNewVariableUnification(rep1, rep2);
				}
			} else if (rep1.isConstant() && rep2.isVariable()) {
				unifier.put(rep2, rep1);
			} else if (rep1.isVariable() && rep2.isConstant()) {
				unifier.put(rep1, rep2);
			} else if (rep1.isConstant() && rep2.isConstant()) {
				if (!rep1.getName().equals(rep2.getName())) {
					success = false;
				}
			}
		}
		// var1 has a representative, but var2 does not. we know that var2 is variable
		else if (rep1 != null && rep2 == null) {
			if (rep1.isVariable()) {
				if (!rep1.getName().equals(var2.getName())) {
					insertNewVariableUnification(rep1, var2);
				}
			} else if (rep1.isConstant()) {
				unifier.put(var2, rep1);
			}
		}
		// var1 does not have a representative, but var2 does.
		else if (rep1 == null && rep2 != null) {
			if (rep2.isVariable()) {
				if (!rep2.getName().equals(var1.getName())) {
					insertNewVariableUnification(var1, rep2);
				}
			} else if (rep2.isConstant()) {
				unifier.put(var1, rep2);
			}
		}
		// both var1 and var2 does not have a representative
		else if (rep1 == null && rep2 == null) {
			insertNewVariableUnification(var1, var2);
		}
	}

	private void insertNewVariableUnification(Term var1, Term var2) {
		assert var1.isVariable();
		assert var2.isVariable();
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

	@Override
	public String toString() {
		return unifier + ", " + success;
	}

}
