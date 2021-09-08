package org.semanticweb.rulewerk.asp.implementation;

/*
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

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.asp.model.Grounder;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * An ASP grounder whose output is the aspif format, which is used by Gringo.
 *
 * @author Philipp Hanisch
 */
public class AspifGrounder implements Grounder {

	private final KnowledgeBase knowledgeBase;
	private final Reasoner reasoner;
	private final BufferedWriter writer;
	private final Set<Predicate> overApproximatedPredicates;

	private int ruleIndex = 1;

	/**
	 * Constructor.
	 * @param knowledgeBase the knowledge base to ground
	 * @param reasoner the reasoner for inferring an over-approximation
	 * @param writer the writer to write the grounding
	 * @param overApproximatedPredicates the set of over-approximated predicates
	 */
	public AspifGrounder(KnowledgeBase knowledgeBase, Reasoner reasoner, BufferedWriter writer, Set<Predicate> overApproximatedPredicates) {
		Validate.notNull(knowledgeBase);
		Validate.notNull(reasoner);
		Validate.notNull(writer);
		Validate.notNull(overApproximatedPredicates);
		Validate.noNullElements(overApproximatedPredicates);

		this.knowledgeBase = knowledgeBase;
		this.reasoner = reasoner;
		this.writer = writer;
		this.overApproximatedPredicates = overApproximatedPredicates;
	}

	@Override
	public boolean ground() {
		return ground(false, Collections.emptySet());
	}

	@Override
	public boolean ground(boolean literalsAsStrings, Set<Predicate> predicates) {
		try {
			this.reasoner.reason();
			System.out.println("Over-approximation computed");
			writer.write("asp 1 0 0");
			writer.newLine();

			ruleIndex = 1;
			for (Statement statement : knowledgeBase.getStatements()) {
				boolean successful = statement.accept(this);
				if (!successful) {
					return false;
				}
			}

			if (literalsAsStrings) {
				for (Map.Entry<Integer, AspifIdentifier> entry : AspifIdentifier.getIntegerAspifIdentifierMap().entrySet()) {
					Predicate predicate = entry.getValue().getPositiveLiteral().getPredicate();
					if (!predicates.contains(predicate)) {
						continue;
					}

					Integer aspifValue = entry.getKey();
					String literalString = entry.getValue().getPositiveLiteral().toString();
					// We encode a literal in the answer set by its aspif integer, and we transform it back with the help of the
					// integer-to-literal map later.
					writer.write("4 "
						+ literalString.length() + " " + literalString
						+ " 1 " + aspifValue.toString());
					writer.newLine();
				}

				for (Predicate predicate : predicates) {
					if (overApproximatedPredicates.contains(predicate)) {
						continue;
					}

					List<Term> variables = new ArrayList<>();
					for (int i=0; i<predicate.getArity(); i++) {
						variables.add(Expressions.makeUniversalVariable("X" + i));
					}
					PositiveLiteral query = Expressions.makePositiveLiteral(predicate, variables);
					QueryResultIterator resultIterator = reasoner.answerQuery(query, true);

					while (resultIterator.hasNext()) {
						String literalString = Expressions.makePositiveLiteral(predicate, resultIterator.next().getTerms()).toString();
						writer.write("4 "
							+ literalString.length() + " " + literalString + " 0");
						writer.newLine();
					}
				}
			} else {
				for (Integer aspifValue : AspifIdentifier.getIntegerAspifIdentifierMap().keySet()) {
					// We encode a literal in the answer set by its aspif integer, and we transform it back with the help of the
					// integer-to-literal map later.
					writer.write("4 "
						+ aspifValue.toString().length() + " " + aspifValue.toString()
						+ " 1 " + aspifValue.toString());
					writer.newLine();
				}
			}

			writer.write("0");
			writer.newLine();
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Map<Integer, Literal> getIntegerLiteralMap() {
		Map<Integer, Literal> map = new Int2ObjectOpenHashMap<>();
		for (Integer integer : AspifIdentifier.getIntegerAspifIdentifierMap().keySet()) {
			AspifIdentifier aspifIdentifier = AspifIdentifier.getIntegerAspifIdentifierMap().get(integer);
			map.put(integer, aspifIdentifier.getPositiveLiteral());
		}
		return map;
	}

	@Override
	public Boolean visit(Fact statement) {
		if (overApproximatedPredicates.contains(statement.getPredicate())) {
			try {
				writer.write("1 0 1 " + AspifIdentifier.getAspifValue(statement, statement.getArguments()) + " 0 0");
				writer.newLine();
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public Boolean visit(Rule statement) {
		if (statement.getHead().getLiterals().stream().map(Literal::getPredicate).anyMatch(overApproximatedPredicates::contains)) {
			PositiveLiteral query = AspReasonerImpl.getBodyVariablesLiteral(statement, ruleIndex++);
			QueryResultIterator answers = reasoner.answerQuery(query, true);
			RuleAspifTemplate ruleTemplate = new RuleAspifTemplate(statement, writer, query, overApproximatedPredicates);
			try {
				while (answers.hasNext()) {
					List<Term> answerTerms = answers.next().getTerms();
					ruleTemplate.writeGroundInstances(answerTerms);
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return false;
			}
		}

		return true;
	}

	@Override
	public Boolean visit(DataSourceDeclaration statement) {
		if (overApproximatedPredicates.contains(statement.getPredicate())) {
			Predicate dataSourcePredicate = statement.getPredicate();
			List<Term> dataSourceQueryVariables = new ArrayList<>();
			for (int i=0; i<dataSourcePredicate.getArity(); i++) {
				dataSourceQueryVariables.add(Expressions.makeUniversalVariable("var" + i));
			}
			PositiveLiteral dataSourceQueryLiteral = Expressions.makePositiveLiteral(dataSourcePredicate, dataSourceQueryVariables);
			QueryResultIterator answers = reasoner.answerQuery(dataSourceQueryLiteral, true);
			try {
				while (answers.hasNext()) {
					writer.write("1 0 1 " + AspifIdentifier.getAspifValue(dataSourceQueryLiteral, answers.next().getTerms()) + " 0 0");
					writer.newLine();
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return false;
			}
		}

		return true;
	}
}
