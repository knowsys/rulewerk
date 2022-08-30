package org.semanticweb.rulewerk.parser.input;

/*-
 * #%L
 * Rulewerk Parser
 * %%
 * Copyright (C) 2018 - 2022 Rulewerk Developers
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
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParser;
import org.semanticweb.rulewerk.parser.javacc.ParseException;
import org.semanticweb.rulewerk.parser.javacc.SimpleNode;
import org.semanticweb.rulewerk.parser.javacc.Token;
import org.semanticweb.rulewerk.parser.javacc.TokenMgrError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputAwareRuleParser {

	public static final String DEFAULT_STRING_ENCODING = "UTF-8";

	private static Logger LOGGER = LoggerFactory.getLogger(InputAwareRuleParser.class);

	private InputAwareRuleParser() {
	}

	public static SimpleNode parse(final KnowledgeBase knowledgeBase, final String input)
			throws FileNotFoundException, InputAwareParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());

//		final InputStream inputStream = new FileInputStream(fileName);
//		final ParserConfiguration parserConfiguration = new DefaultParserConfiguration()
//				.setImportBasePath(file.getParent());
//		RuleParser.parseInto(interpreter.getKnowledgeBase(), inputStream, parserConfiguration);

		final JavaCCParser parser = new JavaCCParser(inputStream, DEFAULT_STRING_ENCODING);
		parser.setKnowledgeBase(knowledgeBase);

		SimpleNode node = doParse(parser);

//		knowledgeBase.mergePrefixDeclarations(parser.getPrefixDeclarationRegistry());

		return node;
	}

	static SimpleNode doParse(final JavaCCParser parser) throws InputAwareParsingException {
		try {
			final SimpleNode simpleNode = parser.parse();
			// TODO: Expose proper interface
			return simpleNode;
		} catch (ParseException | ParsingException | PrefixDeclarationException | TokenMgrError e) {
			// TODO syntax validation
			System.out.println(e.getMessage());

			final Token token = parser.token;
			System.out.println(" Error at Token: " + token.image + " kind: " + token.kind);
			System.out.println(" from: " + token.beginLine + ":" + token.beginColumn + " to " + token.endLine + ":"
					+ token.endColumn);

			System.out.println("Special token: " + token.specialToken);
			System.out.println("Next token: " + token.next);

			System.out.println("parser.jjtNodeName: " + parser.jjtNodeName);

			// TODO log something from token
			LOGGER.error("Error parsing Knowledge Base: " + e.getMessage(), e);
			throw new InputAwareParsingException(token, e.getMessage(), e);
		}

	}

//	final InputStream inputStream = interpreter.getFileInputStream(fileName);
//	final File file = new File(fileName);
//	final ParserConfiguration parserConfiguration = new DefaultParserConfiguration()
//			.setImportBasePath(file.getParent());
//	RuleParser.parseInto(interpreter.getKnowledgeBase(), inputStream, parserConfiguration);

//	@FunctionalInterface
//	public interface KnowledgeBaseProvider {
//		public KnowledgeBase knowledgeBase();
//	}
//
//	final public static KnowledgeBaseProvider EMPTY_KNOWLEDGE_BASE_PROVIDER = new KnowledgeBaseProvider() {
//		@Override
//		public KnowledgeBase knowledgeBase() {
//			return new KnowledgeBase();
//		}
//	};

//	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream, final String encoding,
//			final ParserConfiguration parserConfiguration, final String baseIri) throws ParsingException {
//		parseInto(knowledgeBase, stream, parserConfiguration, baseIri);
//	}
//
//	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream, final String encoding,
//			final ParserConfiguration parserConfiguration) throws ParsingException {
//		parseInto(knowledgeBase, stream, encoding, parserConfiguration, null);
//	}
//
//	public static void parseInto(final KnowledgeBase knowledgeBase, final InputStream stream,
//			final ParserConfiguration parserConfiguration, final String baseIri) throws ParsingException {
//		final JavaCCParser parser = new JavaCCParser(stream, DEFAULT_STRING_ENCODING);
//
//		if (baseIri != null) {
//			PrefixDeclarationRegistry prefixDeclarationRegistry = new LocalPrefixDeclarationRegistry(baseIri);
//			parser.setPrefixDeclarationRegistry(prefixDeclarationRegistry);
//		}
//
//		parser.setKnowledgeBase(knowledgeBase);
//		parser.setParserConfiguration(parserConfiguration);
//
//		doParse(parser);
//
//		knowledgeBase.mergePrefixDeclarations(parser.getPrefixDeclarationRegistry());
//	}
//

//	// TODO: Remove method
//	static void dumpSimpleNode(SimpleNode node, String prefix) {
//		// TODO: Remove method
//		// node.dump(prefix);
//		final Token firstToken = node.jjtGetFirstToken();
//		final Token lastToken = node.jjtGetLastToken();
//
//		System.out.println(prefix + "node " + node.toString() + ": From line " + firstToken.beginLine + ":"
//				+ firstToken.beginColumn + " to line " + lastToken.endLine + ":" + lastToken.endColumn);
//
//		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
//			final SimpleNode childNode = (SimpleNode) node.jjtGetChild(i);
//			if (childNode != null) {
//				dumpSimpleNode(childNode, prefix + " ");
//			}
//		}
//	}

}
