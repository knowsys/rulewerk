package org.semanticweb.rulewerk.core.model.api;

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

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Interface for literals. A positive literal is simply an atomic formula, i.e.,
 * a formula of the form P(t1,...,tn) where P is a {@link Predicate} of arity n
 * and t1,...,tn are {@link Term}s. A negative literal is a negated atomic
 * formula.
 *
 * @author david.carral@tu-dresden.de
 * @author Irina Dragoste
 * @author Lukas Gerlach
 */

// this would also be possible (including other changes)
// but it is not ideal since PositiveLiteral now also extends Conjunction<Literal>
// while it should really extend Conjunction<PositiveLiteral> (but maybe this is not too much of an issue...)
//
// public interface Literal extends Conjunction<Literal> {

// this produces a so called RawType when used without type argument, which seems to be generally considered
// bad practice because some type checking is not done for such types
// maybe this would not be an issue for us...
// what we actually would need is a self referential type that we can pass to Conjunction...
public interface Literal<T extends Literal> extends Conjunction<T> {

	boolean isNegated();

	/**
	 * The literal predicate.
	 *
	 * @return the literal predicate.
	 */
	Predicate getPredicate();

	/**
	 * The list of terms representing the tuple arguments.
	 *
	 * @return an unmodifiable list of terms with the same size as the
	 *         {@link Predicate} arity.
	 */
	List<Term> getArguments();

	@Override
	default Iterator<T> iterator() {
		return getLiterals().iterator();
	}

}
