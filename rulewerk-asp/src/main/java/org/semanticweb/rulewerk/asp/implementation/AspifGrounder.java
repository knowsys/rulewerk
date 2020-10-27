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
import org.semanticweb.rulewerk.asp.model.AspReasoner;
import org.semanticweb.rulewerk.asp.model.Grounder;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
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

	private int ruleIndex = 1;

	/**
	 * Constructor.
	 *
	 * @param knowledgeBase the knowledge base to ground
	 * @param reasoner      the reasoner for inferring an over-approximation
	 * @param writer		the writer to write the grounding
	 */
	public AspifGrounder(KnowledgeBase knowledgeBase, Reasoner reasoner, BufferedWriter writer) {
		Validate.notNull(knowledgeBase);
		Validate.notNull(reasoner);
		Validate.notNull(writer);

		this.knowledgeBase = knowledgeBase;
		this.reasoner = reasoner;
		this.writer = writer;
	}

	@Override
	public boolean ground() throws IOException {
		this.reasoner.reason();
		writer.write("asp 1 0 0");
		writer.newLine();

		for (Statement statement : knowledgeBase.getStatements()) {
			boolean successful = statement.accept(this);
			if (!successful) {
				return false;
			}
		}

		for (Integer aspifValue : AspifIdentifier.getIntegerAspifIdentifierMap().keySet()) {
			// We encode a literal in the answer set by its aspif integer, and we transform it back with the help of the
			// integer-to-literal map later.
			writer.write("4 "
				+ aspifValue.toString().length() + " " + aspifValue.toString()
				+ " 1 " + aspifValue.toString());
			writer.newLine();
		}

		writer.write("0");
		writer.newLine();
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
		try {
			writer.write("1 0 1 " + AspifIdentifier.getAspifValue(statement, statement.getArguments()) + " 0 0");
			writer.newLine();
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Boolean visit(Rule statement) {
		PositiveLiteral query = AspReasonerImpl.getBodyVariablesLiteral(statement, ruleIndex++);
		QueryResultIterator answers = reasoner.answerQuery(query, false);
		RuleAspifTemplate ruleTemplate = new RuleAspifTemplate(statement, writer, query);
		try {
			while (answers.hasNext()) {
				List<Term> answerTerms = answers.next().getTerms();
				ruleTemplate.writeGroundInstances(answerTerms);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public Boolean visit(DataSourceDeclaration statement) {
		Predicate dataSourcePredicate = statement.getPredicate();
		List<Term> dataSourceQueryVariables = new ArrayList<>();
		for (int i=0; i<dataSourcePredicate.getArity(); i++) {
			dataSourceQueryVariables.add(Expressions.makeUniversalVariable("var" + i));
		}
		PositiveLiteral dataSourceQueryLiteral = Expressions.makePositiveLiteral(dataSourcePredicate, dataSourceQueryVariables);
		QueryResultIterator answers = reasoner.answerQuery(dataSourceQueryLiteral, false);
		try {
			while (answers.hasNext()) {
				writer.write("1 0 1 " + AspifIdentifier.getAspifValue(dataSourceQueryLiteral, answers.next().getTerms()) + " 0 0");
				writer.newLine();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Get the ground terms of a literal with respect to a specific answer.
	 *
	 * @param literal   the literal to ground
	 * @param answerMap a map representing the answer
	 * @return			list of ground terms
	 */
	private List<Term> getGroundTerms(Literal literal, Map<Variable, Term> answerMap) {
		List<Term> groundTerms = new ArrayList<>();
		for (int i = 0; i < literal.getArguments().size(); i++) {
			Term term = literal.getArguments().get(i);
			groundTerms.add(term.isVariable() ? answerMap.get(term) : term);
		}
		return groundTerms;
	}
}
