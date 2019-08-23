package org.semanticweb.vlog4j.examples;

import java.io.IOException;

import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.InMemoryDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

/**
 * This example shows how to reason efficiently with data sets generated in
 * Java. We generate a random graph with several million edges, check
 * connectivity, and count triangles.
 * 
 * Parameters can be modified to obtain graphs of different sizes and density.
 * It should be noted, however, that the number of triangles in reasonably dense
 * graphs tends to be huge, and it is easy to exhaust memory in this way.
 * 
 * @author Markus Kroetzsch
 *
 */
public class InMemoryGraphAnalysisExample {

	public static void main(String[] args) throws ParsingException, IOException {
		ExamplesUtils.configureLogging();

		/* 1. Create a simple random graph */
		System.out.println("Generating random graph ...");
		int vertexCount = 10000;
		double density = 0.03;
		// initialise data source for storing edges (estimate how many we'll need)
		InMemoryDataSource edges = new InMemoryDataSource(2, (int) (vertexCount * vertexCount * density) + 1000);
		int edgeCount = 0;
		for (int i = 1; i <= vertexCount; i++) {
			for (int j = 1; j <= vertexCount; j++) {
				if (Math.random() < density) {
					edges.addTuple("v" + i, "v" + j);
					edgeCount++;
				}
			}
		}
		// also make a unary data source to mark vertices:
		InMemoryDataSource vertices = new InMemoryDataSource(1, vertexCount);
		for (int i = 1; i <= vertexCount; i++) {
			vertices.addTuple("v" + i);
		}
		System.out.println("Generated " + edgeCount + " edges in random graph of " + vertexCount + " vertices.");

		/* 2. Initialise database with random data and some rules */

		final String rules = "" //
				+ "biedge(?X,?Y) :- edge(?X,?Y), edge(?Y,?X) ." //
				+ "connected(v1) ." //
				+ "connected(?X) :- connected(?Y), biedge(?Y,?X) ." //
				+ "unreachable(?X) :- vertex(?X), ~connected(?X) . " //
				+ "triangle(?X, ?Y, ?Z) :- biedge(?X,?Y), biedge(?Y, ?Z), biedge(?Z,?X) .";

		final KnowledgeBase kb = RuleParser.parse(rules);
		kb.addStatement(new DataSourceDeclarationImpl(Expressions.makePredicate("vertex", 1), vertices));
		kb.addStatement(new DataSourceDeclarationImpl(Expressions.makePredicate("edge", 2), edges));

		/* 3. Use reasoner to compute some query results */
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();

			System.out.println("Number of vertices not reachable from vertex 1 by a bi-directional path: "
					+ ExamplesUtils.getQueryAnswerCount("unreachable(?X)", reasoner));
			System.out.println("Number of bi-directional triangles: "
					+ ExamplesUtils.getQueryAnswerCount("triangle(?X,?Y,?Z)", reasoner) / 6);
		}
	}

}
