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
	public static final String negativeIdentifier = "~";
	public static final String comma = ",";
	public static final String dot = ".";
	public static final String existentialIdentifier = "!";
	public static final String universalIdentifier = "?";
	public static final String namedNullIdentifier = "_";
	public static final String openParentheses = "(";
	public static final String closeParentheses = ")";
	public static final String ruleSeparator = ":-";

	/**
	 * Constructor.
	 */
	private Serializer() {

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
		return getString(rule.getHead()) + " " + ruleSeparator + " " + getString(rule.getBody()) + dot;
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
			stringBuilder.append(negativeIdentifier);
		}
		stringBuilder.append(literal.getPredicate().getName()).append(openParentheses);
		boolean first = true;
		for (final Term term : literal.getArguments()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(comma + " ");
			}
			stringBuilder.append(term.getSyntacticRepresentation());
		}
		stringBuilder.append(closeParentheses);
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
		return getString(fact) + dot;
	}

	/**
	 * Creates a String representation of a given {@link Constant}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param constant a {@link Constant}
	 * @return String representation corresponding to a given {@link Constant}.
	 */
	public static String getString(Constant constant) {
		return constant.getName();
	}

	public static String getString(DatatypeConstant constant) {
		return getShortConstantName(constant);
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
		return existentialIdentifier + existentialVariable.getName();
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
		return universalIdentifier + universalVariable.getName();
	}

	/**
	 * Creates a String representation of a given {@link NamedNull}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param namedNull a {@link NamedNull}
	 * @return String representation corresponding to a given {@link NamedNull}.
	 */
	public static String getString(NamedNull namedNull) {
		return namedNullIdentifier + namedNull.getName();
	}

	/**
	 * Creates a String representation of a given {@link Predicate}.
	 * 
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param predicate a {@link Predicate}
	 * @return String representation corresponding to a given {@link Predicate}.
	 */
	public static String getString(Predicate predicate) {
		return predicate.getName() + openParentheses + predicate.getArity() + closeParentheses;
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
		return "@source " + dataSourceDeclaration.getPredicate().getName() + openParentheses
				+ dataSourceDeclaration.getPredicate().getArity() + closeParentheses + ": "
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
				stringBuilder.append(comma + " ");
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
		return "\"" + languageStringConstant.getString().replace("\\", "\\\\").replace("\"", "\\\"") + "\"@"
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
	public static String getShortConstantName(DatatypeConstant datatypeConstant) {
		if (datatypeConstant.getDatatype().equals(PrefixDeclarations.XSD_STRING)) {
			return "\"" + datatypeConstant.getLexicalValue() + "\"";
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
		return "\"" + datatypeConstant.getLexicalValue().replace("\\", "\\\\").replace("\"", "\\\"") + "\"^^<"
				+ datatypeConstant.getDatatype() + ">";
	}

}
