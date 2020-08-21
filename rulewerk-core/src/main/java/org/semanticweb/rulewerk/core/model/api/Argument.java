package org.semanticweb.rulewerk.core.model.api;

/*-
 * #%L
 * Rulewerk Parser
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

import java.util.Optional;
import java.util.function.Function;

/**
 * A tagged union representing the possible types allowed to appear as arguments
 * in commands and parser directives.
 *
 * @author Maximilian Marx
 */
public abstract class Argument {
	private Argument() {
	}

	/**
	 * Apply a function to the contained value.
	 *
	 * @param termHandler            the function to apply to a Term
	 * @param ruleHandler            the function to apply to a Rule
	 * @param positiveLiteralHandler the function to apply to a Literal
	 *
	 * @return the value returned by the appropriate handler function
	 */
	public abstract <V> V apply(Function<? super Term, ? extends V> termHandler,
			Function<? super Rule, ? extends V> ruleHandler,
			Function<? super PositiveLiteral, ? extends V> positiveLiteralHandler);

	/**
	 * Partially compare two arguments, without comparing the actual values.
	 *
	 * @param other the Object to compare to.
	 *
	 * @return An {@link Optional} containing true if the arguments are surely
	 *         equal, containing false if the arguments are not equal, or an empty
	 *         Optional if the values of the arguments need to be compared.
	 *
	 */
	protected Optional<Boolean> isEqual(Object other) {
		if (other == null) {
			return Optional.of(false);
		}

		if (other == this) {
			return Optional.of(true);
		}

		if (!(other instanceof Argument)) {
			return Optional.of(false);
		}

		return Optional.empty();
	}

	/**
	 * Create an argument containing a Term.
	 *
	 * @param value the Term value
	 *
	 * @return An argument containing the given Term value
	 */
	public static Argument term(Term value) {
		return new Argument() {
			@Override
			public <V> V apply(Function<? super Term, ? extends V> termHandler,
					Function<? super Rule, ? extends V> ruleHandler,
					Function<? super PositiveLiteral, ? extends V> positiveLiteralHandler) {
				return termHandler.apply(value);
			}

			@Override
			public boolean equals(Object other) {
				Optional<Boolean> maybeEquals = isEqual(other);

				if (maybeEquals.isPresent()) {
					return maybeEquals.get();
				}

				Argument otherArgument = (Argument) other;
				return otherArgument.apply(term -> term.equals(value), rule -> false, positiveLiteral -> false);
			}

			@Override
			public int hashCode() {
				return 47 * value.hashCode();
			}

			@Override
			public String toString() {
				return value.toString();
			}
		};
	}

	/**
	 * Create an argument containing a Rule.
	 *
	 * @param value the Rule value
	 *
	 * @return An argument containing the given Rule value
	 */
	public static Argument rule(Rule value) {
		return new Argument() {
			@Override
			public <V> V apply(Function<? super Term, ? extends V> termHandler,
					Function<? super Rule, ? extends V> ruleHandler,
					Function<? super PositiveLiteral, ? extends V> positiveLiteralHandler) {
				return ruleHandler.apply(value);
			}

			@Override
			public boolean equals(Object other) {
				Optional<Boolean> maybeEquals = isEqual(other);

				if (maybeEquals.isPresent()) {
					return maybeEquals.get();
				}

				Argument otherArgument = (Argument) other;
				return otherArgument.apply(term -> false, rule -> rule.equals(value), positiveLiteral -> false);
			}

			@Override
			public int hashCode() {
				return 53 * value.hashCode();
			}

			@Override
			public String toString() {
				return value.toString();
			}
		};
	}

	/**
	 * Create an argument containing a PositiveLiteral.
	 *
	 * @param value the PositiveLiteral value
	 *
	 * @return An argument containing the given PositiveLiteral value
	 */
	public static Argument positiveLiteral(PositiveLiteral value) {
		return new Argument() {
			@Override
			public <V> V apply(Function<? super Term, ? extends V> termHandler,
					Function<? super Rule, ? extends V> ruleHandler,
					Function<? super PositiveLiteral, ? extends V> positiveLiteralHandler) {
				return positiveLiteralHandler.apply(value);
			}

			@Override
			public boolean equals(Object other) {
				Optional<Boolean> maybeEquals = isEqual(other);

				if (maybeEquals.isPresent()) {
					return maybeEquals.get();
				}

				Argument otherArgument = (Argument) other;
				return otherArgument.apply(term -> false, rule -> false,
						positiveLiteral -> positiveLiteral.equals(value));
			}

			@Override
			public int hashCode() {
				return 59 * value.hashCode();
			}

			@Override
			public String toString() {
				return value.toString();
			}
		};
	}

	/**
	 * Create an optional from a (possible) Term value.
	 *
	 * @return An optional containing the contained Term, or an empty Optional if
	 *         the argument doesn't contain a Term.
	 */
	public Optional<Term> fromTerm() {
		return this.apply(Optional::of, value -> Optional.empty(), value -> Optional.empty());
	}

	/**
	 * Create an optional from a (possible) Rule value.
	 *
	 * @return An optional containing the contained Rule, or an empty Optional if
	 *         the argument doesn't contain a Rule.
	 */
	public Optional<Rule> fromRule() {
		return this.apply(value -> Optional.empty(), Optional::of, value -> Optional.empty());
	}

	/**
	 * Create an optional from a (possible) PositiveLiteral value.
	 *
	 * @return An optional containing the contained PositiveLiteral, or an empty
	 *         Optional if the argument doesn't contain a PositiveLitreal.
	 */
	public Optional<PositiveLiteral> fromPositiveLiteral() {
		return this.apply(value -> Optional.empty(), value -> Optional.empty(), Optional::of);
	}
}
