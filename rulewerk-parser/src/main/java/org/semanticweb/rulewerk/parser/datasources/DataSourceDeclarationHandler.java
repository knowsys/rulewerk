package org.semanticweb.rulewerk.parser.datasources;

import java.io.File;
import java.net.URL;
import java.util.List;

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

import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Terms;
import org.semanticweb.rulewerk.parser.ParsingException;

/**
 * Handler for interpreting the arguments of a custom Data Source declaration.
 *
 * @author Markus Kroetzsch
 */
@FunctionalInterface
public interface DataSourceDeclarationHandler {

	/**
	 * Handle a data source declaration.
	 *
	 * @param terms          the list of arguments given in the declaration
	 * @param importBasePath the base path that relative imports will be resolved
	 *                       against
	 *
	 * @throws ParsingException when the arguments are unsuitable for the data
	 *                          source.
	 *
	 * @return a DataSource instance.
	 */
	DataSource handleDataSourceDeclaration(List<Term> terms, String importBasePath) throws ParsingException;

	/**
	 * Validate the provided number of arguments to the source declaration.
	 *
	 * @param terms  arguments given to the source declaration.
	 * @param number expected number of arguments
	 *
	 * @throws ParsingException when the number of terms does not match expectations
	 */
	public static void validateNumberOfArguments(final List<Term> terms, final int number) throws ParsingException {
		if (terms.size() != number) {
			throw new ParsingException(
					"Invalid number of arguments " + terms.size() + " for @source declaration, expected " + number);
		}
	}

	/**
	 * Returns the string content of the given term, or reports an error if the term
	 * is not an xsd:string.
	 *
	 * @param term          the term to be processed
	 * @param parameterName the string name of the parameter to be used in error
	 *                      messages
	 * @return the extracted string
	 * @throws ParsingException thrown if the term was not a String
	 */
	public static String validateStringArgument(Term term, String parameterName) throws ParsingException {
		try {
			return Terms.extractString(term);
		} catch (IllegalArgumentException e) {
			throw makeParameterParsingException(term, parameterName, "String", e);
		}
	}

	/**
	 * Returns the URL represented by the given term, or reports an error if no
	 * valid URL could be extracted from the term.
	 *
	 * @param term          the term to be processed
	 * @param parameterName the string name of the parameter to be used in error
	 *                      messages
	 * @return the extracted URL
	 * @throws ParsingException thrown if the term was not a URL
	 */
	public static URL validateUrlArgument(Term term, String parameterName) throws ParsingException {
		try {
			return Terms.extractUrl(term);
		} catch (IllegalArgumentException e) {
			throw makeParameterParsingException(term, parameterName, "URL", e);
		}
	}

	/**
	 * Returns the File name represented by the given term, or reports an error if
	 * no valid File name could be extracted from the term.
	 *
	 * @param term           the term to be processed
	 * @param parameterName  the string name of the parameter to be used in error
	 *                       messages
	 * @param importBasePath the base path that relative paths will be resolved
	 *                       against
	 *
	 * @throws ParsingException when the term was not a valid file path
	 * @return the extracted file path
	 */
	public static String validateFileNameArgument(Term term, String parameterName, String importBasePath)
			throws ParsingException {
		File file;

		try {
			file = new File(Terms.extractString(term));
		} catch (IllegalArgumentException e) {
			throw makeParameterParsingException(term, parameterName, "File name", e);
		}

		if (file.isAbsolute() || importBasePath.isEmpty()) {
			return file.getPath();
		}
		return importBasePath + File.separator + file.getPath();
	}

	static ParsingException makeParameterParsingException(Term term, String parameterName, String type,
			Throwable cause) {
		return new ParsingException(
				"Expected " + parameterName + " to be a " + type + ". Found " + term.toString() + ".", cause);
	}
}
