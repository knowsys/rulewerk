package org.semanticweb.rulewerk.reliances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.semanticweb.rulewerk.utils.Filter;
import org.semanticweb.rulewerk.utils.LiteralList;
import org.semanticweb.rulewerk.utils.RuleUtil;
import org.semanticweb.rulewerk.utils.SubsetIterable;

public class Restraint {

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

	// must be true
	static private boolean mapExt2ExtOrExt2Uni(List<Literal> headAtomsRule2, List<Literal> headAtomsRule1,
			List<Literal> headAtoms22, Assignment assignment) {
		Set<ExistentialVariable> extVarsIn22 = LiteralList.getExistentialVariables(headAtoms22);
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

	// must be true
	static private boolean mapExistentialsToTheSame(List<Literal> headRule2, List<Literal> headRule1,
			Assignment assignment) {
		Map<ExistentialVariable, Term> map = new HashMap<>();
		for (Match match : assignment.getMatches()) {
			List<Term> origin = headRule2.get(match.getOrigin()).getArguments();
			List<Term> destination = headRule1.get(match.getDestination()).getArguments();
			for (int i = 0; i < origin.size(); i++) {
				if (origin.get(i).getType() == TermType.EXISTENTIAL_VARIABLE) {
					if (map.containsKey(origin.get(i))) {
						if (map.get(origin.get(i)).equals(destination.get(i))) {
							//
						} else {
							return false;
						}
					} else {
						map.put((ExistentialVariable) origin.get(i), destination.get(i));
					}
				}
			}
		}
		return true;
	}

	/**
	 * Checker for restraining relation.
	 * 
	 * @param rule1
	 * @param rule2
	 * @return True if rule1 restraints rule2.
	 */
	static public boolean restraint(Rule rule1, Rule rule2) {
		// if rule2 is Datalog, it can not be restrained
		if (rule2.getExistentialVariables().count() == 0) {
			return false;
		}

		if (!RuleUtil.isRuleApplicable(rule2)) {
			return false;
		}

		if (rule2.containsUnconnectedPieces()) {
			return true;
		}

		if (rule1.equals(rule2)) {
			return SelfRestraint.restraint(rule1);
		}

		Rule renamedRule1 = SuffixBasedVariableRenamer.rename(rule1, rule1.hashCode() + 1);
		Rule renamedRule2 = SuffixBasedVariableRenamer.rename(rule2, rule2.hashCode() + 2);

		List<PositiveLiteral> headAtomsRule1 = renamedRule1.getHead().getLiterals();
		List<PositiveLiteral> headAtomsRule2 = renamedRule2.getHead().getLiterals();

		List<ExistentialVariable> existentialVariables = renamedRule2.getExistentialVariables()
				.collect(Collectors.toList());

		// to avoid duplicate computation
		Set<Assignment> testedAssignment = new HashSet<>();

		// Iterate over all subsets of existentialVariables
		for (List<ExistentialVariable> extVarComb : new SubsetIterable<ExistentialVariable>(existentialVariables)) {

			if (extVarComb.size() > 0) {
				List<Integer> literalsContainingExtVarsIdxs = LiteralList
						.idxOfLiteralsContainingExistentialVariables(headAtomsRule2, extVarComb);
				// Iterate over all subsets of literalsContainingExtVarsIdxs. Because it could
				// be that we need to match only one of the literals
				for (List<Integer> literaltoUnifyIdx : new SubsetIterable<Integer>(literalsContainingExtVarsIdxs)) {

					if (literaltoUnifyIdx.size() > 0) {
						AssignmentIterable assignmentIterable = new AssignmentIterable(literaltoUnifyIdx.size(),
								headAtomsRule1.size());
						// Iterate over all possible assignments of those Literals
						for (Assignment assignment : assignmentIterable) {

							// We transform the assignment to keep the old indexes
							Assignment transformed = new Assignment(assignment, literaltoUnifyIdx,
									headAtomsRule2.size());

							if (!testedAssignment.contains(transformed)) {
								testedAssignment.add(transformed);

								List<Integer> headAtoms22Idx = transformed.indexesInAssigneeListToBeIgnored();
								MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(headAtomsRule2,
										headAtomsRule1, transformed);
								if (unifier.getSuccess()) {
									UnifierBasedVariableRenamer renamer = new UnifierBasedVariableRenamer(unifier,
											false);
									// rename universal variables (RWU = renamed with unifier)
									Rule rule1RWU = renamer.rename(renamedRule1);
									Rule rule2RWU = renamer.rename(renamedRule2);

									List<Literal> headAtomsRule1RWU = new ArrayList<>();
									headAtomsRule1.forEach(literal -> headAtomsRule1RWU.add(renamer.rename(literal)));

									List<Literal> headAtomsRule2RWU = new ArrayList<>();
									headAtomsRule2.forEach(literal -> headAtomsRule2RWU.add(renamer.rename(literal)));

									List<Literal> headAtoms22 = Filter.indexBased(headAtomsRule2RWU, headAtoms22Idx);

									if (RuleUtil.isRule1Applicable(rule1RWU, rule2RWU)
											&& !mappingUniversalintoExistential(headAtomsRule2, headAtomsRule1,
													transformed)
											&& mapExt2ExtOrExt2Uni(headAtomsRule2RWU, headAtomsRule1RWU, headAtoms22,
													transformed)
											&& mapExistentialsToTheSame(headAtomsRule2RWU, headAtomsRule1RWU,
													transformed)) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
