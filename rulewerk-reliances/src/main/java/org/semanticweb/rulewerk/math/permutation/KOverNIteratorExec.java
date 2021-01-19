package org.semanticweb.rulewerk.math.permutation;

/*-
 * #%L
 * Rulewerk Reliances
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
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

import java.util.BitSet;

public class KOverNIteratorExec {

	static public void main(String args[]) {
		for (int n = 0; n < 6; n++) {
			for (int k = 0; k <=n; k++) {
				System.out.println(n + " " + k);
				KOverNIterator iter = new KOverNIterator(n, k);

				while (iter.hasNext()) {
					print(iter.next(), n);
				}
				System.out.println();
			}
		}
	}

	static private void print(BitSet b, int n) {
		for (int i = 0; i < n; i++) {
			System.out.print(b.get(i) ? 1 : 0);
		}
		System.out.println();
	}

}
