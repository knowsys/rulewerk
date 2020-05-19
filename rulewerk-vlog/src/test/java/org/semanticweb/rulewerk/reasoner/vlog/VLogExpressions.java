package org.semanticweb.rulewerk.reasoner.vlog;

/*-
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

/**
 * Utility class with static methods for creating {@code karmaresearch.vlog}
 * objects.
 * 
 * @author Irina.Dragoste
 *
 */
final class VLogExpressions {

	private VLogExpressions() {
	}

	/**
	 * Creates a {@link karmaresearch.vlog.Term} object with given name and type
	 * {@link karmaresearch.vlog.Term.TermType#VARIABLE}
	 * 
	 * @param name term name
	 * @return a {@link karmaresearch.vlog.Term.TermType#VARIABLE} type term with
	 *         given name.
	 */
	static karmaresearch.vlog.Term makeVariable(final String name) {
		final karmaresearch.vlog.Term variable = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				name);
		return variable;
	}

	/**
	 * Creates a {@link karmaresearch.vlog.Term} object with given name and type
	 * {@link karmaresearch.vlog.Term.TermType#CONSTANT}
	 * 
	 * @param name term name
	 * @return a {@link karmaresearch.vlog.Term.TermType#CONSTANT} type term with
	 *         given name.
	 */
	static karmaresearch.vlog.Term makeConstant(final String name) {
		final karmaresearch.vlog.Term constant = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				name);
		return constant;
	}

	/**
	 * Creates a positive {@link karmaresearch.vlog.Atom} object with given
	 * predicate name and terms.
	 * 
	 * @param predicateName the name of the internal vlog atom predicate.
	 * @param terms         atom terms.
	 * @return an {@link karmaresearch.vlog.Atom} object with given
	 *         {@code predicateName} and {@link karmaresearch.vlog.Term}
	 *         {@code terms}.
	 */
	static karmaresearch.vlog.Atom makeAtom(final String predicateName, final karmaresearch.vlog.Term... terms) {
		return new karmaresearch.vlog.Atom(predicateName, terms);
	}

	/**
	 * Creates a negated {@link karmaresearch.vlog.Atom} object with given predicate
	 * name and terms, and negated value {@code true}.
	 * 
	 * @param predicateName the name of the internal vlog atom predicate.
	 * @param terms         atom terms.
	 * @return an {@link karmaresearch.vlog.Atom} object with given
	 *         {@code predicateName} and {@link karmaresearch.vlog.Term}
	 *         {@code terms}.
	 */
	static karmaresearch.vlog.Atom makeNegatedAtom(final String predicateName, final karmaresearch.vlog.Term... terms) {
		return new karmaresearch.vlog.Atom(predicateName, true, terms);
	}

	/**
	 * Creates a {@link karmaresearch.vlog.Rule} object with given head and body
	 * conjuncts.
	 * 
	 * @param headAtom  rule head atom
	 * @param bodyAtoms rule body conjuncts
	 * @return a {@link karmaresearch.vlog.Rule} object with given {@code headAtom}
	 *         and body conjuncts ({@code bodyAtoms}).
	 */
	static karmaresearch.vlog.Rule makeRule(final karmaresearch.vlog.Atom headAtom,
			final karmaresearch.vlog.Atom... bodyAtoms) {
		return new karmaresearch.vlog.Rule(new karmaresearch.vlog.Atom[] { headAtom }, bodyAtoms);
	}

}
