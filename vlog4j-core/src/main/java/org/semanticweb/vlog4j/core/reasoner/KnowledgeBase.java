package org.semanticweb.vlog4j.core.reasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Statement;
import org.semanticweb.vlog4j.core.model.api.StatementVisitor;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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
 * A knowledge base with rules, facts, and declarations for loading data from
 * further sources. This is a "syntactic" object in that it represents some
 * information that is not relevant for the semantics of reasoning, but that is
 * needed to ensure faithful re-serialisation of knowledge bases loaded from
 * files (e.g., preserving order).
 * 
 * @author Markus Kroetzsch
 *
 */
public class KnowledgeBase implements Iterable<Statement> {

	private final Set<KnowledgeBaseListener> listeners = new HashSet<>();

	/**
	 * Auxiliary class to process {@link Statement}s when added to the knowledge
	 * base. Returns true if a statement was added successfully.
	 * 
	 * @author Markus Kroetzsch
	 *
	 */
	private class AddStatementVisitor implements StatementVisitor<Boolean> {
		@Override
		public Boolean visit(final Fact statement) {
			KnowledgeBase.this.addFact(statement);
			return true;
		}

		@Override
		public Boolean visit(final Rule statement) {
			return true;
		}

		@Override
		public Boolean visit(final DataSourceDeclaration statement) {
			KnowledgeBase.this.dataSourceDeclarations.add(statement);
			return true;
		}
	}

	private final AddStatementVisitor addStatementVisitor = new AddStatementVisitor();

	/**
	 * Auxiliary class to process {@link Statement}s when removed from the knowledge
	 * base. Returns true if a statement was removed successfully.
	 * 
	 * @author Irina Dragoste
	 *
	 */
	private class RemoveStatementVisitor implements StatementVisitor<Boolean> {

		@Override
		public Boolean visit(final Fact statement) {
			KnowledgeBase.this.removeFact(statement);
			return true;
		}

		@Override
		public Boolean visit(final Rule statement) {
			return true;
		}

		@Override
		public Boolean visit(final DataSourceDeclaration statement) {
			KnowledgeBase.this.dataSourceDeclarations.remove(statement);
			return true;
		}
	}

	private final RemoveStatementVisitor removeStatementVisitor = new RemoveStatementVisitor();

	private class ExtractStatementsVisitor<T> implements StatementVisitor<Void> {

		final ArrayList<T> extracted = new ArrayList<>();
		final Class<T> ownType;

		ExtractStatementsVisitor(final Class<T> type) {
			this.ownType = type;
		}

