package org.semanticweb.rulewerk.utils;

/*-
 * #%L
 * rulewerk-utils
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

public class Graph<T> {

	private Map<T, Set<T>> edges;

	public Graph() {
		edges = new HashMap<>();
	}

	public void addEdge(T origin, T destination) {
		doAddEdge(origin, destination);
		doAddEdge(destination, origin);
	}

	public Set<T> getReachableNodes(T node) {
		Set<T> result = new HashSet<>();
		List<T> toVisit = new ArrayList<>();
		toVisit.add(node);
		Set<T> visited = new HashSet<>();

		while (!toVisit.isEmpty()) {
			T current = toVisit.remove(toVisit.size() - 1);
			if (edges.containsKey(current)) {
				for (T next : edges.get(current)) {
					result.add(next);
					if (!visited.contains(next)) {
						toVisit.add(next);
					}
				}
			}
			visited.add(current);
		}

		return result;
	}

	private void doAddEdge(T origin, T destination) {
		if (edges.containsKey(origin)) {
			this.edges.get(origin).add(destination);
		} else {
			Set<T> newSet = new HashSet<>();
			newSet.add(destination);
			this.edges.put(origin, newSet);
		}

	}

}
