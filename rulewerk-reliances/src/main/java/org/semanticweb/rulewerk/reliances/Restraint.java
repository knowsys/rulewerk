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
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class Restraint {

	// TODO create class to instantiate rule and unify.
	// TODO unify this with SelfRestraint
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

	static private Set<ExistentialVariable> getExistentialVariables(List<Literal> literals) {
		Set<ExistentialVariable> result = new HashSet<>();
		literals.forEach(literal -> literal.getExistentialVariables().forEach(extVar -> result.add(extVar)));
		return result;
	}

	static private boolean isRule1Applicable(Rule rule1RWU, Rule rule2RWU) {
		List<Literal> instance = new ArrayList<>();
		List<Literal> query = new ArrayList<>();
		rule1RWU.getPositiveBodyLiterals().forEach(literal -> instance.add(instantiate(literal)));
		rule1RWU.getHead().getLiterals().forEach(literal -> query.add(instantiateQuery(literal)));
		rule2RWU.getPositiveBodyLiterals().forEach(literal -> instance.add(instantiate(literal)));
		rule2RWU.getHead().getLiterals().forEach(literal -> instance.add(instantiate(literal)));

		return !SBCQ.query(instance, query);
	}

	static private boolean isheadAtoms21mappableToheadAtoms11(List<Literal> headAtoms11, List<Literal> headAtoms21) {
		List<Literal> instance = new ArrayList<>();
		List<Literal> query = new ArrayList<>();
		headAtoms11.forEach(literal -> instance.add(instantiate(literal)));
		headAtoms21.forEach(literal -> query.add(instantiateQuery(literal)));

		return SBCQ.query(instance, query);
	}

	/**
	 * 
	 * @return true if an universal variable from head21 is being mapped into an
	 *         existential variable from head11, which makes the unifier invalid.
	 */
	static private boolean mappingUniversalintoExistential(List<PositiveLiteral> headAtomsRule2,
			List<PositiveLiteral> headAtomsRule1, Assignment assignment) {

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

	static List<ExistentialVariable> filter(List<ExistentialVariable> original, int[] combination) {
		List<ExistentialVariable> result = new ArrayList<>();
		for (int i = 0; i < combination.length; i++) {
			if (combination[i] == 1) {
				result.add(original.get(i));
			}
		}
		return result;
	}

	static List<Literal> literalsContainingVariables(List<Literal> literals, List<ExistentialVariable> variables) {
		List<Literal> result = new ArrayList<>();

		for (Literal literal : literals) {
			for (ExistentialVariable extVar : variables) {
				if (literal.getExistentialVariables().anyMatch(containedVar -> containedVar.equals(extVar))) {
					result.add(literal);
					break;
				}
			}
		}
		return result;
	}

	// this must be true to have a restrain
	static private boolean conditionForExistentialVariables(List<Literal> headAtomsRule2, List<Literal> headAtomsRule1,
			List<Literal> headAtoms22, Assignment assignment) {

		Set<ExistentialVariable> extVarsIn22 = getExistentialVariables(headAtoms22);

		for (Match match : assignment.getMatches()) {

			List<Term> origin = headAtomsRule2.get(match.getOrigin()).getArguments();
			List<Term> destination = headAtomsRule1.get(match.getDestination()).getArguments();

			for (int i = 0; i < origin.size(); i++) {
				if (origin.get(i).getType() == TermType.EXISTENTIAL_VARIABLE
						&& destination.get(i).getType() == TermType.EXISTENTIAL_VARIABLE
						&& extVarsIn22.contains(origin.get(i))) {
					return false;
				}
			}
		}

		return true;
	}

	static boolean containsAnyExistential(List<Term> first, List<ExistentialVariable> second) {
		for (Term t : second) {
			if (first.contains(t)) {
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

		// if rule2 is Datalog, it can not be restrained
		if (rule2.getExistentialVariables().count() == 0) {
			return false;
		}

		if (rule1.equals(rule2)) {
			return SelfRestraint.restraint(rule1);
		}

		Rule renamedRule1 = SuffixBasedVariableRenamer.rename(rule1, 1);
		Rule renamedRule2 = SuffixBasedVariableRenamer.rename(rule2, 2);

		List<PositiveLiteral> headAtomsRule1 = renamedRule1.getHead().getLiterals();
		List<PositiveLiteral> headAtomsRule2 = renamedRule2.getHead().getLiterals();

		/* In order to decide what to match with what, get the size of each head. */
		int headSizeRule1 = headAtomsRule1.size();
		int headSizeRule2 = headAtomsRule2.size();

		/*
		 * Mapping from atom indexes in head(rule2) to atom indexes in head(rule1).
		 */
		AssignmentIterable assignmentIterable = new AssignmentIterable(headSizeRule2, headSizeRule1);

		for (Assignment assignment : assignmentIterable) {

			List<Integer> headAtoms11Idx = assignment.indexesInAssignedListToBeUnified();
			List<Integer> headAtoms12Idx = assignment.indexesInAssignedListToBeIgnored();
			List<Integer> headAtoms21Idx = assignment.indexesInAssigneeListToBeUnified();
			List<Integer> headAtoms22Idx = assignment.indexesInAssigneeListToBeIgnored();

			MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(headAtomsRule2, headAtomsRule1, assignment);

			// RWU = renamed with unifier
			if (unifier.success) {
				UnifierBasedVariableRenamer renamer = new UnifierBasedVariableRenamer(unifier, false);

				// rename everything
				Rule rule1RWU = renamer.rename(renamedRule1);
				Rule rule2RWU = renamer.rename(renamedRule2);

				List<Literal> headAtomsRule1RWU = new ArrayList<>();
				headAtomsRule1.forEach(literal -> headAtomsRule1RWU.add(renamer.rename(literal)));

				List<Literal> headAtomsRule2RWU = new ArrayList<>();
				headAtomsRule2.forEach(literal -> headAtomsRule2RWU.add(renamer.rename(literal)));

				List<Literal> headAtoms11 = new ArrayList<>();
				headAtoms11Idx.forEach(idx -> headAtoms11.add(headAtomsRule1RWU.get(idx)));

				List<Literal> headAtoms12 = new ArrayList<>();
				headAtoms12Idx.forEach(idx -> headAtoms12.add(headAtomsRule1RWU.get(idx)));

				List<Literal> headAtoms21 = new ArrayList<>();
				headAtoms21Idx.forEach(idx -> headAtoms21.add(headAtomsRule2RWU.get(idx)));

				List<Literal> headAtoms22 = new ArrayList<>();
				headAtoms22Idx.forEach(idx -> headAtoms22.add(headAtomsRule2RWU.get(idx)));

				if (isRule1Applicable(rule1RWU, rule2RWU)
						&& !mappingUniversalintoExistential(headAtomsRule2, headAtomsRule1, assignment)
						&& conditionForExistentialVariables(headAtomsRule2RWU, headAtomsRule1RWU, headAtoms22,
								assignment)
						&& isheadAtoms21mappableToheadAtoms11(headAtoms11, headAtoms21)) {
					return true;
				}

			}
		}

		return false;
	}

}
