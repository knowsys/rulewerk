package org.semanticweb.rulewerk.core.reasoner;

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

import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Statement;

/**
 * Listener to {@link KnowledgeBase} content change events.
 * 
 * @author Irina Dragoste
 *
 */
public interface KnowledgeBaseListener {

	/**
	 * Event triggered whenever a new statement is added to the associated knowledge
	 * base.
	 * 
	 * @param statementAdded new statement added to the knowledge base.
	 */
	void onStatementAdded(Statement statementAdded);

	/**
	 * Event triggered whenever new statements are added to the associated knowledge
	 * base.
	 * 
	 * @param statementsAdded a list of new statements that have been added to the
	 *                        knowledge base.
	 */
	void onStatementsAdded(List<Statement> statementsAdded);

	/**
	 * Event triggered whenever a new statement is removed from the associated
	 * knowledge base.
	 * 
	 * @param statementRemoved statement removed from the knowledge base.
	 */
	void onStatementRemoved(Statement statementRemoved);

	/**
	 * Event triggered whenever new statements are removed from the associated
	 * knowledge base.
	 * 
	 * @param statementsRemoved a list of new statements that have been removed from
	 *                          the knowledge base.
	 */
	void onStatementsRemoved(List<Statement> statementsRemoved);

}
