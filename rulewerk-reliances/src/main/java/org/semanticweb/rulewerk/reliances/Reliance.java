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

import java.util.List;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.logic.RespectingUnifier;
import org.semanticweb.rulewerk.logic.Substitution;
import org.semanticweb.rulewerk.math.mapping.PartialMapping;
import org.semanticweb.rulewerk.math.mapping.PartialMappingIterable;
import org.semanticweb.rulewerk.utils.RuleUtil;

public class Reliance {

	/**
	 * Checker for positive reliance relation.
	 * 
	 * @param rule1
	 * @param rule2
	 * @return True if rule2 positively relies on rule1.
	 */
	static public boolean positively(Rule first, Rule second) {
		Rule rule1 = RuleUtil.containsRepeatedAtoms(first) ? RuleUtil.cleanRepeatedAtoms(first) : first;
		Rule rule2 = RuleUtil.containsRepeatedAtoms(second) ? RuleUtil.cleanRepeatedAtoms(second) : second;

		if (!RuleUtil.isRuleApplicable(rule1)) {
			return false;
		}

		if (!RuleUtil.isRuleApplicable(rule2)) {
			return false;
		}

		Rule renamedRule1 = SuffixBasedVariableRenamer.rename(rule1, rule2.hashCode() + 1);
		Rule renamedRule2 = SuffixBasedVariableRenamer.rename(rule2, rule1.hashCode() + 2);

		List<PositiveLiteral> head1 = renamedRule1.getHead().getLiterals();
		List<PositiveLiteral> body2 = renamedRule2.getPositiveBodyLiterals().getLiterals();

		for (PartialMapping partialMapping : new PartialMappingIterable(body2.size(), head1.size())) {

			if (!partialMapping.isEmpty()) {

				RespectingUnifier unifier = new RespectingUnifier(body2, head1, partialMapping);
				if (unifier.getSuccess()) {
					// RWU = renamed with unifier
					Rule rule1RWU = Substitution.apply(unifier, renamedRule1);
					Rule rule2RWU = Substitution.apply(unifier, renamedRule2);

					if (RuleUtil.isRule1Applicable(rule2RWU, rule1RWU)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
