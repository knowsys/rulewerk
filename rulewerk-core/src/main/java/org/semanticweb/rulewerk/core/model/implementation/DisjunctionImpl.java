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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Disjunction;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;

/**
 * Simple implementation of {@link Disjunction}.
 *
 * @author Lukas Gerlach
 */
public class DisjunctionImpl<T extends Conjunction<?>> implements Disjunction<T> {

	final List<? extends T> conjunctions;

	/**
	 * Constructor.
	 *
	 * @param conjunctions a non-null list of conjunctions, that cannot contain null
	 *                     elements.
	 */
	public DisjunctionImpl(List<? extends T> conjunctions) {
		Validate.noNullElements(conjunctions);
		this.conjunctions = conjunctions;
	}

	@Override
	public List<T> getConjunctions() {
		return Collections.unmodifiableList(this.conjunctions);
	}

	@Override
	public Stream<Term> getTerms() {
		return this.conjunctions.stream().flatMap(c -> c.getTerms()).distinct();
	}

	@Override
	public int hashCode() {
		return this.conjunctions.size() == 1
			? this.conjunctions.get(0).hashCode()
			: this.conjunctions.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Disjunction<?>)) {
			return false;
		}
		final Disjunction<?> other = (Disjunction<?>) obj;
		return this.conjunctions.equals(other.getConjunctions());
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeDisjunction(this));
	}

}
