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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

		return !BCQ.query(instance, query);
	}

	// TODO This should be in RuleImpl
	static public boolean isRuleApplicable(Rule rule) {
		List<Literal> instance = new ArrayList<>();
		List<Literal> query = new ArrayList<>();
		rule.getPositiveBodyLiterals().forEach(literal -> instance.add(Instantiator.instantiateFact(literal)));
		rule.getHead().getLiterals().forEach(literal -> query.add(Instantiator.instantiateQuery(literal)));

		return !BCQ.query(instance, query);
	}

	/*
	 * Remove head atoms that appear (positively) in the body of the same rule.
	 *
	 * TODO This should be in RuleImpl and/or RuleParser.
	 *
	 * @see containsRepeatedAtoms
	 */
	static public Rule cleanRepeatedAtoms(Rule rule) {
		Set<PositiveLiteral> positiveBody = new HashSet<>(rule.getPositiveBodyLiterals().getLiterals());
		return Expressions.makeRule(Expressions.makeConjunction(rule.getHead().getLiterals().stream()
				.filter(x -> !positiveBody.contains(x)).collect(Collectors.toList())), rule.getBody());
	}

	/*
	 * True if a head atom appears (positively) in the body of the same rule.
	 *
	 * TODO This should be in RuleImpl and/or RuleParser.
	 */
	static public boolean containsRepeatedAtoms(Rule rule) {
		Set<PositiveLiteral> positiveBody = new HashSet<>(rule.getPositiveBodyLiterals().getLiterals());
		for (PositiveLiteral literal : rule.getHead().getLiterals()) {
			if (positiveBody.contains(literal)) {
				return true;
			}
		}
		return false;
	}
}
