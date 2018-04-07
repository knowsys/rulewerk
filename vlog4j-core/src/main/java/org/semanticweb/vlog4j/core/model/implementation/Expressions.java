package org.semanticweb.vlog4j.core.model.implementation;

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

import java.util.Arrays;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * This utilities class provides static methods for creating terms and formulas
 * in vlog4j.
 * 
 * @author Markus Krötzsch
 *
 */
public final class Expressions {
	/**
	 * Private constructor prevents this utilities class to be instantiated.
	 */
	private Expressions() {
	}

	/**
	 * Creates a {@link Variable}.
	 * 
	 * @param name
	 *            name of the variable
	 * @return a {@link Variable} corresponding to the input.
	 */
	public static Variable makeVariable(String name) {
		return new VariableImpl(name);
	}

	/**
	 * Creates a {@link Constant}.
	 * 
	 * @param name
	 *            name of the constant
	 * @return a {@link Constant} corresponding to the input.
	 */
	public static Constant makeConstant(String name) {
		return new ConstantImpl(name);
	}

	/**
	 * Creates a {@link Predicate}.
	 * 
	 * @param name
	 *            non-blank predicate name
	 * @param arity
	 *            predicate arity, strictly greater than 0
	 * @return a {@link Predicate} corresponding to the input.
	 */
	public static Predicate makePredicate(String name, int arity) {
		return new PredicateImpl(name, arity);
	}

	/**
	 * Creates an {@code Atom}.
	 *
	 * @param predicateName
	 *            non-blank {@link Predicate} name
	 * @param terms
	 *            non-empty, non-null list of non-null terms
	 * @return an {@link Atom} with given {@code terms} and {@link Predicate}
	 *         constructed from name given {@code predicateName} and {@code arity}
	 *         given {@code terms} size.
	 */
	public static Atom makeAtom(final String predicateName, final List<Term> terms) {
		final Predicate predicate = makePredicate(predicateName, terms.size());

		return new AtomImpl(predicate, terms);
	}

	/**
	 * Creates an {@code Atom}.
	 *
	 * @param predicateName
	 *            non-blank {@link Predicate} name
	 * @param terms
	 *            non-empty, non-null array of non-null terms
	 * @return an {@link Atom} with given {@code terms} and {@link Predicate}
	 *         constructed from name given {@code predicateName} and {@code arity}
	 *         given {@code terms} length.
	 */
	public static Atom makeAtom(final String predicateName, final Term... terms) {
		final Predicate predicate = makePredicate(predicateName, terms.length);

		return new AtomImpl(predicate, Arrays.asList(terms));
	}

	/**
	 * Creates an {@code Atom}.
	 *
	 * @param predicate
	 *            a non-null {@link Predicate}
	 * @param terms
	 *            non-empty, non-null list of non-null terms. List size must be the
	 *            same as the given {@code predicate} arity.
	 * @return an {@link Atom} corresponding to the input.
	 */
	public static Atom makeAtom(final Predicate predicate, final List<Term> terms) {
		return new AtomImpl(predicate, terms);
	}

	/**
	 * Creates an {@code Atom}.
	 *
	 * @param predicate
	 *            a non-null {@link Predicate}
	 * @param terms
	 *            non-empty, non-null array of non-null terms. Aray size must be the
	 *            same as the given {@code predicate} arity.
	 * @return an {@link Atom} corresponding to the input
	 */
	public static Atom makeAtom(final Predicate predicate, final Term... terms) {
		return new AtomImpl(predicate, Arrays.asList(terms));
	}

	/**
	 * Creates a {@code Conjunction}.
	 *
	 * @param atoms
	 *            list of non-null atoms
	 * @return a {@link Conjunction} corresponding to the input
	 */
	public static Conjunction makeConjunction(final List<Atom> atoms) {
		return new ConjunctionImpl(atoms);
	}

	/**
	 * Creates a {@code Conjunction}.
	 *
	 * @param atoms
	 *            array of non-null atoms
	 * @return a {@link Conjunction} corresponding to the input
	 */
	public static Conjunction makeConjunction(final Atom... atoms) {
		return new ConjunctionImpl(Arrays.asList(atoms));
	}

	/**
	 * Creates a {@code Rule}.
	 *
	 * @param head
	 *            conjunction of atoms
	 * @param body
	 *            conjunction of atoms
	 * @return a {@link Rule} corresponding to the input
	 */
	public static Rule makeRule(final Conjunction head, final Conjunction body) {
		return new RuleImpl(head, body);
	}

	/**
	 * Creates a {@code Rule} with a single atom in its head.
	 *
	 * @param headAtom
	 * @param bodyAtoms
	 *            array of non-null atoms
	 * @return a {@link Rule} corresponding to the input
	 */
	public static Rule makeRule(final Atom headAtom, final Atom... bodyAtoms) {
		return new RuleImpl(new ConjunctionImpl(Arrays.asList(headAtom)),
				new ConjunctionImpl(Arrays.asList(bodyAtoms)));
	}

}
