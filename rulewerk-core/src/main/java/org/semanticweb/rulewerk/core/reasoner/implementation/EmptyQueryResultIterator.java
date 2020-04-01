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

import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;

/**
 * Iterator that represents an empty query result.
 *
 * @author Markus Kroetzsch
 *
 */
public class EmptyQueryResultIterator implements QueryResultIterator {

	final Correctness correctness;

	public EmptyQueryResultIterator(Correctness correctness) {
		this.correctness = correctness;
	}

	@Override
	public void close() {
		// nothing to do
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public QueryResult next() {
		return null;
	}

	public Correctness getCorrectness() {
		return this.correctness;
	}

}
