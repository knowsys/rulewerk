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

import java.util.Iterator;

/**
 * A class to iterate over all non-empty {@code PartialMapping}s
 * 
 * @author Larry González
 *
 */
public class PartialMappingIterable implements Iterable<PartialMapping> {

	PartialMappingIterator partialMappingIterator;

	/**
	 * Constructor of PartialMappintIterable
	 * 
	 * @param domainSize   number of objects in the domine list
	 * @param codomineSize number of objects in the codomine list
	 */
	public PartialMappingIterable(int domainSize, int codomineSize) {
		partialMappingIterator = new PartialMappingIterator(domainSize, codomineSize);
	}

	@Override
	public Iterator<PartialMapping> iterator() {
		return partialMappingIterator;
	}

}
