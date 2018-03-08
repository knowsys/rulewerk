package org.semanticweb.vlog4j.core.reasoner.util;

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

import org.semanticweb.vlog4j.core.model.Atom;
import org.semanticweb.vlog4j.core.model.Term;
import org.semanticweb.vlog4j.core.model.TermType;

public class ModelToVLogConverter {

	public static karmaresearch.vlog.Term toVLogTerm(Term term) {
		// TODO treat null case: throw exception or return null?

		// FIXME perhaps declare enum class only in VLog project, and use
		// karmaresearch.vlog.Term.EnumType dependency
		TermType type = term.getType();
		karmaresearch.vlog.Term.TermType vlogTermType = type == TermType.CONSTANT
				? karmaresearch.vlog.Term.TermType.CONSTANT
				: karmaresearch.vlog.Term.TermType.VARIABLE;
		karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(vlogTermType, term.getName());
		return vLogTerm;
	}

	public static karmaresearch.vlog.Atom toVLogAtom(Atom atom) {
		// TODO treat null case: throw exception or return null?

		karmaresearch.vlog.Term[] terms = toVLogTermArray(atom.getArguments());
		// FIXME: should we generate predicate names by appending the terms arity to the
		// given name?
		karmaresearch.vlog.Atom vLogAtom = new karmaresearch.vlog.Atom(atom.getPredicateName(), terms);
		return vLogAtom;
	}
	
	

	private static karmaresearch.vlog.Term[] toVLogTermArray(List<Term> arguments) {
		karmaresearch.vlog.Term[] terms = new karmaresearch.vlog.Term[arguments.size()];
		for (int i = 0; i < terms.length; i++) {
			terms[i] = toVLogTerm(arguments.get(i));
		}
		return terms;
	}

}
