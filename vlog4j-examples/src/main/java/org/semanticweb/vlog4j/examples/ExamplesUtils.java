package org.semanticweb.vlog4j.examples;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*-
 * #%L
 * VLog4j Examples
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

public final class ExamplesUtils {

	public static final String OUTPUT_FOLDER = "src/main/data/output/";
	public static final String INPUT_FOLDER = "src/main/data/input/";

	/*
	 * This is a utility class. Therefore, it is best practice to do the following:
	 * (1) Make the class final, (2) make its constructor private, (3) make all its
	 * fields and methods static. This prevents the classes instantiation and
	 * inheritance.
	 */
	private ExamplesUtils() {

	}

	/**
	 * Prints out the {@code reasoner} answer's to given query ({@code queryAtom}).
	 *
	 * @param queryAtom query to be answered
	 * @param reasoner  reasoner to query on
	 * @throws ReasonerStateException in case the reasoner has not yet been loaded.
	 */
	public static void printOutQueryAnswers(final Atom queryAtom, final Reasoner reasoner)
			throws ReasonerStateException {
		System.out.println("Answers to query " + queryAtom + " :");
		try (final QueryResultIterator answers = reasoner.answerQuery(queryAtom, true)) {
			answers.forEachRemaining(answer -> System.out.println(" - " + answer));
			System.out.println();
		}
	}

	/**
	 * Returns the size of an Iterator
	 *
	 * @param Iterator<T> to iterate over
	 */
	public static <T> int iteratorSize(Iterator<T> iterator) {
		int size = 0;
		for (; iterator.hasNext(); ++size)
			iterator.next();
		return size;
	}

	/**
	 * Creates an Atom with @numberOfVariables variables
	 *
	 * @param atomName  for the new predicate
	 * @param atomArity number of variables
	 */
	private static Atom generalAtom(String atomName, int atomArity) {
		final List<Term> vars = new ArrayList<>();
		for (int i = 0; i < atomArity; i++)
			vars.add(makeVariable("x" + i));
		final Atom atom = Expressions.makeAtom(atomName, vars);
		return atom;
	}

	/**
	 * Exports the extension of the Atom with name @predicateName
	 *
	 * @param reasoner reasoner to query on
	 * @param atomName atom's name
	 * @param arity    atom's arity
	 */
	public static void exportQueryAnswersToCSV(Reasoner reasoner, String atomName, int atomArity)
			throws ReasonerStateException, IOException {
		final Atom atom = generalAtom(atomName, atomArity);
		String path = ExamplesUtils.OUTPUT_FOLDER + atomName + ".csv";
		reasoner.exportQueryAnswersToCsv(atom, path, true);
	}

}
