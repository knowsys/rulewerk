package org.semanticweb.rulewerk.examples.core;

/*-
 * #%L
 * Rulewerk Examples
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

import java.io.IOException;

import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

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

	public static void main(final String[] args) throws IOException, ParsingException {

		ExamplesUtils.configureLogging();

		final String initialFactsHasPart = ""// a file input:
				+ "@source hasPart[2] : load-csv(\"" + ExamplesUtils.INPUT_FOLDER + "hasPartEDB.csv.gz\") .";

		final String rules = "" // first declare file inputs:
				+ "@source bicycle[1] : load-csv(\"" + ExamplesUtils.INPUT_FOLDER + "bicycleEDB.csv.gz\") ."
				+ "@source wheel[1] : load-csv(\"" + ExamplesUtils.INPUT_FOLDER + "wheelEDB.csv.gz\") ."
				// every bicycle has some part that is a wheel:
				+ "hasPart(?X, !Y), wheel(!Y) :- bicycle(?X) ."
				// every wheel is part of some bicycle:
				+ "isPartOf(?X, !Y), bicycle(!Y) :- wheel(?X) ."
				// hasPart and isPartOf are mutually inverse relations:
				+ "hasPart(?X, ?Y) :- isPartOf(?Y, ?X) ." //
				+ "isPartOf(?X, ?Y) :- hasPart(?Y, ?X) .";

		/*
		 * Loading, reasoning, and querying while using try-with-resources to close the
		 * reasoner automatically.
		 */
		final KnowledgeBase kb = new KnowledgeBase();
		try (final Reasoner reasoner = new VLogReasoner(kb)) {

			/*
			 * 1. Loading the initial facts with hasPart predicate into reasoner.
			 */
			RuleParser.parseInto(kb, initialFactsHasPart);
			reasoner.reason();

			/*
			 * Query initial facts with hasPart predicate.
			 */
			System.out.println("Before materialisation:");
			ExamplesUtils.printOutQueryAnswers("hasPart(?X, ?Y)", reasoner);

			/*
			 * 2. Loading further facts and rules into the reasoner, and materialising the
			 * loaded facts with the rules.
			 */
			RuleParser.parseInto(kb, rules);
			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();

			/*
			 * Querying facts with hasPart predicate after materialisation.
			 */
			System.out.println("After materialisation:");
			final PositiveLiteral hasPartXY = RuleParser.parsePositiveLiteral("hasPart(?X, ?Y)");
			ExamplesUtils.printOutQueryAnswers(hasPartXY, reasoner);

			/* Exporting query answers to {@code .csv} files. */
			reasoner.exportQueryAnswersToCsv(hasPartXY, ExamplesUtils.OUTPUT_FOLDER + "hasPartWithBlanks.csv", true);
			reasoner.exportQueryAnswersToCsv(hasPartXY, ExamplesUtils.OUTPUT_FOLDER + "hasPartWithoutBlanks.csv",
					false);
			final PositiveLiteral hasPartRedBikeY = RuleParser.parsePositiveLiteral("hasPart(redBike, ?Y)");
			reasoner.exportQueryAnswersToCsv(hasPartRedBikeY,
					ExamplesUtils.OUTPUT_FOLDER + "hasPartRedBikeWithBlanks.csv", true);
		}
	}

}
