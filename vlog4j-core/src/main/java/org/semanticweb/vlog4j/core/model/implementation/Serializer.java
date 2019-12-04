package org.semanticweb.vlog4j.core.model.implementation;

import org.semanticweb.vlog4j.core.model.api.AbstractConstant;

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
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.FileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;

/**
 * A utility class with static methods to obtain the correct parsable string
 * representation of the different data models.
 *
 * @author Ali Elhalawati
 *
 */
public final class Serializer {
	public static final String STATEMENT_SEPARATOR = " .";
	public static final String COMMA = ", ";
	public static final String NEGATIVE_IDENTIFIER = "~";
	public static final String EXISTENTIAL_IDENTIFIER = "!";
	public static final String UNIVERSAL_IDENTIFIER = "?";
	public static final String NAMEDNULL_IDENTIFIER = "_";
	public static final String OPENING_PARENTHESIS = "(";
	public static final String CLOSING_PARENTHESIS = ")";
	public static final String OPENING_BRACKET = "[";
	public static final String CLOSING_BRACKET = "]";
	public static final String RULE_SEPARATOR = " :- ";
	public static final String AT = "@";
	public static final String DATA_SOURCE = "@source ";
	public static final String CSV_FILE_DATA_SOURCE = "load-csv";
	private static final String RDF_FILE_DATA_SOURCE = "load-rdf";
	private static final String SPARQL_QUERY_RESULT_DATA_SOURCE = "sparql";
	public static final String DATA_SOURCE_SEPARATOR = ": ";
	public static final String COLON = ":";
	public static final String DOUBLE_CARET = "^^";
	public static final String LESS_THAN = "<";
	public static final String MORE_THAN = ">";
	public static final String QUOTE = "\"";

	public static final String REGEX_DOUBLE = "^[-+]?[0-9]+[.]?[0-9]*([eE][-+]?[0-9]+)?$";
	public static final String REGEX_INTEGER = "^[-+]?\\d+$";
	public static final String REGEX_DECIMAL = "^(\\d*\\.)?\\d+$";
	public static final String REGEX_TRUE = "true";
	public static final String REGEX_FALSE = "false";

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
	public static String getString(final Rule rule) {
		return getString(rule.getHead()) + RULE_SEPARATOR + getString(rule.getBody()) + STATEMENT_SEPARATOR;
	}

