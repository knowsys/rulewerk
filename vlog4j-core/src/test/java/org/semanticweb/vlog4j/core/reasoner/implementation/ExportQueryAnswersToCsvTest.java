package org.semanticweb.vlog4j.core.reasoner.implementation;

/*-
 * #%L
 * VLog4j Core Components
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.CsvFileUtils;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

public class ExportQueryAnswersToCsvTest {

	@Test
	public void testEDBQuerySameConstantSubstitutesSameVariableName()
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final String predicate = "p";
		final Constant constantC = Expressions.makeConstant("c");
		final Constant constantD = Expressions.makeConstant("d");
		final Variable x = Expressions.makeVariable("X");
		final Variable y = Expressions.makeVariable("Y");
		final Variable z = Expressions.makeVariable("Z");
		final Atom fact = Expressions.makeAtom(predicate, constantC, constantC, constantD);

		final boolean includeBlanks = false;
		// final String csvFilePath = CSV_EXPORT_FOLDER + "output";
		final List<List<String>> factCCD = Arrays.asList(Arrays.asList("c", "c", "d"));

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addFacts(fact);
			reasoner.load();

			final Atom queryAtomXYZ = Expressions.makeAtom(predicate, x, y, z);
			final String csvFilePathXYZ = CsvFileUtils.CSV_EXPORT_FOLDER + "outputXYZ.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXYZ, csvFilePathXYZ, includeBlanks);
			final List<List<String>> csvContentXYZ = CsvFileUtils.getCSVContent(csvFilePathXYZ);
			assertEquals(factCCD, csvContentXYZ);

			final Atom queryAtomXXZ = Expressions.makeAtom(predicate, x, x, z);
			final String csvFilePathXXZ = CsvFileUtils.CSV_EXPORT_FOLDER + "outputXXZ.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXXZ, csvFilePathXXZ, includeBlanks);
			final List<List<String>> csvContentXXZ = CsvFileUtils.getCSVContent(csvFilePathXXZ);
			assertEquals(factCCD, csvContentXXZ);

			final Atom queryAtomXXX = Expressions.makeAtom("q", x, x, x);
			final String csvFilePathXXX = CsvFileUtils.CSV_EXPORT_FOLDER + "outputXXX.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXXX, csvFilePathXXX, includeBlanks);
			final List<List<String>> csvContentXXX = CsvFileUtils.getCSVContent(csvFilePathXXX);
			assertTrue(csvContentXXX.isEmpty());

			final Atom queryAtomXYX = Expressions.makeAtom("q", x, y, x);
			final String csvFilePathXYX = CsvFileUtils.CSV_EXPORT_FOLDER + "outputXYX.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXYX, csvFilePathXYX, includeBlanks);
			final List<List<String>> csvContentXYX = CsvFileUtils.getCSVContent(csvFilePathXYX);
			assertTrue(csvContentXYX.isEmpty());
		}

	}

	@Test
	public void testExportQueryEmptyKnowledgeBase()
			throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		final Atom queryAtom = Expressions.makeAtom("p", Expressions.makeVariable("?x"),
				Expressions.makeVariable("?y"));
		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.load();
			final String emptyFilePath = CsvFileUtils.CSV_EXPORT_FOLDER + "empty.csv";
			reasoner.exportQueryAnswersToCsv(queryAtom, emptyFilePath, true);
			assertTrue(CsvFileUtils.getCSVContent(emptyFilePath).isEmpty());

			reasoner.exportQueryAnswersToCsv(queryAtom, emptyFilePath, false);
			assertTrue(CsvFileUtils.getCSVContent(emptyFilePath).isEmpty());
		}
	}

}
