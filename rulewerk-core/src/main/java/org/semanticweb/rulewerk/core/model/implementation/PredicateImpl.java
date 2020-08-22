package org.semanticweb.rulewerk.core.model.implementation;

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

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Predicate;

/**
 * Implementation for {@link Predicate}. Supports predicates of arity 1 or
 * higher.
 *
 * @author Irina Dragoste
 *
 */
public class PredicateImpl implements Predicate {

	final private String name;

	final private int arity;

	/**
	 * Constructor for {@link Predicate}s of arity 1 or higher.
	 *
	 * @param name  a non-blank String (not null, nor empty or whitespace).
	 * @param arity an int value strictly greater than 0.
	 */
	public PredicateImpl(final String name, int arity) {
		Validate.notBlank(name, "Predicates cannot be named by blank Strings.");
		Validate.isTrue(arity > 0, "Predicate arity must be greater than zero: %d", arity);

		this.name = name;
		this.arity = arity;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getArity() {
		return this.arity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.arity;
		result = prime * result + this.name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Predicate)) {
			return false;
		}
		final Predicate other = (Predicate) obj;

		return this.arity == other.getArity() && this.name.equals(other.getName());
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writePredicate(this));
	}

}
