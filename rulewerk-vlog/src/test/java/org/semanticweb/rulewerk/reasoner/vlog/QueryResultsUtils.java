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

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;

/**
 * Utility class with static methods for collecting the results of a query for
 * testing purposes.
 *
 * @author Irina Dragoste
 *
 */
final class QueryResultsUtils {

	/*
	 * This is a utility class. Therefore, it is best practice to do the following:
	 * (1) Make the class final, (2) make its constructor private, (3) make all its
	 * fields and methods static. This prevents the classes instantiation and
	 * inheritance.
	 */
	private QueryResultsUtils() {

	}

	/**
	 * Iterates trough all the the results and collects their term lists in a Set.
	 * Asserts that there are no duplicate results. Closes the iterator after
	 * collecting the last result.
	 *
	 * @param queryResultIterator iterator for all {@link QueryResult}s of a query.
	 * @return a set of all query results terms ({@link QueryResult#getTerms()}).
	 */
	static Set<List<Term>> collectQueryResults(final QueryResultIterator queryResultIterator) {
		final Set<List<Term>> results = new HashSet<>();
		queryResultIterator.forEachRemaining(queryResult -> {
			final boolean isUnique = results.add(queryResult.getTerms());
			assertTrue(isUnique);
		});
		queryResultIterator.close();
		return results;
	}

}
