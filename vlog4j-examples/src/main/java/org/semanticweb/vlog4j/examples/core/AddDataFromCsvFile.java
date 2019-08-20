package org.semanticweb.vlog4j.examples.core;

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

import java.io.IOException;

import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

/**
 * This example shows how facts can be imported from files in the CSV format.
 * Specifically, it imports from a {@code .csv.gz} file, but you can also import
 * from {@code .csv} files. Moreover, it shows how query answers that result
 * from reasoning over these facts can be exported to {@code .csv} files.
 * <p>
 * For importing, a {@link CsvFileDataSource} that contains a path to the
 * corresponding {@code .csv.gz} file must be created. A {@code .csv} file
 * contains facts in the CSV format over exactly one predicate. A
 * {@code .csv.gz} file is the gzipped version of such a {@code .csv} file.
 * <p>
 * For exporting, a path to the output {@code .csv} file must be specified.
 *
 * @author Christian Lewe
 * @author Irina Dragoste
 * @author Markus Kroetzsch
 *
 */
public class AddDataFromCsvFile {

	public static void main(final String[] args) throws EdbIdbSeparationException, IOException, ReasonerStateException,
			IncompatiblePredicateArityException, ParsingException {

		ExamplesUtils.configureLogging();

		final String rules = "" // first declare file inputs:
				+ "@source bicycleEDB(1) : load-csv(\"" + ExamplesUtils.INPUT_FOLDER + "bicycleEDB.csv.gz\") ."
				+ "@source hasPartEDB(2) : load-csv(\"" + ExamplesUtils.INPUT_FOLDER + "hasPartEDB.csv.gz\") ."
				+ "@source wheelEDB(1) : load-csv(\"" + ExamplesUtils.INPUT_FOLDER + "wheelEDB.csv.gz\") ."
				// rules to load all data from the file-based ("EDB") predicates:
				+ "bicycleIDB(?X) :- bicycleEDB(?X) ." //
				+ "wheelIDB(?X) :- wheelEDB(?X) ." //
				+ "hasPartIDB(?X, ?Y) :- hasPartEDB(?X, ?Y) ." //
				+ "isPartOfIDB(?X, ?Y) :- isPartOfEDB(?X, ?Y) ."
				// every bicycle has some part that is a wheel:
				+ "hasPartIDB(?X, !Y), wheelIDB(!Y) :- bicycleIDB(?X) ."
				// every wheel is part of some bicycle:
				+ "isPartOfIDB(?X, !Y), bicycleIDB(!Y) :- wheelIDB(?X) ."
				// hasPart and isPartOf are mutually inverse relations:
				+ "hasPartIDB(?X, ?Y) :- isPartOfIDB(?Y, ?X) ." //
				+ "isPartOfIDB(?X, ?Y) :- hasPartIDB(?Y, ?X) .";

		RuleParser ruleParser = new RuleParser();
		final KnowledgeBase kb = ruleParser.parse(rules);

		/*
		 * Loading, reasoning, and querying while using try-with-resources to close the
		 * reasoner automatically.
		 */
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();

			System.out.println("Before materialisation:");
			ExamplesUtils.printOutQueryAnswers("hasPartEDB(?X, ?Y)", reasoner);

			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();
			System.out.println("After materialisation:");
			final PositiveLiteral hasPartIdbXY = ruleParser.parsePositiveLiteral("hasPartIDB(?X, ?Y)");
			ExamplesUtils.printOutQueryAnswers(hasPartIdbXY, reasoner);

			/* Exporting query answers to {@code .csv} files. */
			reasoner.exportQueryAnswersToCsv(hasPartIdbXY, ExamplesUtils.OUTPUT_FOLDER + "hasPartIDBXYWithBlanks.csv",
					true);
			reasoner.exportQueryAnswersToCsv(hasPartIdbXY,
					ExamplesUtils.OUTPUT_FOLDER + "hasPartIDBXYWithoutBlanks.csv", false);

			final PositiveLiteral hasPartIDBRedBikeY = ruleParser.parsePositiveLiteral("hasPartIDB(redBike, ?Y)");
			reasoner.exportQueryAnswersToCsv(hasPartIDBRedBikeY,
					ExamplesUtils.OUTPUT_FOLDER + "hasPartIDBRedBikeYWithBlanks.csv", true);
		}
	}

}
