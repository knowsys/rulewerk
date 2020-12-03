package org.semanticweb.rulewerk.reliances;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;

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
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class Restraint {

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

	static private Set<ExistentialVariable> getExistentialVariables(Set<Literal> literals) {
		Set<ExistentialVariable> result = new HashSet<>();
		literals.forEach(literal -> literal.getExistentialVariables().forEach(extVar -> result.add(extVar)));
		return result;
	}

	static private boolean isRule1Applicable(Rule rule1RWU, Rule rule2RWU) {
		List<Literal> instance = new ArrayList<>();
		List<Literal> query = new ArrayList<>();
		rule1RWU.getPositiveBodyLiterals().forEach(literal -> instance.add(instantiate(literal)));
		rule1RWU.getHeadAtoms().forEach(literal -> query.add(instantiateQuery(literal)));
		rule2RWU.getPositiveBodyLiterals().forEach(literal -> instance.add(instantiate(literal)));
		rule2RWU.getHeadAtoms().forEach(literal -> instance.add(instantiate(literal)));

//		System.out.println("instance: " + instance);
//		System.out.println("query:    " + query);
		return !SBCQ.query(instance, query);
	}

	static private boolean isheadAtoms21mappableToheadAtoms11(Set<Literal> headAtoms11, Set<Literal> headAtoms21) {
		List<Literal> instance = new ArrayList<>();
		List<Literal> query = new ArrayList<>();
		headAtoms11.forEach(literal -> instance.add(instantiate(literal)));
		headAtoms21.forEach(literal -> query.add(instantiateQuery(literal)));

//		System.out.println("instance: " + instance);
//		System.out.println("query:    " + query);
		return SBCQ.query(instance, query);
	}

	/**
	 * 
	 * @return true if an universal variable from head21 is being mapped into an
	 *         existential variable from head11, which makes the unifier invalid.
	 */
	static private boolean mappingUniversalintoExistential(List<Literal> headAtomsRule2, List<Literal> headAtomsRule1,
			Assignment assignment) {

		for (Match match : assignment.getMatches()) {
			List<Term> fromHead2 = headAtomsRule2.get(match.getOrigin()).getArguments();
			List<Term> fromHead1 = headAtomsRule1.get(match.getDestination()).getArguments();

			for (int i = 0; i < fromHead2.size(); i++) {
				if (fromHead2.get(i).getType() == TermType.UNIVERSAL_VARIABLE
						&& fromHead1.get(i).getType() == TermType.EXISTENTIAL_VARIABLE) {
					return true;
				}
			}
		}
		return false;
	}

	static private boolean existentialInHead21AppearsInHead22(Set<Literal> headAtoms21, Set<Literal> headAtoms22) {
		Set<ExistentialVariable> existentialVariablesInHeadAtoms22 = getExistentialVariables(headAtoms22);

		for (ExistentialVariable var : getExistentialVariables(headAtoms21)) {
			if (existentialVariablesInHeadAtoms22.contains(var)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checker for restraining relation.
	 * 
	 * @param rule1
	 * @param rule2
	 * @return True if rule1 restraints rule1.
	 */
	static public boolean restraint(Rule rule1, Rule rule2) {

		if (rule1.equals(rule2) && SelfRestraint.restraint(rule1)) {
			return true;
		}

		Rule renamedRule1 = SuffixBasedVariableRenamer.rename(rule1, 1);
		Rule renamedRule2 = SuffixBasedVariableRenamer.rename(rule2, 2);

//		System.out.println("Rule 1: " + rule1);
//		System.out.println("Rule 2: " + rule2);
//		System.out.println();
//		System.out.println("Renamed Rule 1: " + renamedRule1);
//		System.out.println("Renamed Rule 2: " + renamedRule2);
//		System.out.println();

		/* Get the list of Literals/Atoms from the rules. */
//		List<Literal> positiveBodyLiteralsRule1 = renamedRule1.getPositiveBodyLiterals();
//		List<Literal> negativeBodyLiteralsRule1 = renamedRule1.getNegativeBodyLiterals();
		List<Literal> headAtomsRule1 = renamedRule1.getHeadAtoms();
//		List<Literal> positiveBodyLiteralsRule2 = renamedRule2.getPositiveBodyLiterals();
//		List<Literal> negativeBodyLiteralsRule2 = renamedRule2.getNegativeBodyLiterals();
		List<Literal> headAtomsRule2 = renamedRule2.getHeadAtoms();

//		System.out.println("Rule1: ");
//		System.out.println("positiveBodyLiteralsRule1: " + Arrays.toString(positiveBodyLiteralsRule1.toArray()));
//		System.out.println("negativeBodyLiteralsRule1: " + Arrays.toString(negativeBodyLiteralsRule1.toArray()));
//		System.out.println("headAtomsRule1: " + Arrays.toString(headAtomsRule1.toArray()));
//		System.out.println();
//		System.out.println("Rule2: ");
//		System.out.println("positiveBodyLiteralsRule2: " + Arrays.toString(positiveBodyLiteralsRule2.toArray()));
//		System.out.println("negativeBodyLiteralsRule2: " + Arrays.toString(negativeBodyLiteralsRule2.toArray()));
//		System.out.println("headAtomsRule2: " + Arrays.toString(headAtomsRule2.toArray()));
//		System.out.println();

		/* In order to decide what to match with what, get the size of each head. */
		int headSizeRule1 = headAtomsRule1.size();
		int headSizeRule2 = headAtomsRule2.size();

		/*
		 * Mapping from atom indexes in head(rule2) to atom indexes in head(rule1).
		 */
		AssignmentIterable assignmentIterable = new AssignmentIterable(headSizeRule2, headSizeRule1);

		for (Assignment assignment : assignmentIterable) {
//			System.out.println("Assignment: " + assignment);

			List<Integer> headAtoms11Idx = assignment.indexesInAssignedListToBeUnified();
			List<Integer> headAtoms12Idx = assignment.indexesInAssignedListToBeIgnored();
			List<Integer> headAtoms21Idx = assignment.indexesInAssigneeListToBeUnified();
			List<Integer> headAtoms22Idx = assignment.indexesInAssigneeListToBeIgnored();

//			System.out.println("headAtoms11Idx: " + Arrays.toString(headAtoms11Idx.toArray()));
//			System.out.println("headAtoms12Idx: " + Arrays.toString(headAtoms12Idx.toArray()));
//			System.out.println("headAtoms21Idx: " + Arrays.toString(headAtoms21Idx.toArray()));
//			System.out.println("headAtoms22Idx: " + Arrays.toString(headAtoms22Idx.toArray()));

			MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(headAtomsRule2, headAtomsRule1, assignment);
//			System.out.println("Unifier: " + unifier);

			// RWU = renamed with unifier
			if (unifier.success) {
				UnifierBasedVariableRenamer renamer = new UnifierBasedVariableRenamer(unifier, false);

				// rename everything
				Rule rule1RWU = renamer.rename(renamedRule1);
				Rule rule2RWU = renamer.rename(renamedRule2);
//				System.out.println("RWU Rule1: " + rule1RWU);
//				System.out.println("RWU Rule2: " + rule2RWU);

//				List<Literal> positiveBodyLiteralsRule1RWU = renamer.rename(positiveBodyLiteralsRule1);
//				List<Literal> negativeBodyLiteralsRule1RWU = renamer.rename(negativeBodyLiteralsRule2);
				List<Literal> headAtomsRule1RWU = renamer.rename(headAtomsRule1);
//				List<Literal> positiveBodyLiteralsRule2RWU = renamer.rename(positiveBodyLiteralsRule2);
//				List<Literal> negativeBodyLiteralsRule2RWU = renamer.rename(negativeBodyLiteralsRule2);
				List<Literal> headAtomsRule2RWU = renamer.rename(headAtomsRule2);

				// check if we can use Lists here
				Set<Literal> headAtoms11 = new HashSet<>();
				headAtoms11Idx.forEach(idx -> headAtoms11.add(headAtomsRule1RWU.get(idx)));

				Set<Literal> headAtoms12 = new HashSet<>();
				headAtoms12Idx.forEach(idx -> headAtoms12.add(headAtomsRule1RWU.get(idx)));

				Set<Literal> headAtoms21 = new HashSet<>();
				headAtoms21Idx.forEach(idx -> headAtoms21.add(headAtomsRule2RWU.get(idx)));

				Set<Literal> headAtoms22 = new HashSet<>();
				headAtoms22Idx.forEach(idx -> headAtoms22.add(headAtomsRule2RWU.get(idx)));

//				System.out.println("Rule1: ");
//				System.out.println(
//						"positiveBodyLiteralsRule1: " + Arrays.toString(positiveBodyLiteralsRule1RWU.toArray()));
//				System.out.println(
//						"negativeBodyLiteralsRule1: " + Arrays.toString(negativeBodyLiteralsRule1RWU.toArray()));
//				System.out.println("headAtoms11: " + Arrays.toString(headAtoms11.toArray()));
//				System.out.println("headAtoms12: " + Arrays.toString(headAtoms12.toArray()));
//				System.out.println();
//				System.out.println("Rule2: ");
//				System.out.println(
//						"positiveBodyLiteralsRule2: " + Arrays.toString(positiveBodyLiteralsRule2RWU.toArray()));
//				System.out.println(
//						"negativeBodyLiteralsRule2: " + Arrays.toString(negativeBodyLiteralsRule2RWU.toArray()));
//				System.out.println("headAtoms21: " + Arrays.toString(headAtoms21.toArray()));
//				System.out.println("headAtoms22: " + Arrays.toString(headAtoms22.toArray()));
//				System.out.println();

				if (isRule1Applicable(rule1RWU, rule2RWU)
						&& !mappingUniversalintoExistential(headAtomsRule2, headAtomsRule1, assignment)
						&& !existentialInHead21AppearsInHead22(headAtoms21, headAtoms22)
						&& isheadAtoms21mappableToheadAtoms11(headAtoms11, headAtoms21)) {
//					System.out.println(Arrays.toString(headAtoms11.toArray()));
//					System.out.println(Arrays.toString(headAtoms12.toArray()));
//					System.out.println(Arrays.toString(headAtoms21.toArray()));
//					System.out.println(Arrays.toString(headAtoms22.toArray()));
//					System.out.println(unifier);
					return true;
				}

			}
//			System.out.println();
		}

		return false;
	}

}
