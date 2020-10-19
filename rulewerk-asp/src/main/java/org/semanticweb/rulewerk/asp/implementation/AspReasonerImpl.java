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
import org.semanticweb.rulewerk.asp.model.AnswerSet;
import org.semanticweb.rulewerk.asp.model.AnswerSetIterator;
import org.semanticweb.rulewerk.asp.model.AspReasoner;
import org.semanticweb.rulewerk.asp.model.Grounder;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.*;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

	private AnswerSet cautiousAnswerSet;

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

		// Construct transformed knowledge base
		this.datalogKnowledgeBase = new KnowledgeBase();
		OverApproximationStatementVisitor visitor = new OverApproximationStatementVisitor();
		for (Statement statement : knowledgeBase.getStatements()) {
			this.datalogKnowledgeBase.addStatements(statement.accept(visitor));
		}

		// Create reasoner with the transformed knowledge base
		this.datalogReasoner = new VLogReasoner(datalogKnowledgeBase);
		this.setLogLevel(LogLevel.WARNING);
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.knowledgeBase;
	}

	@Override
	public KnowledgeBase getDatalogKnowledgeBase() {
		return this.datalogKnowledgeBase;
	}

	@Override
	public void setLogLevel(LogLevel logLevel) {
		this.datalogReasoner.setLogLevel(logLevel);
	}

	@Override
	public LogLevel getLogLevel() {
		return this.datalogReasoner.getLogLevel();
	}

	@Override
	public void setLogFile(String filePath) {
		this.datalogReasoner.setLogFile(filePath);
	}

	@Override
	public void setReasoningTimeout(Integer seconds) {
		this.datalogReasoner.setReasoningTimeout(seconds);
	}

	@Override
	public Integer getReasoningTimeout() {
		return this.datalogReasoner.getReasoningTimeout();
	}

	@Override
	public void setAlgorithm(Algorithm algorithm) {
		datalogReasoner.setAlgorithm(algorithm);
	}

	@Override
	public Algorithm getAlgorithm() {
		return datalogReasoner.getAlgorithm();
	}

	@Override
	public void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) {
		datalogReasoner.setRuleRewriteStrategy(ruleRewritingStrategy);
	}

	@Override
	public RuleRewriteStrategy getRuleRewriteStrategy() {
		return datalogReasoner.getRuleRewriteStrategy();
	}

	@Override
	public boolean reason() throws IOException {
		if (cautiousAnswerSet == null) {
			this.datalogReasoner.reason();

			Process clasp = Runtime.getRuntime().exec("clasp -e cautious");
			BufferedWriter writerToClasp = new BufferedWriter(new OutputStreamWriter(clasp.getOutputStream()));

			Grounder grounder = new AspifGrounder(knowledgeBase, datalogReasoner, writerToClasp);
			boolean successful = grounder.ground();
			writerToClasp.close();
			if (!successful) {
				clasp.destroy();
				return false;
			}

			try {
				if (getReasoningTimeout() == null) {
					clasp.waitFor();
				} else {
					clasp.waitFor(getReasoningTimeout(), TimeUnit.SECONDS);
				}
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
				clasp.destroy();
				return false;
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(clasp.getInputStream()));
			try {
				cautiousAnswerSet = getLastAnswerSet(reader, grounder.getIntegerLiteralMap());
			} catch (NoSuchElementException noSuchElementException) {
				noSuchElementException.printStackTrace();
				clasp.destroy();
				reader.close();
				return false;
			}
			reader.close();
			clasp.destroy();
		}
		return true;
	}

	/**
	 * Reads the next answer set from a buffered reader. It is assumed that the reader contains a line that starts with
	 * "Answer: n" with an integer n, and that this line is followed by the string representation of an answer set,
	 * i.e., a space-separated list of integers. The integer-to-literal map is used to retrieve the correct literal for
	 * a integer.
	 *
	 * @param reader 				  the reader with the answer set
	 * @param integerLiteralMap 	  the integer-to-literal map
	 * @return 						  the next answer set
	 * @throws NoSuchElementException exception if the reader contains no answer set
	 * @throws IOException			  an IO exception
	 */
	private AnswerSet getNextAnswerSet(BufferedReader reader, Map<Integer, Literal> integerLiteralMap) throws NoSuchElementException, IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.trim().startsWith("Answer: ")) {
				return new AnswerSetImpl(reader.readLine(), integerLiteralMap);
			}
		}
		throw new NoSuchElementException("The given reader did not contain an answer set");
	}

	/**
	 * Reads the last answer set from a buffered reader. It is assumed that the reader contains a line that starts with
	 * "Answer: n" with an integer n, and that this line is followed by the string representation of an answer set,
	 * i.e., a space-separated list of integers. The integer-to-literal map is used to retrieve the correct literal for
	 * a integer.
	 *
	 * @param reader 				  the reader with the answer set
	 * @param integerLiteralMap 	  the integer-to-literal map
	 * @return 						  the last answer set
	 * @throws NoSuchElementException exception if the reader contains no answer set
	 * @throws IOException			  an IO exception
	 */
	private AnswerSet getLastAnswerSet(BufferedReader reader, Map<Integer, Literal> integerLiteralMap) throws NoSuchElementException, IOException {
		String line;
		String answerSetLine = null;
		while ((line = reader.readLine()) != null) {
			if (line.trim().startsWith("Answer: ")) {
				answerSetLine = reader.readLine();
			}
		}

		if (answerSetLine == null) {
			throw new NoSuchElementException("The given reader did not contain an answer set");
		} else {
			return new AnswerSetImpl(answerSetLine, integerLiteralMap);
		}
	}

	@Override
	public QueryResultIterator answerQuery(PositiveLiteral query, boolean includeNulls) {
		if (cautiousAnswerSet == null) {
			try {
				reason();
			} catch (Exception e) {
				throw new RulewerkRuntimeException("Exception while cautious ASP reasoning", e);
			}
		}
		return cautiousAnswerSet.getQueryResults(query);
	}

	@Override
	public AnswerSetIterator getAnswerSets() throws IOException {
		return getAnswerSets(0);
	}

	@Override
	public AnswerSetIterator getAnswerSets(int maximum) throws IOException {
		Validate.isTrue(maximum >= 0);
		this.datalogReasoner.reason();

		Process clasp = Runtime.getRuntime().exec("clasp -n " + maximum);
		BufferedWriter writerToClasp = new BufferedWriter(new OutputStreamWriter(clasp.getOutputStream()));

		Grounder grounder = new AspifGrounder(knowledgeBase, datalogReasoner, writerToClasp);
		boolean successful = grounder.ground();
		writerToClasp.close();
		if (!successful) {
			clasp.destroy();
			return AnswerSetIteratorImpl.getErrorAnswerSetIterator();
		}

		try {
			if (getReasoningTimeout() == null) {
				clasp.waitFor();
			} else {
				clasp.waitFor(getReasoningTimeout(), TimeUnit.SECONDS);
			}		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(clasp.getInputStream()));
		AnswerSetIterator answerSetIterator = new AnswerSetIteratorImpl(reader, grounder.getIntegerLiteralMap());
		reader.close();
		clasp.destroy();
		return answerSetIterator;
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
