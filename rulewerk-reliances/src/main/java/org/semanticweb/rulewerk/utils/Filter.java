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
