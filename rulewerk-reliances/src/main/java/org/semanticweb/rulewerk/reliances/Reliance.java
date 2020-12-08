package org.semanticweb.rulewerk.reliances;

import java.util.List;

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
import org.semanticweb.rulewerk.utils.RuleUtil;

public class Reliance {

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

		List<PositiveLiteral> headAtomsRule1 = renamedRule1.getHead().getLiterals();
		List<Literal> positiveBodyLiteralsRule2 = renamedRule2.getPositiveBodyLiterals();

		int sizeHead1 = headAtomsRule1.size();
		int sizePositiveBody2 = positiveBodyLiteralsRule2.size();

		AssignmentIterable assignmentIterable = new AssignmentIterable(sizePositiveBody2, sizeHead1);

		for (Assignment assignment : assignmentIterable) {
			MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(positiveBodyLiteralsRule2, headAtomsRule1,
					assignment);

			// RWU = renamed with unifier
			if (unifier.getSuccess()) {
				UnifierBasedVariableRenamer renamer = new UnifierBasedVariableRenamer(unifier, true);

				Rule rule1RWU = renamer.rename(renamedRule1);
				Rule rule2RWU = renamer.rename(renamedRule2);

				if (RuleUtil.isRule1Applicable(rule2RWU, rule1RWU)) {
					return true;
				}
			}
		}
		return false;
	}
}
