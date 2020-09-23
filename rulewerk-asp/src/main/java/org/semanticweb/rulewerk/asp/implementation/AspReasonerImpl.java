package org.semanticweb.rulewerk.asp.implementation;

/*-
 * #%L
 * Rulewerk ASP Components
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

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.asp.model.AspReasoner;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.*;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Asp reasoner implementation using the VLog backend and clasp.
 *
 * @author Philipp Hanisch
 */
public class AspReasonerImpl implements AspReasoner {

	final private KnowledgeBase knowledgeBase;

	final private KnowledgeBase datalogKnowledgeBase;
	final private Reasoner datalogReasoner;

	final private Map<Rule, Integer> ruleIndexMap;

	private LogLevel internalLogLevel = LogLevel.WARNING;

	/**
	 * Auxiliary class to over-approximate ASP statements by plain Datalog statements.
	 *
	 * @author Philipp Hanisch
	 */
	private static class OverApproximationStatementVisitor implements StatementVisitor<List<Statement>> {

		@Override
		public List<Statement> visit(Fact statement) {
			return Collections.singletonList(statement);
		}

		@Override
		public List<Statement> visit(Rule statement) {
			if (statement.getExistentialVariables().count() > 0) {
				throw new IllegalArgumentException("ASP features and existential variables are not simultaneously allowed.");
			}
			List<Literal> positiveBodyLiterals = statement.getBody().getLiterals().stream().filter(
				literal -> !literal.isNegated()).collect(Collectors.toList());

			Conjunction<Literal> positiveBodyConjunction = Expressions.makeConjunction(positiveBodyLiterals);
			Conjunction<PositiveLiteral> bodyVariablesLiteralConjunction = Expressions.makePositiveConjunction(statement.getBodyVariablesLiteral());

			List<Statement> rules = new ArrayList<>();
			if (positiveBodyLiterals.isEmpty()) {
				PositiveLiteral bodyVariableLiteral = statement.getBodyVariablesLiteral();
				rules.add(Expressions.makeFact(bodyVariableLiteral.getPredicate(), bodyVariableLiteral.getArguments()));
			} else {
				rules.add(Expressions.makeRule(bodyVariablesLiteralConjunction, positiveBodyConjunction));
			}
			rules.add(Expressions.makePositiveLiteralsRule(statement.getHead(), bodyVariablesLiteralConjunction));
			return rules;
		}

		@Override
		public List<Statement> visit(DataSourceDeclaration statement) {
			return Collections.singletonList(statement);
		}
	}

	/**
	 * The constructor.
	 *
	 * @param knowledgeBase the ASP knowledge base
	 */
	public AspReasonerImpl(KnowledgeBase knowledgeBase) {
		super();

		Validate.notNull(knowledgeBase);
		this.knowledgeBase = knowledgeBase;
		this.ruleIndexMap = new HashMap<>();

		// Construct transformed knowledge base
		this.datalogKnowledgeBase = new KnowledgeBase();
		OverApproximationStatementVisitor visitor = new OverApproximationStatementVisitor();
		for (Statement statement : knowledgeBase.getStatements()) {
			this.datalogKnowledgeBase.addStatements(statement.accept(visitor));
		}

		// Create reasoner with the transformed knowledge base
		this.datalogReasoner = new VLogReasoner(datalogKnowledgeBase);
		this.setLogLevel(this.internalLogLevel);
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.knowledgeBase;
	}

	@Override
	public KnowledgeBase getDatalogKnowledgeBase() {
		return this.datalogKnowledgeBase;
	}

	// start: dummy implementations
	@Override
	public Correctness forEachInference(InferenceAction action) throws IOException {
		return null;
	}

	@Override
	public Correctness getCorrectness() {
		return null;
	}

	@Override
	public void setAlgorithm(Algorithm algorithm) {

	}

	@Override
	public Algorithm getAlgorithm() {
		return null;
	}

	@Override
	public void setReasoningTimeout(Integer seconds) {

	}

	@Override
	public Integer getReasoningTimeout() {
		return null;
	}

	@Override
	public void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) {

	}

	@Override
	public RuleRewriteStrategy getRuleRewriteStrategy() {
		return null;
	}

	@Override
	public void setLogLevel(LogLevel logLevel) {

	}

	@Override
	public LogLevel getLogLevel() {
		return null;
	}

	@Override
	public void setLogFile(String filePath) {

	}

	@Override
	public CyclicityResult checkForCycles() {
		return null;
	}

	@Override
	public boolean isJA() {
		return false;
	}

	@Override
	public boolean isRJA() {
		return false;
	}

	@Override
	public boolean isMFA() {
		return false;
	}

	@Override
	public boolean isRMFA() {
		return false;
	}

	@Override
	public boolean isMFC() {
		return false;
	}

	@Override
	public boolean reason() throws IOException {
		return false;
	}

	@Override
	public QueryResultIterator answerQuery(PositiveLiteral query, boolean includeNulls) {
		return null;
	}

	@Override
	public QueryAnswerCount countQueryAnswers(PositiveLiteral query, boolean includeNulls) {
		return null;
	}

	@Override
	public Correctness exportQueryAnswersToCsv(PositiveLiteral query, String csvFilePath, boolean includeNulls) throws IOException {
		return null;
	}

	@Override
	public void resetReasoner() {

	}

	@Override
	public void close() {

	}

	@Override
	public void onStatementAdded(Statement statementAdded) {

	}

	@Override
	public void onStatementsAdded(List<Statement> statementsAdded) {

	}

	@Override
	public void onStatementRemoved(Statement statementRemoved) {

	}

	@Override
	public void onStatementsRemoved(List<Statement> statementsRemoved) {

	}
	// end: dummy implementations
}
