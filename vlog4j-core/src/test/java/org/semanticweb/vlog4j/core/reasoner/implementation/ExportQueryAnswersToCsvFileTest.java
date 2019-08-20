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
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;

public class ExportQueryAnswersToCsvFileTest {

	@Test
	public void testEDBQuerySameConstantSubstitutesSameVariableName()
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {
		final String predicate = "p";
		final Constant constantC = Expressions.makeConstant("c");
		final Constant constantD = Expressions.makeConstant("d");
		final Variable x = Expressions.makeVariable("X");
		final Variable y = Expressions.makeVariable("Y");
		final Variable z = Expressions.makeVariable("Z");
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

			final PositiveLiteral queryAtomXXX = Expressions.makePositiveLiteral("q", x, x, x);
			final String csvFilePathXXX = FileDataSourceTestUtils.OUTPUT_FOLDER + "outputXXX.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXXX, csvFilePathXXX, includeBlanks);
			final List<List<String>> csvContentXXX = FileDataSourceTestUtils.getCSVContent(csvFilePathXXX);
			assertTrue(csvContentXXX.isEmpty());

			final PositiveLiteral queryAtomXYX = Expressions.makePositiveLiteral("q", x, y, x);
			final String csvFilePathXYX = FileDataSourceTestUtils.OUTPUT_FOLDER + "outputXYX.csv";
			reasoner.exportQueryAnswersToCsv(queryAtomXYX, csvFilePathXYX, includeBlanks);
			final List<List<String>> csvContentXYX = FileDataSourceTestUtils.getCSVContent(csvFilePathXYX);
			assertTrue(csvContentXYX.isEmpty());
		}

	}

	@Test
	public void testExportQueryEmptyKnowledgeBase()
			throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		final PositiveLiteral queryAtom = Expressions.makePositiveLiteral("p", Expressions.makeVariable("?x"),
				Expressions.makeVariable("?y"));
		
		final KnowledgeBase kb = new KnowledgeBase();

		try (final VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			final String emptyFilePath = FileDataSourceTestUtils.OUTPUT_FOLDER + "empty.csv";
			reasoner.exportQueryAnswersToCsv(queryAtom, emptyFilePath, true);
			assertTrue(FileDataSourceTestUtils.getCSVContent(emptyFilePath).isEmpty());

			reasoner.exportQueryAnswersToCsv(queryAtom, emptyFilePath, false);
			assertTrue(FileDataSourceTestUtils.getCSVContent(emptyFilePath).isEmpty());
		}
	}

}
