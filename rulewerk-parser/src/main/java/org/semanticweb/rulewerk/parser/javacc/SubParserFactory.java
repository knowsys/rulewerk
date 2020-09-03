package org.semanticweb.rulewerk.parser.javacc;

import java.io.ByteArrayInputStream;

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

import java.io.InputStream;

import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.implementation.Skolemization;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * Factory for creating a SubParser sharing configuration, (semantic) state, and
 * prefixes, but with an independent input stream, to be used, e.g., for parsing
 * arguments in data source declarations. The parser will start in the
 * {@code DEFAULT} lexical state.
 *
 * @author Maximilian Marx
 */
public class SubParserFactory {
	private final KnowledgeBase knowledgeBase;
	private final ParserConfiguration parserConfiguration;
	private final PrefixDeclarationRegistry prefixDeclarationRegistry;
	private final Skolemization skolemization;

	/**
	 * Construct a SubParserFactory.
	 *
	 * @param parser the parser instance to get the (semantic) state from.
	 */
	SubParserFactory(final JavaCCParser parser) {
		this.knowledgeBase = parser.getKnowledgeBase();
		this.prefixDeclarationRegistry = parser.getPrefixDeclarationRegistry();
		this.parserConfiguration = parser.getParserConfiguration();
		this.skolemization = parser.getSkolemization();
	}

	/**
	 * Create a new parser with the specified (semantic) state and given input.
	 *
	 * @param inputStream the input stream to parse.
	 * @param encoding    encoding of the input stream.
	 *
	 * @return A new {@link JavaCCParser} bound to inputStream and with the
	 *         specified parser state.
	 */
	public JavaCCParser makeSubParser(final InputStream inputStream, final String encoding) {
		final JavaCCParser subParser = new JavaCCParser(inputStream, encoding);
		subParser.setKnowledgeBase(this.knowledgeBase);
		subParser.setPrefixDeclarationRegistry(this.prefixDeclarationRegistry);
		subParser.setParserConfiguration(this.parserConfiguration);
		subParser.setSkolemization(this.skolemization);

		return subParser;
	}

	public JavaCCParser makeSubParser(final InputStream inputStream) {
		return this.makeSubParser(inputStream, RuleParser.DEFAULT_STRING_ENCODING);
	}

	public JavaCCParser makeSubParser(final String string) {
		return this.makeSubParser(new ByteArrayInputStream(string.getBytes()));
	}
}
