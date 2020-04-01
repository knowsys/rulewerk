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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.NoSuchElementException;

import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.parser.javacc.JavaCCParser;
import org.semanticweb.rulewerk.parser.javacc.SubParserFactory;

/**
 * Handler for parsing a custom directive.
 *
 * @author Maximilian Marx
 */
@FunctionalInterface
public interface DirectiveHandler<T> {
	/**
	 * Parse a Directive.
	 *
	 * This is called by the parser to parse directives.
	 *
	 * @param arguments        Arguments given to the Directive statement.
	 * @param subParserFactory a factory for obtaining a SubParser, sharing the
	 *                         parser's state, but bound to new input.
	 *
	 * @throws ParsingException when any of the arguments is invalid for the
	 *                          directive, or the number of arguments is invalid.
	 * @return a {@code T} instance corresponding to the given arguments.
	 */
	public T handleDirective(List<DirectiveArgument> arguments, final SubParserFactory subParserFactory)
			throws ParsingException;

	/**
	 * Validate the provided number of arguments to the directive statement.
	 *
	 * @param arguments Arguments given to the Directive statement.
	 * @param number    expected number of arguments
	 *
	 * @throws ParsingException when the given number of Arguments is invalid for
	 *                          the Directive statement.
	 */
	public static void validateNumberOfArguments(final List<DirectiveArgument> arguments, final int number)
			throws ParsingException {
		if (arguments.size() != number) {
			throw new ParsingException(
					"Invalid number of arguments " + arguments.size() + " for Directive statement, expected " + number);
		}
	}

	/**
	 * Validate that the provided argument is a {@link String}.
	 *
	 * @param argument    the argument to validate
	 * @param description a description of the argument, used in constructing the
	 *                    error message.
	 *
	 * @throws ParsingException when the given argument is not a {@link String}.
	 *
	 * @return the contained {@link String}.
	 */
	public static String validateStringArgument(final DirectiveArgument argument, final String description)
			throws ParsingException {
		return argument.fromString()
				.orElseThrow(() -> new ParsingException("description \"" + argument + "\" is not a string."));
	}

	/**
	 * Validate that the provided argument is a file path.
	 *
	 * @param argument    the argument to validate
	 * @param description a description of the argument, used in constructing the
	 *                    error message.
	 *
	 * @throws ParsingException when the given argument is not a valid file path.
	 *
	 * @return the File corresponding to the contained file path.
	 */
	public static File validateFilenameArgument(final DirectiveArgument argument, final String description)
			throws ParsingException {
		String fileName = DirectiveHandler.validateStringArgument(argument, description);
		File file = new File(fileName);
		try {
			// we don't care about the actual path, just that there is one.
			file.toPath();
		} catch (InvalidPathException e) {
			throw new ParsingException(description + "\"" + argument + "\" is not a valid file path.", e);
		}

		return file;
	}

	/**
	 * Validate that the provided argument is an IRI.
	 *
	 * @param argument    the argument to validate
	 * @param description a description of the argument, used in constructing the
	 *                    error message.
	 *
	 * @throws ParsingException when the given argument is not an IRI.
	 *
	 * @return the contained IRI.
	 */
	public static URI validateIriArgument(final DirectiveArgument argument, final String description)
			throws ParsingException {
		return argument.fromIri()
				.orElseThrow(() -> new ParsingException(description + "\"" + argument + "\" is not an IRI."));
	}

	/**
	 * Validate that the provided argument is a {@link URL}.
	 *
	 * @param argument    the argument to validate
	 * @param description a description of the argument, used in constructing the
	 *                    error message.
	 *
	 * @throws ParsingException when the given argument is not a valid {@link URL}.
	 *
	 * @return the {@link URL} corresponding to the contained IRI.
	 */
	public static URL validateUrlArgument(final DirectiveArgument argument, final String description)
			throws ParsingException {
		URI iri = DirectiveHandler.validateIriArgument(argument, description);
		try {
			return iri.toURL();
		} catch (MalformedURLException e) {
			throw new ParsingException(description + "\"" + argument + "\" is not a valid URL.", e);
		}
	}

	/**
	 * Validate that the provided argument is a {@link Term}.
	 *
	 * @param argument    the argument to validate
	 * @param description a description of the argument, used in constructing the
	 *                    error message.
	 *
	 * @throws ParsingException when the given argument is not a {@link Term}.
	 *
	 * @return the contained {@link Term}.
	 */
	public static Term validateTermArgument(final DirectiveArgument argument, final String description)
			throws ParsingException {
		return argument.fromTerm()
				.orElseThrow(() -> new ParsingException(description + "\"" + argument + "\" is not a Term."));
	}

	/**
	 * Obtain a {@link KnowledgeBase} from a {@link SubParserFactory}.
	 *
	 * @param subParserFactory the SubParserFactory.
	 *
	 * @return the knowledge base.
	 */
	default KnowledgeBase getKnowledgeBase(SubParserFactory subParserFactory) {
		JavaCCParser subParser = subParserFactory.makeSubParser("");

		return subParser.getKnowledgeBase();
	}

	/**
	 * Obtain a {@link ParserConfiguration} from a {@link SubParserFactory}.
	 *
	 * @param subParserFactory the SubParserFactory.
	 *
	 * @return the parser configuration.
	 */
	default ParserConfiguration getParserConfiguration(SubParserFactory subParserFactory) {
		JavaCCParser subParser = subParserFactory.makeSubParser("");

		return subParser.getParserConfiguration();
	}

	/**
	 * Obtain {@link PrefixDeclarationRegistry} from a {@link SubParserFactory}.
	 *
	 * @param subParserFactory the SubParserFactory.
	 *
	 * @return the prefix declarations.
	 */
	default PrefixDeclarationRegistry getPrefixDeclarationRegistry(SubParserFactory subParserFactory) {
		JavaCCParser subParser = subParserFactory.makeSubParser("");

		return subParser.getPrefixDeclarationRegistry();
	}
}
