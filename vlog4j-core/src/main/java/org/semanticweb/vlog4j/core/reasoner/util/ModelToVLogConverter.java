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
import org.semanticweb.vlog4j.core.model.Rule;
import org.semanticweb.vlog4j.core.model.Term;
import org.semanticweb.vlog4j.core.model.TermType;

public class ModelToVLogConverter {

	public static karmaresearch.vlog.Term toVLogTerm(final Term term) {
		// TODO treat null case: throw exception or return null?
		// TODO visitor pattern
		// FIXME perhaps declare enum class only in VLog project, and use
		// karmaresearch.vlog.Term.EnumType dependency
		// FIXME The names of variables and constants need to be appropriately modified so VLog recognises them as such.
		// Notes: Upper case for variables; lower case for constants? enclose constants with <>?
		final TermType type = term.getType();
		final karmaresearch.vlog.Term.TermType vlogTermType = type == TermType.CONSTANT ? karmaresearch.vlog.Term.TermType.CONSTANT
				: karmaresearch.vlog.Term.TermType.VARIABLE;
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(vlogTermType, term.getName());
		return vLogTerm;
	}

	public static karmaresearch.vlog.Term[] toVLogTermArray(final List<Term> terms) {
		// TODO treat null case: throw exception or return null?
		final karmaresearch.vlog.Term[] vlogTerms = new karmaresearch.vlog.Term[terms.size()];
		for (int i = 0; i < vlogTerms.length; i++) {
			vlogTerms[i] = toVLogTerm(terms.get(i));
		}
		return vlogTerms;
	}

	public static karmaresearch.vlog.Atom toVLogAtom(final Atom atom) {
		// TODO treat null case: throw exception or return null?
		final karmaresearch.vlog.Term[] terms = toVLogTermArray(atom.getArguments());
		// FIXME: Append arity at the end of the predicate name (punning)
		// FIXME: should we generate predicate names by appending the terms arity to the
		// given name?
		final karmaresearch.vlog.Atom vLogAtom = new karmaresearch.vlog.Atom(atom.getPredicateName(), terms);
		return vLogAtom;
	}

	public static karmaresearch.vlog.Atom[] toVLogAtomArray(final List<Atom> atoms) {
		// TODO treat null case: throw exception or return null?
		final karmaresearch.vlog.Atom[] vlogAtoms = new karmaresearch.vlog.Atom[atoms.size()];
		for (int i = 0; i < atoms.size(); i++) {
			vlogAtoms[i] = toVLogAtom(atoms.get(i));
		}
		return vlogAtoms;
	}

	public static karmaresearch.vlog.Rule toVLogRule(final Rule rule) {
		// TODO treat null case: throw exception or return null?
		final karmaresearch.vlog.Atom[] vlogHead = toVLogAtomArray(rule.getHead());
		final karmaresearch.vlog.Atom[] vlogBody = toVLogAtomArray(rule.getBody());
		return new karmaresearch.vlog.Rule(vlogHead, vlogBody);
	}

	public static karmaresearch.vlog.Rule[] toVLogRuleArray(final List<Rule> rules) {
		// TODO treat null case: throw exception or return null?
		final karmaresearch.vlog.Rule[] vlogRules = new karmaresearch.vlog.Rule[rules.size()];
		for (int i = 0; i < rules.size(); i++) {
			vlogRules[i] = toVLogRule(rules.get(i));
		}
		return vlogRules;
	}
}
