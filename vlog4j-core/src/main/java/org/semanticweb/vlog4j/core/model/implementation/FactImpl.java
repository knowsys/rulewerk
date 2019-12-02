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

import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.StatementVisitor;
import org.semanticweb.vlog4j.core.model.api.Term;

/**
 * Standard implementation of the {@Fact} interface.
 * 
 * @author Markus Kroetzsch
 *
 */
public class FactImpl extends PositiveLiteralImpl implements Fact {

	public FactImpl(Predicate predicate, List<Term> terms) {
		super(predicate, terms);
		for (Term t : terms) {
			if (t.isVariable())
				throw new IllegalArgumentException("Facts cannot contain variables.");
		}
	}

	@Override
	public <T> T accept(StatementVisitor<T> statementVisitor) {
		return statementVisitor.visit(this);
	}

	@Override
	public String toString() {
		return getSyntacticRepresentation();
	}

}
