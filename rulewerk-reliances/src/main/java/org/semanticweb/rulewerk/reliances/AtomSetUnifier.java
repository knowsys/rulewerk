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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;

public class LiteralSetUnifier {

	// Variable name to term name
	final HashMap<String, String> unifier = new HashMap<>();
	boolean success = true;

	public LiteralSetUnifier(Set<Literal> set1, Set<Literal> set2) {
		if (set1.size() != set2.size()) {
			success = false;
			return;
		}

		Predicate predicate;
		HashMap<Predicate, Integer> predicatesInSet1 = new HashMap<>();
		HashMap<Predicate, Integer> predicatesInSet2 = new HashMap<>();

		for (Literal literal : set1) {
			predicate = literal.getPredicate();
			if (predicatesInSet1.containsKey(predicate)) {
				predicatesInSet1.put(predicate, predicatesInSet1.get(predicate) + 1);
			} else {
				predicatesInSet1.put(predicate, 1);
			}
		}

		for (Literal literal : set2) {
			predicate = literal.getPredicate();
			if (predicatesInSet2.containsKey(predicate)) {
				predicatesInSet2.put(predicate, predicatesInSet2.get(predicate) + 1);
			} else {
				predicatesInSet2.put(predicate, 1);
			}
		}

		if (!predicatesInSet1.equals(predicatesInSet2)) {
			success = false;
			return;
		}

		System.out.println("calling unify set1, set2");
		System.out.println(set1);
		System.out.println(set2);
		unify(set1, set2);
	}

	public void print() {
		System.out.println("{");
		for (String key : unifier.keySet()) {
			System.out.println(key + ":" + unifier.get(key));
		}
		System.out.println("}");
	}

	private String getNewFreshVariableName() {
		return "NewFreshVariable-" + unifier.size();
	}

	private void unify(Variable var, Constant cons) {
		if (unifier.containsKey(var.getName()) && cons.getName() != unifier.get(var.getName())) {
			success = false;
		} else {
			unifier.putIfAbsent(var.getName(), cons.getName());
		}
	}

	private void unify(Variable var1, Variable var2) {
		String vn1 = var1.getName();
		String vn2 = var2.getName();
		if (unifier.containsKey(vn1) && unifier.containsKey(vn2)) {
			if (unifier.get(vn1) != unifier.get(vn2)) {
				success = false;
			}
		} else if (!unifier.containsKey(vn1) && unifier.containsKey(vn2)) {
			unifier.put(vn1, unifier.get(vn2));
		} else if (unifier.containsKey(vn1) && !unifier.containsKey(vn2)) {
			unifier.put(vn2, unifier.get(vn1));
		} else {
			String newVarName = getNewFreshVariableName();
			unifier.put(vn1, newVarName);
			unifier.put(vn2, newVarName);
		}
	}

	private void unify(Term term1, Term term2) {
		if (term1.isConstant() && term2.isConstant() && !term1.equals(term2)) {
			success = false;
		} else if (term1.isConstant() && term2.isVariable()) {
			unify((Variable) term2, (Constant) term1);
		} else if (term1.isVariable() && term2.isConstant()) {
			unify((Variable) term1, (Constant) term2);
		} else {
			unify((Variable) term1, (Variable) term2);
		}
	}

	public void unify(Literal literal1, Literal literal2) {
		if (literal1.isNegated() != literal2.isNegated() || !literal1.getPredicate().equals(literal2.getPredicate())) {
			success = false;
			return;
		}
		List<Term> terms1 = literal1.getArguments();
		List<Term> terms2 = literal2.getArguments();
		for (int i = 0; i < terms1.size(); i++) {
			unify(terms1.get(i), terms2.get(i));
		}
	}

	private void unify(Set<Literal> set1, Set<Literal> set2) {
		Set<List<Literal>> pairs = getPairsOfLiterals(set1, set2);

		for (List<Literal> pair : pairs) {
			if (success) {
				unify(pair.get(0), pair.get(1));
			}
		}
	}

	private Set<List<Literal>> getPairsOfLiterals(Set<Literal> set1, Set<Literal> set2) {
		Set<Literal> copy2 = new HashSet<>(set2);

		Set<List<Literal>> result = new HashSet<>();
		for (Literal lit1 : set1) {
			Predicate p1 = lit1.getPredicate();
			for (Literal lit2 : copy2) {
				Predicate p2 = lit2.getPredicate();
				if (p1.equals(p2)) {
					System.out.println("equal");
					Set<Literal> adding = new HashSet<>();
					adding.add(lit1);
					adding.add(lit2);
					result.add(Arrays.asList(lit1, lit2));
					copy2.remove(lit2);
					break;
				}
			}
		}
		// System.out.println("result: " + result);
		return result;
	}

}
