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

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.math.mapping.Pair;
import org.semanticweb.rulewerk.math.mapping.PartialMapping;
import org.semanticweb.rulewerk.utils.LiteralList;

/**
 * An implementation for the Respecting Unifier described in [citation needed].
 * {@code ExistentialVariable}s might appear only in the second list of
 * {@code Literal}s and make up the respecting variables. During the unification
 * process, these {@code ExistentialVariable}s are treated as {@code Constant}s.
 * 
 * @author Larry González
 *
 */
// TODO
public class RespectingUnifier implements Unifier {
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
	 * @TODO update doc
	 *
	 * @param first          List of Literals to be unified
	 * @param second         List of Literals to be unified
	 * @param partialMapping a partial mapping of indexes from {@code first} to
	 *                       {@code second}.
	 */
	public RespectingUnifier(List<? extends Literal> first, List<? extends Literal> second, PartialMapping partialMapping) {
		Validate.isTrue(LiteralList.getExistentialVariables(first).isEmpty());
		this.unifier = new HashMap<>();
		this.success = true;
		for (Pair<Integer, Integer> pair : partialMapping.getImages()) {
			unify(first.get(pair.getX()), second.get(pair.getY()));
		}
	}

	private void unify(Literal first, Literal second) {
		if (success) {
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
	}

	private void unify(Term first, Term second) {
		if (success) {
			if (first.isConstant()) {
				if (second.isConstant()) {
					doUnify((Constant) first, (Constant) second);
				} else if (second.isExistentialVariable()) {
					unify((Constant) first, (ExistentialVariable) second);
				} else if (second.isUniversalVariable()) {
					unify((UniversalVariable) second, (Constant) first);
				}
			} else if (first.isExistentialVariable()) {
				success = false; // TODO look here. We might need the reason why success = false
			} else if (first.isUniversalVariable()) {
				if (second.isConstant()) {
					unify((UniversalVariable) first, (Constant) second);
				} else if (second.isExistentialVariable()) {
					unify((UniversalVariable) first, (ExistentialVariable) second);
				} else if (second.isUniversalVariable()) {
					unify((UniversalVariable) first, (UniversalVariable) second);
				}
			}
		}
	}

	private void unify(Constant first, ExistentialVariable second) {
		success = false;
	}

	private void unify(UniversalVariable var, Constant cons) {
		if (unifier.containsKey(var)) {
			Term rep = getValue(var);
			if (rep.isConstant()) {
				if (!rep.equals(cons)) {
					success = false;
				}
			} else if (rep.isExistentialVariable()) {
				success = false;
			} else if (rep.isUniversalVariable()) {
				unifier.put(rep, cons);
			}
		} else {
			unifier.put(var, cons);
		}
	}

	private void unify(UniversalVariable first, ExistentialVariable second) {
		if (unifier.containsKey(first)) {
			Term rep = getValue(first);
			if (rep.isConstant()) {
				success = false;
			} else if (rep.isExistentialVariable()) {
				if (!rep.equals(second)) {
					success = false;
				}
			} else if (rep.isUniversalVariable()) {
				unifier.put(rep, second);
			}
		} else {
			unifier.put(first, second);
		}
	}

	private void unify(UniversalVariable first, UniversalVariable second) {
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
//			if (rep1.isVariable() && rep2.isVariable()) {
//				if (!rep1.getName().equals(rep2.getName())) {
//					putTwoNewVariables((Variable) rep1, (Variable) rep2);
//				}
//			} else if (rep1.isConstant() && rep2.isVariable()) {
//				unifier.put(rep2, rep1);
//			} else if (rep1.isVariable() && rep2.isConstant()) {
//				unifier.put(rep1, rep2);
//			} else {
//				if (!rep1.getName().equals(rep2.getName())) {
//					success = false;
//				}
//			}
			if (rep1.isConstant()) {
				if (rep2.isConstant()) {
					doUnify((Constant) rep1, (Constant) rep2);
				} else if (rep2.isExistentialVariable()) {
					success = false;
				} else if (rep2.isUniversalVariable()) {
					unifier.put(rep2, rep1);
				}
			} else if (rep1.isExistentialVariable()) {
				if (rep2.isConstant()) {
					success = false;
				} else if (rep2.isExistentialVariable()) {
					success = false;
				} else if (rep2.isUniversalVariable()) {
					unifier.put(rep2, rep1);
				}
			} else if (rep1.isUniversalVariable()) {
				if (rep2.isConstant()) {
					unifier.put(rep1, rep2);
				} else if (rep2.isExistentialVariable()) {
					unifier.put(rep1, rep2);
				} else if (rep2.isUniversalVariable()) {
					putTwoNewVariables((Variable) rep1, (Variable) rep2);
				}
			}
		}

		// first has a representative, but second does not. we know that second is
		// UniversalVariable
		else if (rep1 != null && rep2 == null) {
			if (rep1.isConstant()) {
				unifier.put(second, rep1);
			} else if (rep1.isExistentialVariable()) {
				unifier.put(second, rep1);
			} else if (rep1.isUniversalVariable()) {
				putTwoNewVariables((Variable) rep1, second);
			}
		}

		// first does not have a representative, but second does. first is universal
		else if (rep1 == null && rep2 != null) {
			if (rep2.isConstant()) {
				unifier.put(first, rep2);
			} else if (rep2.isExistentialVariable()) {
				unifier.put(first, rep2);
			} else if (rep2.isUniversalVariable()) {
				putTwoNewVariables((Variable) rep2, first);
			}
		}

		// both first and second does not have a representative
		else {
			putTwoNewVariables(first, second);
		}
	}

	private void doUnify(Constant first, Constant second) {
		if (!first.equals(second)) {
			success = false;
		}
	}

	private String getNewFreshVariableName() {
		return "FN-" + unifier.size();
	}

	private void putTwoNewVariables(Variable first, Variable second) {
		String newVarName = getNewFreshVariableName();
		if (first.isExistentialVariable()) {
			unifier.put(first, Expressions.makeExistentialVariable(newVarName));
		} else {
			unifier.put(first, Expressions.makeUniversalVariable(newVarName));
		}
		if (second.isExistentialVariable()) {
			unifier.put(second, Expressions.makeExistentialVariable(newVarName));
		} else {
			unifier.put(second, Expressions.makeUniversalVariable(newVarName));
		}
	}

//	private void putOnewNewVariable(Variable newVariable, Variable presentVariable) {
//		if (newVariable.isExistentialVariable()) {
//			unifier.put(newVariable, Expressions.makeExistentialVariable(presentVariable.getName()));
//		} else {
//			unifier.put(newVariable, Expressions.makeUniversalVariable(presentVariable.getName()));
//		}
//	}

	@Override
	public String toString() {
		return success + ", " + unifier;
	}

}
