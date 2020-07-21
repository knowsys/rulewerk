package org.semanticweb.rulewerk.reliances;

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

public class NumbersInBaseAndLengthFromMinusOneExecutable {

	static private void print(int[] toprint) {
		System.out.print("[");
		for (int i = 0; i < toprint.length; i++)
			System.out.print(toprint[i] + ",");
		System.out.println("]");
	}

	static public void main(String args[]) {
		NumbersInBaseAndLengthFromMinusOne iterator = new NumbersInBaseAndLengthFromMinusOne(3, 3);
		while (iterator.hasNext()) {
			print(iterator.next());
		}
	}

}
