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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.Entity;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParser;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParserBase.FormulaContext;
import org.semanticweb.rulewerk.parser.javacc.ParseException;
import org.semanticweb.rulewerk.parser.javacc.TokenMgrError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to statically access VLog parsing functionality.
 *
 * @author Markus Kroetzsch
 *
 */
public class RuleParser {

	public static final String DEFAULT_STRING_ENCODING = "UTF-8";

	private static Logger LOGGER = LoggerFactory.getLogger(RuleParser.class);

	private RuleParser() {
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream, final String encoding,
			final ParserConfiguration parserConfiguration, final String baseIri) throws ParsingException {
		final JavaCCParser parser = new JavaCCParser(stream, encoding);

		if (baseIri != null) {
			PrefixDeclarationRegistry prefixDeclarationRegistry = new LocalPrefixDeclarationRegistry(baseIri);
			parser.setPrefixDeclarationRegistry(prefixDeclarationRegistry);
		}

		parser.setKnowledgeBase(knowledgeBase);
		parser.setParserConfiguration(parserConfiguration);
		doParse(parser);
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream, final String encoding,
			final ParserConfiguration parserConfiguration) throws ParsingException {
		parseInto(knowledgeBase, stream, encoding, parserConfiguration, null);
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream,
			final ParserConfiguration parserConfiguration, final String baseIri) throws ParsingException {
		parseInto(knowledgeBase, stream, DEFAULT_STRING_ENCODING, parserConfiguration, baseIri);
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream,
			final ParserConfiguration parserConfiguration) throws ParsingException {
		parseInto(knowledgeBase, stream, DEFAULT_STRING_ENCODING, parserConfiguration);
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final String input,
			final ParserConfiguration parserConfiguration, final String baseIri) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		parseInto(knowledgeBase, inputStream, parserConfiguration, baseIri);
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final String input,
			final ParserConfiguration parserConfiguration) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		parseInto(knowledgeBase, inputStream, parserConfiguration);
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream, final String encoding)
			throws ParsingException {
		final JavaCCParser javaCcParser = new JavaCCParser(stream, encoding);
		javaCcParser.setKnowledgeBase(knowledgeBase);
		doParse(javaCcParser);
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream) throws ParsingException {
		parseInto(knowledgeBase, stream, DEFAULT_STRING_ENCODING);
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final String input) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		parseInto(knowledgeBase, inputStream);
	}

	public static KnowledgeBase parse(final InputStream stream, final String encoding,
			final ParserConfiguration parserConfiguration) throws ParsingException {
		JavaCCParser parser = new JavaCCParser(stream, encoding);
		parser.setParserConfiguration(parserConfiguration);
		return doParse(parser);
	}

	public static KnowledgeBase parse(final InputStream stream, final ParserConfiguration parserConfiguration)
			throws ParsingException {
		return parse(stream, DEFAULT_STRING_ENCODING, parserConfiguration);
	}

	public static KnowledgeBase parse(final String input, final ParserConfiguration parserConfiguration)
			throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		return parse(inputStream, parserConfiguration);
	}

	public static KnowledgeBase parse(final InputStream stream, final String encoding) throws ParsingException {
		return doParse(new JavaCCParser(stream, encoding));
	}

	public static KnowledgeBase parse(final InputStream stream) throws ParsingException {
		return parse(stream, DEFAULT_STRING_ENCODING);
	}

	public static KnowledgeBase parse(final String input) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		return parse(inputStream);
	}

	/**
	 * Interface for a method parsing a fragment of the supported syntax.
	 *
	 * This is needed to specify the exceptions thrown by the parse method.
	 */
	@FunctionalInterface
	interface SyntaxFragmentParser<T extends Entity> {
		T parse(final JavaCCParser parser)
				throws ParsingException, ParseException, PrefixDeclarationException, TokenMgrError;
	}

	/**
	 * Parse a syntax fragment.
	 *
	 * @param input               Input string.
	 * @param parserAction        Parsing method for the {@code T}.
	 * @param syntaxFragmentType  Description of the type {@code T} being parsed.
	 * @param parserConfiguration {@link ParserConfiguration} instance, or null.
	 *
	 * @throws ParsingException when an error during parsing occurs.
	 * @return an appropriate instance of {@code T}
	 */
	static <T extends Entity> T parseSyntaxFragment(final String input, SyntaxFragmentParser<T> parserAction,
			final String syntaxFragmentType, final ParserConfiguration parserConfiguration) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
		final JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");

		if (parserConfiguration != null) {
			localParser.setParserConfiguration(parserConfiguration);
		}

		T result;
		try {
			result = parserAction.parse(localParser);
			localParser.ensureEndOfInput();
		} catch (ParseException | PrefixDeclarationException | TokenMgrError | RuntimeException e) {
			LOGGER.error("Error parsing " + syntaxFragmentType + ": {}!", input);
			throw new ParsingException("Error parsing " + syntaxFragmentType + ": " + e.getMessage(), e);
		}
		return result;
	}

	public static Rule parseRule(final String input, final ParserConfiguration parserConfiguration)
			throws ParsingException {
		return parseSyntaxFragment(input, JavaCCParser::rule, "rule", parserConfiguration);
	}

	public static Rule parseRule(final String input) throws ParsingException {
		return parseRule(input, null);
	}

	public static Literal parseLiteral(final String input, final ParserConfiguration parserConfiguration)
			throws ParsingException {
		return parseSyntaxFragment(input, parser -> parser.literal(FormulaContext.HEAD), "literal",
				parserConfiguration);
	}

	public static Literal parseLiteral(final String input) throws ParsingException {
		return parseLiteral(input, null);
	}

	public static PositiveLiteral parsePositiveLiteral(final String input,
			final ParserConfiguration parserConfiguration) throws ParsingException {
		return parseSyntaxFragment(input, parser -> parser.positiveLiteral(FormulaContext.HEAD), "positive literal",
				parserConfiguration);
	}

	public static PositiveLiteral parsePositiveLiteral(final String input) throws ParsingException {
		return parsePositiveLiteral(input, null);
	}

	public static Fact parseFact(final String input, final ParserConfiguration parserConfiguration)
			throws ParsingException {
		return parseSyntaxFragment(input, parser -> parser.fact(FormulaContext.HEAD), "fact", parserConfiguration);
	}

	public static Fact parseFact(final String input) throws ParsingException {
		return parseFact(input, null);
	}

	public static Term parseTerm(final String input, final FormulaContext context,
			final ParserConfiguration parserConfiguration) throws ParsingException {
		return parseSyntaxFragment(input, parser -> parser.term(context), "term", parserConfiguration);
	}

	public static Term parseTerm(final String input, final ParserConfiguration parserConfiguration)
			throws ParsingException {
		return parseTerm(input, FormulaContext.HEAD, parserConfiguration);
	}

	public static Term parseTerm(final String input, final FormulaContext context) throws ParsingException {
		return parseTerm(input, context, null);
	}

	public static Term parseTerm(final String input) throws ParsingException {
		return parseTerm(input, (ParserConfiguration) null);
	}

	public static DataSourceDeclaration parseDataSourceDeclaration(final String input,
			ParserConfiguration parserConfiguration) throws ParsingException {
		return parseSyntaxFragment(input, RuleParser::parseAndExtractDatasourceDeclaration, "data source declaration",
				parserConfiguration);
	}

	public static DataSourceDeclaration parseDataSourceDeclaration(final String input) throws ParsingException {
		return parseDataSourceDeclaration(input, null);
	}

	static KnowledgeBase doParse(final JavaCCParser parser) throws ParsingException {
		try {
			parser.parse();
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			LOGGER.error("Error parsing Knowledge Base: " + e.getMessage(), e);
			throw new ParsingException(e.getMessage(), e);
		}

		KnowledgeBase knowledgeBase = parser.getKnowledgeBase();
		knowledgeBase.mergePrefixDeclarations(parser.getPrefixDeclarationRegistry());

		return knowledgeBase;
	}

	protected static DataSourceDeclaration parseAndExtractDatasourceDeclaration(final JavaCCParser parser)
			throws ParsingException, ParseException, PrefixDeclarationException {
		parser.source();

		final List<DataSourceDeclaration> dataSourceDeclarations = parser.getKnowledgeBase()
				.getDataSourceDeclarations();

		if (dataSourceDeclarations.size() != 1) {
			throw new ParsingException(
					"Unexpected number of data source declarations: " + dataSourceDeclarations.size());
		}

		return dataSourceDeclarations.get(0);
	}

}
