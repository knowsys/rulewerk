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
import org.semanticweb.rulewerk.asp.model.*;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.*;
import org.semanticweb.rulewerk.core.reasoner.implementation.QueryAnswerCountImpl;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Asp reasoner implementation using the VLog backend and clasp.
 *
 * @author Philipp Hanisch
 */
public class AspReasonerImpl implements AspReasoner {

	private static final Logger LOGGER = LoggerFactory.getLogger(AspReasonerImpl.class);

	final private KnowledgeBase knowledgeBase;
	private Set<Predicate> overApproximatedPredicates;

	final private KnowledgeBase datalogKnowledgeBase;
	final private Reasoner datalogReasoner;

	private AnswerSet cautiousAnswerSet;
	private Map<Predicate, Set<Literal>> answerSetCore;

	/**
	 * Auxiliary class to over-approximate ASP statements by plain Datalog statements.
	 *
	 * @author Philipp Hanisch
	 */
	private static class OverApproximationStatementVisitor implements StatementVisitor<List<Statement>> {

		private int ruleIndex = 1;
		private final Set<Predicate> overApproximatedPredicates;

		/**
		 * Constructor.
		 *
		 * @param overApproximatedPredicates set of over-approximated {@link Predicate}s
		 */
		public OverApproximationStatementVisitor(Set<Predicate> overApproximatedPredicates) {
			this.overApproximatedPredicates = overApproximatedPredicates;
		}

		@Override
		public List<Statement> visit(Fact statement) {
			return Collections.singletonList(statement);
		}

		@Override
		public List<Statement> visit(Rule statement) {
//			if (statement.getExistentialVariables().count() > 0) {
//				throw new IllegalArgumentException("ASP features and existential variables are not simultaneously allowed.");
//			}
			Set<UniversalVariable> positiveBodyVariables = statement.getBody().getLiterals().stream()
				.filter(literal -> !literal.isNegated())
				.flatMap(Literal::getUniversalVariables)
				.collect(Collectors.toSet());
			if (!statement.getUniversalVariables().allMatch(positiveBodyVariables::contains)) {
				throw new IllegalArgumentException("ASP features require that every variable occurs in a positive body literal.");
			}

			if (statement.getHead().getLiterals().stream().map(Literal::getPredicate).anyMatch(overApproximatedPredicates::contains)) {
				List<Statement> rules = new ArrayList<>();
				PositiveLiteral bodyVariableLiteral = getBodyVariablesLiteral(statement, ruleIndex++);

				List<Literal> bodyLiteralsToKeep = statement.getBody().getLiterals().stream().filter(
					literal -> !(literal.isNegated() && overApproximatedPredicates.contains(literal.getPredicate()))
				).collect(Collectors.toList());
				Conjunction<Literal> positiveBodyConjunction = Expressions.makeConjunction(bodyLiteralsToKeep);

				if (bodyLiteralsToKeep.isEmpty()) {
					rules.add(Expressions.makeFact(bodyVariableLiteral.getPredicate(), bodyVariableLiteral.getArguments()));
					for (PositiveLiteral literal : statement.getHead()) {
						rules.add(Expressions.makeFact(literal.getPredicate(), literal.getArguments()));
					}
				} else {
					List<PositiveLiteral> literals = new ArrayList<>(statement.getHead().getLiterals());
					literals.add(bodyVariableLiteral);
					Conjunction<PositiveLiteral> conjunction = Expressions.makeConjunction(literals);
					rules.add(Expressions.makeRule(conjunction, positiveBodyConjunction));
				}
				return rules;
			} else {
				return Collections.singletonList(statement);
			}
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
		this.knowledgeBase.addListener(this);
		this.datalogKnowledgeBase = new KnowledgeBase();

		transformKnowledgeBase();

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
		if (cautiousAnswerSet != null) {
			return true;
		}

		try (AspSolver solver = instantiateSolver(true, 0)) {
			solver.exec();
			Grounder grounder = instantiateGrounder(solver);
			System.out.println("Start grounding...");
			if (!grounder.ground()) {
				return false;
			}

			System.out.println("Start solving...");
			solver.solve();
			cautiousAnswerSet = getLastAnswerSet(solver.getReaderFromSolver(), grounder.getIntegerLiteralMap());

		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
			return false;
		}

		return true;
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
			return new AnswerSetImpl(getAnswerSetCore(), answerSetLine, integerLiteralMap);
		}
	}

	/**
	 * Get the core of all answer sets, i.e., the literals for the non-approximated predicates, which are part of every
	 * answer set.
	 *
	 * @return a map of literals per predicate
	 * @throws IOException an IO exception
	 */
	private Map<Predicate, Set<Literal>> getAnswerSetCore() throws IOException {
		if (answerSetCore == null) {
			datalogReasoner.reason();

			answerSetCore = new HashMap<>();
			for (Predicate predicate : knowledgeBase.getPredicates()) {
				if (overApproximatedPredicates.contains(predicate)) {
					continue;
				}

				List<Term> variables = new ArrayList<>();
				for (int i=0; i<predicate.getArity(); i++) {
					variables.add(Expressions.makeUniversalVariable("X" + i));
				}
				PositiveLiteral query = Expressions.makePositiveLiteral(predicate, variables);
				QueryResultIterator resultIterator = datalogReasoner.answerQuery(query, true);

				Set<Literal> results = new HashSet<>();
				while (resultIterator.hasNext()) {
					results.add(Expressions.makePositiveLiteral(predicate, resultIterator.next().getTerms()));
				}
				answerSetCore.put(predicate, Collections.unmodifiableSet(results));
			}
		}
		return Collections.unmodifiableMap(answerSetCore);
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

		try (AspSolver solver = instantiateSolver(false, maximum)) {
			solver.exec();
			Grounder grounder = instantiateGrounder(solver);
			System.out.println("Start grounding...");
			if (!grounder.ground()) {
				LOGGER.error("An error occurred while grounding the ASP knowledge base.");
				return AnswerSetIteratorImpl.getErrorAnswerSetIterator();
			}

			try {
				System.out.println("Start solving...");
				solver.solve();
			} catch (InterruptedException interruptedException) {
				LOGGER.warn("Clasp was interrupted while computing the answer sets.");
				interruptedException.printStackTrace();
			}

			return new AnswerSetIteratorImpl(getAnswerSetCore(), solver.getReaderFromSolver(), grounder.getIntegerLiteralMap());
		}
	}

