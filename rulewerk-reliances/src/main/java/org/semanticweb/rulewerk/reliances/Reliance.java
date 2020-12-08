package org.semanticweb.rulewerk.reliances;

import java.util.ArrayList;
//import java.util.Arrays;
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
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.utils.Filter;
import org.semanticweb.rulewerk.utils.LiteralList;

public class Reliance {

	static private boolean shareAnyExistentialVariable(List<Literal> head11, List<Literal> body22) {
		Set<String> vars1 = LiteralList.getExistentialVariableNames(head11);
		Set<String> vars2 = LiteralList.getUniversalVariableNames(body22);
		Set<String> intersection = new HashSet<>(vars1); // copy constructor
		intersection.retainAll(vars2);
		return !intersection.isEmpty();
	}

	static private boolean universalVariableInPositionOfExistentialVariable(List<Literal> head11,
			List<Literal> body22) {
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

	static private boolean isThereSomethingNew(List<Literal> headAtoms2, List<Literal> positiveBodyLiterals1,
			List<Literal> headAtoms1) {
		Set<Literal> copyHeadAtoms2 = new HashSet<>(headAtoms2);
		positiveBodyLiterals1.forEach(literal -> copyHeadAtoms2.remove(literal));
		headAtoms1.forEach(literal -> copyHeadAtoms2.remove(literal));
		return !copyHeadAtoms2.isEmpty();
	}

	/**
	 * Checker for positive reliance relation.
	 * 
	 * @param rule1
	 * @param rule2
	 * @return True if rule2 positively relies on rule1.
	 */
	static public boolean positively(Rule rule1, Rule rule2) {
		Rule renamedRule1 = SuffixBasedVariableRenamer.rename(rule1, 1);
		Rule renamedRule2 = SuffixBasedVariableRenamer.rename(rule2, 2);

		List<Literal> positiveBodyLiteralsRule1 = renamedRule1.getPositiveBodyLiterals();
		List<PositiveLiteral> headAtomsRule1 = renamedRule1.getHead().getLiterals();
		List<Literal> positiveBodyLiteralsRule2 = renamedRule2.getPositiveBodyLiterals();
		List<PositiveLiteral> headAtomsRule2 = renamedRule2.getHead().getLiterals();

		int sizeHead1 = headAtomsRule1.size();
		int sizePositiveBody2 = positiveBodyLiteralsRule2.size();

		AssignmentIterable assignmentIterable = new AssignmentIterable(sizePositiveBody2, sizeHead1);

		for (Assignment assignment : assignmentIterable) {

			List<Integer> headAtoms11Idx = assignment.indexesInAssignedListToBeUnified();
//			List<Integer> headAtoms12Idx = assignment.indexesInAssignedListToBeIgnored();
//			List<Integer> positiveBodyLiterals21Idx = assignment.indexesInAssigneeListToBeUnified();
			List<Integer> positiveBodyLiterals22Idx = assignment.indexesInAssigneeListToBeIgnored();

			MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(positiveBodyLiteralsRule2, headAtomsRule1,
					assignment);

			// RWU = renamed with unifier
			if (unifier.success) {
				UnifierBasedVariableRenamer renamer = new UnifierBasedVariableRenamer(unifier, true);

				List<Literal> positiveBodyLiteralsRule1RWU = new ArrayList<>();
				positiveBodyLiteralsRule1.forEach(literal -> positiveBodyLiteralsRule1RWU.add(renamer.rename(literal)));

				List<Literal> headAtomsRule1RWU = new ArrayList<>();
				headAtomsRule1.forEach(literal -> headAtomsRule1RWU.add(renamer.rename(literal)));

				List<Literal> positiveBodyLiteralsRule2RWU = new ArrayList<>();
				positiveBodyLiteralsRule2.forEach(literal -> positiveBodyLiteralsRule2RWU.add(renamer.rename(literal)));

				List<Literal> headAtomsRule2RWU = new ArrayList<>();
				headAtomsRule2.forEach(literal -> headAtomsRule2RWU.add(renamer.rename(literal)));

				List<Literal> headAtoms11 = Filter.indexBased(headAtomsRule1RWU, headAtoms11Idx);
//				List<Literal> headAtoms12 = Filter.indexBased(headAtomsRule1RWU, headAtoms12Idx);
//				List<Literal> positiveBodyLiterals21 = Filter.indexBased(positiveBodyLiteralsRule2RWU,
//						positiveBodyLiterals21Idx);
				List<Literal> positiveBodyLiterals22 = Filter.indexBased(positiveBodyLiteralsRule2RWU,
						positiveBodyLiterals22Idx);

				if (!shareAnyExistentialVariable(headAtoms11, positiveBodyLiterals22)
						&& !universalVariableInPositionOfExistentialVariable(headAtoms11, positiveBodyLiterals22)
						&& isThereSomethingNew(headAtomsRule2RWU, positiveBodyLiteralsRule1RWU, headAtomsRule1RWU)) {
					return true;
				}
			}
		}
		return false;
	}

}
