package org.semanticweb.rulewerk.executables;

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

import java.util.Vector;

public class Graph {

	int nodes;
	// 0: no edge
	// 1: removable edge
	// 2; non-removable edge
	int[][] adjacencyMatrix; // [from, to]

	public Graph(int nodes) {
		this.nodes = nodes;
		this.adjacencyMatrix = new int[nodes][nodes];
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				adjacencyMatrix[i][j] = 0;
			}
		}
	}

	public void addRemovableEdge(int from, int to) {
		adjacencyMatrix[from][to] = 1;
	}

	public void addNonRemovableEdge(int from, int to) {
		adjacencyMatrix[from][to] = 2;
	}

	public void deleteEdge(int from, int to) {
		if (adjacencyMatrix[from][to] != 2) {
			adjacencyMatrix[from][to] = 0;
		}
	}

	private Vector<Integer> getSuccessors(int from) {
		Vector<Integer> successors = new Vector<>();
		for (int j = 0; j < nodes; j++) {
			if (adjacencyMatrix[from][j] != 0) {
				successors.add(j);
			}
		}
		return successors;
	}

	private void DFS(Vector<Vector<Integer>> cycles, Vector<Integer> visited) {
		if (visited.size() == 0) {
			throw new RuntimeException("empty path");
		} else {
			for (int successor : getSuccessors(visited.lastElement())) {
				if (visited.firstElement() == successor) {
					cycles.add(visited);
				} else if (!visited.contains(successor)) {
					Vector<Integer> copy = new Vector<Integer>(visited);
					copy.add(successor);
					DFS(cycles, copy);
				}
			}
		}
	}

	private int getMin(Vector<Integer> vector) {
		int result = vector.firstElement();
		for (int value : vector) {
			if (result > value) {
				result = value;
			}
		}
		return result;
	}

	private boolean contains(Vector<Vector<Integer>> container, Vector<Integer> vector) {
		for (Vector<Integer> helper : container) {
			if (helper.equals(vector)) {
				return true;
			}
		}
		return false;
	}

	private Vector<Vector<Integer>> consolidate(Vector<Vector<Integer>> cycles) {
		Vector<Vector<Integer>> consolidation = new Vector<Vector<Integer>>();
		for (Vector<Integer> cycle : cycles) {
			int min = getMin(cycle);
			while (min != cycle.firstElement()) {
				int helper = cycle.firstElement();
				cycle.remove(0);
				cycle.add(helper);
			}
			if (!contains(consolidation, cycle)) {
				consolidation.add(cycle);
			}
		}
		return consolidation;
	}

	private void getCycles(Vector<Vector<Integer>> cycles) {
		Vector<Integer> newPath;
		for (int i = 0; i < nodes; i++) {
			newPath = new Vector<>();
			newPath.add(i);
			DFS(cycles, newPath);
		}
	}

	public Vector<Vector<Integer>> getCycles() {
		Vector<Vector<Integer>> cycles = new Vector<>();
		getCycles(cycles);
		cycles = consolidate(cycles);
		return cycles;
	}

	public void removeCycles() {
		Vector<Vector<Integer>> cycles = getCycles();
		for (Vector<Integer> cycle : cycles) {
			for (int i = 0; i < cycle.size() - 1; i++) {
				deleteEdge(cycle.elementAt(i), cycle.elementAt(i + 1));
			}
			deleteEdge(cycle.lastElement(), cycle.firstElement());
		}
	}

	public Vector<int[]> getEdges() {
		Vector<int[]> edges = new Vector<>();
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				if (adjacencyMatrix[i][j] != 0) {
					edges.add(new int[] { i, j });
				}
			}
		}
		return edges;
	}

	public void print() {
		String helper;
		for (int i = 0; i < nodes; i++) {
			helper = "";
			for (int j = 0; j < nodes; j++) {
				helper += adjacencyMatrix[i][j] + " ";
			}
			System.out.println(helper);
		}
	}

	public static void print(Vector<Integer> vector) {
		String toPrint = "[";
		for (int value : vector) {
			toPrint += value + " ";
		}
		toPrint += "]";
		System.out.println(toPrint);
	}

	public int size() {
		return getEdges().size();
	}
}
