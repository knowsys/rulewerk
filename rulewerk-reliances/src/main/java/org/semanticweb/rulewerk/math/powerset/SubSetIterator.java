package org.semanticweb.rulewerk.math.powerset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

/**
 * Given a set, iterate over all subsets in its power set over an Iterator.
 * 
 * @author Larry González
 *
 */
class SubSetIterator<T> implements Iterator<List<T>> {
	List<T> elements;
	SubSetIndexIterator iter;

	public SubSetIterator(List<T> elements) {
		this.elements = elements;
		iter = new SubSetIndexIterator(elements.size());
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public List<T> next() {
		List<T> subset = new ArrayList<>();
		for (int i : iter.next()) {
			subset.add(elements.get(i));
		}
		return subset;
	}
}