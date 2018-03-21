package org.semanticweb.vlog4j.core.reasoner;

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
import org.semanticweb.vlog4j.core.reasoner.util.VLogToModelConverter;

import karmaresearch.vlog.StringQueryResultEnumeration;

public class QueryResultIterator implements Iterator<QueryResult> {

	private final StringQueryResultEnumeration stringQueryResultEnumeration;

	public QueryResultIterator(StringQueryResultEnumeration stringQueryResultEnumeration) {
		this.stringQueryResultEnumeration = stringQueryResultEnumeration;
	}

	@Override
	public boolean hasNext() {
		final boolean hasNext = this.stringQueryResultEnumeration.hasMoreElements();
		return hasNext;
	}

	@Override
	public QueryResult next() {
		final String[] vlogQueryResult = this.stringQueryResultEnumeration.nextElement();
		final QueryResult queryResult = VLogToModelConverter.toQueryResult(vlogQueryResult);
		return queryResult;
	}

	public void dispose() {
		this.stringQueryResultEnumeration.cleanup();
	}

}
