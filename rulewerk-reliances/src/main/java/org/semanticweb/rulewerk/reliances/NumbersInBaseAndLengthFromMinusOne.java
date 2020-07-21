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

import java.util.Arrays;
import java.util.Iterator;

public class NumbersInBaseAndLengthFromMinusOne implements Iterator<int[]> {
	int base;
	int length;
	int[] representation;
	int[] start;
	boolean stop;

	public NumbersInBaseAndLengthFromMinusOne(int base, int length) {
		this.base = base;
		this.length = length;
		this.representation = new int[length];
		this.start = new int[length];
		for (int i = 0; i < length; i++) {
			representation[i] = -1;
			start[i] = -1;
		}
		stop = false;
	}

	@Override
	public boolean hasNext() {
		return !stop;
	}

	@Override
	public int[] next() {
		int[] helper = representation.clone();
		addOneToPointer(0);
		return helper;
	}

	private void addOneToPointer(int idx) {
		if (idx < length) {
			if (representation[idx] == base - 1) {
				representation[idx] = -1;
				addOneToPointer(idx + 1);
			} else {
				representation[idx] += 1;
			}
		}
		if (Arrays.equals(representation, start))
			stop = true;
	}
}
