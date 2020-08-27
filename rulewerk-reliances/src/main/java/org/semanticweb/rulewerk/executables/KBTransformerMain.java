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

import java.io.IOException;

import org.semanticweb.rulewerk.parser.ParsingException;

public class KBTransformerMain {

	static private void chasingSets() throws ParsingException, IOException {
		String originalPath = "/home/lgonzale/ontologies/chasing-sets/original/";
		String transformedPath = "/home/lgonzale/ontologies/chasing-sets/transformed/";
		KBTransformer kbt = new KBTransformer();
		kbt.transform(originalPath + "rules.rls", originalPath + "data.lp", transformedPath + "rules.rls",
				transformedPath + "data.lp");
	}

	static private void crossword() throws ParsingException, IOException {
		String originalPath = "/home/lgonzale/ontologies/phillip/original/";
		String transformedPath = "/home/lgonzale/ontologies/phillip/transformed/";
		KBTransformer kbt = new KBTransformer();
		for (int i = 1; i < 11; i++) {
			kbt.transform(originalPath + "crossword-rules.rls", originalPath + "crossword-size-" + i + ".lp",
					transformedPath + "crossword-rules.rls", transformedPath + "crossword-size-" + i + ".lp");
		}
	}

	static private void threeColErdos() throws ParsingException, IOException {
		String originalPath = "/home/lgonzale/ontologies/phillip/original/";
		String transformedPath = "/home/lgonzale/ontologies/phillip/transformed/";

		String[] graphs = { "v32_e153", "v32_e307", "v32_e460", "v32_e614", "v64_e1228", "v64_e1843", "v64_e2457",
				"v64_e614", "v128_e2457", "v128_e4915", "v128_e7372", "v128_e9830", "v256_e19660", "v256_e29491",
				"v256_e39321", "v256_e9830", "v512_e117964", "v512_e157286", "v512_e39321", "v512_e78643",
				"v1024_e157286", "v1024_e314572", "v1024_e471859", "v1024_e629145" };
		KBTransformer kbt = new KBTransformer();
		for (String g : graphs) {
			kbt.transform(originalPath + "3-col.rls", originalPath + "erdos-renyi-graph-" + g + ".lp",
					transformedPath + "3-col.rls", transformedPath + "3col-erdos-renyi-graph-" + g + ".lp");
		}
	}

	static private void threeColPowerLaw() throws ParsingException, IOException {
		String originalPath = "/home/lgonzale/ontologies/phillip/original/";
		String transformedPath = "/home/lgonzale/ontologies/phillip/transformed/";

		String[] graphs = { "v32_e102_k3", "v32_e102_k7", "v32_e204_k3", "v32_e204_k7", "v32_e307_k3", "v32_e307_k7",
				"v64_e1228_k3", "v64_e1228_k7", "v64_e409_k3", "v64_e409_k7", "v64_e819_k3", "v64_e819_k7",
				"v128_e1638_k3", "v128_e1638_k7", "v128_e3276_k3", "v128_e3276_k7", "v128_e4915_k3", "v128_e4915_k7",
				"v256_e13107_k3", "v256_e13107_k7", "v256_e19660_k3", "v256_e19660_k7", "v256_e6553_k3",
				"v256_e6553_k7", "v512_e26214_k3", "v512_e26214_k7", "v512_e52428_k3", "v512_e52428_k7",
				"v512_e78643_k3", "v512_e78643_k7", "v1024_e104857_k3", "v1024_e104857_k7", "v1024_e209715_k3",
				"v1024_e209715_k7", "v1024_e314572_k3", "v1024_e314572_k7" };
		KBTransformer kbt = new KBTransformer();
		for (String g : graphs) {
			kbt.transform(originalPath + "3-col.rls", originalPath + "power-law-graph-" + g + ".lp",
					transformedPath + "3-col.rls", transformedPath + "3col-power-law-graph-" + g + ".lp");
		}
	}

