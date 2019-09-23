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
import org.semanticweb.vlog4j.parser.javacc.ParseException;
import org.semanticweb.vlog4j.parser.javacc.TokenMgrError;
import org.semanticweb.vlog4j.parser.javacc.JavaCCParserBase.FormulaContext;
import org.semanticweb.vlog4j.parser.ParsingException;

/**
 * Class to statically access VLog parsing functionality.
 * 
 * @author Markus Kroetzsch
 *
 */
public class RuleParser {

	public static void parseInto(KnowledgeBase knowledgeBase, InputStream stream, String encoding)
			throws ParsingException {
		JavaCCParser javaCcParser = new JavaCCParser(stream, encoding);
		javaCcParser.setKnowledgeBase(knowledgeBase);
		doParse(javaCcParser);
	}

	public static void parseInto(KnowledgeBase knowledgeBase, InputStream stream) throws ParsingException {
		parseInto(knowledgeBase, stream, "UTF-8");
	}

	public static void parseInto(KnowledgeBase knowledgeBase, String input) throws ParsingException {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		parseInto(knowledgeBase, inputStream, "UTF-8");
	}

	public static KnowledgeBase parse(InputStream stream, String encoding) throws ParsingException {
		return doParse(new JavaCCParser(stream, encoding));
	}

	public static KnowledgeBase parse(InputStream stream) throws ParsingException {
		return parse(stream, "UTF-8");
	}

	public static KnowledgeBase parse(String input) throws ParsingException {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		return parse(inputStream, "UTF-8");
	}

	public static Rule parseRule(String input) throws ParsingException {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		try {
			return localParser.rule();
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			throw new ParsingException(e.getMessage(), e);
		}
	}

	public static Literal parseLiteral(String input) throws ParsingException {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		try {
			return localParser.literal(FormulaContext.HEAD);
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			throw new ParsingException(e.getMessage(), e);
		}
	}

	public static PositiveLiteral parsePositiveLiteral(String input) throws ParsingException {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		try {
			return localParser.positiveLiteral(FormulaContext.HEAD);
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			throw new ParsingException(e.getMessage(), e);
		}
	}

	public static Fact parseFact(String input) throws ParsingException {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		JavaCCParser localParser = new JavaCCParser(inputStream, "UTF-8");
		try {
			return localParser.fact(FormulaContext.HEAD);
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			throw new ParsingException(e.getMessage(), e);
		}
	}

	static KnowledgeBase doParse(JavaCCParser parser) throws ParsingException {
		try {
			parser.parse();
			return parser.getKnowledgeBase();
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			throw new ParsingException(e.getMessage(), e);
		}
	}

}
