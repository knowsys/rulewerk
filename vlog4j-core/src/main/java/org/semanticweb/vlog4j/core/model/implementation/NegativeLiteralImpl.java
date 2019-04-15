package org.semanticweb.vlog4j.core.model.implementation;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.NegativeLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;

public class NegativeLiteralImpl extends AbstractLiteral implements NegativeLiteral {

	public NegativeLiteralImpl(@NonNull Predicate predicate, @NonNull List<Term> terms) {
		super(predicate, terms);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.isNegated() ? 1231 : 1237);
		result = prime * result + this.getPredicate().hashCode();
		result = prime * result + this.getTerms().hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NegativeLiteral)) {
			return false;
		}
		final NegativeLiteral other = (NegativeLiteral) obj;

		return this.getPredicate().equals(other.getPredicate()) && this.getTerms().equals(other.getTerms());
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder("~");
		stringBuilder.append(this.getPredicate().getName()).append("(");
		boolean first = true;
		for (final Term term : this.getTerms()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(term);
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}