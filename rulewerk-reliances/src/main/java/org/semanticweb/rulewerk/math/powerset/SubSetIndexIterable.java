package org.semanticweb.rulewerk.math.powerset;

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

/**
 * Given a set, iterate over all subsets in its power set, represented by a list
 * of indexes, over an Iterable.
 * 
 * @author Larry González
 *
 */
public class SubSetIndexIterable implements Iterable<List<Integer>> {

	SubSetIndexIterator iter;

	public SubSetIndexIterable(int n) {
		iter = new SubSetIndexIterator(n);
	}

	@Override
	public Iterator<List<Integer>> iterator() {
		return iter;
	}

}
