package org.semanticweb.rulewerk.core.model.implementation;

/*-
 * #%L
 * Rulewerk Core Components
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

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.DatatypeConstant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.LanguageStringConstant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.FileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;

/**
 * A utility class with static methods to obtain the correct parsable string
 * representation of the different data models.
 *
 * @author Ali Elhalawati
 *
 */
public final class Serializer {
	private static final String NEW_LINE = "\n";
	public static final String STATEMENT_SEPARATOR = " .";
	public static final String COMMA = ", ";
	public static final String NEGATIVE_IDENTIFIER = "~";
	public static final String EXISTENTIAL_IDENTIFIER = "!";
	public static final String UNIVERSAL_IDENTIFIER = "?";
	public static final String NAMEDNULL_IDENTIFIER = "_:";
	public static final String OPENING_PARENTHESIS = "(";
	public static final String CLOSING_PARENTHESIS = ")";
	public static final String OPENING_BRACKET = "[";
	public static final String CLOSING_BRACKET = "]";
	public static final String RULE_SEPARATOR = " :- ";
	public static final char AT = '@';
	public static final String DATA_SOURCE = "@source ";
	public static final String BASE = "@base ";
	public static final String PREFIX = "@prefix ";
	public static final String CSV_FILE_DATA_SOURCE = "load-csv";
	public static final String RDF_FILE_DATA_SOURCE = "load-rdf";
	public static final String SPARQL_QUERY_RESULT_DATA_SOURCE = "sparql";
	public static final String DATA_SOURCE_SEPARATOR = ": ";
	public static final String COLON = ":";
	public static final String DOUBLE_CARET = "^^";
	public static final char LESS_THAN = '<';
	public static final char MORE_THAN = '>';
	public static final char QUOTE = '"';

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
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
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
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
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
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param literal a {@link Literal}
	 * @return String representation corresponding to a given {@link Literal}.
	 */
	public static String getString(final Literal literal) {
		final StringBuilder stringBuilder = new StringBuilder("");
		if (literal.isNegated()) {
			stringBuilder.append(NEGATIVE_IDENTIFIER);
		}
		stringBuilder.append(getString(literal.getPredicate(), literal.getArguments()));
		return stringBuilder.toString();
	}

	/**
	 * Creates a String representation of a given {@link Fact}.
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param fact a {@link Fact}
	 * @return String representation corresponding to a given {@link Fact}.
	 */
	public static String getFactString(final Fact fact) {
		return getString(fact) + STATEMENT_SEPARATOR;
	}

	/**
	 * Creates a String representation of a given {@link AbstractConstant}.
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param constant       a {@link AbstractConstant}
	 * @param iriTransformer a function to transform IRIs with.
	 * @return String representation corresponding to a given
	 *         {@link AbstractConstant}.
	 */
	public static String getString(final AbstractConstant constant, Function<String, String> iriTransformer) {
		return getIRIString(constant.getName(), iriTransformer);
	}

	/**
	 * Creates a String representation of a given {@link AbstractConstant}.
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param constant a {@link AbstractConstant}
	 * @return String representation corresponding to a given
	 *         {@link AbstractConstant}.
	 */
	public static String getString(final AbstractConstant constant) {
		return getIRIString(constant.getName());
	}

	/**
	 * Creates a String representation corresponding to the name of a given
	 * {@link LanguageStringConstant}.
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param languageStringConstant a {@link LanguageStringConstant}
	 * @return String representation corresponding to the name of a given
	 *         {@link LanguageStringConstant}.
	 */
	public static String getConstantName(final LanguageStringConstant languageStringConstant) {
		return getString(languageStringConstant.getString()) + AT + languageStringConstant.getLanguageTag();
	}

