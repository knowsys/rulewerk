package org.semanticweb.rulewerk.reliances;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Variable;

public class Reliance {

	static private Set<String> getExistentialVariableNames(Set<Literal> literals) {
		Set<String> result = new HashSet<>();
		literals.forEach(literal -> literal.getExistentialVariables().forEach(extVar -> result.add(extVar.getName())));
		return result;
	}

	static private Set<String> getUniversalVariableNames(Set<Literal> literals) {
		Set<String> result = new HashSet<>();
		literals.forEach(literal -> literal.getUniversalVariables().forEach(uniVar -> result.add(uniVar.getName())));
		return result;
	}

	static private boolean shareAnyExistentialVariable(Set<Literal> head11, Set<Literal> body22) {
		Set<String> vars1 = getExistentialVariableNames(head11);
		Set<String> vars2 = getUniversalVariableNames(body22);
		Set<String> intersection = new HashSet<>(vars1); // copy constructor
		intersection.retainAll(vars2);
		return !intersection.isEmpty();
	}

	static private boolean universalVariableInPositionOfExistentialVariable(Set<Literal> head11, Set<Literal> body22) {
		Set<Predicate> predicatesWithExistentialVariables = new HashSet<>();
		for (Literal literal : head11) {
			Set<Variable> existentialVariables = literal.getExistentialVariables().collect(Collectors.toSet());
			if (!existentialVariables.isEmpty()) {
				predicatesWithExistentialVariables.add(literal.getPredicate());
			}
		}

		for (Literal literal : body22) {
			if (predicatesWithExistentialVariables.contains(literal.getPredicate())) {
				return true;
			}
		}
		return false;

	}

//	static private void print(String name, ArrayList<Literal> literals) {
//		String result = name + ": ";
//		for (Literal literal : literals)
//			result += literal + ", ";
//		System.out.println(result);
//	}
//
//	static private void print(String name, Set<Literal> literals) {
//		String result = name + ": ";
//		for (Literal literal : literals)
//			result += literal + ", ";
//		System.out.println(result);
//	}
//
//	static private void print(String name, int[] intArray) {
//		String base = name + ": [";
//		for (int i = 0; i < intArray.length; i++) {
//			base += intArray[i] + ",";
//		}
//		base += "]";
//		System.out.println(base);
//	}
//
//	static private void print(String name, List<Integer> list) {
//		String base = name + ": [";
//		for (int i = 0; i < list.size(); i++) {
//			base += list.get(i) + ",";
//		}
//		base += "]";
//		System.out.println(base);
//	}

	/*
	 * @return True if rule2 positively relies in rule1 $\arule_1\rpos\arule_2$
	 */
	static public boolean positively(Rule rule1, Rule rule2) {
		Rule firstRuleRenamedVariables = VariableRenamer.renameVariables(rule1, 1);
		Rule secondRuleRenamedVariables = VariableRenamer.renameVariables(rule2, 2);

//		System.out.println("rule 1: " + rule1);
//		System.out.println("rule 2: " + rule2);
//		System.out.println(firstRuleRenamedVariables);
//		System.out.println(secondRuleRenamedVariables);

		ArrayList<Literal> literalsInHead1 = new ArrayList<>();
		firstRuleRenamedVariables.getHead().getLiterals().forEach(literal -> literalsInHead1.add(literal));
//		print("literalsInHead1", literalsInHead1);
//
		ArrayList<Literal> literalsInBody2 = new ArrayList<>();
		for (Literal literal : secondRuleRenamedVariables.getBody().getLiterals()) {
			if (!literal.isNegated()) {
				literalsInBody2.add(literal);
			}
		}
//		secondRuleRenamedVariables.getBody().getLiterals().forEach(literal -> literalsInBody2.add(literal));
//		print("literalsInBody2", literalsInBody2);

		int sizeHead1 = literalsInHead1.size();
		int sizeBody2 = literalsInBody2.size();

		Assignment assignment = new Assignment(sizeBody2, sizeHead1);

		for (int[] match : assignment) {
//			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//			print("match", match);

			List<Integer> head11Idx = Assignment.head11Idx(sizeHead1, match);
			List<Integer> head12Idx = Assignment.head12Idx(sizeHead1, match);
			List<Integer> body21Idx = Assignment.body21Idx(sizeBody2, match);
			List<Integer> body22Idx = Assignment.body22Idx(sizeBody2, match);

//			print("head11Idx: ", head11Idx);
//			print("head12Idx: ", head12Idx);
//			print("body21Idx: ", body21Idx);
//			print("body22Idx: ", body22Idx);

			Unifier unifier = new Unifier(literalsInBody2, literalsInHead1, match);

			ArrayList<Literal> literalsInHead1RenamedWithUnifier = new ArrayList<>();
			literalsInHead1.forEach(literal -> literalsInHead1RenamedWithUnifier
					.add(VariableRenamer.renameVariables(literal, unifier)));
			ArrayList<Literal> literalsInBody2RenamedWithUnifier = new ArrayList<>();
			literalsInBody2.forEach(literal -> literalsInBody2RenamedWithUnifier
					.add(VariableRenamer.renameVariables(literal, unifier)));

//			System.out.println(unifier);
			if (unifier.success) {

				Set<Literal> head11 = new HashSet<>();
				head11Idx.forEach(idx -> head11.add(literalsInHead1RenamedWithUnifier.get(idx)));

				Set<Literal> head12 = new HashSet<>();
				head12Idx.forEach(idx -> head12.add(literalsInHead1RenamedWithUnifier.get(idx)));

				Set<Literal> body21 = new HashSet<>();
				body21Idx.forEach(idx -> body21.add(literalsInBody2RenamedWithUnifier.get(idx)));

				Set<Literal> body22 = new HashSet<>();
				body22Idx.forEach(idx -> body22.add(literalsInBody2RenamedWithUnifier.get(idx)));

//				print("head11: ", head11);
//				print("head12: ", head12);
//				print("body21: ", body21);
//				print("body22: ", body22);

				if (!shareAnyExistentialVariable(head11, body22)
						&& !universalVariableInPositionOfExistentialVariable(head11, body22)) {
					return true;
				}
			}
		}
		return false;
	}

}
