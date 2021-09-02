package org.semanticweb.rulewerk.asp.implementation;

/*-
 * #%L
 * Rulewerk ASP Components
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

import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;

import java.util.*;

/**
 * Utility class to compute all predicates of an ASP-based knowledge base that are over-approximated. A predicate is
 * over-approximated if it is part of a cycle with a negative edge in the precedence graph or if it dependents on an
 * over-approximated predicate.
 */
public class KnowledgeBaseAnalyser {

	private final Map<Predicate, Set<Predicate>> adjacencyListMap;
	private final Map<Predicate, Set<Predicate>> negativeEdgeAdjacencyListMap;

	private final Map<Integer, Set<Predicate>> stronglyConnectedComponents;
	private final Set<Predicate> approximatedPredicates;
	private int componentCounter = 0;

	private final Map<Predicate, Vertex> predicateVertexMap;

	/**
	 * Auxiliary class to model the vertices of the precedence graph together with additional information required by
	 * Tarjan's algorithm.
	 */
	static class Vertex {

		static private int globalIndex;
		static private final List<Vertex> stack = new ArrayList<>();

		private final Predicate predicate;
		private boolean onStack;

		// depth-first search (discovery) index
		private int index;

		// lowest index of vertex this vertex has a loop with
		private int lowLink;

		/**
		 * Static method to get a vertex from the class-based stack
		 *
		 * @return a vertex
		 */
		public static Vertex pop() {
			return stack.remove(stack.size() - 1);
		}

		/**
		 * Constructor. Creates a (not discovered) vertex for a given predicate
		 *
		 * @param predicate a predicate
		 */
		public Vertex(Predicate predicate) {
			this.predicate = predicate;
			index = -1;
			lowLink = -1;
			onStack = false;
		}

		/**
		 * Marks the vertex as discovered.
		 */
		public void discover() {
			index = globalIndex;
			lowLink = globalIndex;
			globalIndex++;
			push();
		}

		/**
		 * Adds vertex to the stack.
		 */
		public void push() {
			stack.add(this);
			onStack = true;
		}

		public Predicate getPredicate() {
			return predicate;
		}

		public int getIndex() {
			return index;
		}

		public int getLowLink() {
			return lowLink;
		}

		public void setLowLink(int lowLink) {
			this.lowLink = lowLink;
		}

		public boolean isOnStack() {
			return onStack;
		}

		public void setOnStack(boolean onStack) {
			this.onStack = onStack;
		}
	}

	/**
	 * Constructor. Triggers the analysis.
	 *
	 * @param knowledgeBase the knowledge base to analyse
	 */
	public KnowledgeBaseAnalyser(KnowledgeBase knowledgeBase) {
		predicateVertexMap = new HashMap<>();
		stronglyConnectedComponents = new HashMap<>();
		adjacencyListMap = new HashMap<>();
		negativeEdgeAdjacencyListMap = new HashMap<>();
		approximatedPredicates = new HashSet<>();

		Set<Predicate> predicates = knowledgeBase.getPredicates();
		for (Predicate predicate : predicates) {
			predicateVertexMap.put(predicate, new Vertex(predicate));
			adjacencyListMap.put(predicate, new HashSet<>());
			negativeEdgeAdjacencyListMap.put(predicate, new HashSet<>());
		}

		// build (negative-edge) adjacency list
		for (Rule rule : knowledgeBase.getRules()) {
			Set<Predicate> headPredicates = new HashSet<>();
			for (PositiveLiteral headLiteral : rule.getHead()) {
				headPredicates.add(headLiteral.getPredicate());
			}

			for (Literal bodyLiteral : rule.getBody()) {
				adjacencyListMap.get(bodyLiteral.getPredicate()).addAll(headPredicates);
				if (bodyLiteral.isNegated()) {
					negativeEdgeAdjacencyListMap.get(bodyLiteral.getPredicate()).addAll(headPredicates);
				}
			}
		}

		tarjan();
		extendApproximation();
	}

	/**
	 * Trigger Tarjan's algorithm to compute the strongly connected components.
	 */
	private void tarjan() {
		for (Vertex vertex : predicateVertexMap.values()) {
			if (vertex.getIndex() == -1) {
				stronglyConnect(vertex);
			}
		}
	}

	/**
	 * Helper function of Tarjan's algorithm.
	 *
	 * @param vertex a vertex
	 */
	private void stronglyConnect(Vertex vertex) {
		vertex.discover();

		// Consider successors
		for (Predicate predicate : adjacencyListMap.get(vertex.getPredicate())) {
			Vertex successor = predicateVertexMap.get(predicate);
			if (successor.getIndex() == -1) {
				stronglyConnect(successor);
				vertex.setLowLink(Math.min(vertex.getLowLink(), successor.getLowLink()));
			} else if (successor.isOnStack()) {
				vertex.setLowLink(Math.min(vertex.getLowLink(), successor.getIndex()));
			}
		}

		// Check if vertex is the root of a strongly connected component
		if (vertex.getLowLink() == vertex.getIndex()) {
			Set<Predicate> connectedComponent = new HashSet<>();
			Set<Predicate> negativelyDependentPredicates = new HashSet<>();
			Vertex stackedVertex;
			do {
				stackedVertex = Vertex.pop();
				stackedVertex.setOnStack(false);
				connectedComponent.add(stackedVertex.getPredicate());
				negativelyDependentPredicates.addAll(negativeEdgeAdjacencyListMap.get(stackedVertex.getPredicate()));
			} while (!stackedVertex.equals(vertex));
			stronglyConnectedComponents.put(componentCounter++, connectedComponent);
			// Check if the component contains a cycle with a negative edge. Then all predicates of the component are
			// over-approximated.
			if (connectedComponent.stream().anyMatch(negativelyDependentPredicates::contains)) {
				approximatedPredicates.addAll(connectedComponent);
				approximatedPredicates.addAll(negativelyDependentPredicates);
			}
		}
	}

	/**
	 * Extends the set of over-approximated predicates by adding all predicates that dependent on them.
	 */
	private void extendApproximation() {
		List<Predicate> newlyApproximated = new ArrayList<>(approximatedPredicates);
		Set<Predicate> visited = new HashSet<>();
		while (!newlyApproximated.isEmpty()) {
			Predicate predicate = newlyApproximated.remove(newlyApproximated.size() - 1);
			if (approximatedPredicates.addAll(adjacencyListMap.get(predicate))) {
				for (Predicate successor : adjacencyListMap.get(predicate)) {
					if (!visited.contains(successor) && !newlyApproximated.contains(successor)) {
						newlyApproximated.add(successor);
					}
				}
			}
			visited.add(predicate);
		}
	}

	/**
	 * Gets the strongly connected components.
	 *
	 * @return the strongly connected components
	 */
	public Map<Integer,Set<Predicate>> getStronglyConnectedComponents() {
		return stronglyConnectedComponents;
	}

	/**
	 * Gets the over-approximated predicates.
	 *
	 * @return a set of {@link Predicate}s
	 */
	public Set<Predicate> getOverApproximatedPredicates() {
		return approximatedPredicates;
	}
}
