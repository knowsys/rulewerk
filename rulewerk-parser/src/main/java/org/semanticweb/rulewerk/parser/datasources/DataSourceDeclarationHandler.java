package org.semanticweb.rulewerk.parser.datasources;

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

	DataSource handleDataSourceDeclaration(List<Term> terms) throws ParsingException;

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
			throw makeParameterParsingException(term, parameterName, e);
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
			throw makeParameterParsingException(term, parameterName, e);
		}
	}

	static ParsingException makeParameterParsingException(Term term, String parameterName, Throwable cause) {
		return new ParsingException("Expected " + parameterName + " to be a string. Found " + term.toString() + ".",
				cause);
	}
}
