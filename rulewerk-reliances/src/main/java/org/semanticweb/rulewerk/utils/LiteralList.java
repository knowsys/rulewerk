package org.semanticweb.rulewerk.utils;

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

	static public Set<ExistentialVariable> getExistentialVariables(List<Literal> literals) {
		Set<ExistentialVariable> result = new HashSet<>();
		literals.forEach(literal -> literal.getExistentialVariables().forEach(extVar -> result.add(extVar)));
		return result;
	}

	static public Set<String> getExistentialVariableNames(List<Literal> literals) {
		Set<String> result = new HashSet<>();
		literals.forEach(literal -> literal.getExistentialVariables().forEach(extVar -> result.add(extVar.getName())));
		return result;
	}

	static public Set<String> getUniversalVariableNames(List<Literal> literals) {
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

	static public Map<Predicate, List<Integer>> getPredicatePositions(List<PositiveLiteral> literals) {
		Map<Predicate, List<Integer>> result = new HashMap<>();

		for (int i = 0; i < literals.size(); i++) {
			Predicate pred = literals.get(i).getPredicate();
			System.out.println(pred);
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
