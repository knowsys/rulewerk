package org.semanticweb.vlog4j.core.reasoner.implementation;

/*
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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.BlankImpl;
import org.semanticweb.vlog4j.core.model.implementation.ConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.VariableImpl;

/**
 * Utility class with static methods for converting from VLog internal model
 * ({@code karmaresearch.vlog} objects) to VLog API model
 * ({@code org.semanticweb.vlog4j.core.model.api}) objects.
 * 
 * @author Irina Dragoste
 *
 */
class VLogToModelConverter {

	/**
	 * Converts internal VLog query results (represented as arrays of
	 * {@link karmaresearch.vlog.Term}s) into VLog model API QueryResults.
	 * 
	 * @param vLogQueryResult
	 *            an array of terms that represent an answer to a query.
	 * @return a QueryResult containing the corresponding {@code vLogQueryResult} as
	 *         a List of {@link Term}s.
	 */
	static QueryResult toQueryResult(karmaresearch.vlog.Term[] vLogQueryResult) {
		return new QueryResultImpl(toTermList(vLogQueryResult));
	}

	/**
	 * Converts an array of internal VLog terms ({@link karmaresearch.vlog.Term})
	 * into the corresponding list of VLog API model {@link Term}.
	 * 
	 * @param vLogTerms
	 *            input terms array, to be converted to a list of corresponding
	 *            {@link Term}s.
	 * @return list of {@link Term}s, where each element corresponds to the element
	 *         in given {@code vLogTerms} at the same position.
	 */
	static List<Term> toTermList(karmaresearch.vlog.Term[] vLogTerms) {
		List<Term> terms = new ArrayList<>(vLogTerms.length);
		for (karmaresearch.vlog.Term vLogTerm : vLogTerms) {
			terms.add(toTerm(vLogTerm));
		}
		return terms;
	}

	/**
	 * Converts an internal VLog term ({@link karmaresearch.vlog.Term}) to a VLog
	 * API model {@link Term} of the same type and name.
	 * 
	 * @param vLogTerm
	 *            term to be converted
	 * @return a ({@link karmaresearch.vlog.Term}) with the same name as given
	 *         {@code vLogTerm} and of the corresponding type.
	 */
	static Term toTerm(karmaresearch.vlog.Term vLogTerm) {
		String name = vLogTerm.getName();
		switch (vLogTerm.getTermType()) {
		case CONSTANT:
			return new ConstantImpl(name);
		case BLANK:
			return new BlankImpl(name);
		case VARIABLE:
			return new VariableImpl(name);
		default:
			throw new IllegalArgumentException("Unexpected vlog term type: " + vLogTerm.getTermType());
		}
	}

}
