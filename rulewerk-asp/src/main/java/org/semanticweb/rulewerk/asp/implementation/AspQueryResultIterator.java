package org.semanticweb.rulewerk.asp.implementation;

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
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.implementation.QueryResultImpl;

import java.util.Iterator;
import java.util.Set;

/**
 * Iterates through all answers to a query that asks for all literals with a specific predicate in an answer set.
 * Each answer is a {@link QueryResult}. Each answer is distinct.
 *
 * @author Philipp Hanisch
 */
public class AspQueryResultIterator implements QueryResultIterator {

	final private Iterator<Literal> literalIterator;

	/**
	 * Constructor.
	 *
	 * @param answers set of literals that are an answer to the query
	 */
	public AspQueryResultIterator(Set<Literal> answers) {
		Validate.notNull(answers);
		Validate.noNullElements(answers);
		literalIterator = answers.iterator();
	}

	@Override
	public Correctness getCorrectness() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
	}

	@Override
	public boolean hasNext() {
		return literalIterator.hasNext();
	}

	@Override
	public QueryResult next() {
		return new QueryResultImpl(literalIterator.next().getArguments());
	}
}
