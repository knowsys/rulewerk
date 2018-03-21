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

import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.impl.ConstantImpl;
import org.semanticweb.vlog4j.core.model.validation.IllegalEntityNameException;
import org.semanticweb.vlog4j.core.reasoner.QueryResult;

public class VLogToModelConverter {

	public static QueryResult toQueryResult(String[] vlogQueryResult) {
		return new QueryResult(toGroundTermsArray(vlogQueryResult));
	}

	private static List<Term> toGroundTermsArray(String[] vlogGroundTerms) {
		// TODO support blanks
		final List<Term> groundTerms = new ArrayList<>();
		for (final String term : vlogGroundTerms) {
			Term groundTerm;
			try {
				groundTerm = new ConstantImpl(term);
				groundTerms.add(groundTerm);
			} catch (final IllegalEntityNameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return groundTerms;
	}

}
