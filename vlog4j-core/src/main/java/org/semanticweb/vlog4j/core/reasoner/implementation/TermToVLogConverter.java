package org.semanticweb.vlog4j.core.reasoner.implementation;

/*-
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

import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.TermVisitor;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * A visitor that converts {@link Term}s of different types to corresponding
 * internal VLog model {@link karmaresearch.vlog.Term}s.
 * 
 * @author Irina Dragoste
 *
 */
class TermToVLogConverter implements TermVisitor<karmaresearch.vlog.Term> {

	/**
	 * Transforms a Constant to a {@link karmaresearch.vlog.Term} with the same name
	 * and type {@link karmaresearch.vlog.Term.TermType#CONSTANT}.
	 */
	@Override
	public karmaresearch.vlog.Term visit(Constant term) {
		return new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, term.getName());
	}

	/**
	 * Transforms a Variable to a {@link karmaresearch.vlog.Term} with the same name
	 * and type {@link karmaresearch.vlog.Term.TermType#VARIABLE}.
	 */
	@Override
	public karmaresearch.vlog.Term visit(Variable term) {
		return new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, term.getName());
	}

	/**
	 * Transforms a Blank to a {@link karmaresearch.vlog.Term} with the same name
	 * and type {@link karmaresearch.vlog.Term.TermType#BLANK}.
	 */
	@Override
	public karmaresearch.vlog.Term visit(Blank term) {
		return new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK, term.getName());
	}

}
