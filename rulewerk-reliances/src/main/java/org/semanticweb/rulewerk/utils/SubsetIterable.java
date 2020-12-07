package org.semanticweb.rulewerk.utils;

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

import org.semanticweb.rulewerk.utils.Filter;
import org.semanticweb.rulewerk.utils.PowerSet;

/**
 * Given a set, iterate over all subsets of it.
 * 
 * @author Larry Gonzalez
 *
 */
public class SubsetIterable<T> implements Iterable<List<T>> {

	SubsetIterator<T> subsetIterator;

	private class SubsetIterator<T1> implements Iterator<List<T1>> {

		PowerSet iterator;
		List<T1> elements;

		public SubsetIterator(List<T1> elements) {
			iterator = new PowerSet(elements.size());
			this.elements = elements;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public List<T1> next() {
			List<T1> result = Filter.combinationBased(this.elements, iterator.next());
			if (result.size() > 0) {
				return result;
			} else {
				return next();
			}
		}

	}

	public SubsetIterable(List<T> elements) {
		subsetIterator = new SubsetIterator<T>(elements);

	}

	@Override
	public Iterator<List<T>> iterator() {
		return subsetIterator;
	}

}