		ArrayList<T> getExtractedStatements() {
			return this.extracted;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Void visit(final Fact statement) {
			if (this.ownType.equals(Fact.class)) {
				this.extracted.add((T) statement);
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Void visit(final Rule statement) {
			if (this.ownType.equals(Rule.class)) {
				this.extracted.add((T) statement);
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Void visit(final DataSourceDeclaration statement) {
			if (this.ownType.equals(DataSourceDeclaration.class)) {
				this.extracted.add((T) statement);
			}
			return null;
		}
	}

	/**
	 * The primary storage for the contents of the knowledge base.
	 */
	private final LinkedHashSet<Statement> statements = new LinkedHashSet<>();

// TODO support prefixes
//	/**
//	 * Known prefixes that can be used to pretty-print the contents of the knowledge
//	 * base. We try to preserve user-provided prefixes found in files when loading
//	 * data.
//	 */
//	PrefixDeclarations prefixDeclarations;

	/**
	 * Index structure that organises all facts by their predicate.
	 */
	private final Map<Predicate, Set<PositiveLiteral>> factsByPredicate = new HashMap<>();

	/**
	 * Index structure that holds all data source declarations of this knowledge
	 * base.
	 */
	private final Set<DataSourceDeclaration> dataSourceDeclarations = new HashSet<>();

	/**
	 * Registers a listener for changes on the knowledge base
	 * 
	 * @param listener
	 */
	public void addListener(final KnowledgeBaseListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Unregisters given listener from changes on the knowledge base
	 * 
	 * @param listener
	 */
	public void deleteListener(final KnowledgeBaseListener listener) {
		this.listeners.remove(listener);

	}

	/**
	 * Adds a single statement to the knowledge base.
	 * 
	 * @param statement the statement to be added
	 */
	public void addStatement(final Statement statement) {
		if (this.doAddStatement(statement)) {
			this.notifyListenersOnStatementAdded(statement);
		}
	}

	/**
	 * Adds a single statement to the knowledge base.
	 * 
	 * @param statement the statement to be added
	 * @return true, if the knowledge base has changed.
	 */
	boolean doAddStatement(final Statement statement) {
		Validate.notNull(statement, "Statement cannot be Null!");
		if (!this.statements.contains(statement) && statement.accept(this.addStatementVisitor)) {
			this.statements.add(statement);
			return true;
		}
		return false;
	}

	/**
	 * Adds a collection of statements to the knowledge base.
	 * 
	 * @param statements the statements to be added
	 */
	public void addStatements(final Collection<? extends Statement> statements) {
		final List<Statement> addedStatements = new ArrayList<>();

		for (final Statement statement : statements) {
			if (this.doAddStatement(statement)) {
				addedStatements.add(statement);
			}
		}

		this.notifyListenersOnStatementsAdded(addedStatements);
	}

	/**
	 * Adds a list of statements to the knowledge base.
	 * 
	 * @param statements the statements to be added
	 */
	public void addStatements(final Statement... statements) {
		final List<Statement> addedStatements = new ArrayList<>();

		for (final Statement statement : statements) {
			if (this.doAddStatement(statement)) {
				addedStatements.add(statement);
			}
		}

		this.notifyListenersOnStatementsAdded(addedStatements);
	}

	/**
	 * Removes a single statement from the knowledge base.
	 * 
	 * @param statement the statement to remove
	 */
	public void removeStatement(final Statement statement) {
		if (this.doRemoveStatement(statement)) {
			this.notifyListenersOnStatementRemoved(statement);
		}
	}

	/**
	 * Removes a single statement from the knowledge base.
	 * 
	 * @param statement the statement to remove
	 * @return true, if the knowledge base has changed.
	 */
	boolean doRemoveStatement(final Statement statement) {
		Validate.notNull(statement, "Statement cannot be Null!");

		if (this.statements.contains(statement) && statement.accept(this.removeStatementVisitor)) {
			this.statements.remove(statement);
			return true;
		}
		return false;
	}

	/**
	 * Removes a collection of statements to the knowledge base.
	 * 
	 * @param statements the statements to remove
	 */
	public void removeStatements(final Collection<? extends Statement> statements) {
		final List<Statement> removedStatements = new ArrayList<>();

		for (final Statement statement : statements) {
			if (this.doRemoveStatement(statement)) {
				removedStatements.add(statement);
			}
		}

		this.notifyListenersOnStatementsRemoved(removedStatements);
	}

	/**
	 * Removes a list of statements from the knowledge base.
	 * 
	 * @param statements the statements to remove
	 */
	public void removeStatements(final Statement... statements) {
		final List<Statement> removedStatements = new ArrayList<>();

		for (final Statement statement : statements) {
			if (this.doRemoveStatement(statement)) {
				removedStatements.add(statement);
			}
		}

		this.notifyListenersOnStatementsRemoved(removedStatements);
	}

	private void notifyListenersOnStatementAdded(final Statement addedStatement) {
		for (final KnowledgeBaseListener listener : this.listeners) {
			listener.onStatementAdded(addedStatement);
		}
	}

	private void notifyListenersOnStatementsAdded(final List<Statement> addedStatements) {
		if (!addedStatements.isEmpty()) {
			for (final KnowledgeBaseListener listener : this.listeners) {
				listener.onStatementsAdded(addedStatements);
			}
		}
	}

	private void notifyListenersOnStatementRemoved(final Statement removedStatement) {
		for (final KnowledgeBaseListener listener : this.listeners) {
			listener.onStatementRemoved(removedStatement);
		}
	}

	private void notifyListenersOnStatementsRemoved(final List<Statement> removedStatements) {
		if (!removedStatements.isEmpty()) {
			for (final KnowledgeBaseListener listener : this.listeners) {
				listener.onStatementsRemoved(removedStatements);
			}
		}
	}

	/**
	 * Get the list of all rules that have been added to the knowledge base. The
	 * list is read-only and cannot be modified to add or delete rules.
	 * 
	 * @return list of {@link Rule}s
	 */
	public List<Rule> getRules() {
		return this.getStatementsByType(Rule.class);
	}

	/**
	 * Get the list of all facts that have been added to the knowledge base. The
	 * list is read-only and cannot be modified to add or delete facts.
	 * 
	 * @return list of {@link Fact}s
	 */
	public List<Fact> getFacts() {
		return this.getStatementsByType(Fact.class);
	}

	/**
	 * Get the list of all data source declarations that have been added to the
	 * knowledge base. The list is read-only and cannot be modified to add or delete
	 * facts.
	 * 
	 * @return list of {@link DataSourceDeclaration}s
	 */
	public List<DataSourceDeclaration> getDataSourceDeclarations() {
		return this.getStatementsByType(DataSourceDeclaration.class);
	}

	<T> List<T> getStatementsByType(final Class<T> type) {
		final ExtractStatementsVisitor<T> visitor = new ExtractStatementsVisitor<>(type);
		for (final Statement statement : this.statements) {
			statement.accept(visitor);
		}
		return Collections.unmodifiableList(visitor.getExtractedStatements());
	}

	/**
	 * Add a single fact to the internal data structures. It is assumed that it has
	 * already been checked that this fact is not present yet.
	 * 
	 * @param fact the fact to add
	 */
	void addFact(final Fact fact) {
		final Predicate predicate = fact.getPredicate();
		this.factsByPredicate.putIfAbsent(predicate, new HashSet<>());
		this.factsByPredicate.get(predicate).add(fact);
	}

	/**
	 * Removes a single fact from the internal data structure. It is assumed that it
	 * has already been checked that this fact is already present.
	 * 
	 * @param fact the fact to remove
	 */
	void removeFact(final Fact fact) {
		final Predicate predicate = fact.getPredicate();
		final Set<PositiveLiteral> facts = this.factsByPredicate.get(predicate);
		facts.remove(fact);
		if (facts.isEmpty()) {
			this.factsByPredicate.remove(predicate);
		}
	}

	/**
	 * Returns all {@link Statement}s of this knowledge base.
	 * 
	 * The result can be iterated over and will return statements in the original
	 * order. The collection is read-only and cannot be modified to add or delete
	 * statements.
	 * 
	 * @return a collection of statements
	 */
	public Collection<Statement> getStatements() {
		return Collections.unmodifiableCollection(this.statements);
	}

	@Override
	public Iterator<Statement> iterator() {
		return Collections.unmodifiableCollection(this.statements).iterator();
	}

	Map<Predicate, Set<PositiveLiteral>> getFactsByPredicate() {
		return this.factsByPredicate;
	}

}