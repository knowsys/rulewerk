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
import java.util.Iterator;

/**
 * A class to iterate over all combinations of choosing k elements from n,
 * represented as set indexes in a BitSet, over an Iterator.
 *
 * @author Larry González
 * 
 */
public class KOverNIterator implements Iterator<BitSet> {
	int k;
	int n;
	BitSet combination;
	BitSet end;

	/**
	 * {@code KOverNIterator} constructor.
	 * 
	 * @note The bit {@value n} is used as a flag. If true then the iteration has
	 *       finished.
	 * 
	 * @param n total number of set elements
	 * @param k number of elements to choose over n
	 * @return
	 * @return
	 */
	public KOverNIterator(int n, int k) {
		this.n = n;
		this.k = k;
		this.combination = new BitSet(n + 1);
		this.end = new BitSet(n + 1);
		this.combination.set(0, k, true);
		this.combination.set(k, n, false);
		this.end.set(0, n - k, false);
		this.end.set(n - k, n, true);
	}

	@Override
	public boolean hasNext() {
		return !combination.get(n);
	}

	@Override
	public BitSet next() {
		BitSet helper = (BitSet) combination.clone();
		work();
		if (helper.equals(end)) {
			combination.set(n);
			end.set(n);
		}
		return helper;
	}

	/**
	 * Change the representation of combination in such a way that the next valid
	 * combination is computed
	 */
	private void work() {
		int toMove;
		if (n > 0) {
			if (!combination.get(n - 1)) {
				toMove = combination.previousSetBit(n - 1);
				if (toMove > -1) {
					swap(toMove, toMove + 1);
				}
			} else {
				toMove = getLeftOfFirstClearGroup();
				if (toMove > 0) {
					swap(toMove, toMove - 1);
					if (getLeftOfFirstClearGroup() > toMove) {
						while (combination.get(n - 1)) {
							swap(getLeftOfFirstSetGroup(), getLeftOfFirstClearGroup());
						}
					}
				}
			}
		} else {
			combination.set(n);
			end.set(n);
		}

	}

	/**
	 * @return the index of the left most bit in the first set group from left to
	 *         right.
	 */
	private int getLeftOfFirstSetGroup() {
		return combination.previousClearBit(combination.previousSetBit(n - 1)) + 1;
	}

	/**
	 * @return the index of the left most bit in the first clear group from left to
	 *         right.
	 */
	private int getLeftOfFirstClearGroup() {
		return combination.previousSetBit(combination.previousClearBit(n - 1)) + 1;
	}

	/**
	 * Swap the values of indexes {@code i} and {@code j}.
	 * 
	 * @param i index to be swapped
	 * @param j index to be swapped
	 */
	private void swap(int i, int j) {
		boolean helper = combination.get(i);
		combination.set(i, combination.get(j));
		combination.set(j, helper);
	}
}
