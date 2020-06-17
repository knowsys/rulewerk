package org.semanticweb.rulewerk.reasoner.vlog;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/*
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

import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.implementation.QueryResultImpl;

import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.VLog;

/**
 * Iterates trough all answers to a query. An answer to a query is a
 * {@link QueryResult}. Each query answer is distinct.
 *
 * @author Markus Kroetzsch
 *
 */
public class VLogFastQueryResultIterator implements QueryResultIterator {
	
	/**
	 * Use of Java's LinkedHashMap for implementing a simple LRU cache that is used
	 * here for mapping VLog ids to terms.
	 * 
	 * @author Markus Kroetzsch
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static class SimpleLruMap<K, V> extends LinkedHashMap<K, V> {
		private static final long serialVersionUID = 7151535464938775359L;
		private int maxCapacity;

		public SimpleLruMap(int initialCapacity, int maxCapacity) {
			super(initialCapacity, 0.75f, true);
			this.maxCapacity = maxCapacity;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() >= this.maxCapacity;
		}
	}

	/**
	 * The internal result iterator of VLog, returning numeric ids only.
	 */
	private final karmaresearch.vlog.QueryResultIterator vLogQueryResultIterator;
	/**
	 * The VLog instance. Used for resolving numeric ids to term names.
	 */
	private final VLog vLog;
	/**
	 * VLog ids of the previous tuple, with the last id omitted (since it is not
	 * useful in caching).
	 */
	private long[] prevIds = null;
	/**
	 * RuleWerk terms corresponding to the previously fetched tuple, with the last
	 * term omitted.
	 */
	private Term[] prevTerms = null;
	/**
	 * True if this is the first result that is returned.
	 */
	boolean firstResult = true;
	/**
	 * Size of the tuples returned in this result.
	 */
	int resultSize = -1;
	/**
	 * LRU cache mapping ids to terms.
	 */
	final SimpleLruMap<Long, Term> termCache;

	private final Correctness correctness;

	/**
	 * Create a new {@link VLogFastQueryResultIterator}.
	 * 
	 * @param queryResultIterator
	 * @param materialisationState
	 * @param vLog
	 */
	public VLogFastQueryResultIterator(final karmaresearch.vlog.QueryResultIterator queryResultIterator,
			final Correctness materialisationState, final VLog vLog) {
		this.vLogQueryResultIterator = queryResultIterator;
		this.correctness = materialisationState;
		this.vLog = vLog;
		this.termCache = new SimpleLruMap<Long, Term>(256, 16384);
	}

	@Override
	public boolean hasNext() {
		return this.vLogQueryResultIterator.hasNext();
	}

	@Override
	public QueryResult next() {
		final Term[] terms;
		long[] idTuple = vLogQueryResultIterator.next();
		terms = new Term[idTuple.length];

		if (firstResult) {
			resultSize = terms.length;
			prevIds = new long[resultSize - 1];
			prevTerms = new Term[resultSize - 1];
		}

		int i = 0;
		for (long id : idTuple) {
			if (!firstResult && i < resultSize - 1 && prevIds[i] == id) {
				terms[i] = prevTerms[i];
			} else {
				Term term = this.termCache.get(id);
				if (term == null) {
					try {
						String s = vLog.getConstant(id);
						// This internal handling is copied from VLog's code in {@link
						// karmaresearch.vlog.TermQueryResultIterator}.
						// TODO: the string operation to make null names should possibly be provided by
						// VLog rather than being hardcoded here?
						if (s == null) {
							term = new NamedNullImpl(
									"" + (id >> 40) + "_" + ((id >> 32) & 0377) + "_" + (id & 0xffffffffL));
						} else {
							term = VLogToModelConverter.toConstant(s);
						}
					} catch (NotStartedException e) {
						// Should not happen, we just did a query ...
						throw new RuntimeException(e);
					}
					this.termCache.put(id, term);
				}
				terms[i] = term;
				if (i < resultSize - 1) {
					prevTerms[i] = term;
					prevIds[i] = id;
				}
			}
			i++;
		}

		firstResult = false;
		return new QueryResultImpl(Arrays.asList(terms));
	}

	@Override
	public void close() {
		this.vLogQueryResultIterator.close();
	}

	@Override
	public Correctness getCorrectness() {
		return this.correctness;
	}

}
