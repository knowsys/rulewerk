package org.semanticweb.rulewerk.asp.implementation;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.asp.AspReasoner;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.reasoner.*;

import java.io.IOException;
import java.util.List;

/**
 * Asp reasoner implementation using the VLog backend and clasp.
 *
 * @author Philipp Hanisch
 */
public class AspReasonerImpl implements AspReasoner {

	final private KnowledgeBase knowledgeBase;

	public AspReasonerImpl(KnowledgeBase knowledgeBase) {
		super();
		Validate.notNull(knowledgeBase);

		this.knowledgeBase = knowledgeBase;
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.knowledgeBase;
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
