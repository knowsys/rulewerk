package org.semanticweb.rulewerk.math.mapping;

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

/**
 * An iterator class to iterate over all {@code PartialMapping}s. 
 * 
 * @author Larry González
 */
public class PartialMappingIterator implements Iterator<PartialMapping> {
	int domineSize;
	int codomineSize;
	int[] representation;
	int[] start;
	boolean stop;

	public PartialMappingIterator(int domineSize, int codomineSize) {
		this.domineSize = domineSize;
		this.codomineSize = codomineSize;
		this.representation = new int[domineSize];
		this.start = new int[domineSize];
		for (int i = 0; i < domineSize; i++) {
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
	public PartialMapping next() {
		PartialMapping pm = new PartialMapping(representation.clone(), codomineSize);
		addOneToPointer(0);
		return pm;
	}

	private void addOneToPointer(int idx) {
		if (idx < domineSize) {
			if (representation[idx] == codomineSize - 1) {
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
