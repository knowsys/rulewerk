package org.semanticweb.vlog4j.core.model.api;

import org.semanticweb.vlog4j.core.model.implementation.Serializer;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

/**
 * A Predicate represents a relation between terms. Is uniquely identified by
 * its name and arity. The arity determines the number of terms allowed in the
 * relation. For example, a Predicate with name {@code P} and arity {@code n}
 * allows atomic formulae of the form {@code P(t1,...,tn)}.
 * 
 * @author Irina Dragoste
 *
 */
public interface Predicate extends Entity {

	/**
	 * The name of the Predicate.
	 * 
	 * @return the name of the Predicate.
	 */
	String getName();

	/**
	 * The arity represents the number of terms allowed as relation arguments for
	 * this Predicate. For example, a Predicate with name {@code P} and arity
	 * {@code n} allows atomic formulae of the form {@code P(t1,...,tn)}.
	 * 
	 * @return the arity of the Predicate.
	 */
	int getArity();

	@Override
	default String getSyntacticRepresentation() {
		return Serializer.getString(this);
	}

}