	// TODO
	static private void hamiltonianErdos() throws ParsingException, IOException {
		String originalPath = "/home/lgonzale/ontologies/phillip/original/";
		String transformedPath = "/home/lgonzale/ontologies/phillip/transformed/";

		String[] graphs = { "v32_e153", "v32_e307", "v32_e460", "v32_e614", "v64_e1228", "v64_e1843", "v64_e2457",
				"v64_e614", "v128_e2457", "v128_e4915", "v128_e7372", "v128_e9830", "v256_e19660", "v256_e29491",
				"v256_e39321", "v256_e9830", "v512_e117964", "v512_e157286", "v512_e39321", "v512_e78643",
				"v1024_e157286", "v1024_e314572", "v1024_e471859", "v1024_e629145" };
		KBTransformer kbt = new KBTransformer();
		for (String g : graphs) {
			kbt.transform(originalPath + "hamiltonian.rls", originalPath + "erdos-renyi-graph-" + g + ".lp",
					transformedPath + "hamiltonian.rls",
					transformedPath + "hamiltonian-erdos-renyi-graph-" + g + ".lp");
		}
	}

	static private void hamiltonianPowerLaw() throws ParsingException, IOException {
		String originalPath = "/home/lgonzale/ontologies/phillip/original/";
		String transformedPath = "/home/lgonzale/ontologies/phillip/transformed/";

		String[] graphs = { "v32_e102_k3", "v32_e102_k7", "v32_e204_k3", "v32_e204_k7", "v32_e307_k3", "v32_e307_k7",
				"v64_e1228_k3", "v64_e1228_k7", "v64_e409_k3", "v64_e409_k7", "v64_e819_k3", "v64_e819_k7",
				"v128_e1638_k3", "v128_e1638_k7", "v128_e3276_k3", "v128_e3276_k7", "v128_e4915_k3", "v128_e4915_k7",
				"v256_e13107_k3", "v256_e13107_k7", "v256_e19660_k3", "v256_e19660_k7", "v256_e6553_k3",
				"v256_e6553_k7", "v512_e26214_k3", "v512_e26214_k7", "v512_e52428_k3", "v512_e52428_k7",
				"v512_e78643_k3", "v512_e78643_k7", "v1024_e104857_k3", "v1024_e104857_k7", "v1024_e209715_k3",
				"v1024_e209715_k7", "v1024_e314572_k3", "v1024_e314572_k7" };
		KBTransformer kbt = new KBTransformer();
		for (String g : graphs) {
			kbt.transform(originalPath + "hamiltonian.rls", originalPath + "power-law-graph-" + g + ".lp",
					transformedPath + "hamiltonian.rls", transformedPath + "hamiltonian-power-law-graph-" + g + ".lp");
		}
	}

	static private void chain() throws ParsingException, IOException {
		String originalPath = "/home/lgonzale/ontologies/chain/original/";
		String transformedPath = "/home/lgonzale/ontologies/chain/transformed/";

		String[] graphs = { "r-10-e-10.lp", "r-10-e-100.lp", "r-100-e-10.lp", "r-100-e-100.lp", "r-1000-e-10.lp",
				"r-1000-e-100.lp", "r-10000-e-10.lp", "r-10000-e-100.lp", "r-100000-e-10.lp", "r-100000-e-100.lp",
				"r-1000000-e-10.lp", "r-1000000-e-100.lp" };

		KBTransformer kbt = new KBTransformer();
		for (String g : graphs) {
			kbt.transform(originalPath + "rules.rls", originalPath + g, transformedPath + "rules.rls",
					transformedPath + g);
		}
	}

	static public void main(String args[]) throws ParsingException, IOException {
//		chasingSets();
//		crossword();
//		threeColErdos();
//		threeColPowerLaw();
//		hamiltonianErdos();
//		hamiltonianPowerLaw();
		chain();
	}
}
