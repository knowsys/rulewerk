package org.semanticweb.rulewerk.core.utils;

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

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.rulewerk.core.utils.Graph;

public class GraphTest {

	@Test
	public void getReachableNodesSimpleGraphTest() {
		final Graph<Integer> g = new Graph<>();
		g.addEdge(1, 1);

		assertEquals(Sets.newSet(1), g.getReachableNodes(1));
	}

	@Test
	public void getReachableNodesCyclicGraphTest() {

		final Graph<Integer> g = new Graph<>();
		final Set<Integer> s = Sets.newSet(1, 2, 3);
		g.addEdge(1, 2);
		g.addEdge(2, 3);
		g.addEdge(3, 1);

		assertEquals(s, g.getReachableNodes(1));
		assertEquals(s, g.getReachableNodes(2));
		assertEquals(s, g.getReachableNodes(3));
	}

	@Test
	public void getReachableNodesTreeGraphTest() {

		final Graph<Integer> g = new Graph<>();
		final Set<Integer> s = Sets.newSet(1, 2, 3, 4, 5, 6, 7);
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		g.addEdge(2, 4);
		g.addEdge(2, 5);
		g.addEdge(1, 6);
		g.addEdge(1, 7);

		assertEquals(s, g.getReachableNodes(1));
		assertEquals(s, g.getReachableNodes(2));
		assertEquals(s, g.getReachableNodes(3));
		assertEquals(s, g.getReachableNodes(4));
		assertEquals(s, g.getReachableNodes(5));
		assertEquals(s, g.getReachableNodes(6));
		assertEquals(s, g.getReachableNodes(7));
	}

	@Test
	public void getReachableNodesUnconnectedGraphTest() {

		final Graph<Integer> g = new Graph<>();
		final Set<Integer> s1 = Sets.newSet(1, 2, 3);
		final Set<Integer> s2 = Sets.newSet(4, 5, 6);
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		g.addEdge(4, 5);
		g.addEdge(5, 6);
		g.addEdge(6, 4);

		assertEquals(s1, g.getReachableNodes(1));
		assertEquals(s1, g.getReachableNodes(2));
		assertEquals(s1, g.getReachableNodes(3));
		assertEquals(s2, g.getReachableNodes(4));
		assertEquals(s2, g.getReachableNodes(5));
		assertEquals(s2, g.getReachableNodes(6));
	}

}
