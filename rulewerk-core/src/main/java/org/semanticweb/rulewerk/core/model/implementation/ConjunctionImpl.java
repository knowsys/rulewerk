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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;

/**
 * Simple implementation of {@link Conjunction}.
 * 
 * @author Markus Krötzsch
 */
public class ConjunctionImpl<T extends Literal> implements Conjunction<T> {

	final List<? extends T> literals;

	/**
	 * Constructor.
	 * 
	 * @param literals a non-null list of literals, that cannot contain null
	 *                 elements.
	 */
	public ConjunctionImpl(List<? extends T> literals) {
		Validate.noNullElements(literals);
		this.literals = literals;
	}

	@Override
	public List<T> getLiterals() {
		return Collections.unmodifiableList(this.literals);
	}

	@Override
	public Stream<Term> getTerms() {
		return this.literals.stream().flatMap(l -> l.getTerms()).distinct();
	}

	@Override
	public int hashCode() {
		return this.literals.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Conjunction<?>)) {
			return false;
		}
		final Conjunction<?> other = (Conjunction<?>) obj;
		return this.literals.equals(other.getLiterals());
	}

	@Override
	public Iterator<T> iterator() {
		return getLiterals().iterator();
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeLiteralConjunction(this));
	}

}
