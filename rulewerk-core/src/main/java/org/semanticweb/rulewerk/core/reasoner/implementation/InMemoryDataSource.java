package org.semanticweb.rulewerk.core.reasoner.implementation;

/*-
 * #%L
 * Rulewerk Core Components
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

import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.Fact;

/**
 * A {@link DataSource} for representing a large number of facts that were
 * generated in Java. Rather than creating {@link Fact} objects for all of them,
 * the object will directly accept tuples of constant names that are internally
 * stored in a form that can be passed to the reasoner directly, thereby saving
 * memory and loading time.
 *
 * @author Markus Kroetzsch
 *
 */
public abstract class InMemoryDataSource implements ReasonerDataSource {

	protected int capacity;
	protected final int arity;

	/**
	 * Create a new in-memory data source for facts of the specified arity. The
	 * given capacity is the initial size of the space allocated. For best
	 * efficiency, the actual number of facts should exactly correspond to this
	 * capacity.
	 *
	 * @param arity           the number of parameters in a fact from this source
	 * @param initialCapacity the planned number of facts
	 */
	public InMemoryDataSource(final int arity, final int initialCapacity) {
		this.capacity = initialCapacity;
		this.arity = arity;
	}

	/**
	 * Adds a fact to this data source. The number of constant names must agree with
	 * the arity of this data source.
	 *
	 * @param constantNames the string names of the constants in this fact
	 */
	public abstract void addTuple(final String... constantNames);

	protected void validateArity(final String... constantNames) {
		if (constantNames.length != this.arity) {
			throw new IllegalArgumentException("This data source holds tuples of arity " + this.arity
					+ ". Adding a tuple of size " + constantNames.length + " is not possible.");
		}
	}
}
