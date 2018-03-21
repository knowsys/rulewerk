package org.semanticweb.vlog4j.core.model.impl;

import java.util.Collections;

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

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.QueryResult;

public final class QueryResultImpl implements QueryResult {

	private final List<Constant> constants;

	public QueryResultImpl(List<Constant> constants) {
		Validate.noNullElements(constants);
		this.constants = constants;
	}

	@Override
	public List<Constant> getConstants() {
		return Collections.unmodifiableList(this.constants);
	}

	@Override
	public int hashCode() {
		return this.constants.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof QueryResult)) {
			return false;
		}
		final QueryResult other = (QueryResult) obj;
		return this.constants.equals(other.getConstants());
	}

	@Override
	public String toString() {
		return "QueryResult [constants=" + this.constants + "]";
	}

}
