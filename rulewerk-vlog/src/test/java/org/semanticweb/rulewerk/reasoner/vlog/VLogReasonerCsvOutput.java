package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;

public class VLogReasonerCsvOutput {

	private final static String nonExistingFilePath = FileDataSourceTestUtils.OUTPUT_FOLDER + "empty.csv";

	@Test
	public void testEDBQuerySameConstantSubstitutesSameVariableName() throws IOException {
		final String predicate = "p";
		final Constant constantC = Expressions.makeAbstractConstant("c");
		final Constant constantD = Expressions.makeAbstractConstant("d");
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Variable z = Expressions.makeUniversalVariable("Z");
		final Fact fact = Expressions.makeFact(predicate, Arrays.asList(constantC, constantC, constantD));

		final boolean includeBlanks = false;
		// final String csvFilePath = CSV_EXPORT_FOLDER + "output";
		final List<List<String>> factCCD = Arrays.asList(Arrays.asList("c", "c", "d"));

		final KnowledgeBase kb = new KnowledgeBase();

		kb.addStatement(fact);

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			final PositiveLiteral queryAtomXYZ = Expressions.makePositiveLiteral(predicate, x, y, z);
			final String csvFilePathXYZ = FileDataSourceTestUtils.OUTPUT_FOLDER + "outputXYZ.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXYZ, csvFilePathXYZ, includeBlanks);
			final List<List<String>> csvContentXYZ = FileDataSourceTestUtils.getCSVContent(csvFilePathXYZ);
			assertEquals(factCCD, csvContentXYZ);

			final PositiveLiteral queryAtomXXZ = Expressions.makePositiveLiteral(predicate, x, x, z);
			final String csvFilePathXXZ = FileDataSourceTestUtils.OUTPUT_FOLDER + "outputXXZ.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXXZ, csvFilePathXXZ, includeBlanks);
			final List<List<String>> csvContentXXZ = FileDataSourceTestUtils.getCSVContent(csvFilePathXXZ);
			assertEquals(factCCD, csvContentXXZ);

			final PositiveLiteral queryAtomXXX = Expressions.makePositiveLiteral(predicate, x, x, x);
			final String csvFilePathXXX = FileDataSourceTestUtils.OUTPUT_FOLDER + "outputXXX.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXXX, csvFilePathXXX, includeBlanks);
			final List<List<String>> csvContentXXX = FileDataSourceTestUtils.getCSVContent(csvFilePathXXX);
			assertTrue(csvContentXXX.isEmpty());

			final PositiveLiteral queryAtomXYX = Expressions.makePositiveLiteral(predicate, x, y, x);
			final String csvFilePathXYX = FileDataSourceTestUtils.OUTPUT_FOLDER + "outputXYX.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXYX, csvFilePathXYX, includeBlanks);
			final List<List<String>> csvContentXYX = FileDataSourceTestUtils.getCSVContent(csvFilePathXYX);
			assertTrue(csvContentXYX.isEmpty());
		}

	}

	@Test
	public void testExportQueryEmptyKnowledgeBaseBeforeReasoningIncludeBlanks() throws IOException {
		final PositiveLiteral queryAtom = Expressions.makePositiveLiteral("p", Expressions.makeUniversalVariable("?x"),
				Expressions.makeUniversalVariable("?y"));

		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.exportQueryAnswersToCsv(queryAtom, nonExistingFilePath, true);
		}
		assertFalse(Files.exists(Paths.get(nonExistingFilePath)));
	}

	@Test
	public void testExportQueryEmptyKnowledgeBaseBeforeReasoningExcludeBlanks() throws IOException {
		final PositiveLiteral queryAtom = Expressions.makePositiveLiteral("p", Expressions.makeUniversalVariable("?x"),
				Expressions.makeUniversalVariable("?y"));

		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			reasoner.exportQueryAnswersToCsv(queryAtom, nonExistingFilePath, false);
		}
		assertFalse(Files.exists(Paths.get(nonExistingFilePath)));
	}

	@Test
	public void testExportQueryEmptyKnowledgeBaseAfterReasoningIncludeBlanks() throws IOException {
		final PositiveLiteral queryAtom = Expressions.makePositiveLiteral("p", Expressions.makeUniversalVariable("?x"),
				Expressions.makeUniversalVariable("?y"));

		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();

			reasoner.exportQueryAnswersToCsv(queryAtom, nonExistingFilePath, true);
		}
		assertFalse(Files.exists(Paths.get(nonExistingFilePath)));
	}

	public void testExportQueryEmptyKnowledgeBaseAfterReasoningExcludeBlanks() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();

		final PositiveLiteral queryAtom = Expressions.makePositiveLiteral("p", Expressions.makeUniversalVariable("?x"),
				Expressions.makeUniversalVariable("?y"));

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();

			reasoner.exportQueryAnswersToCsv(queryAtom, nonExistingFilePath, false);
		}
		assertFalse(Files.exists(Paths.get(nonExistingFilePath)));
	}

}
