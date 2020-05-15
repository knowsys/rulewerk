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

import org.semanticweb.rulewerk.core.model.api.DatatypeConstant;

/**
 * Handler for parsing a custom Datatype constant.
 *
 * @author Maximilian Marx
 */
@FunctionalInterface
public interface DatatypeConstantHandler {
	/**
	 * Parse a datatype constant.
	 *
	 * @param lexicalForm lexical representation of the constant.
	 *
	 * @throws ParsingException when the given representation is invalid for this
	 *                          datatype.
	 *
	 * @return a {@link DatatypeConstant} corresponding to the lexical form.
	 */
	public DatatypeConstant createConstant(String lexicalForm) throws ParsingException;
}
