package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
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

import org.semanticweb.rulewerk.core.reasoner.implementation.InMemoryDataSource;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.reasoner.implementation.DataSourceConfigurationVisitor;

/**
 * Implementation of {@link InMemoryDataSource} for the VLog backend.
 */
public class VLogInMemoryDataSource extends InMemoryDataSource {
	String[][] data;
	int nextEmptyTuple = 0;

	public VLogInMemoryDataSource(final int arity, final int initialCapacity) {
		super(arity, initialCapacity);
		this.data = new String[initialCapacity][arity];
	}

	/**
	 * Adds a fact to this data source. The number of constant names must agree with
	 * the arity of this data source.
	 *
	 * @param constantNames the string names of the constants in this fact
	 */
	public void addTuple(final String... constantNames) {
		validateArity(constantNames);

		if (this.nextEmptyTuple == this.capacity) {
			this.capacity = this.capacity * 2;
			this.data = Arrays.copyOf(this.data, this.capacity);
		}
		this.data[this.nextEmptyTuple] = new String[this.arity];
		for (int i = 0; i < this.arity; i++) {
			this.data[this.nextEmptyTuple][i] = TermToVLogConverter.getVLogNameForConstantName(constantNames[i]);
		}
		this.nextEmptyTuple++;
	}

	/**
	 * Returns the data stored in this data source, in the format expected by the
	 * VLog reasoner backend.
	 *
	 * @return the data
	 */
	public String[][] getData() {
		if (this.nextEmptyTuple == this.capacity) {
			return this.data;
		} else {
			return Arrays.copyOf(this.data, this.nextEmptyTuple);
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(
				"This InMemoryDataSource holds the following tuples of constant names, one tuple per line:");
		for (int i = 0; i < getData().length; i++) {
			for (int j = 0; j < this.data[i].length; j++) {
				sb.append(this.data[i][j] + " ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public void accept(DataSourceConfigurationVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Fact getDeclarationFact() {
		throw new UnsupportedOperationException("VLogInMemoryDataSource is cannot be serialized.");
	}
}
