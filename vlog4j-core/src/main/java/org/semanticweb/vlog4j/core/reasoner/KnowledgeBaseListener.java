package org.semanticweb.vlog4j.core.reasoner;

import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Statement;

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

}
