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

import java.util.Iterator;

import org.semanticweb.vlog4j.core.model.api.QueryResult;

import karmaresearch.vlog.Term;
import karmaresearch.vlog.TermQueryResultIterator;

/**
 * Iterates trough all answers to a query. An answer to a query is a
 * {@link QueryResult}. Each query answer is distinct.
 * 
 * @author Irina Dragoste
 *
 */
public class QueryResultIterator implements Iterator<QueryResult>, AutoCloseable {

	private final TermQueryResultIterator vLogTermQueryResultIterator;

	public QueryResultIterator(TermQueryResultIterator termQueryResultIterator) {
		this.vLogTermQueryResultIterator = termQueryResultIterator;
	}

	@Override
	public boolean hasNext() {
		return this.vLogTermQueryResultIterator.hasNext();
	}

	@Override
	public QueryResult next() {
		final Term[] vLogQueryResult = this.vLogTermQueryResultIterator.next();
		return VLogToModelConverter.toQueryResult(vLogQueryResult);
	}

	@Override
	public void close() {
		this.vLogTermQueryResultIterator.close();
	}

}
