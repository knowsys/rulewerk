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

import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.DatatypeConstant;
import org.semanticweb.vlog4j.core.model.api.ExistentialVariable;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.LanguageStringConstant;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.NamedNull;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.UniversalVariable;

/**
 * A utility class with static methods to obtain the correct parsable string
 * representation of the different data models.
 * 
 * @author Ali Elhalawati
 *
 */
public final class Serializer {
	/**
	 * Constructor.
	 */
	private Serializer() {

	}

	/**
	 * Creates a String representation of a given {@link Rule}. Example: "p(?X) :-
	 * q(?X,?Y)."
	 * 
	 * @param rule a {@link Rule}
	 * @return String representation corresponding to a given {@link Rule}.
	 */
	public static String getString(Rule rule) {
		return getString(rule.getHead()) + " :- " + getString(rule.getBody()) + ".";
	}

	/**
	 * Creates a String representation of a given {@link Literal}. Example:
	 * "~q(?X,?Y)"
	 * 
	 * @param literal a {@link Literal}
	 * @return String representation corresponding to a given {@link Literal}.
	 */
	public static String getString(Literal literal) {
		final StringBuilder stringBuilder = new StringBuilder("");
		if (literal.isNegated()) {
			stringBuilder.append("~");
		}
		stringBuilder.append(literal.getPredicate().getName()).append("(");
		boolean first = true;
		for (final Term term : literal.getArguments()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(term.getSyntacticRepresentation());
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	/**
	 * Creates a String representation of a given {@link Fact}. Example: "q(a)."
	 * 
	 * @param fact a {@link Fact}
	 * @return String representation corresponding to a given {@link Fact}.
	 */
	public static String getFactString(Fact fact) {
		return getString(fact) + ".";
	}

	/**
	 * Creates a String representation of a given {@link Constant}. Example: "c"
	 * 
	 * @param constant a {@link Constant}
	 * @return String representation corresponding to a given {@link Constant}.
	 */
	public static String getString(Constant constant) {
		return constant.getName();
	}

	/**
	 * Creates a String representation of a given {@link ExistentialVariable}.
	 * Example: "!X"
	 * 
	 * @param existentialVariable a {@link ExistentialVariable}
	 * @return String representation corresponding to a given
	 *         {@link ExistentialVariable}.
	 */
	public static String getString(ExistentialVariable existentialVariable) {
		return "!" + existentialVariable.getName();
	}

	/**
	 * Creates a String representation of a given {@link UniversalVariable}.
	 * Example: "?X"
	 * 
	 * @param universalVariable a {@link UniversalVariable}
	 * @return String representation corresponding to a given
	 *         {@link UniversalVariable}.
	 */
	public static String getString(UniversalVariable universalVariable) {
		return "?" + universalVariable.getName();
	}

	/**
	 * Creates a String representation of a given {@link NamedNull}. Example: "_123"
	 * 
	 * @param namedNull a {@link NamedNull}
	 * @return String representation corresponding to a given {@link NamedNull}.
	 */
	public static String getString(NamedNull namedNull) {
		return "_" + namedNull.getName();
	}

	/**
	 * Creates a String representation of a given {@link Predicate}. Example: "p(2)"
	 * 
	 * @param predicate a {@link Predicate}
	 * @return String representation corresponding to a given {@link Predicate}.
	 */
	public static String getString(Predicate predicate) {
		return predicate.getName() + "(" + predicate.getArity() + ")";
	}

	/**
	 * Creates a String representation of a given {@link DataSourceDeclaration}.
	 * Example: "@source p(3): sparql(<https://example.org/sparql>, "var", "?var
	 * wdt:P31 wd:Q5 .") ."
	 * 
	 * @param dataSourceDeclaration a {@link DataSourceDeclaration}
	 * @return String representation corresponding to a given
	 *         {@link DataSourceDeclaration}.
	 */
	public static String getString(DataSourceDeclaration dataSourceDeclaration) {
		return "@source " + dataSourceDeclaration.getPredicate().getName() + "("
				+ dataSourceDeclaration.getPredicate().getArity() + "): "
				+ dataSourceDeclaration.getDataSource().getSyntacticRepresentation();
	}

	/**
	 * Creates a String representation of a given {@link Conjunction}. Example:
	 * "p(?X,?Y), ~q(a,?Z)"
	 * 
	 * @param conjunction a {@link Conjunction}
	 * @return String representation corresponding to a given {@link Conjunction}.
	 */
	public static String getString(Conjunction<? extends Literal> conjunction) {
		final StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;
		for (final Literal literal : conjunction.getLiterals()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(getString(literal));
		}
		return stringBuilder.toString();
	}

	/**
	 * Creates a String representation corresponding to the name of a given
	 * {@link LanguageStringConstant}. Example: ""Test"@en"
	 * 
	 * @param languageStringConstant a {@link LanguageStringConstant}
	 * @return String representation corresponding to the name of a given
	 *         {@link LanguageStringConstant}.
	 */
	public static String getConstantName(LanguageStringConstant languageStringConstant) {
		return "\"" + languageStringConstant.getString().replace("\\", "\\\\").replace("\"", "\\\"") + "\"@"
				+ languageStringConstant.getLanguageTag();
	}

	/**
	 * Creates a String representation corresponding to the name of a given
	 * {@link DatatypeConstant}. Example: ""c"^^<http://example.org/mystring>"
	 * 
	 * @param datatypeConstant a {@link DatatypeConstant}
	 * @return String representation corresponding to a given
	 *         {@link DatatypeConstant}.
	 */
	public static String getConstantName(DatatypeConstant datatypeConstant) {
		return "\"" + datatypeConstant.getLexicalValue().replace("\\", "\\\\").replace("\"", "\\\"") + "\"^^<"
				+ datatypeConstant.getDatatype() + ">";
	}

}
