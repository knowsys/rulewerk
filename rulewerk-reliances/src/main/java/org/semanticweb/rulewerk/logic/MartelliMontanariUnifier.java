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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.math.mapping.Pair;
import org.semanticweb.rulewerk.math.mapping.PartialMapping;

/**
 * An implementation of the Martelli & Montanari unification algorithm for
 * predicate logic without function symbols.
 * 
 * @author Larry González
 *
 */
public class MartelliMontanariUnifier implements Unifier {
	final private Map<Term, Term> unifier;
	private boolean success;

	@Override
	public Term getValue(Term key) {
		if (unifier.containsKey(key)) {
			return getValue(unifier.get(key));
		} else {
			return key;
		}
	}

	@Override
	public boolean getSuccess() {
		return success;
	}

	/**
	 * An implementation of the Martelli & Montanari unification algorithm for
	 * predicate logic without function symbols.
	 * 
	 * @note that this algorithm is commutative.
	 *
	 * @param first          List of Literals to be unified
	 * @param second         List of Literals to be unified
	 * @param partialMapping a partial mapping of indexes from {@code first} to
	 *                       {@code second}.
	 */
	public MartelliMontanariUnifier(List<Literal> first, List<Literal> second, PartialMapping partialMapping) {
		unifier = new HashMap<>();
		success = true;
		for (Pair<Integer, Integer> image : partialMapping.getImages()) {
			unify((Literal) first.get(image.getX()), (Literal) second.get(image.getY()));
		}
	}

	private void unify(Literal first, Literal second) {
		if (success) {
			if (!first.getPredicate().equals(second.getPredicate()) || first.isNegated() != second.isNegated()) {
				success = false;
				return;
			}
			List<Term> terms1 = first.getArguments();
			List<Term> terms2 = second.getArguments();
			for (int i = 0; i < terms1.size(); i++) {
				unify(terms1.get(i), terms2.get(i));
			}
		}
	}

	private void unify(Term first, Term second) {
		if (first.isConstant() && second.isConstant()) {
			if (!first.equals(second)) {
				success = false;
			}
		} else if (first.isConstant() && second.isVariable()) {
			unify((Variable) second, (Constant) first);
		} else if (first.isVariable() && second.isConstant()) {
			unify((Variable) first, (Constant) second);
		} else {
			unify((Variable) first, (Variable) second);
		}
	}

	private void unify(Variable var, Constant cons) {
		if (unifier.containsKey(var)) {
			Term rep = getValue(var);
			if (rep.isVariable()) {
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

	private void unify(Variable first, Variable second) {
		Term rep1 = null;
		Term rep2 = null;
		if (unifier.containsKey(first)) {
			rep1 = getValue(first);
		}
		if (unifier.containsKey(second)) {
			rep2 = getValue(second);
		}
		// both variables have a representative
		if (rep1 != null && rep2 != null) {
			if (rep1.isVariable() && rep2.isVariable()) {
				if (!rep1.getName().equals(rep2.getName())) {
					putTwoNewVariables((Variable) rep1, (Variable) rep2);
				}
			} else if (rep1.isConstant() && rep2.isVariable()) {
				unifier.put(rep2, rep1);
			} else if (rep1.isVariable() && rep2.isConstant()) {
				unifier.put(rep1, rep2);
			} else {
				if (!rep1.getName().equals(rep2.getName())) {
					success = false;
				}
			}
		}
		// first has a representative, but second does not. we know that second is
		// variable
		else if (rep1 != null && rep2 == null) {
			if (rep1.isVariable()) {
				if (!rep1.getName().equals(second.getName())) {
					putOnewNewVariable(second, (Variable) rep1);
				}
			} else {
				unifier.put(second, rep1);
			}
		}
		// first does not have a representative, but second does.
		else if (rep1 == null && rep2 != null) {
			if (rep2.isVariable()) {
				if (!rep2.getName().equals(first.getName())) {
					putOnewNewVariable(first, (Variable) rep2);
				}
			} else {
				unifier.put(first, rep2);
			}
		}
		// both first and second does not have a representative
		else {
			putTwoNewVariables(first, second);
		}
	}

	private String getNewFreshVariableName() {
		return "FN-" + unifier.size();
	}

	private void putTwoNewVariables(Variable first, Variable second) {
		String newVarName = getNewFreshVariableName();
		if (first.getType() == TermType.EXISTENTIAL_VARIABLE) {
			unifier.put(first, Expressions.makeExistentialVariable(newVarName));
		} else {
			unifier.put(first, Expressions.makeUniversalVariable(newVarName));
		}
		if (second.getType() == TermType.EXISTENTIAL_VARIABLE) {
			unifier.put(second, Expressions.makeExistentialVariable(newVarName));
		} else {
			unifier.put(second, Expressions.makeUniversalVariable(newVarName));
		}
	}

	private void putOnewNewVariable(Variable newVariable, Variable presentVariable) {
		if (newVariable.getType() == TermType.EXISTENTIAL_VARIABLE) {
			unifier.put(newVariable, Expressions.makeExistentialVariable(presentVariable.getName()));
		} else {
			unifier.put(newVariable, Expressions.makeUniversalVariable(presentVariable.getName()));
		}
	}

	@Override
	public String toString() {
		return success + ", " + unifier;
	}

}
