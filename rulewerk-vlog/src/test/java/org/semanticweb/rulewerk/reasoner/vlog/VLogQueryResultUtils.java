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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;

/**
 * Utility class with static methods used for collecting query results for
 * testing purposes.
 *
 * @author Irina Dragoste
 *
 */
final class VLogQueryResultUtils {

	private VLogQueryResultUtils() {
	}

	/**
	 * Collects TermQueryResultIterator results into a Set. Transforms the array of
	 * {@link Term}s into a set of {@link Term}s. Asserts that the results do not
	 * contain duplicates. Closes the iterator after collecting the results.
	 *
	 * @param queryResultIterator
	 * @return a set of unique query result. A query result is a List of Term
	 *         tuples.
	 */
	static Set<List<Term>> collectResults(final TermQueryResultIterator queryResultIterator) {
		final Set<List<Term>> answers = new HashSet<>();
		queryResultIterator.forEachRemaining(result -> {
			final boolean isUnique = answers.add(Arrays.asList(result));
			assertTrue(isUnique);
		});
		queryResultIterator.close();
		return answers;
	}

	@SuppressWarnings("unchecked")
	private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
		throw (E) e;
	}

	/**
	 * Throw an {@link IOException}, uncheckedly. Needed for testing
	 * {@link VLogReasoner#unsafeForEachInference}.
	 */
	static void sneakilyThrowIOException() {
		sneakyThrow(new IOException());
	}
}
