package org.semanticweb.rulewerk.utils;

import java.util.ArrayList;
import java.util.List;

public class Filter {
	static public <T> List<T> combinationBased(List<T> original, int[] combination) {
		List<T> result = new ArrayList<>();
		for (int i = 0; i < combination.length; i++) {
			if (combination[i] == 1) {
				result.add(original.get(i));
			}
		}
		return result;
	}

	static public <T> List<T> indexBased(List<T> original, List<Integer> positions) {
		List<T> result = new ArrayList<>();
		for (int index : positions) {
			result.add(original.get(index));
		}
		return result;
	}
}