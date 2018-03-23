package org.semanticweb.vlog4j.core.reasoner.util;

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

import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.impl.Expressions;

public class VLogToModelConverter {

	public static QueryResult toQueryResult(String[] vLogQueryResult) {
		return new QueryResultImpl(toConstantList(vLogQueryResult));
	}

	private static List<Constant> toConstantList(String[] vLogGroundTerms) {
		// TODO support blanks (now we assume every query result term is a named
		// individual)
		final List<Constant> constants = new ArrayList<>();
		for (final String term : vLogGroundTerms) {
			final Constant groundTerm = Expressions.makeConstant(term);
			constants.add(groundTerm);
		}
		return constants;
	}

}
