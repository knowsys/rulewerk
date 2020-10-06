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
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.asp.model.Grounder;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

/**
 * An ASP grounder whose output is the aspif format, which is used by Gringo.
 *
 * @author Philipp Hanisch
 */
public class AspifGrounder implements Grounder {

	private final Map<Integer, Literal> integerLiteralMap;
	private final Map<Literal, Integer> literalIntegerMap;
	private int aspifCounter;

	private final KnowledgeBase knowledgeBase;
	private final Reasoner reasoner;
	private final BufferedWriter writer;

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
		this.integerLiteralMap = new Int2ObjectOpenHashMap<>();
		this.literalIntegerMap = new Object2IntOpenHashMap<>();
		this.aspifCounter = 1;
	}

	@Override
	public boolean ground() throws IOException {
		writer.write("asp 1 0 0");
		writer.newLine();

		for (Statement statement : knowledgeBase.getStatements()) {
			boolean successful = statement.accept(this);
			if (!successful) {
				return false;
			}
		}

		writer.write("0");
		writer.newLine();
		return true;
	}

	@Override
	public Map<Integer, Literal> getIntegerLiteralMap() {
		return this.integerLiteralMap;
	}

	@Override
	public Boolean visit(Fact statement) {
		try {
			writer.write("1 0 1 " + getAspifValue(statement) + " 0 0");
			writer.newLine();
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Boolean visit(Rule statement) {
		return null;
	}

	@Override
	public Boolean visit(DataSourceDeclaration statement) {
		return null;
	}

	/**
	 * Get and possibly negate the aspif integer for a ground literal.
	 *
	 * @param literal the aspif identifier for a grounded literal
	 * @return the aspif integer
	 */
	private int getAspifValue(Literal groundLiteral) throws IOException {
		Integer aspifValue = literalIntegerMap.getOrDefault(groundLiteral, 0);
		if (aspifValue == 0) {
			aspifValue = aspifCounter++;
			literalIntegerMap.put(groundLiteral, aspifValue);
			integerLiteralMap.put(aspifValue, groundLiteral);

			// We encode a literal in the answer set by its aspif integer, and we transform it back with the help of the
			// integer-to-literal map later.
			writer.write("4 "
				+ aspifValue.toString().length() + " " + aspifValue.toString()
				+ " 1 " + aspifValue.toString());
			writer.newLine();
		}

		return groundLiteral.isNegated() ? -aspifValue : aspifValue;
	}

	/**
	 * Get a one-time only aspif integer that can be used to abbreviate constructs.
	 *
	 * @return an aspif integer
	 */
	private int getAspifValue() {
		return aspifCounter++;
	}
}
