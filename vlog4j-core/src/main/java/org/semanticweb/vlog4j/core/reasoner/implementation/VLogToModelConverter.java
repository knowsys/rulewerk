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

class VLogToModelConverter {

	static QueryResult toQueryResult(karmaresearch.vlog.Term[] vLogQueryResult) {
		return new QueryResultImpl(toTermList(vLogQueryResult));
	}

	static List<Term> toTermList(karmaresearch.vlog.Term[] vLogTerms) {
		List<Term> terms = new ArrayList<>(vLogTerms.length);
		for (karmaresearch.vlog.Term vLogTerm : vLogTerms) {
			terms.add(toTerm(vLogTerm));
		}
		return terms;
	}

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
