package org.semanticweb.rulewerk.asp.model;

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

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;

import java.util.Set;

/**
 * An answer set represents a set of ground literals.
 *
 * @author Philipp Hanisch
 */
public interface AnswerSet {

	/**
	 * Gets a query result iterator for a specific predicate.
	 *
	 * @param predicate the predicate of interest
	 * @return query result iterator
	 */
	QueryResultIterator getQueryResults(Predicate predicate);

	/**
	 * Gets a query result iterator for the given query. A query result is a ground literal in the answer set whose
	 * constants agree with the constants in the query.
	 *
	 * @param query the query literal
	 * @return		a query result iterator
	 */
	QueryResultIterator getQueryResults(PositiveLiteral query);

	/**
	 * Gets all literals contained in the answer set.
	 *
	 * @return list of literals
	 */
	Set<Literal> getLiterals();

	/**
	 * Gets the literals contained in the answer set for a specific predicate.
	 *
	 * @param predicate the predicate of interest
	 * @return list of literals
	 */
	Set<Literal> getLiterals(Predicate predicate);
}
