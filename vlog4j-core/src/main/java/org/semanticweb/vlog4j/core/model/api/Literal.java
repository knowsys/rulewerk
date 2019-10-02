package org.semanticweb.vlog4j.core.model.api;

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

import java.util.List;

/**
 * Interface for literals. A literal is predicate applied to a tuple of terms,
 * with a positive or negative polarity. An atomic formula is a formula of the
 * form P(t1,...,tn) for P a {@link Predicate} name, and t1,...,tn some
 * {@link Term}s. A Literal is a positive or negated atomic formula. The number
 * of terms in the tuple corresponds to the {@link Predicate} arity.
 *
 * @author david.carral@tu-dresden.de
 * @author Irina Dragoste
 */
public interface Literal extends SyntaxObject {

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

}