	/**
	 * Creates a String representation corresponding to the given
	 * {@link DatatypeConstant}. For datatypes that have specialised lexical
	 * representations (i.e., xsd:String, xsd:Decimal, xsd:Integer, and xsd:Double),
	 * this representation is returned, otherwise the result is a generic literal
	 * with full datatype IRI.
	 *
	 * examples:
	 * <ul>
	 * <li>{@code "string"^^xsd:String} results in {@code "string"},</li>
	 * <li>{@code "23.0"^^xsd:Decimal} results in {@code 23.0},</li>
	 * <li>{@code "42"^^xsd:Integer} results in {@code 42},</li>
	 * <li>{@code "23.42"^^xsd:Double} results in {@code 23.42E0}, and</li>
	 * <li>{@code "test"^^<http://example.org>} results in
	 * {@code "test"^^<http://example.org>}, modulo transformation of the datatype
	 * IRI.</li>
	 * </ul>
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param datatypeConstant a {@link DatatypeConstant}
	 * @param iriTransformer   a function to transform IRIs with.
	 * @return String representation corresponding to a given
	 *         {@link DatatypeConstant}.
	 */
	public static String getString(final DatatypeConstant datatypeConstant, Function<String, String> iriTransformer) {
		if (datatypeConstant.getDatatype().equals(PrefixDeclarationRegistry.XSD_STRING)) {
			return getString(datatypeConstant.getLexicalValue());
		} else if (datatypeConstant.getDatatype().equals(PrefixDeclarationRegistry.XSD_DECIMAL)
				|| datatypeConstant.getDatatype().equals(PrefixDeclarationRegistry.XSD_INTEGER)
				|| datatypeConstant.getDatatype().equals(PrefixDeclarationRegistry.XSD_DOUBLE)) {
			return datatypeConstant.getLexicalValue();
		}

		return getConstantName(datatypeConstant, iriTransformer);
	}

	/**
	 * Creates a String representation corresponding to the given
	 * {@link DatatypeConstant}. For datatypes that have specialised lexical
	 * representations (i.e., xsd:String, xsd:Decimal, xsd:Integer, and xsd:Double),
	 * this representation is returned, otherwise the result is a generic literal
	 * with full datatype IRI.
	 *
	 * examples:
	 * <ul>
	 * <li>{@code "string"^^xsd:String} results in {@code "string"},</li>
	 * <li>{@code "23.0"^^xsd:Decimal} results in {@code 23.0},</li>
	 * <li>{@code "42"^^xsd:Integer} results in {@code 42},</li>
	 * <li>{@code "23.42"^^xsd:Double} results in {@code 23.42E0}, and</li>
	 * <li>{@code "test"^^<http://example.org>} results in
	 * {@code "test"^^<http://example.org>}.</li>
	 * </ul>
	 *
	 * @param datatypeConstant a {@link DatatypeConstant}
	 * @return String representation corresponding to a given
	 *         {@link DatatypeConstant}.
	 */
	public static String getString(final DatatypeConstant datatypeConstant) {
		return getString(datatypeConstant, Function.identity());
	}

	/**
	 * Creates a String representation corresponding to the name of a given
	 * {@link DatatypeConstant} including an IRI.
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param datatypeConstant a {@link DatatypeConstant}
	 * @return String representation corresponding to a given
	 *         {@link DatatypeConstant}.
	 */
	public static String getConstantName(final DatatypeConstant datatypeConstant,
			Function<String, String> iriTransformer) {
		return getString(datatypeConstant.getLexicalValue()) + DOUBLE_CARET
				+ getIRIString(datatypeConstant.getDatatype(), iriTransformer);
	}

	/**
	 * Creates a String representation corresponding to the name of a given
	 * {@link DatatypeConstant} including an IRI.
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param datatypeConstant a {@link DatatypeConstant}
	 * @return String representation corresponding to a given
	 *         {@link DatatypeConstant}.
	 */
	public static String getConstantName(final DatatypeConstant datatypeConstant) {
		return getString(datatypeConstant.getLexicalValue()) + DOUBLE_CARET
				+ addAngleBrackets(datatypeConstant.getDatatype());
	}

	/**
	 * Creates a String representation of a given {@link ExistentialVariable}.
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
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
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
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
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param namedNull a {@link NamedNull}
	 * @return String representation corresponding to a given {@link NamedNull}.
	 */
	public static String getString(final NamedNull namedNull) {
		return NAMEDNULL_IDENTIFIER + namedNull.getName();
	}

	/**
	 * Creates a String representation of a given {@link Predicate}.
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
	 * @param predicate a {@link Predicate}
	 * @return String representation corresponding to a given {@link Predicate}.
	 */
	public static String getString(final Predicate predicate) {
		return predicate.getName() + OPENING_BRACKET + predicate.getArity() + CLOSING_BRACKET;
	}

