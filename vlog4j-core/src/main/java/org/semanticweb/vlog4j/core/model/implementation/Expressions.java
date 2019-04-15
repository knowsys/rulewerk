package org.semanticweb.vlog4j.core.model.implementation;

import java.util.ArrayList;

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

import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.NegativeLiteral;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
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
	 * @param name name of the variable
	 * @return a {@link Variable} corresponding to the input.
	 */
	public static Variable makeVariable(String name) {
		return new VariableImpl(name);
	}

	/**
	 * Creates a {@link Constant}.
	 * 
	 * @param name name of the constant
	 * @return a {@link Constant} corresponding to the input.
	 */
	public static Constant makeConstant(String name) {
		return new ConstantImpl(name);
	}

	/**
	 * Creates a {@link Predicate}.
	 * 
	 * @param name  non-blank predicate name
	 * @param arity predicate arity, strictly greater than 0
	 * @return a {@link Predicate} corresponding to the input.
	 */
	public static Predicate makePredicate(String name, int arity) {
		return new PredicateImpl(name, arity);
	}

	/**
	 * Creates a {@code PositiveLiteral}.
	 *
	 * @param predicateName non-blank {@link Predicate} name
	 * @param terms         non-empty, non-null list of non-null terms
	 * @return a {@link PositiveLiteral} with given {@code terms} and
	 *         {@link Predicate} constructed from name given {@code predicateName}
	 *         and {@code arity} given {@code terms} size.
	 */
	public static PositiveLiteral makePositiveLiteral(final String predicateName, final List<Term> terms) {
		final Predicate predicate = makePredicate(predicateName, terms.size());

		return new PositiveLiteralImpl(predicate, terms);
	}

	/**
	 * Creates a {@code PositiveLiteral}.
	 *
	 * @param predicateName non-blank {@link Predicate} name
	 * @param terms         non-empty, non-null array of non-null terms
	 * @return a {@link PositiveLiteral} with given {@code terms} and
	 *         {@link Predicate} constructed from name given {@code predicateName}
	 *         and {@code arity} given {@code terms} length.
	 */
	public static PositiveLiteral makePositiveLiteral(final String predicateName, final Term... terms) {
		final Predicate predicate = makePredicate(predicateName, terms.length);

		return new PositiveLiteralImpl(predicate, Arrays.asList(terms));
	}

	/**
	 * Creates a {@code PositiveLiteral}.
	 *
	 * @param predicate a non-null {@link Predicate}
	 * @param terms     non-empty, non-null list of non-null terms. List size must
	 *                  be the same as the given {@code predicate} arity.
	 * @return a {@link PositiveLiteral} corresponding to the input.
	 */
	public static PositiveLiteral makePositiveLiteral(final Predicate predicate, final List<Term> terms) {
		return new PositiveLiteralImpl(predicate, terms);
	}

	/**
	 * Creates a {@code PositiveLiteral}.
	 *
	 * @param predicate a non-null {@link Predicate}
	 * @param terms     non-empty, non-null array of non-null terms. Array size must
	 *                  be the same as the given {@code predicate} arity.
	 * @return a {@link PositiveLiteral} corresponding to the input
	 */
	public static PositiveLiteral makePositiveLiteral(final Predicate predicate, final Term... terms) {
		return new PositiveLiteralImpl(predicate, Arrays.asList(terms));
	}

	/**
	 * Creates a {@code NegativeLiteral}.
	 *
	 * @param predicateName non-blank {@link Predicate} name
	 * @param terms         non-empty, non-null list of non-null terms
	 * @return a {@link NegativeLiteral} with given {@code terms} and
	 *         {@link Predicate} constructed from name given {@code predicateName}
	 *         and {@code arity} given {@code terms} size.
	 */
	public static NegativeLiteral makeNegativeLiteral(final String predicateName, final List<Term> terms) {
		final Predicate predicate = makePredicate(predicateName, terms.size());

		return new NegativeLiteralImpl(predicate, terms);
	}

	/**
	 * Creates a {@code NegativeLiteral}.
	 *
	 * @param predicateName non-blank {@link Predicate} name
	 * @param terms         non-empty, non-null array of non-null terms
	 * @return a {@link NegativeLiteral} with given {@code terms} and
	 *         {@link Predicate} constructed from name given {@code predicateName}
	 *         and {@code arity} given {@code terms} length.
	 */
	public static NegativeLiteral makeNegativeLiteral(final String predicateName, final Term... terms) {
		final Predicate predicate = makePredicate(predicateName, terms.length);

		return new NegativeLiteralImpl(predicate, Arrays.asList(terms));
	}

	/**
	 * Creates a {@code NegativeLiteral}.
	 *
	 * @param predicate a non-null {@link Predicate}
	 * @param terms     non-empty, non-null list of non-null terms. List size must
	 *                  be the same as the given {@code predicate} arity.
	 * @return a {@link NegativeLiteral} corresponding to the input.
	 */
	public static NegativeLiteral makeNegativeLiteral(final Predicate predicate, final List<Term> terms) {
		return new NegativeLiteralImpl(predicate, terms);
	}

	/**
	 * Creates a {@code NegativeLiteral}.
	 *
	 * @param predicate a non-null {@link Predicate}
	 * @param terms     non-empty, non-null array of non-null terms. Array size must
	 *                  be the same as the given {@code predicate} arity.
	 * @return a {@link NegativeLiteral} corresponding to the input
	 */
	public static NegativeLiteral makeNegativeLiteral(final Predicate predicate, final Term... terms) {
		return new NegativeLiteralImpl(predicate, Arrays.asList(terms));
	}

	/**
	 * Creates a {@code Conjunction} of {@link T} ({@link Literal} type) objects.
	 *
	 * @param literals list of non-null literals
	 * @return a {@link Conjunction} corresponding to the input
	 */
	public static <T extends Literal> Conjunction<T> makeConjunction(final List<T> literals) {
		return new ConjunctionImpl<>(literals);
	}

	/**
	 * Creates a {@code Conjunction} of {@link Literal} objects.
	 *
	 * @param literals array of non-null literals
	 * @return a {@link Conjunction} corresponding to the input
	 */
	public static Conjunction<Literal> makeConjunction(final Literal... literals) {
		return new ConjunctionImpl<>(Arrays.asList(literals));
	}

	/**
	 * Creates a {@code Conjunction} of {@link PositiveLiteral} objects.
	 *
	 * @param literals array of non-null positive literals
	 * @return a {@link Conjunction} corresponding to the input
	 */
	public static Conjunction<PositiveLiteral> makePositiveConjunction(final PositiveLiteral... literals) {
		return new ConjunctionImpl<>(Arrays.asList(literals));
	}

	/**
	 * Creates a {@code Rule} with a single atom in its head.
	 *
	 * @param headLiteral  the single positive literal in the rule head
	 * @param bodyLiterals array of non-null literals
	 * @return a {@link Rule} corresponding to the input
	 */
	public static Rule makeRule(final PositiveLiteral headLiteral, final Literal... bodyLiterals) {
		return new RuleImpl(new ConjunctionImpl<>(Arrays.asList(headLiteral)),
				new ConjunctionImpl<>(Arrays.asList(bodyLiterals)));
	}
	
	/**
	 * Creates a {@code Rule}.
	 *
	 * @param head conjunction of positive (non-negated) literals
	 * @param body conjunction of literals (negated or not)
	 * @return a {@link Rule} corresponding to the input
	 */
	public static Rule makeRule(final Conjunction<PositiveLiteral> head, final Conjunction<Literal> body) {
		return new RuleImpl(head, body);
	}

	/**
	 * Creates a {@code Rule}.
	 *
	 * @param head conjunction of positive (non-negated) literals
	 * @param body conjunction of positive (non-negated) literals
	 * @return a {@link Rule} corresponding to the input
	 */
	public static Rule makePositiveLiteralsRule(final Conjunction<PositiveLiteral> head, final Conjunction<PositiveLiteral> body) {
		final List<Literal> bodyLiteralList = new ArrayList<>(body.getLiterals());
		@NonNull
		final Conjunction<Literal> literalsBody = makeConjunction(bodyLiteralList);
		return new RuleImpl(head, literalsBody);
	
	}

}
