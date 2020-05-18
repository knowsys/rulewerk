package org.semanticweb.rulewerk.examples.graal;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.graal.GraalConjunctiveQueryToRule;
import org.semanticweb.rulewerk.graal.GraalToRulewerkModelConverter;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * This example shows how facts can be imported from files in the
 * <a href="http://graphik-team.github.io/graal/doc/dlgp">DLGP/DLP</a> format.
 *
 * The <a href="http://graphik-team.github.io/graal/">Graal</a>
 * {@link DlgpParser} is used to parse the program. This step requires a
 * {@link File}, {@link InputStream}, {@link Reader}, or {@link String}
 * containing or pointing to the program.
 *
 * The {@link Atom Atoms}, {@link Rule Rules}, and {@link ConjunctiveQuery
 * ConjunctiveQueries} are then converted for use by Rulewerk. Take care to add
 * the rules resulting from the {@link ConjunctiveQuery ConjunctiveQueries} as
 * well as the {@link Rule Rules} to the {@link Reasoner}; see
 * {@link GraalConjunctiveQueryToRule} for details.
 *
 * @author Adrian Bielefeldt
 *
 */
public class AddDataFromDlgpFile {

	public static void main(final String[] args) throws IOException {

		final List<fr.lirmm.graphik.graal.api.core.Atom> graalAtoms = new ArrayList<>();
		final List<fr.lirmm.graphik.graal.api.core.Rule> graalRules = new ArrayList<>();
		final List<ConjunctiveQuery> graalConjunctiveQueries = new ArrayList<>();

		/*
		 * 1. Parse the DLGP/DLP file using the DlgpParser.
		 *
		 * DlgpParser supports Files, InputStreams, Readers, and Strings. While other
		 * objects such as prefixes can also be part of the iterator, they are
		 * automatically resolved and do not need to be handled here.
		 */
		try (final DlgpParser parser = new DlgpParser(new File("src/main/data/input/graal/", "example.dlgp"))) {
			while (parser.hasNext()) {
				final Object object = parser.next();
				if (object instanceof Atom) {
					graalAtoms.add((Atom) object);
				} else if (object instanceof Rule) {
					graalRules.add((Rule) object);
				} else if (object instanceof ConjunctiveQuery) {
					graalConjunctiveQueries.add((ConjunctiveQuery) object);
				}
			}
		}

		/*
		 * 2. ConjunctiveQueries consist of a conjunction of literals and a set of
		 * answer variables. To query this with Rulewerk, an additional rule needs to be
		 * added for each ConjunctiveQuery. See GraalConjunctiveQueryToRule for details.
		 */
		final List<GraalConjunctiveQueryToRule> convertedConjunctiveQueries = new ArrayList<>();

		for (final ConjunctiveQuery conjunctiveQuery : graalConjunctiveQueries) {
			final String queryUniqueId = "query" + convertedConjunctiveQueries.size();
			convertedConjunctiveQueries.add(GraalToRulewerkModelConverter.convertQuery(queryUniqueId, conjunctiveQuery));
		}

		/*
		 * 3. Loading, reasoning, and querying while using try-with-resources to close
		 * the reasoner automatically.
		 */

		try (Reasoner reasoner = new VLogReasoner(new KnowledgeBase())) {
			final KnowledgeBase kb = reasoner.getKnowledgeBase();

			/*
			 * Add facts to the reasoner knowledge base
			 */
			kb.addStatements(GraalToRulewerkModelConverter.convertAtomsToFacts(graalAtoms));
			/*
			 * Load the knowledge base into the reasoner
			 */
			reasoner.reason();
			System.out.println("Before materialisation:");
			for (final GraalConjunctiveQueryToRule graalConjunctiveQueryToRule : convertedConjunctiveQueries) {
				ExamplesUtils.printOutQueryAnswers(graalConjunctiveQueryToRule.getQuery(), reasoner);
			}

			/*
			 * Add rules to the reasoner knowledge base
			 */
			kb.addStatements(GraalToRulewerkModelConverter.convertRules(graalRules));
			for (final GraalConjunctiveQueryToRule graalConjunctiveQueryToRule : convertedConjunctiveQueries) {
				kb.addStatement(graalConjunctiveQueryToRule.getRule());
			}
			/*
			 * Materialise facts using rules
			 */
			reasoner.reason();
			System.out.println("After materialisation:");
			for (final GraalConjunctiveQueryToRule graalConjunctiveQueryToRule : convertedConjunctiveQueries) {
				ExamplesUtils.printOutQueryAnswers(graalConjunctiveQueryToRule.getQuery(), reasoner);
			}
		}
	}

}
