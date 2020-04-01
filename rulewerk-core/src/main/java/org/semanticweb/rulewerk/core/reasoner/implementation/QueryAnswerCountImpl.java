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

import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.QueryAnswerCount;

public class QueryAnswerCountImpl implements QueryAnswerCount {

	final private Correctness correctness;
	final private long count;

	/**
	 * Constructor of QueryAnswerSize
	 *
	 * @param correctness of the evaluated query. See {@link Correctness}.
	 *
	 * @param size        number of query answers, i.e. number of facts in the
	 *                    extension of the query.
	 */

	public QueryAnswerCountImpl(Correctness correctness, long size) {
		this.correctness = correctness;
		this.count = size;
	}

	@Override
	public Correctness getCorrectness() {
		return this.correctness;
	}

	@Override
	public long getCount() {
		return this.count;
	}

	@Override
	public String toString() {
		return this.count + " (" + this.correctness.toString() + ")";
	}

}
