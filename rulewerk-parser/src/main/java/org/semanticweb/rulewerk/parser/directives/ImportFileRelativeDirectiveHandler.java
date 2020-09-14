package org.semanticweb.rulewerk.parser.directives;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.semanticweb.rulewerk.core.exceptions.RulewerkException;
import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.parser.DirectiveHandler;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.parser.javacc.SubParserFactory;

/**
 * Handler for parsing {@code @import-relative} statements.
 *
 * @author Maximilian Marx
 */
public class ImportFileRelativeDirectiveHandler implements DirectiveHandler<KnowledgeBase> {
	@Override
	public KnowledgeBase handleDirective(List<Argument> arguments, SubParserFactory subParserFactory)
			throws ParsingException {
		final ParserConfiguration parserConfiguration = new ParserConfiguration(
				getParserConfiguration(subParserFactory));
		DirectiveHandler.validateNumberOfArguments(arguments, 1);
		PrefixDeclarationRegistry prefixDeclarationRegistry = getPrefixDeclarationRegistry(subParserFactory);
		File file = DirectiveHandler.validateFilenameArgument(arguments.get(0), "rules file",
				parserConfiguration.getImportBasePath());
		KnowledgeBase knowledgeBase = getKnowledgeBase(subParserFactory);
		parserConfiguration.setImportBasePath(file.getParent());

		try {
			knowledgeBase.importRulesFile(file, (InputStream stream, KnowledgeBase kb) -> {
				RuleParser.parseInto(kb, stream, parserConfiguration, prefixDeclarationRegistry.getBaseIri());
			});
		} catch (RulewerkException | IOException | IllegalArgumentException e) {
			throw new ParsingException("Failed while trying to import rules file \"" + file.getName() + "\"", e);
		}

		return knowledgeBase;
	}
}
