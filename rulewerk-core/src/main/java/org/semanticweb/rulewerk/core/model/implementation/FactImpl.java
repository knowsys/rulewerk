package org.semanticweb.rulewerk.core.model.implementation;

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

import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.StatementVisitor;
import org.semanticweb.rulewerk.core.model.api.Term;

/**
 * Standard implementation of the {@link Fact} interface.
 * 
 * @author Markus Kroetzsch
 *
 */
public class FactImpl extends PositiveLiteralImpl implements Fact {

	public FactImpl(final Predicate predicate, final List<Term> terms) {
		super(predicate, terms);
		for (final Term t : terms) {
			if (t.isVariable()) {
				throw new IllegalArgumentException("Facts cannot contain variables.");
			}
		}
	}

	@Override
	public <T> T accept(final StatementVisitor<T> statementVisitor) {
		return statementVisitor.visit(this);
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeFact(this));
	}

}
