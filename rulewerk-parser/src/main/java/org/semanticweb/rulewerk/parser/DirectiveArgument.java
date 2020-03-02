package org.semanticweb.rulewerk.parser;

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

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import org.semanticweb.rulewerk.core.model.api.Term;

/**
 * A tagged union representing the possible types allowed to appear as arguments
 * in directives.
 *
 * @author Maximilian Marx
 */
public abstract class DirectiveArgument {
	private DirectiveArgument() {
	}

	/**
	 * Apply a function to the contained value.
	 *
	 * @param stringHandler the function to apply to a string argument
	 * @param iriHandler    the function to apply to an IRI
	 * @param termHandler   the function to apply to a Term
	 *
	 * @return the value returned by the appropriate handler function
	 */
	public abstract <V> V apply(Function<? super String, ? extends V> stringHandler,
			Function<? super URI, ? extends V> iriHandler, Function<? super Term, ? extends V> termHandler);

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

		if (!(other instanceof DirectiveArgument)) {
			return Optional.of(false);
		}

		return Optional.empty();
	}

	/**
	 * Create an argument containing a String.
	 *
	 * @param value the string value
	 *
	 * @return An argument containing the given string value
	 */
	public static DirectiveArgument string(String value) {
		return new DirectiveArgument() {
			@Override
			public <V> V apply(Function<? super String, ? extends V> stringHandler,
					Function<? super URI, ? extends V> iriHandler, Function<? super Term, ? extends V> termHandler) {
				return stringHandler.apply(value);
			}

			@Override
			public boolean equals(Object other) {
				Optional<Boolean> maybeEquals = isEqual(other);

				if (maybeEquals.isPresent()) {
					return maybeEquals.get();
				}

				DirectiveArgument otherArgument = (DirectiveArgument) other;
				return otherArgument.apply(str -> str.equals(value), iri -> false, term -> false);
			}

			@Override
			public int hashCode() {
				return 41 * value.hashCode();
			}
		};
	}

	/**
	 * Create an argument containing a IRI.
	 *
	 * @param value the IRI value
	 *
	 * @return An argument containing the given IRI value
	 */
	public static DirectiveArgument iri(URI value) {
		return new DirectiveArgument() {
			@Override
			public <V> V apply(Function<? super String, ? extends V> stringHandler,
					Function<? super URI, ? extends V> iriHandler, Function<? super Term, ? extends V> termHandler) {
				return iriHandler.apply(value);
			}

			@Override
			public boolean equals(Object other) {
				Optional<Boolean> maybeEquals = isEqual(other);

				if (maybeEquals.isPresent()) {
					return maybeEquals.get();
				}

				DirectiveArgument otherArgument = (DirectiveArgument) other;
				return otherArgument.apply(str -> false, iri -> iri.equals(value), term -> false);
			}

			@Override
			public int hashCode() {
				return 43 * value.hashCode();
			}
		};
	}

	/**
	 * Create an argument containing a Term.
	 *
	 * @param value the Term value
	 *
	 * @return An argument containing the given Term value
	 */
	public static DirectiveArgument term(Term value) {
		return new DirectiveArgument() {
			@Override
			public <V> V apply(Function<? super String, ? extends V> stringHandler,
					Function<? super URI, ? extends V> iriHandler, Function<? super Term, ? extends V> termHandler) {
				return termHandler.apply(value);
			}

			@Override
			public boolean equals(Object other) {
				Optional<Boolean> maybeEquals = isEqual(other);

				if (maybeEquals.isPresent()) {
					return maybeEquals.get();
				}

				DirectiveArgument otherArgument = (DirectiveArgument) other;
				return otherArgument.apply(str -> false, iri -> false, term -> term.equals(value));
			}

			@Override
			public int hashCode() {
				return 47 * value.hashCode();
			}
		};
	}

	/**
	 * Create an optional from a (possible) string value.
	 *
	 * @return An optional containing the contained string, or an empty Optional if
	 *         the argument doesn't contain a string.
	 */
	public Optional<String> fromString() {
		return this.apply(Optional::of, value -> Optional.empty(), value -> Optional.empty());
	}

	/**
	 * Create an optional from a (possible) IRI value.
	 *
	 * @return An optional containing the contained IRI, or an empty Optional if the
	 *         argument doesn't contain a IRI.
	 */
	public Optional<URI> fromIri() {
		return this.apply(value -> Optional.empty(), Optional::of, value -> Optional.empty());
	}

	/**
	 * Create an optional from a (possible) Term value.
	 *
	 * @return An optional containing the contained Term, or an empty Optional if
	 *         the argument doesn't contain a Term.
	 */
	public Optional<Term> fromTerm() {
		return this.apply(value -> Optional.empty(), value -> Optional.empty(), Optional::of);
	}
}
