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
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;
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
	public static final String NEGATIVE_IDENTIFIER = "~";
	public static final String COMMA = ", ";
	public static final String DOT = ".";
	public static final String EXISTENTIAL_IDENTIFIER = "!";
	public static final String UNIVERSAL_IDENTIFIER = "?";
	public static final String NAMEDNULL_IDENTIFIER = "_";
	public static final String OPEN_PARENTHESIS = "(";
	public static final String CLOSING_PARENTHESIS = ")";
	public static final String RULE_SEPARATOR = " :- ";
	public static final String AT = "@";
	public static final String SOURCE = "@source ";
	public static final String COLON = ": ";
	public static final String COLON_UNSPACED = ":";
	public static final String CARET = "^";
	public static final String LESS_THAN = "<";
	public static final String MORE_THAN = ">";
	public static final String QUOTE = "\"";
	public static final String DOUBLE = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	public static final String INTEGER = "^[-+]?\\d+$";
	public static final String DECIMAL = "^(\\d*\\.)?\\d+$";
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	/**
	 * Constructor.
	 */
	private Serializer() {

	}

	private static String escape(String string) {
		return string.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	/**
	 * Creates a String representation of a given {@link Rule}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param rule a {@link Rule}.
	 * @return String representation corresponding to a given {@link Rule}.
	 * 
	 */
	public static String getString(Rule rule) {
		return getString(rule.getHead()) + RULE_SEPARATOR + getString(rule.getBody()) + DOT;
	}

	/**
	 * Creates a String representation of a given {@link Literal}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param literal a {@link Literal}
	 * @return String representation corresponding to a given {@link Literal}.
	 */
	public static String getString(Literal literal) {
		final StringBuilder stringBuilder = new StringBuilder("");
		if (literal.isNegated()) {
			stringBuilder.append(NEGATIVE_IDENTIFIER);
		}
		stringBuilder.append(literal.getPredicate().getName()).append(OPEN_PARENTHESIS);
		boolean first = true;
		for (final Term term : literal.getArguments()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(COMMA);
			}
			String string = term.getSyntacticRepresentation();
			stringBuilder.append(string);
		}
		stringBuilder.append(CLOSING_PARENTHESIS);
		return stringBuilder.toString();
	}

	/**
	 * Creates a String representation of a given {@link Fact}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param fact a {@link Fact}
	 * @return String representation corresponding to a given {@link Fact}.
	 */
	public static String getFactString(Fact fact) {
		return getString(fact) + DOT;
	}

	/**
	 * Creates a String representation of a given {@link Constant}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param constant a {@link Constant}
	 * @return String representation corresponding to a given {@link Constant}.
	 */
	public static String getString(Constant constant) {
		if (constant.getName().contains(COLON_UNSPACED) || constant.getName().matches(INTEGER)
				|| constant.getName().matches(DOUBLE) || constant.getName().matches(DECIMAL)
				|| constant.getName().equals(TRUE) || constant.getName().equals(FALSE)) {
			return LESS_THAN + constant.getName() + MORE_THAN;
		} else {
			return constant.getName();
		}
	}

	/**
	 * Creates a String representation of a given {@link ExistentialVariable}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param existentialVariable a {@link ExistentialVariable}
	 * @return String representation corresponding to a given
	 *         {@link ExistentialVariable}.
	 */
	public static String getString(ExistentialVariable existentialVariable) {
		return EXISTENTIAL_IDENTIFIER + existentialVariable.getName();
	}

	/**
	 * Creates a String representation of a given {@link UniversalVariable}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param universalVariable a {@link UniversalVariable}
	 * @return String representation corresponding to a given
	 *         {@link UniversalVariable}.
	 */
	public static String getString(UniversalVariable universalVariable) {
		return UNIVERSAL_IDENTIFIER + universalVariable.getName();
	}

	/**
	 * Creates a String representation of a given {@link NamedNull}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param namedNull a {@link NamedNull}
	 * @return String representation corresponding to a given {@link NamedNull}.
	 */
	public static String getString(NamedNull namedNull) {
		return NAMEDNULL_IDENTIFIER + namedNull.getName();
	}

	/**
	 * Creates a String representation of a given {@link Predicate}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param predicate a {@link Predicate}
	 * @return String representation corresponding to a given {@link Predicate}.
	 */
	public static String getString(Predicate predicate) {
		return predicate.getName() + OPEN_PARENTHESIS + predicate.getArity() + CLOSING_PARENTHESIS;
	}

	/**
	 * Creates a String representation of a given {@link DataSourceDeclaration}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param dataSourceDeclaration a {@link DataSourceDeclaration}
	 * @return String representation corresponding to a given
	 *         {@link DataSourceDeclaration}.
	 */
	public static String getString(DataSourceDeclaration dataSourceDeclaration) {
		return SOURCE + dataSourceDeclaration.getPredicate().getName() + OPEN_PARENTHESIS
				+ dataSourceDeclaration.getPredicate().getArity() + CLOSING_PARENTHESIS + COLON
				+ dataSourceDeclaration.getDataSource().getSyntacticRepresentation();
	}

	/**
	 * Creates a String representation of a given {@link Conjunction}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
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
				stringBuilder.append(COMMA);
			}
			stringBuilder.append(getString(literal));
		}
		return stringBuilder.toString();
	}

	/**
	 * Creates a String representation corresponding to the name of a given
	 * {@link LanguageStringConstant}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param languageStringConstant a {@link LanguageStringConstant}
	 * @return String representation corresponding to the name of a given
	 *         {@link LanguageStringConstant}.
	 */
	public static String getConstantName(LanguageStringConstant languageStringConstant) {
		return QUOTE + escape(languageStringConstant.getString()) + QUOTE + AT
				+ languageStringConstant.getLanguageTag();
	}

	/**
	 * Creates a String representation corresponding to the name of a given
	 * {@link DatatypeConstant} without an IRI.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param datatypeConstant a {@link DatatypeConstant}
	 * @return String representation corresponding to a given
	 *         {@link DatatypeConstant}.
	 */
	public static String getString(DatatypeConstant datatypeConstant) {
		if (datatypeConstant.getDatatype().equals(PrefixDeclarations.XSD_STRING)) {
			return QUOTE + datatypeConstant.getLexicalValue() + QUOTE;
		} else {
			if (datatypeConstant.getDatatype().equals(PrefixDeclarations.XSD_DECIMAL)
					|| datatypeConstant.getDatatype().equals(PrefixDeclarations.XSD_INTEGER)
					|| datatypeConstant.getDatatype().equals(PrefixDeclarations.XSD_DOUBLE)) {
				return datatypeConstant.getLexicalValue();
			} else {
				return getConstantName(datatypeConstant);
			}

		}
	}

	/**
	 * Creates a String representation corresponding to the name of a given
	 * {@link DatatypeConstant} including an IRI.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param datatypeConstant a {@link DatatypeConstant}
	 * @return String representation corresponding to a given
	 *         {@link DatatypeConstant}.
	 */
	public static String getConstantName(DatatypeConstant datatypeConstant) {
		return QUOTE + escape(datatypeConstant.getLexicalValue()) + QUOTE + CARET + CARET + LESS_THAN
				+ datatypeConstant.getDatatype() + MORE_THAN;
	}

}