	/**
	 * Creates a String representation of a given {@link DataSourceDeclaration}.
	 *
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
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
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> ..
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
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> ..
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
	 * @see <a href=
	 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">Rule
	 *      syntax</a> .
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
		return getString(fileDataSource.getPath().toString());
	}

	private static String getIRIString(final String string) {
		return getIRIString(string, Function.identity());
	}

	private static String getIRIString(final String string, Function<String, String> iriTransformer) {
		String transformed = iriTransformer.apply(string);

		if (!transformed.equals(string)) {
			return transformed;
		}

		if (string.contains(COLON) || string.matches(REGEX_INTEGER) || string.matches(REGEX_DOUBLE)
				|| string.matches(REGEX_DECIMAL) || string.equals(REGEX_TRUE) || string.equals(REGEX_FALSE)) {
			return addAngleBrackets(string);
		}

		return string;
	}

	/**
	 * Constructs the parseable, serialized representation of given {@code string}.
	 * Escapes (with {@code \}) special character occurrences in given
	 * {@code string}, and surrounds the result with double quotation marks
	 * ({@code "}). The special characters are:
	 * <ul>
	 * <li>{@code \}</li>
	 * <li>{@code "}</li>
	 * <li>{@code \t}</li>
	 * <li>{@code \b}</li>
	 * <li>{@code \n}</li>
	 * <li>{@code \r}</li>
	 * <li>{@code \f}</li>
	 * </ul>
	 * Example for {@code string = "\\a"}, the returned value is
	 * {@code string = "\"\\\\a\""}
	 *
	 * @param string
	 * @return an escaped string surrounded by {@code "}.
	 */
	public static String getString(final String string) {
		return addQuotes(escape(string));
	}

	/**
	 * Escapes (with {@code \}) special character occurrences in given
	 * {@code string}. The special characters are:
	 * <ul>
	 * <li>{@code \}</li>
	 * <li>{@code "}</li>
	 * <li>{@code \t}</li>
	 * <li>{@code \b}</li>
	 * <li>{@code \n}</li>
	 * <li>{@code \r}</li>
	 * <li>{@code \f}</li>
	 * </ul>
	 *
	 * @param string
	 * @return an escaped string
	 */
	private static String escape(final String string) {
		return string.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\b", "\\b")
				.replace(NEW_LINE, "\\n").replace("\r", "\\r").replace("\f", "\\f");
		// don't touch single quotes here since we only construct double-quoted strings
	}

	private static String addQuotes(final String string) {
		return QUOTE + string + QUOTE;
	}

	private static String addAngleBrackets(final String string) {
		return LESS_THAN + string + MORE_THAN;
	}

	public static String getFactString(Predicate predicate, List<Term> terms) {
		return getString(predicate, terms) + STATEMENT_SEPARATOR + NEW_LINE;
	}

	public static String getFactString(Predicate predicate, List<Term> terms, Function<String, String> iriTransformer) {
		return getString(predicate, terms, iriTransformer) + STATEMENT_SEPARATOR + NEW_LINE;
	}

	public static String getString(Predicate predicate, List<Term> terms) {
		return getString(predicate, terms, Function.identity());
	}

	public static String getString(Predicate predicate, List<Term> terms, Function<String, String> iriTransformer) {
		final StringBuilder stringBuilder = new StringBuilder(getIRIString(predicate.getName(), iriTransformer));
		stringBuilder.append(OPENING_PARENTHESIS);

		boolean first = true;
		for (final Term term : terms) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(COMMA);
			}
			final String string = term.getSyntacticRepresentation(iriTransformer);
			stringBuilder.append(string);
		}
		stringBuilder.append(CLOSING_PARENTHESIS);
		return stringBuilder.toString();
	}

	public static String getBaseString(KnowledgeBase knowledgeBase) {
		String baseIri = knowledgeBase.getBaseIri();

		return baseIri.equals(PrefixDeclarationRegistry.EMPTY_BASE) ? baseIri : getBaseDeclarationString(baseIri);
	}

	private static String getBaseDeclarationString(String baseIri) {
		return BASE + addAngleBrackets(baseIri) + STATEMENT_SEPARATOR + NEW_LINE;
	}

	public static String getPrefixString(Entry<String, String> prefix) {
		return PREFIX + prefix.getKey() + " " + addAngleBrackets(prefix.getValue()) + STATEMENT_SEPARATOR + NEW_LINE;
	}

	public static String getBaseAndPrefixDeclarations(KnowledgeBase knowledgeBase) {
		StringBuilder sb = new StringBuilder();

		sb.append(getBaseString(knowledgeBase));
		knowledgeBase.getPrefixes().forEachRemaining(prefix -> sb.append(getPrefixString(prefix)));

		return sb.toString();
	}
}
