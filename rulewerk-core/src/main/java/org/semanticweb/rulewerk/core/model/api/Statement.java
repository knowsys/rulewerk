package org.semanticweb.rulewerk.core.model.api;

/*-
 * #%L
 * Rulewerk Core Components
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

/**
 * A statement is any element that a knowledge base can consist of, such as a
 * {@link Rule}, {@link Fact}, or {@link DataSourceDeclaration}.
 * 
 * @author Markus Kroetzsch
 *
 */
public interface Statement extends Entity {

	/**
	 * Accept a {@link StatementVisitor} and return its output.
	 *
	 * @param statementVisitor the StatementVisitor
	 * @return output of the visitor
	 */
	<T> T accept(StatementVisitor<T> statementVisitor);
}
