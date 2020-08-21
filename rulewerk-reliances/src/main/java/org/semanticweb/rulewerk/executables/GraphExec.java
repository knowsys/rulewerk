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

public class GraphExec {

	static public void main(String args[]) {
		Graph g = new Graph(5);
		g.addRemovableEdge(1, 1);
		g.addRemovableEdge(1, 2);
		g.addRemovableEdge(2, 1);
		g.addNonRemovableEdge(2, 3);
		g.addNonRemovableEdge(3, 1);
		g.addNonRemovableEdge(1, 3);
		g.addNonRemovableEdge(3, 2);
		g.print();

		Vector<Vector<Integer>> cycles = g.getCycles();

		for (Vector<Integer> cycle : cycles) {
			Graph.print(cycle);
		}
		
		System.out.println("XXXXX");
		Vector<Integer> v1 = new Vector<>();
		Vector<Integer> v2 = new Vector<>();
		v1.add(1);
		v1.add(3);
		v2.add(1);
		v2.add(3);
		System.out.println(v1.equals(v2));
	}
}
