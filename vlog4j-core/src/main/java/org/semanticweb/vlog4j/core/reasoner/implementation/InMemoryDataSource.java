package org.semanticweb.vlog4j.core.reasoner.implementation;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import org.semanticweb.vlog4j.core.model.api.DataSource;

/**
 * A {@link DataSource} for representing a large number of facts that were
 * generated in Java. Rather than making {@link Fact} objects for all of them,
 * the object will directly accept tuples of constant names that are internally
 * stored in a form that can be passed to the reasoner directly, thereby saving
 * memory and loading time.
 * 
 * @author Markus Kroetzsch
 *
 */
public class InMemoryDataSource implements DataSource {

	String[][] data;
	int nextEmptyTuple = 0;
	int capacity;
	final int arity;

	/**
	 * Create a new in-memory data source for facts of the specified arity. The
	 * given capacity is the initial size of the space allocated. For best
	 * efficiency, the actual number of facts should exactly correspond to this
	 * capacity.
	 * 
	 * @param arity           the number of parameters in a fact from this source
	 * @param initialCapacity the planned number of facts
	 */
	public InMemoryDataSource(int arity, int initialCapacity) {
		this.capacity = initialCapacity;
		this.arity = arity;
		data = new String[initialCapacity][arity];
	}

	/**
	 * Adds a fact to this data source. The number of constant names must agree with
	 * the arity of this data source.
	 * 
	 * @param constantNames the string names of the constants in this fact
	 */
	public void addTuple(String... constantNames) {
		if (constantNames.length != arity) {
			throw new IllegalArgumentException("This data source holds tuples of arity " + arity
					+ ". Adding a tuple of size " + constantNames.length + " is not possible.");
		}
		if (nextEmptyTuple == capacity) {
			capacity = capacity * 2;
			this.data = Arrays.copyOf(data, capacity);
		}
		data[nextEmptyTuple] = new String[arity];
		for (int i = 0; i < arity; i++) {
			data[nextEmptyTuple][i] = TermToVLogConverter.getVLogNameForConstantName(constantNames[i]);
		}
		nextEmptyTuple++;
	}

	/**
	 * Returns the data stored in this data source, in the format expected by the
	 * VLog reasoner backend.
	 * 
	 * @return the data
	 */
	public String[][] getData() {
		if (nextEmptyTuple == capacity) {
			return this.data;
		} else {
			return Arrays.copyOf(this.data, this.nextEmptyTuple);
		}
	}

	/**
	 * Returns null to indicate that this {@link DataSource} cannot be passed to
	 * VLog in a configuration string.
	 */
	@Override
	public String toConfigString() {
		return null;
	}

	@Override
	public String getSyntacticRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
