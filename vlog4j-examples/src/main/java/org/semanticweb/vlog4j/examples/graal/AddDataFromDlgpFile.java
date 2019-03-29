package org.semanticweb.vlog4j.examples.graal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.graal.GraalConjunctiveQueryToRule;
import org.semanticweb.vlog4j.graal.GraalToVLog4JModelConverter;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * This example shows how facts can be imported from files in the
 * <a href="http://graphik-team.github.io/graal/doc/dlgp">DLGP/DLP</a> format.
 * 
 * The Graal {@link DlgpParser} is used to parse the program. This step requires
 * a {@link File}, {@link InputStream}, {@link Reader}, or {@link String}
 * containing or pointing to the program.
 * 
 * The {@link Atom Atoms}, {@link Rule Rules}, and {@link ConjunctiveQuery
 * ConjunctiveQueries} are then converted for use by VLog4J. Take care to add
 * the rules resulting from the {@link ConjunctiveQuery ConjunctiveQueries} as
 * well as the {@link Rule Rules} to the {@link Reasoner}; see
 * {@link GraalConjunctiveQueryToRule} for details.
 *
 * @author Adrian Bielefeldt
 *
 */
public class AddDataFromDlgpFile {

	public static void main(final String[] args) throws EdbIdbSeparationException, IOException, ReasonerStateException, IncompatiblePredicateArityException {
		
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
		 * 2. ConjunctiveQueries consist of a conjunction of atoms and a set of answer
		 * variables. To query this with VLog4J, an additional rule needs to be added
		 * for each ConjunctiveQuery. See GraalConjunctiveQueryToRule for details.
		 */
		final List<GraalConjunctiveQueryToRule> convertedConjunctiveQueries = new ArrayList<>();

		int queryCount = 0;
		for (final ConjunctiveQuery conjunctiveQuery : graalConjunctiveQueries) {
			convertedConjunctiveQueries
					.add(GraalToVLog4JModelConverter.convertQuery("query" + queryCount, conjunctiveQuery));
			queryCount++;
		}

		/*
		 * 3. Loading, reasoning, and querying while using try-with-resources to close
		 * the reasoner automatically.
		 */
		try (Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(GraalToVLog4JModelConverter.convertRules(graalRules));
			reasoner.addFacts(GraalToVLog4JModelConverter.convertAtoms(graalAtoms));
			for (final GraalConjunctiveQueryToRule graalConjunctiveQueryToRule : convertedConjunctiveQueries) {
				reasoner.addRules(graalConjunctiveQueryToRule.getRule());
			}

			reasoner.load();
			System.out.println("Before materialisation:");
			for (final GraalConjunctiveQueryToRule graalConjunctiveQueryToRule : convertedConjunctiveQueries) {
				ExamplesUtils.printOutQueryAnswers(graalConjunctiveQueryToRule.getQueryAtom(), reasoner);
			}

			/* The reasoner will use the Restricted Chase by default. */
			reasoner.reason();
			System.out.println("After materialisation:");
			for (final GraalConjunctiveQueryToRule graalConjunctiveQueryToRule : convertedConjunctiveQueries) {
				ExamplesUtils.printOutQueryAnswers(graalConjunctiveQueryToRule.getQueryAtom(), reasoner);
			}
		}
	}

}
