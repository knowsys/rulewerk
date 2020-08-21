package org.semanticweb.rulewerk.examples;

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

import org.semanticweb.rulewerk.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.implementation.InMemoryDataSource;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogInMemoryDataSource;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

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

	public static void main(final String[] args) throws ParsingException, IOException {
		ExamplesUtils.configureLogging();

		/* 1. Create a simple random graph */
		System.out.println("Generating random graph ...");
		final int vertexCount = 10000;
		final double density = 0.03;
		// initialise data source for storing edges (estimate how many we'll need)
		final InMemoryDataSource edges = new VLogInMemoryDataSource(2, (int) (vertexCount * vertexCount * density) + 1000);
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
		final InMemoryDataSource vertices = new VLogInMemoryDataSource(1, vertexCount);
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
				+ "triangle(?X, ?Y, ?Z) :- biedge(?X,?Y), biedge(?Y, ?Z), biedge(?Z,?X) ." //
				+ "loop(?X,?X) :- edge(?X,?X) . " //
				+ "properTriangle(?X, ?Y, ?Z) :- triangle(?X,?Y,?Z), ~loop(?X,?Y), ~loop(?Y, ?Z), ~loop(?Z, ?X) . ";

		final KnowledgeBase kb = RuleParser.parse(rules);
		kb.addStatement(new DataSourceDeclarationImpl(Expressions.makePredicate("vertex", 1), vertices));
		kb.addStatement(new DataSourceDeclarationImpl(Expressions.makePredicate("edge", 2), edges));

		/* 3. Use reasoner to compute some query results */
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();

			final long unreachable = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("unreachable(?X)"))
					.getCount();
			final long triangles = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("properTriangle(?X,?Y,?Z)"))
					.getCount();

			System.out
					.println("Number of vertices not reachable from vertex 1 by a bi-directional path: " + unreachable);
			System.out.println("Number of proper bi-directional triangles: " + (triangles / 6) + " (found in " + triangles + " matches due to symmetry.)");
		}
	}

}
