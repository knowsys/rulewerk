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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;

public class LiteralList {

	static public Set<ExistentialVariable> getExistentialVariables(List<? extends Literal> literals) {
		Set<ExistentialVariable> result = new HashSet<>();
		literals.forEach(literal -> literal.getExistentialVariables().forEach(extVar -> result.add(extVar)));
		return result;
	}

	static public Set<String> getExistentialVariableNames(List<? extends Literal> literals) {
		Set<String> result = new HashSet<>();
		literals.forEach(literal -> literal.getExistentialVariables().forEach(extVar -> result.add(extVar.getName())));
		return result;
	}

	static public Set<String> getUniversalVariableNames(List<? extends Literal> literals) {
		Set<String> result = new HashSet<>();
		literals.forEach(literal -> literal.getUniversalVariables().forEach(uniVar -> result.add(uniVar.getName())));
		return result;
	}

	static public List<PositiveLiteral> filterLiteralsByExistentialVariables(List<PositiveLiteral> literals,
			List<ExistentialVariable> existentialVariables) {
		List<PositiveLiteral> result = new ArrayList<>();

		for (PositiveLiteral literal : literals) {
			for (ExistentialVariable extVar : existentialVariables) {
				if (((Literal) literal).getExistentialVariables()
						.anyMatch(containedVar -> containedVar.equals(extVar))) {
					result.add(literal);
					break;
				}
			}
		}
		return result;
	}

	static public List<Integer> idxOfLiteralsContainingExistentialVariables(List<PositiveLiteral> literals,
			List<ExistentialVariable> existentialVariables) {
		List<Integer> result = new ArrayList<>();

		for (int i = 0; i < literals.size(); i++) {
			for (ExistentialVariable extVar : existentialVariables) {
				if (literals.get(i).getExistentialVariables().anyMatch(containedVar -> containedVar.equals(extVar))) {
					result.add(i);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Returns a map from predicate to literals having that predicate.
	 * 
	 * @param literals List of literals
	 * @return map {predicate -> [literals]}
	 */
	static public Map<Predicate, List<Integer>> getPredicate2Literals(List<PositiveLiteral> literals) {
		Map<Predicate, List<Integer>> result = new HashMap<>();

		for (int i = 0; i < literals.size(); i++) {
			Predicate pred = literals.get(i).getPredicate();
			if (result.containsKey(pred)) {
				result.get(pred).add(i);
			} else {
				List<Integer> helper = new ArrayList<>();
				helper.add(i);
				result.put(pred, helper);
			}
		}
		return result;
	}

}