	/**
	 * Creates a String representation of a given {@link Conjunction}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param conjunction a {@link Conjunction}
	 * @return String representation corresponding to a given {@link Conjunction}.
	 */
	public static String getString(final Conjunction<? extends Literal> conjunction) {
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
	 * Creates a String representation of a given {@link Literal}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param literal a {@link Literal}
	 * @return String representation corresponding to a given {@link Literal}.
	 */
	public static String getString(final Literal literal) {
		final StringBuilder stringBuilder = new StringBuilder("");
		if (literal.isNegated()) {
			stringBuilder.append(NEGATIVE_IDENTIFIER);
		}
		stringBuilder.append(getIRIString(literal.getPredicate().getName())).append(OPENING_PARENTHESIS);
		boolean first = true;
		for (final Term term : literal.getArguments()) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(COMMA);
			}
			final String string = term.getSyntacticRepresentation();
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
	public static String getFactString(final Fact fact) {
		return getString(fact) + STATEMENT_SEPARATOR;
	}

	/**
	 * Creates a String representation of a given {@link Constant}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param constant a {@link Constant}
	 * @return String representation corresponding to a given {@link Constant}.
	 */
	public static String getString(final AbstractConstant constant) {
		return getIRIString(constant.getName());
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
	public static String getConstantName(final LanguageStringConstant languageStringConstant) {
		return addQuotes(escape(languageStringConstant.getString())) + AT + languageStringConstant.getLanguageTag();
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
	public static String getString(final DatatypeConstant datatypeConstant) {
		if (datatypeConstant.getDatatype().equals(PrefixDeclarations.XSD_STRING)) {
			return addQuotes(datatypeConstant.getLexicalValue());
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
	public static String getConstantName(final DatatypeConstant datatypeConstant) {
		return addQuotes(escape(datatypeConstant.getLexicalValue())) + DOUBLE_CARET
				+ addAngleBrackets(datatypeConstant.getDatatype());
	}

	/**
	 * Creates a String representation of a given {@link ExistentialVariable}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param existentialVariable a {@link ExistentialVariable}
	 * @return String representation corresponding to a given
	 *         {@link ExistentialVariable}.
	 */
	public static String getString(final ExistentialVariable existentialVariable) {
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
	public static String getString(final UniversalVariable universalVariable) {
		return UNIVERSAL_IDENTIFIER + universalVariable.getName();
	}

	/**
	 * Creates a String representation of a given {@link NamedNull}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param namedNull a {@link NamedNull}
	 * @return String representation corresponding to a given {@link NamedNull}.
	 */
	public static String getString(final NamedNull namedNull) {
		return NAMEDNULL_IDENTIFIER + namedNull.getName();
	}

	/**
	 * Creates a String representation of a given {@link Predicate}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param predicate a {@link Predicate}
	 * @return String representation corresponding to a given {@link Predicate}.
	 */
	public static String getString(final Predicate predicate) {
		return predicate.getName() + OPENING_BRACKET + predicate.getArity() + CLOSING_BRACKET;
	}

	/**
	 * Creates a String representation of a given {@link DataSourceDeclaration}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki"> for wiki.
	 * @param dataSourceDeclaration a {@link DataSourceDeclaration}
	 * @return String representation corresponding to a given
	 *         {@link DataSourceDeclaration}.
	 */
	public static String getString(final DataSourceDeclaration dataSourceDeclaration) {
		return DATA_SOURCE + getString(dataSourceDeclaration.getPredicate()) + DATA_SOURCE_SEPARATOR
				+ dataSourceDeclaration.getDataSource().getSyntacticRepresentation() + STATEMENT_SEPARATOR;
	}

	/**
	 * Creates a String representation of a given {@link CsvFileDataSource}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki">.
	 *
	 * @param csvFileDataSource
	 * @return String representation corresponding to a given
	 *         {@link CsvFileDataSource}.
	 */
	public static String getString(final CsvFileDataSource csvFileDataSource) {
		return CSV_FILE_DATA_SOURCE + OPENING_PARENTHESIS + getFileString(csvFileDataSource) + CLOSING_PARENTHESIS;
	}

	/**
	 * Creates a String representation of a given {@link RdfFileDataSource}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki">.
	 *
	 *
	 * @param rdfFileDataSource
	 * @return String representation corresponding to a given
	 *         {@link RdfFileDataSource}.
	 */
	public static String getString(final RdfFileDataSource rdfFileDataSource) {
		return RDF_FILE_DATA_SOURCE + OPENING_PARENTHESIS + getFileString(rdfFileDataSource) + CLOSING_PARENTHESIS;
	}

	/**
	 * Creates a String representation of a given
	 * {@link SparqlQueryResultDataSource}.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki">.
	 *
	 *
	 * @param dataSource
	 * @return String representation corresponding to a given
	 *         {@link SparqlQueryResultDataSource}.
	 */
	public static String getString(final SparqlQueryResultDataSource dataSource) {
		return SPARQL_QUERY_RESULT_DATA_SOURCE + OPENING_PARENTHESIS
				+ addAngleBrackets(dataSource.getEndpoint().toString()) + COMMA
				+ addQuotes(dataSource.getQueryVariables()) + COMMA + addQuotes(dataSource.getQueryBody())
				+ CLOSING_PARENTHESIS;
	}

	private static String getFileString(final FileDataSource fileDataSource) {
		return addQuotes(fileDataSource.getFile().toString());
	}

	private static String getIRIString(final String string) {
		if (string.contains(COLON) || string.matches(REGEX_INTEGER) || string.matches(REGEX_DOUBLE)
				|| string.matches(REGEX_DECIMAL) || string.equals(REGEX_TRUE) || string.equals(REGEX_FALSE)) {
			return addAngleBrackets(string);
		} else {
			return string;
		}
	}

	private static String escape(final String string) {
		return string.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private static String addQuotes(final String string) {
		return QUOTE + string + QUOTE;
	}

	private static String addAngleBrackets(final String string) {
		return LESS_THAN + string + MORE_THAN;
	}

}
