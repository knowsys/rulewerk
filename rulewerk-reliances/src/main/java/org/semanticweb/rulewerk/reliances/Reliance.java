package org.semanticweb.rulewerk.reliances;

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

	static private boolean thereIsSomethingNew(List<Literal> headLiterals2, List<Literal> positiveLiterals1,
			List<Literal> headLiterals1) {
		Set<Literal> copyHeadLiterals2 = new HashSet<>(headLiterals2);
		positiveLiterals1.forEach(literal -> copyHeadLiterals2.remove(literal));
		headLiterals1.forEach(literal -> copyHeadLiterals2.remove(literal));
		return !copyHeadLiterals2.isEmpty();
	}

//	static private <T> void print(String name, List<T> objects) {
//		String result = name + ": [";
//		for (T o : objects)
//			result += o + ", ";
//		result += "]";
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

	/*
	 * @return True if rule2 positively relies in rule1 $\arule_1\rpos\arule_2$
	 */
	static public boolean positively(Rule rule1, Rule rule2) {
		Rule renamedRule1 = VariableRenamer.rename(rule1, 1);
		Rule renamedRule2 = VariableRenamer.rename(rule2, 2);

//		System.out.println("rule 1: " + rule1);
//		System.out.println("rule 2: " + rule2);
//		System.out.println("rule 1 renamed: " + renamedRule1);
//		System.out.println("rule 2 renamed: " + renamedRule2);

		List<Literal> positiveBodyLiterals1 = renamedRule1.getPositiveBodyLiterals();
//		List<Literal> negativeBodyLiterals1 = renamedRule1.getNegativeBodyLiterals();
		List<Literal> headLiterals1 = renamedRule1.getHeadLiterals();
		List<Literal> positiveBodyLiterals2 = renamedRule2.getPositiveBodyLiterals();
//		List<Literal> negativeBodyLiterals2 = renamedRule2.getNegativeBodyLiterals();
		List<Literal> headLiterals2 = renamedRule2.getHeadLiterals();

//		print("positiveBodyLiterals1: ", positiveBodyLiterals1);
//		print("negativeBodyLiterals1: ", negativeBodyLiterals1);
//		print("headLiterals1: ", headLiterals1);
//		print("positiveBodyLiterals2", positiveBodyLiterals2);
//		print("negativeBodyLiterals2", negativeBodyLiterals2);
//		print("headLiterals2: ", headLiterals2);

		int sizeHead1 = headLiterals1.size();
		int sizePositiveBody2 = positiveBodyLiterals2.size();

		Assignment assignment = new Assignment(sizePositiveBody2, sizeHead1);

		for (int[] match : assignment) {
//			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//			print("match", match);

			// this could be improved
			List<Integer> headLiterals11Idx = Assignment.head11Idx(sizeHead1, match);
			List<Integer> headLiterals12Idx = Assignment.head12Idx(sizeHead1, match);
			List<Integer> positiveBodyLiterals21Idx = Assignment.body21Idx(sizePositiveBody2, match);
			List<Integer> positiveBodyLiterals22Idx = Assignment.body22Idx(sizePositiveBody2, match);

//			print("headLiterals11Idx: ", headLiterals11Idx);
//			print("headLiterals12Idx: ", headLiterals12Idx);
//			print("positiveBodyLiterals21Idx: ", positiveBodyLiterals21Idx);
//			print("positiveBodyLiterals22Idx: ", positiveBodyLiterals22Idx);

			Unifier unifier = new Unifier(positiveBodyLiterals2, headLiterals1, match);
//			System.out.println(unifier);

			// RWU = renamed with unifier
			if (unifier.success) {
				List<Literal> positiveBodyLiterals1RWU = VariableRenamer.rename(positiveBodyLiterals1, unifier);
//				List<Literal> negativeBodyLiterals1RWU = VariableRenamer.rename(negativeBodyLiterals1, unifier);
				List<Literal> headLiterals1RWU = VariableRenamer.rename(headLiterals1, unifier);
				List<Literal> positiveBodyLiterals2RWU = VariableRenamer.rename(positiveBodyLiterals2, unifier);
//				List<Literal> negativeBodyLiterals2RWU = VariableRenamer.rename(negativeBodyLiterals2, unifier);
				List<Literal> headLiterals2RWU = VariableRenamer.rename(headLiterals2, unifier);

				Set<Literal> headLiterals11 = new HashSet<>();
				headLiterals11Idx.forEach(idx -> headLiterals11.add(headLiterals1RWU.get(idx)));

				Set<Literal> headLiterals12 = new HashSet<>();
				headLiterals12Idx.forEach(idx -> headLiterals12.add(headLiterals1RWU.get(idx)));

				Set<Literal> positiveBodyLiterals21 = new HashSet<>();
				positiveBodyLiterals21Idx.forEach(idx -> positiveBodyLiterals21.add(positiveBodyLiterals2RWU.get(idx)));

				Set<Literal> positiveBodyLiterals22 = new HashSet<>();
				positiveBodyLiterals22Idx.forEach(idx -> positiveBodyLiterals22.add(positiveBodyLiterals2RWU.get(idx)));

//				print("positiveBodyLiterals1: ", positiveBodyLiterals1RWU);
//				print("headLiterals11: ", headLiterals11);
//				print("headLiterals12: ", headLiterals12);
//				print("positiveBodyLiterals21: ", positiveBodyLiterals21);
//				print("positiveBodyLiterals22: ", positiveBodyLiterals22);
//				print("headLiterals2RWU: ", headLiterals2RWU);

//				System.out.println(!shareAnyExistentialVariable(headLiterals11, positiveBodyLiterals22));
//				System.out.println(
//						!universalVariableInPositionOfExistentialVariable(headLiterals11, positiveBodyLiterals22));
//				System.out.println(thereIsSomethingNew(headLiterals2RWU, positiveBodyLiterals1RWU, headLiterals1RWU));
				if (!shareAnyExistentialVariable(headLiterals11, positiveBodyLiterals22)
						&& !universalVariableInPositionOfExistentialVariable(headLiterals11, positiveBodyLiterals22)
						&& thereIsSomethingNew(headLiterals2RWU, positiveBodyLiterals1RWU, headLiterals1RWU)) {
					return true;
				}
			}
		}
		return false;
	}

}
