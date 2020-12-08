package org.semanticweb.rulewerk.reliances;

import java.util.List;
import java.util.Map;

import org.semanticweb.rulewerk.core.model.api.Conjunction;

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

import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.RuleImpl;
import org.semanticweb.rulewerk.utils.Filter;
import org.semanticweb.rulewerk.utils.LiteralList;
import org.semanticweb.rulewerk.utils.RuleUtil;
import org.semanticweb.rulewerk.utils.SubsetIterable;

public class SelfRestraint {

	/**
	 * 
	 * @param rule
	 * @return True if the rule restraints itself.
	 */
	static public boolean restraint(Rule rule) {
		// if rule2 is Datalog, it can not be restrained
		if (rule.getExistentialVariables().count() == 0) {
			return false;
		}

		if (!RuleUtil.isRuleApplicable(rule)) {
			return false;
		}

		if (rule.containsUnconnectedPieces()) {
			return true;
		}

		Rule renamedRule = SuffixBasedVariableRenamer.rename(rule, rule.hashCode() + 1);
		if (Restraint.restraint(rule, renamedRule)) {
			return true;
		}

		List<PositiveLiteral> headAtoms = rule.getHead().getLiterals();
		int headSize = headAtoms.size();

		Map<Predicate, List<Integer>> predToLiterals = LiteralList.getPredicate2Literals(headAtoms);

		for (Predicate pred : predToLiterals.keySet()) {

			List<Integer> positions = predToLiterals.get(pred);

			List<Integer> rest = Filter.complement(positions, headSize);

			if (positions.size() > 0) {
				SubsetIterable<Integer> subsetIterable = new SubsetIterable<>(positions);

				for (List<Integer> subset : subsetIterable) {
					List<Integer> complement = Filter.complement(positions, subset);

					if (subset.size() > 0 && complement.size() > 0) {
						List<Integer> head1Idx = Filter.join(rest, subset);
						List<Integer> head2Idx = Filter.join(rest, complement);

						List<PositiveLiteral> headAtoms1 = Filter.indexBased(headAtoms, head1Idx);
						List<PositiveLiteral> headAtoms2 = Filter.indexBased(headAtoms, head2Idx);

						Conjunction<PositiveLiteral> head1 = Expressions.makeConjunction(headAtoms1);
						Conjunction<PositiveLiteral> head2 = Expressions.makeConjunction(headAtoms2);

						Rule rule1 = new RuleImpl(head1, rule.getBody());
						Rule rule2 = new RuleImpl(head2, rule.getBody());

						if (Restraint.restraint(rule1, rule2)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
