package org.semanticweb.vlog4j.core.reasoner;

import java.util.Iterator;

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
		final String[] nextElement = this.stringQueryResultEnumeration.nextElement();
		final QueryResult queryResult = VLogToModelConverter.toQueryResult(nextElement);
		return queryResult;
	}

	public void dispose() {
		this.stringQueryResultEnumeration.cleanup();
	}

}