	@Override
	public void groundToFile(String file, Set<Predicate> predicates) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		Grounder grounder = new AspifGrounder(knowledgeBase, datalogReasoner, writer, overApproximatedPredicates);
		System.out.println("Start grounding...");
		if (!grounder.ground(true, predicates)) {
			LOGGER.error("An error occurred while grounding the ASP knowledge base.");
		}
		writer.close();
		System.out.println("Grounding done...");
	}

	@Override
	public AspSolver instantiateSolver(boolean cautious, int maximumAnswerSets) throws IOException {
		return new Clasp(cautious, maximumAnswerSets, getReasoningTimeout());
	}

	@Override
	public Grounder instantiateGrounder(AspSolver aspSolver) {
		return new AspifGrounder(knowledgeBase, datalogReasoner, aspSolver.getWriterToSolver(), overApproximatedPredicates);
	}

	@Override
	public void onStatementAdded(Statement statementAdded) {
		transformKnowledgeBase();
	}

	@Override
	public void onStatementsAdded(List<Statement> statementsAdded) {
		transformKnowledgeBase();
	}

	@Override
	public void onStatementRemoved(Statement statementRemoved) {
		transformKnowledgeBase();
	}

	@Override
	public void onStatementsRemoved(List<Statement> statementsRemoved) {
		transformKnowledgeBase();
	}

	private void transformKnowledgeBase() {
		cautiousAnswerSet = null;
		answerSetCore = null;
		this.datalogKnowledgeBase.removeStatements(new ArrayList<>(this.datalogKnowledgeBase.getStatements()));
		KnowledgeBaseAnalyser analyser = new KnowledgeBaseAnalyser(this.knowledgeBase);
		overApproximatedPredicates = analyser.getOverApproximatedPredicates();
		OverApproximationStatementVisitor visitor = new OverApproximationStatementVisitor(overApproximatedPredicates);

		for (Statement statement : knowledgeBase.getStatements()) {
			this.datalogKnowledgeBase.addStatements(statement.accept(visitor));
		}
	}

	@Override
	public void resetReasoner() {
		this.datalogReasoner.resetReasoner();
		this.cautiousAnswerSet = null;
		this.answerSetCore = null;
	}

	@Override
	public void close() {
		this.datalogReasoner.close();
	}

	/**
	 * Returns a literal that is unique for the rule and index, and it contains all universal variables of the rule body.
	 *
	 * @param rule a rule
	 * @param index a unique rule index
	 * @return a positive literal
	 */
	public static PositiveLiteral getBodyVariablesLiteral(Rule rule, int index) {
		if (rule.getBody().getUniversalVariables().count() == 0) {
			return Expressions.makePositiveLiteral("_rule_" + index, Expressions.makeAbstractConstant("_0"));
		} else {
			return Expressions.makePositiveLiteral("_rule_" + index, rule.getBody().getUniversalVariables().collect(Collectors.toList()));
		}
	}

	@Override
	public CyclicityResult checkForCycles() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isJA() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRJA() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isMFA() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRMFA() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isMFC() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Correctness getCorrectness() {
		return datalogReasoner.getCorrectness();
	}

	@Override
	public QueryAnswerCount countQueryAnswers(PositiveLiteral query, boolean includeNulls) {
		if (cautiousAnswerSet == null) {
			try {
				reason();
			} catch (IOException ioException) {
				throw new RulewerkRuntimeException("Cautious answer set could not be computed");
			}
		}

		long count = 0;
		match:
		for (Literal literal : cautiousAnswerSet.getLiterals(query.getPredicate())) {
			List<Term> terms = query.getArguments();
			int idx = 0;
			while (idx < terms.size()) {
				if (terms.get(idx).isConstant() && !terms.get(idx).equals(literal.getArguments().get(idx))) {
					continue match;
				}
				idx++;
			}
			count++;
		}
		return new QueryAnswerCountImpl(getCorrectness(), count);
	}

	@Override
	public Correctness exportQueryAnswersToCsv(PositiveLiteral query, String csvFilePath, boolean includeNulls) throws IOException {
		if (cautiousAnswerSet == null) {
			try {
				reason();
			} catch (IOException ioException) {
				throw new RulewerkRuntimeException("Cautious answer set could not be computed");
			}
		}

		cautiousAnswerSet.exportQueryAnswersToCsv(query, csvFilePath);
		return getCorrectness();
	}

	@Override
	public Correctness forEachInference(InferenceAction action) throws IOException {
		// TODO
		return null;
	}
}
