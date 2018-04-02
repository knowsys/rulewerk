package org.semanticweb.vlog4j.examples;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

public class ExamplesUtil {
	static void printOutQueryAnswers(Atom queryAtom, Reasoner reasoner) throws ReasonerStateException {
		System.out.println();
		System.out.println("Answers to query " + queryAtom + " before materialisation:");
		try (QueryResultIterator answersBeforeMaterialisation = reasoner.answerQuery(queryAtom, true)) {
			while (answersBeforeMaterialisation.hasNext()) {
				System.out.println(" - " + answersBeforeMaterialisation.next());
			}
			System.out.println();
		}
	}
}
