package org.semanticweb.rulewerk.utils;

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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class RuleUtil {

	static public boolean isRule1Applicable(Rule rule1, Rule rule2) {
		List<Literal> instance = new ArrayList<>();
		List<Literal> query = new ArrayList<>();
		rule1.getPositiveBodyLiterals().forEach(literal -> instance.add(Instantiator.instantiateFact(literal)));
		rule1.getHead().getLiterals().forEach(literal -> query.add(Instantiator.instantiateQuery(literal)));
		rule2.getPositiveBodyLiterals().forEach(literal -> instance.add(Instantiator.instantiateFact(literal)));
		rule2.getHead().getLiterals().forEach(literal -> instance.add(Instantiator.instantiateFact(literal)));

		return !SBCQ.query(instance, query);
	}

	static public boolean isRuleApplicable(Rule rule) {
		List<Literal> instance = new ArrayList<>();
		List<Literal> query = new ArrayList<>();
		rule.getPositiveBodyLiterals().forEach(literal -> instance.add(Instantiator.instantiateFact(literal)));
		rule.getHead().getLiterals().forEach(literal -> query.add(Instantiator.instantiateQuery(literal)));

		return !SBCQ.query(instance, query);
	}

	static public Rule moveLiteralsWithExistentialVariablesToTheFront(Rule rule) {
		List<PositiveLiteral> headAtomsWithExistentials = new ArrayList<>();
		List<PositiveLiteral> headAtomsWithoutExistentials = new ArrayList<>();
		for (PositiveLiteral atom : rule.getHead().getLiterals()) {
			if (atom.containsExistentialVariables()) {
				headAtomsWithExistentials.add(atom);
			} else {
				headAtomsWithoutExistentials.add(atom);
			}
		}

		List<PositiveLiteral> newHead = new ArrayList<>();
		Stream.of(headAtomsWithExistentials, headAtomsWithoutExistentials).forEach(newHead::addAll);

		return Expressions.makeRule(Expressions.makeConjunction(newHead), rule.getBody());

	}
}
