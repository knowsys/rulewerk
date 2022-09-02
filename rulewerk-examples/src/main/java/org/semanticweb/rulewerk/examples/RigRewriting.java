package org.semanticweb.rulewerk.examples;

/*-
 * #%L
 * Rulewerk Examples
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class RigRewriting {
	final static String baseDir = "C:\\Users\\Ali Elhalawati\\files\\el";
	static HashSet<String> bodies = new HashSet<String>();
	static HashSet<String> heads = new HashSet<String>();

	public static void main(final String[] args) throws IOException {
		File f = new File(baseDir + "\\elk-calculus2.rls");
		FileWriter fw = new FileWriter(new File(baseDir + "/s_rules3.txt"));
		Scanner y = new Scanner(f);
		int ruleCount = 0;
		while (y.hasNextLine()) {
			String x = y.nextLine();
			String[] split = x.split(":-");
			System.out.println(split[1]);
			String[] split2 = split[1].split("\\),");
			String[] split1 = split[0].split("\\),");
			LinkedHashSet<String> vars = new LinkedHashSet<String>();
			LinkedHashSet<String> rew = new LinkedHashSet<String>();
			LinkedHashSet<String> bprov = new LinkedHashSet<String>();
			int i = 0;
			String rb = "";
			String rh = "";
			String r1 = "";
			String r2 = "";
			String v = "";
			String r3 = "";
			String r3h = "";
			String r3b = "";
			LinkedHashSet<String> zew = new LinkedHashSet<String>();
			int and = 0;
			int h = 0;
			String current = "";
			for (String s : split2) {
				// System.out.println(s);
				if (!s.contains(")")) {
					bodies.add(s + ")");
					s = s + ")";
				} else {
					bodies.add(s.replace(").", ")"));
				}
				s = s.replace("(", "_p(");
				s = s.replace(")", ",?prov" + "" + i + ")");
				v = v + "?prov" + "" + i + ",";
				bprov.add("?prov" + i);
				i++;
				if (!s.contains(").")) {
					rb = rb + s.replaceAll(" ", "") + ", ";
				} else {
					rb = rb + s.replaceAll(" ", "");
				}
			}
			if (bprov.size() == 1) {
				r3h = "rig_edge1(?prov0,!andN), rig_edge2(?prov0,!andN), ";
				current = "!andN";
				rew.add("?prov0");
				rew.add(current);
			} else {
				String vs = bprov.toString().replace("[", "").replace("]", "");
				String[] vsp = vs.split(", ");
				current = vsp[0];
				for (int j = 1; j < vsp.length; j++) {
					r3h = r3h + "rig_edge1(" + current + ",!andN" + and + "), rig_edge2(" + vsp[j] + ",!andN" + and
							+ "), ";
					rew.add(current);
					rew.add(vsp[j]);
					rew.add("!andN" + and);
					current = "!andN" + and;
					and++;
				}
			}
			for (String s : split1) {
				if (!s.contains(")")) {
					heads.add(s + ")");
					s = s + ")";
				} else {
					heads.add(s.replace(").", ")"));
					s = s.replace(").", ")");
				}
				String[] v1 = s.split("\\(");
				String[] v2 = v1[1].split("\\)");
				String[] var = v2[0].split(",");
				for (String ss : var) {
					vars.add(ss);
				}
			}
			for (String s : split1) {
				if (!s.contains(")")) {
					// heads.add(s+")");
					s = s + ")";
				} else {
					// heads.add(s.replace(".", ""));
					s = s.replace(").", ")");
				}
				// System.out.println(s);
				// System.out.println(vars.toString());
				String k = "";
				k = s.replace("(", "_p(");
				k = k.replace(")", ",?provH" + "" + h + ")");
				// System.out.println(k);
				r2 = k.replaceAll("!", "?").replaceAll(" ", "");
				r2 = r2.replace("?provH", "!provH");
				rew.add("?provH" + "" + h);
				zew.add("?provH" + "" + h);
				// System.out.println(r2);
				r3h = r3h + "idb_edge(" + current + ",?provH" + "" + h + "), ";
				r3b = r3b + (k.replaceAll("!", "?").replaceAll(" ", "") + ", ");
				r2 = r2 + " :- " + "r_" + ruleCount + "("
						+ vars.toString().replace("[", "").replace("]", "").replaceAll(" ", "").replaceAll("!", "?")
						+ "," + v + ").";
				r2 = r2.replaceAll(",\\)", "\\)");
				fw.write(r2 + "\n");
				// System.out.println(r2);
				h++;
			}

			// System.out.println(r3h);
			r1 = "r_" + ruleCount + "(" + vars.toString().replace("[", "").replace("]", "").replaceAll(" ", "") + ","
					+ v + ") :- " + rb;
			r1 = r1.replaceAll(",\\)", "\\)").replace(".,", ".");
			r2 = r2 + " :- " + "r_" + ruleCount + "("
					+ vars.toString().replace("[", "").replace("]", "").replaceAll(" ", "").replaceAll("!", "?") + ","
					+ v + ").";
			r2 = r2.replaceAll(",\\)", "\\)");
			r3b = "r_" + ruleCount + "("
					+ vars.toString().replace("[", "").replace("]", "").replaceAll(" ", "").replaceAll("!", "?") + ","
					+ v + "), " + r3b;
			r3b = r3b.replaceAll(",\\)", "\\)");
			String rews = "r" + ruleCount + "" + "("
					+ rew.toString().replace("[", "").replace("]", "").replaceAll(" ", "") + ")";
			String r5 = rews + " :- " + r3b + ".";
			r5 = r5.replace(", .", ".");
			r5= r5.replace(").", "), iterate(?provH0).");
			fw.write(r5 + "\n");
			// System.out.println(rews);
			r3 = r3h + " :- " + r3b + ".";
			r3 = r3.replace(", .", ".");
			r3 = r3.replace(",  :-", " :-");
			String[] heads = r3h.split(", ");
			String r4 = "";
			for (String a : heads) {
				// System.out.println(a);
				r4 = r4 + a + ",";
				
				
				// System.out.println(r4);
			}
			r4= r4+" :- "+ rews;
			//for (String k : zew) {
				String r7=r4;
			r7=r7+".";
			r7 = r7.replaceAll("!", "?").replace(", :-"," :-");
			//System.out.println(r4.toString());
			fw.write(r7 + "\n");
			//}
			fw.write(r1 + "\n");
			// fw.write(r3+"\n");
			// System.out.println(r1);
			// System.out.println(r2);
			// System.out.println(r3);
			ruleCount++;
		}
		y.close();
		for (String s : bodies) {
			String[] pred = s.split("\\(");
			Boolean match = false;
			for (String k : heads) {
				String[] pred2 = k.split("\\(");
				 System.out.println(pred[1]);
				if ((pred2[0].replace(" ", "")).equals(pred[0].replace(" ", ""))) {
					match = true;
					// System.out.println("yessss");
				}
			}
			if (!match) {
				String rule = "edb(!prov), " + pred[0] + "_p(" + pred[1].replaceAll("\\)", ",!prov\\)") + " :- " + s
						+ ".";
				rule = rule.replaceAll("  ", " ");
				 System.out.println(rule);
				fw.write(rule + "\n");
			}
		}
		fw.flush();
		fw.close();
	}

}
