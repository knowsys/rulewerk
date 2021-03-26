package org.semanticweb.rulewerk.math.permutation;

import java.util.BitSet;

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

import java.util.Iterator;

/**
 * A class to iterate over all combinations of choosing k elements from n,
 * represented as set indexes in a BitSet, over an Iterable.
 *
 * @author Larry González
 * 
 */
public class KOverNIterable implements Iterable<BitSet> {

	KOverNIterator kOverNIterator;

	public KOverNIterable(int n, int k) {
		kOverNIterator = new KOverNIterator(n, k);
	}

	@Override
	public Iterator<BitSet> iterator() {
		return kOverNIterator;
	}

}
