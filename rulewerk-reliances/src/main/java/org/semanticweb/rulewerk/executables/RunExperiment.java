package org.semanticweb.rulewerk.executables;

/*-
 * #%L
 * Rulewerk Reliances
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

import java.io.FileInputStream;
import java.io.IOException;

import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

public class RunExperiment {
	static private long materialize(String rulePath, String dataPath, String logPath)
			throws IOException, ParsingException {
		/* Configure rules */
		KnowledgeBase kb = new KnowledgeBase();
		RuleParser.parseInto(kb, new FileInputStream(rulePath));
		RuleParser.parseInto(kb, new FileInputStream(dataPath));

		long startTime;
		long endTime;
		try (Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setLogFile(logPath);
			reasoner.setLogLevel(LogLevel.DEBUG);

			/* Initialise reasoner and compute inferences */
			startTime = System.currentTimeMillis();
			reasoner.reason();
			endTime = System.currentTimeMillis();

		}
		return endTime - startTime;
	}

	static private long average(long[] array) {
		long sum = 0;
		for (long l : array) {
			sum += l;
		}
		return sum / array.length;
	}

	static private void print(long[] array) {
		String content = "[";
		for (int i = 0; i < array.length; i++) {
			content += array[i] + ", ";
		}
		content += "]";
		System.out.println(content);
	}

	// not stratifiable. Is it?
	static private void chasingSets() throws ParsingException, IOException {
		String base = "/home/lgonzale/ontologies/chasing-sets/";
		// normal
		long first[] = new long[10];
		long second[] = new long[10];
		for (int i = 0; i < 1; i++) {
			first[i] = materialize(base + "original/rules.rls", base + "original/data.lp", base + "logs/data-ori.log");
			System.out.println(first[i]);
			second[i] = materialize(base + "transformed/rules.rls", base + "transformed/data.lp",
					base + "logs/data-tra.log");
			System.out.println(second[i]);
		}

		print(first);
		print(second);
		System.out.println(average(first));
		System.out.println(average(second));
	}

	static private void crossword() throws ParsingException, IOException {
		String base = "/home/lgonzale/ontologies/phillip/";
		// normal
		long first[] = new long[10];
		long second[] = new long[10];
		for (int i = 1; i < 11; i++) {
			for (int j = 0; j < 10; j++) {
				first[j] = materialize(base + "original/crossword-rules.rls",
						base + "original/crossword-size-" + i + ".lp",
						base + "logs/crossword-original-" + i + "-" + j + ".log");
//				System.out.println(first[j]);
				second[j] = materialize(base + "transformed/crossword-rules.rls",
						base + "transformed/crossword-size-" + i + ".lp",
						base + "logs/crossword-transformed-" + i + "-" + j + ".log");
//				System.out.println(second[j]);
			}
//			print(first);
//			print(second);

			System.out.println("original crossword, data size: " + i + ". average: " + average(first));
			System.out.println("transfo. crossword, data size: " + i + ". average: " + average(second));
		}
	}

	static private void threeColErdos() throws ParsingException, IOException {
		String base = "/home/lgonzale/ontologies/phillip/";

		String[] graphs = { "v32_e153", "v32_e307", "v32_e460", "v32_e614", "v64_e1228", "v64_e1843", "v64_e2457",
				"v64_e614", "v128_e2457", "v128_e4915", "v128_e7372", "v128_e9830", "v256_e19660", "v256_e29491",
				"v256_e39321", "v256_e9830", "v512_e117964", "v512_e157286", "v512_e39321", "v512_e78643",
				"v1024_e157286", "v1024_e314572", "v1024_e471859", "v1024_e629145" };

		long first[] = new long[10];
		long second[] = new long[10];

		for (String g : graphs) {

			for (int j = 0; j < 10; j++) {
				first[j] = materialize(base + "original/3-col.rls", base + "original/erdos-renyi-graph-" + g + ".lp",
						base + "logs/3col-erdos-renyi-graph-" + g + "-original.log");
//				System.out.println(first[j]);
				second[j] = materialize(base + "transformed/3-col.rls",
						base + "transformed/3col-erdos-renyi-graph-" + g + ".lp",
						base + "logs/3col-erdos-renyi-graph-" + g + "-transformed.log");
//				System.out.println(second[j]);
			}
//			print(first);
//			print(second);
			System.out.println("3col-erdos-renyi-graph-" + g + ": " + average(first) + " " + average(second));

		}
	}

	// not stratifiable
	static private void hamiltonianErdos() throws ParsingException, IOException {
		String base = "/home/lgonzale/ontologies/phillip/";

		String[] graphs = { "v32_e153", "v32_e307", "v32_e460", "v32_e614", "v64_e1228", "v64_e1843", "v64_e2457",
				"v64_e614", "v128_e2457", "v128_e4915", "v128_e7372", "v128_e9830", "v256_e19660", "v256_e29491",
				"v256_e39321", "v256_e9830", "v512_e117964", "v512_e157286", "v512_e39321", "v512_e78643",
				"v1024_e157286", "v1024_e314572", "v1024_e471859", "v1024_e629145" };

		long first[] = new long[10];
		long second[] = new long[10];

		for (String g : graphs) {

			for (int j = 0; j < 10; j++) {
				first[j] = materialize(base + "original/hamiltonian.rls",
						base + "original/erdos-renyi-graph-" + g + ".lp",
						base + "logs/hamiltonian-erdos-renyi-graph-" + g + "-original.log");
//				System.out.println(first[j]);
				second[j] = materialize(base + "transformed/hamiltonian.rls",
						base + "transformed/hamiltonian-erdos-renyi-graph-" + g + ".lp",
						base + "logs/hamiltonian-erdos-renyi-graph-" + g + "-transformed.log");
//				System.out.println(second[j]);
			}
//			print(first);
//			print(second);
			System.out.println("hamiltonian-erdos-renyi-graph-" + g + ": " + average(first) + " " + average(second));

		}
	}

	static private void threeColPowerLaw() throws ParsingException, IOException {
		String base = "/home/lgonzale/ontologies/phillip/";

		String[] graphs = { "v32_e102_k3", "v32_e102_k7", "v32_e204_k3", "v32_e204_k7", "v32_e307_k3", "v32_e307_k7",
				"v64_e1228_k3", "v64_e1228_k7", "v64_e409_k3", "v64_e409_k7", "v64_e819_k3", "v64_e819_k7",
				"v128_e1638_k3", "v128_e1638_k7", "v128_e3276_k3", "v128_e3276_k7", "v128_e4915_k3", "v128_e4915_k7",
				"v256_e13107_k3", "v256_e13107_k7", "v256_e19660_k3", "v256_e19660_k7", "v256_e6553_k3",
				"v256_e6553_k7", "v512_e26214_k3", "v512_e26214_k7", "v512_e52428_k3", "v512_e52428_k7",
				"v512_e78643_k3", "v512_e78643_k7", "v1024_e104857_k3", "v1024_e104857_k7", "v1024_e209715_k3",
				"v1024_e209715_k7", "v1024_e314572_k3", "v1024_e314572_k7" };

		long first[] = new long[10];
		long second[] = new long[10];
		for (String g : graphs) {
			for (int j = 0; j < 10; j++) {
				first[j] = materialize(base + "original/3-col.rls", base + "original/power-law-graph-" + g + ".lp",
						base + "logs/3col-power-law-graph-" + g + "-original.log");
//				System.out.println(first[j]);
				second[j] = materialize(base + "transformed/3-col.rls",
						base + "transformed/3col-power-law-graph-" + g + ".lp",
						base + "logs/3col-power-law-graph-" + g + "-transformed.log");
//				System.out.println(second[j]);
			}
//			print(first);
//			print(second);
			System.out.println("3col-power-law-graph-" + g + ": " + average(first) + " " + average(second));

		}
	}

	static private void hamiltonianPowerLaw() throws ParsingException, IOException {
		String base = "/home/lgonzale/ontologies/phillip/";

		String[] graphs = { "v32_e102_k3", "v32_e102_k7", "v32_e204_k3", "v32_e204_k7", "v32_e307_k3", "v32_e307_k7",
				"v64_e1228_k3", "v64_e1228_k7", "v64_e409_k3", "v64_e409_k7", "v64_e819_k3", "v64_e819_k7",
				"v128_e1638_k3", "v128_e1638_k7", "v128_e3276_k3", "v128_e3276_k7", "v128_e4915_k3", "v128_e4915_k7",
				"v256_e13107_k3", "v256_e13107_k7", "v256_e19660_k3", "v256_e19660_k7", "v256_e6553_k3",
				"v256_e6553_k7", "v512_e26214_k3", "v512_e26214_k7", "v512_e52428_k3", "v512_e52428_k7",
				"v512_e78643_k3", "v512_e78643_k7", "v1024_e104857_k3", "v1024_e104857_k7", "v1024_e209715_k3",
				"v1024_e209715_k7", "v1024_e314572_k3", "v1024_e314572_k7" };

		long first[] = new long[10];
		long second[] = new long[10];

		for (String g : graphs) {

			for (int j = 0; j < 10; j++) {
				first[j] = materialize(base + "original/hamiltonian.rls",
						base + "original/power-law-graph-" + g + ".lp",
						base + "logs/hamiltonian-power-law-graph-" + g + "-original.log");
//				System.out.println(first[j]);
				second[j] = materialize(base + "transformed/hamiltonian.rls",
						base + "transformed/hamiltonian-power-law-graph-" + g + ".lp",
						base + "logs/hamiltonian-power-law-graph-" + g + "-transformed.log");
//				System.out.println(second[j]);
			}
//			print(first);
//			print(second);
			System.out.println("hamiltonian-power-law-graph-" + g + ": " + average(first) + " " + average(second));

		}
	}

	static private void chain() throws ParsingException, IOException {
		String base = "/home/lgonzale/ontologies/chain/";

		String[] graphs = { "r-10-e-10.lp", "r-10-e-100.lp", "r-100-e-10.lp", "r-100-e-100.lp", "r-1000-e-10.lp",
				"r-1000-e-100.lp", "r-10000-e-10.lp", "r-10000-e-100.lp", "r-100000-e-10.lp", "r-100000-e-100.lp",
				"r-1000000-e-10.lp", "r-1000000-e-100.lp" };

		long first[] = new long[2];
		long second[] = new long[2];

		for (String g : graphs) {

			for (int j = 0; j < 2; j++) {
				first[j] = materialize(base + "original/rules.rls", base + "original/" + g,
						base + "logs/" + g + "-original.log");
//				System.out.println(first[j]);
				second[j] = materialize(base + "transformed/rules.rls", base + "transformed/" + g,
						base + "logs/" + g + "-transformed.log");
//				System.out.println(second[j]);
			}
//			print(first);
//			print(second);
			System.out.println(g + ": " + average(first) + " " + average(second));

		}
	}

	public static void main(final String[] args) throws IOException, ParsingException {
//		chasingSets();         // error in c?
//		crossword();           // slower with our transformation. more rule executions also
//		threeColErdos();       // better with big graphs
//		threeColPowerLaw();    // better only in special cases
//		hamiltonianErdos();    // slower
//		hamiltonianPowerLaw(); // slower
		chain();
	}
}
