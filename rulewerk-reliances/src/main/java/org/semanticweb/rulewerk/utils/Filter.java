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
import java.util.stream.Collectors;

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
	
	static public List<Integer> complement(List<Integer> indexes, int length) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			if (!indexes.contains(i)) {
				result.add(i);
			}
		}
		return result;
	}

	static public List<Integer> complement(List<Integer> set, List<Integer> subset) {

		List<Integer> result = new ArrayList<>();
		for (int element : set) {
			if (!subset.contains(element)) {
				result.add(element);
			}
		}
		return result;
	}

	static public List<Integer> join(List<Integer> first, List<Integer> second) {

		List<Integer> result = new ArrayList<>();
		result.addAll(first);
		result.addAll(second);

		List<Integer> sorted = result.stream().sorted().collect(Collectors.toList());
		return sorted;
	}
}
