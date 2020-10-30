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
import org.semanticweb.rulewerk.asp.model.AspReasoningState;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class AnswerSetIteratorImpl implements AnswerSetIterator {

	private final Iterator<String> answerSetStringIterator;
	private AspReasoningState reasoningState;
	private final Map<Integer, Literal> integerLiteralMap;

	private final Map<Predicate, Set<Literal>> core;

	/**
	 * Static function to create an answer set iterator that represents an erroneous computation.
	 * @return an answer set iterator
	 */
	public static AnswerSetIterator getErrorAnswerSetIterator() {
		return new AnswerSetIteratorImpl();
	}

	/**
	 * The constructor.
	 *
	 * @param core a map of literals per predicate that are part of every answer set
	 * @param reader the reader containing the answer sets
	 * @param integerLiteralMap map of integers to the literals they represent
	 * @throws IOException an IO exception
	 */
	public AnswerSetIteratorImpl(Map<Predicate, Set<Literal>> core, BufferedReader reader, Map<Integer, Literal> integerLiteralMap) throws IOException {
		Validate.notNull(reader);
		Validate.notNull(integerLiteralMap);
		Validate.notNull(core);
		this.core = core;

		String line;
		List<String> answerSetStrings = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("SATISFIABLE")) {
				reasoningState = AspReasoningState.SATISFIABLE;
			} else if (line.startsWith("UNSATISFIABLE")) {
				reasoningState = AspReasoningState.UNSATISFIABLE;
			} else if (line.startsWith("INTERRUPTED")) {
				reasoningState = AspReasoningState.INTERRUPTED;
			} else if (line.startsWith("Answer: ")) {
				answerSetStrings.add(reader.readLine());
			}
		}
		answerSetStringIterator = answerSetStrings.iterator();
		this.integerLiteralMap = integerLiteralMap;
	}

	/**
	 * Private constructor for an answer set iterator representing an erroneous result.
	 */
	private AnswerSetIteratorImpl() {
		answerSetStringIterator = Collections.emptyIterator();
		reasoningState = AspReasoningState.ERROR;
		integerLiteralMap = null;
		core = null;
	}

	@Override
	public boolean hasNext() {
		return answerSetStringIterator.hasNext();
	}

	@Override
	public AnswerSet next() {
		return new AnswerSetImpl(core, answerSetStringIterator.next(), integerLiteralMap);
	}

	@Override
	public AspReasoningState getReasoningState() {
		return reasoningState;
	}
}
