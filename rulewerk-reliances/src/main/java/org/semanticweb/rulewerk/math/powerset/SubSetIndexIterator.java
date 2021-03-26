package org.semanticweb.rulewerk.math.powerset;

import java.util.ArrayList;
import java.util.BitSet;

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

import java.util.Iterator;
import java.util.List;

import org.semanticweb.rulewerk.math.permutation.KOverNIterator;

/**
 * Given a set, iterate over all subsets in its power set, represented by a list
 * of indexes, over an Iterator.
 * 
 * @author Larry González
 *
 */
class SubSetIndexIterator implements Iterator<List<Integer>> {
	int n;
	int i;
	KOverNIterator iter;

	public SubSetIndexIterator(int n) {
		this.n = n;
		this.i = 0;
		this.iter = new KOverNIterator(n, i);
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext() || i < n;
	}

	@Override
	public List<Integer> next() {
		if (!iter.hasNext()) {
			i++;
			iter = new KOverNIterator(n, i);
		}
		return getSubSet(iter.next());
	}

	private List<Integer> getSubSet(BitSet bs) {
		List<Integer> subset = new ArrayList<>();
		for (int j = 0; j < n; j++) {
			if (bs.get(j)) {
				subset.add(j);
			}
		}
		return subset;
	}
}