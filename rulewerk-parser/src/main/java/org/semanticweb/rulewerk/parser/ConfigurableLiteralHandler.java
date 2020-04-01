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

import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.parser.javacc.SubParserFactory;

/**
 * Handler for parsing a configurable literal expression. Note that these are
 * not Literals in the logical sense (i.e., Atoms with a specific polarity), but
 * rather expressions in the sense of
 * <a href="https://www.w3.org/TR/turtle/#literals">RDF literals</a>;
 * essentially adding further quoted literals with custom delimiters.
 *
 * @author Maximilian Marx
 */
@FunctionalInterface
public interface ConfigurableLiteralHandler {
	/**
	 * Parse a Data Source Declaration.
	 *
	 * @param syntacticForm    syntactic form of the literal expression.
	 * @param subParserFactory a factory for obtaining a SubParser, sharing the
	 *                         parser's state, but bound to new input.
	 *
	 * @throws ParsingException when the given syntactic form is invalid.
	 * @return an appropriate @{link Term} instance.
	 */
	public Term parseLiteral(String syntacticForm, final SubParserFactory subParserFactory) throws ParsingException;
}
