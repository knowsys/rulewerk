package org.semanticweb.rulewerk.executables;

import org.semanticweb.rulewerk.reliances.Assignment;

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

public class AssignmentExecutable {

	static private void print(int[] intArray) {
		String base = "[";
		for (int i = 0; i < intArray.length; i++) {
			base += intArray[i] + ",";
		}
		base += "]";
		System.out.println(base);
	}

	static public void main(String args[]) {
		Assignment iter = new Assignment(4, 3);// assign,to
		for (int[] aux : iter) {
			print(aux);
		}
	}

}
