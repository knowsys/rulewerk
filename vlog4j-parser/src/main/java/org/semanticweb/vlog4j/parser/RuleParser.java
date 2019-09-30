package org.semanticweb.vlog4j.parser;

/*-
 * #%L
 * vlog4j-parser
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.semanticweb.vlog4j.core.exceptions.PrefixDeclarationException;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.parser.javacc.JavaCCParser;
import org.semanticweb.vlog4j.parser.javacc.JavaCCParserBase.FormulaContext;
import org.semanticweb.vlog4j.parser.javacc.ParseException;
import org.semanticweb.vlog4j.parser.javacc.TokenMgrError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to statically access VLog parsing functionality.
 *
 * @author Markus Kroetzsch
 *
 */
public class RuleParser {

	private static Logger LOGGER = LoggerFactory.getLogger(RuleParser.class);

	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream, final String encoding)
			throws ParsingException {
		final JavaCCParser javaCcParser = new JavaCCParser(stream, encoding);
		javaCcParser.setKnowledgeBase(knowledgeBase);
		doParse(javaCcParser);
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream) throws ParsingException {
		parseInto(knowledgeBase, stream, "UTF-8");
	}

	public static void parseInto(final KnowledgeBase knowledgeBase, final String input) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		parseInto(knowledgeBase, inputStream, "UTF-8");
	}

	public static KnowledgeBase parse(final InputStream stream, final String encoding) throws ParsingException {
		return doParse(new JavaCCParser(stream, encoding));
	}

	public static KnowledgeBase parse(final InputStream stream) throws ParsingException {
		return parse(stream, "UTF-8");
	}

	public static KnowledgeBase parse(final String input) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		return parse(inputStream, "UTF-8");
	}

	public static Rule parseRule(final String input) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		final JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		try {
			return localParser.rule();
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			LOGGER.error("Exception while parsing rule: {}!", input);
			throw new ParsingException("Exception while parsing rule", e);
		}
	}

	public static Literal parseLiteral(final String input) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		final JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		try {
			return localParser.literal(FormulaContext.HEAD);
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			LOGGER.error("Exception while parsing literal: {}!", input);
			throw new ParsingException("Exception while parsing literal", e);
		}
	}

	public static PositiveLiteral parsePositiveLiteral(final String input) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		final JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		try {
			return localParser.positiveLiteral(FormulaContext.HEAD);
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			LOGGER.error("Exception while parsing positive literal: {}!", input);
			throw new ParsingException("Exception while parsing positive literal", e);
		}
	}

	public static Fact parseFact(final String input) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		final JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		try {
			return localParser.fact(FormulaContext.HEAD);
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			LOGGER.error("Exception while parsing fact: {}!", input);
			throw new ParsingException("Exception while parsing fact: {}!", e);
		}
	}

	static KnowledgeBase doParse(final JavaCCParser parser) throws ParsingException {
		try {
			parser.parse();
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			LOGGER.error("Exception while parsing Knowledge Base!", e);
			throw new ParsingException("Exception while parsing Knowledge Base.", e);
		}
		return parser.getKnowledgeBase();
	}

}
