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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Rule;

import com.google.common.collect.Sets;

public class PositiveDependency {

	static boolean reliesPositivelyOn(Rule rule1, Rule rule2) {
		Rule renamedFirstRule = VariableRenamer.renameVariables(rule1, 1);
		Rule renamedSecondRule = VariableRenamer.renameVariables(rule2, 2);

		Set<Literal> literalsInBody1 = new HashSet<>();
		renamedFirstRule.getBody().getLiterals().forEach(literal -> literalsInBody1.add(literal));

		Set<Literal> literalsInHead1 = new HashSet<>();
		renamedFirstRule.getHead().getLiterals().forEach(literal -> literalsInHead1.add(literal));
		Set<Set<Literal>> powerSetLiteralsInHead1 = Sets.powerSet(literalsInHead1);

		Set<Literal> literalsInBody2 = new HashSet<>();
		renamedSecondRule.getBody().getLiterals().forEach(literal -> literalsInBody2.add(literal));
		Set<Set<Literal>> powerSetLiteralsInBody2 = Sets.powerSet(literalsInBody2);

		Set<Literal> literalsInHead2 = new HashSet<>();
		renamedSecondRule.getHead().getLiterals().forEach(literal -> literalsInHead2.add(literal));

		for (Set<Literal> litInHead1 : powerSetLiteralsInHead1) {
			for (Set<Literal> litInBody2 : powerSetLiteralsInBody2) {
				if (!litInHead1.isEmpty() && !litInBody2.isEmpty()) {
					LiteralSetUnifier lsu = new LiteralSetUnifier(litInHead1, litInBody2);
					lsu.print();

					if (lsu.success && lsu.unifier.size() > 0) {
//					HERE I HAVE four SETS
//					litInHead1, litInHead1Complement 
//					litInBody2, litInBody2Complement
//					ALSO I HAVE THE UNIFIER
						Set<Literal> litInHead1Prime = new HashSet<>(literalsInHead1);
						litInHead1Prime.removeAll(litInHead1);
						Set<Literal> litInBody2Prime = new HashSet<>(literalsInBody2);
						litInBody2Prime.removeAll(litInBody2);

//					 now I rename variables following the unifier
						Set<Literal> litInBody1 = VariableRenamer.renameVariables(literalsInBody1, lsu);
						Set<Literal> litInHead2 = VariableRenamer.renameVariables(literalsInHead2, lsu);
						litInHead1 = VariableRenamer.renameVariables(litInHead1, lsu);
						litInBody2 = VariableRenamer.renameVariables(litInBody2, lsu);
						litInHead1Prime = VariableRenamer.renameVariables(litInHead1Prime, lsu);
						litInBody2Prime = VariableRenamer.renameVariables(litInBody2Prime, lsu);
						// this is not correct
						
						return true;
//					TODO
//					Which are the conditions to check reliance?
					}
				}
			}
		}
		return false;
	}

}
