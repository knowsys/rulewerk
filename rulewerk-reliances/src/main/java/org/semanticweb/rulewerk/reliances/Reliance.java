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

	/**
	 * Checker for positive reliance relation.
	 * 
	 * @param rule1
	 * @param rule2
	 * @return True if rule2 positively relies on rule1.
	 */
	static public boolean positively(Rule rule1, Rule rule2) {
		Rule renamedRule1 = VariableRenamer.rename(rule1, 1);
		Rule renamedRule2 = VariableRenamer.rename(rule2, 2);

		System.out.println("Rule 1: " + rule1);
		System.out.println("Rule 2: " + rule2);
		System.out.println("Renamed rule 1: " + renamedRule1);
		System.out.println("Renamed rule 2: " + renamedRule2);

		List<Literal> positiveBodyLiteralsRule1 = renamedRule1.getPositiveBodyLiterals();
//		List<Literal> negativeBodyLiteralsRule1 = renamedRule1.getNegativeBodyLiterals();
		List<Literal> headAtomsRule1 = renamedRule1.getHeadAtoms();
		List<Literal> positiveBodyLiteralsRule2 = renamedRule2.getPositiveBodyLiterals();
//		List<Literal> negativeBodyLiteralsRule2 = renamedRule2.getNegativeBodyLiterals();
		List<Literal> headAtomsRule2 = renamedRule2.getHeadAtoms();

//		System.out.println("positiveBodyLiteralsRule1: " + Arrays.toString(positiveBodyLiteralsRule1.toArray()));
//		System.out.println("negativeBodyLiteralsRule1: " + Arrays.toString(negativeBodyLiteralsRule1.toArray()));
//		System.out.println("headLiteralsRule1: " + Arrays.toString(headAtomsRule1.toArray()));
//		System.out.println("positiveBodyLiteralsRule2" + Arrays.toString(positiveBodyLiteralsRule2.toArray()));
//		System.out.println("negativeBodyLiteralsRule2" + Arrays.toString(negativeBodyLiteralsRule2.toArray()));
//		System.out.println("headLiteralsRule2: " + Arrays.toString(headAtomsRule2.toArray()));

		int sizeHead1 = headAtomsRule1.size();
		int sizePositiveBody2 = positiveBodyLiteralsRule2.size();

		/**
		 * Given two
		 */
		AssignmentIterable assignmentIterable = new AssignmentIterable(sizePositiveBody2, sizeHead1);

		for (Assignment assignment : assignmentIterable) {
//			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//			System.out.println("Assignment" + assignment);

			List<Integer> headLiterals11Idx = assignment.indexesInAssignedListToBeUnified();
			List<Integer> headLiterals12Idx = assignment.indexesInAssignedListToBeIgnored();
			List<Integer> positiveBodyLiterals21Idx = assignment.indexesInAssigneeListToBeUnified();
			List<Integer> positiveBodyLiterals22Idx = assignment.indexesInAssigneeListToBeIgnored();

//			System.out.println("headLiterals11Idx: " + Arrays.toString(headLiterals11Idx.toArray()));
//			System.out.println("headLiterals12Idx: " + Arrays.toString(headLiterals12Idx.toArray()));
//			System.out.println("positiveBodyLiterals21Idx: " + Arrays.toString(positiveBodyLiterals21Idx.toArray()));
//			System.out.println("positiveBodyLiterals22Idx: " + Arrays.toString(positiveBodyLiterals22Idx.toArray()));

			MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(positiveBodyLiteralsRule2, headAtomsRule1,
					assignment);
//			System.out.println(unifier);

			// RWU = renamed with unifier
			if (unifier.success) {
				List<Literal> positiveBodyLiterals1RWU = VariableRenamer.rename(positiveBodyLiteralsRule1, unifier);
//				List<Literal> negativeBodyLiterals1RWU = VariableRenamer.rename(negativeBodyLiteralsRule1, unifier);
				List<Literal> headLiterals1RWU = VariableRenamer.rename(headAtomsRule1, unifier);
				List<Literal> positiveBodyLiterals2RWU = VariableRenamer.rename(positiveBodyLiteralsRule2, unifier);
//				List<Literal> negativeBodyLiterals2RWU = VariableRenamer.rename(negativeBodyLiteralsRule2, unifier);
				List<Literal> headLiterals2RWU = VariableRenamer.rename(headAtomsRule2, unifier);

				Set<Literal> headLiterals11 = new HashSet<>();
				headLiterals11Idx.forEach(idx -> headLiterals11.add(headLiterals1RWU.get(idx)));

				Set<Literal> headLiterals12 = new HashSet<>();
				headLiterals12Idx.forEach(idx -> headLiterals12.add(headLiterals1RWU.get(idx)));

				Set<Literal> positiveBodyLiterals21 = new HashSet<>();
				positiveBodyLiterals21Idx.forEach(idx -> positiveBodyLiterals21.add(positiveBodyLiterals2RWU.get(idx)));

				Set<Literal> positiveBodyLiterals22 = new HashSet<>();
				positiveBodyLiterals22Idx.forEach(idx -> positiveBodyLiterals22.add(positiveBodyLiterals2RWU.get(idx)));

//				System.out.println("positiveBodyLiterals1: " + Arrays.toString(positiveBodyLiterals1RWU.toArray()));
//				System.out.println("headLiterals11: " + Arrays.toString(headLiterals11.toArray()));
//				System.out.println("headLiterals12: " + Arrays.toString(headLiterals12.toArray()));
//				System.out.println("positiveBodyLiterals21: " + Arrays.toString(positiveBodyLiterals21.toArray()));
//				System.out.println("positiveBodyLiterals22: " + Arrays.toString(positiveBodyLiterals22.toArray()));
//				System.out.println("headLiterals2RWU: " + Arrays.toString(headLiterals2RWU.toArray()));

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
