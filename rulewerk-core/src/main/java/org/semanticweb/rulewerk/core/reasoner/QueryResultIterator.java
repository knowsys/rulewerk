package org.semanticweb.rulewerk.core.reasoner;

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

import java.util.Iterator;

import org.semanticweb.rulewerk.core.model.api.QueryResult;

/**
 * Iterator for {@link QueryResult}s.
 * 
 * @author Irina Dragoste
 *
 */
public interface QueryResultIterator extends Iterator<QueryResult>, AutoCloseable {

	/**
	 * Returns the correctness of the query result.
	 * <ul>
	 * <li>If {@link Correctness#SOUND_AND_COMPLETE}, the query results are
	 * guaranteed to be correct.</li>
	 * <li>If {@link Correctness#SOUND_BUT_INCOMPLETE}, the results are guaranteed
	 * to be sound, but may be incomplete.</li>
	 * <li>If {@link Correctness#INCORRECT}, the results may be incomplete, and some
	 * results may be unsound.
	 * </ul>
	 * 
	 * @return query result correctness
	 */
	public Correctness getCorrectness();

	@Override
	public void close();
}
