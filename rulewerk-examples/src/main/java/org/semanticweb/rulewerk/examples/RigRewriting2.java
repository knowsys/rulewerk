package org.semanticweb.rulewerk.examples;

/*-
 * #%L
 * Rulewerk Examples
 * %%
 * Copyright (C) 2018 - 2022 Rulewerk Developers
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

public class RigRewriting2 {
	final static String baseDir = "C:\\Users\\Ali Elhalawati\\files\\bench\\rvcheck";
	static HashSet<String> edbs = new HashSet<String>();
	static HashSet<String> heads = new HashSet<String>();
	static HashSet<String> bodies = new HashSet<String>();
	static LinkedHashSet<String> graph = new LinkedHashSet<String>();
	static LinkedHashSet<String> rules = new LinkedHashSet<String>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		edbs.add("opSucc");
		edbs.add("Certificate");
		edbs.add("Verify");
		edbs.add("Check");
		int i = 0;
		File f = new File(baseDir + "\\rules.small.dl");
		FileWriter fw = new FileWriter(new File(baseDir + "/s_rules.txt"));
		Scanner y = new Scanner(f);
		int ruleCount = 0;
		int gg=0;
		while (y.hasNextLine()) {
			String hh="";
			String x =y.nextLine();
			gg=0;
			String[] split = x.split(":-");
			String[] split2 = split[1].split("\\),");
			String[] split1 = split[0].split("\\),");
			LinkedHashSet<String> vars = new LinkedHashSet<String>();
			LinkedHashSet<String> rew = new LinkedHashSet<String>();
			LinkedHashSet<String> bprov = new LinkedHashSet<String>();
			
			String rb = "";
			String rh = "";
			String r1 = "";
			String r2 = "";
			String v = "";
			String r3 = "";
			String r3h = "";
			String r3b = "";
			//rh=x;
			LinkedHashSet<String> zew = new LinkedHashSet<String>();
			int and = 0;
			int h = 0;
			String current = "";
			for (String s : split2) {
				s=s.replace(".", "").replace(")", "");			
				 String[] sp= s.split("\\(");
				 sp[0]=sp[0].replaceAll("\\s","");
				 System.out.println(s);
				 if (!(edbs.contains(sp[0]))) {
					 System.out.println(s);
				//	 System.out.println(x);
					rh=rh+  sp[0]+"_p"+"("+sp[1]+", ?prov"+gg+"), ";
					bprov.add("?prov" + gg);
					gg++;
					s = s + ")";
					
				 }else {
					 //s = s + ")";
					 s=s.replace(" )",")");
					 rh=rh+s+"), ";
				 }
			//	s = s.replace("(", "_p(");
				//s = s.replace(")", ",?prov" + "" + i + ")");
				//v = v + "?prov" + "" + i + ",";
//				if (!s.contains(").")) {
//					rb = rb + s.replaceAll(" ", "") + ", ";
//				} else {
//					rb = rb + s.replaceAll(" ", "");
//				}
			}
			rh = rh+".";
			rh = rh.replace(", .",".").replace(" , ",", ").replace(" )", ")");
			rh = rh.replace("  ", " ");
			//System.out.println(rh);
			//hh=x;
			//System.out.println(bprov);
			bprov.add("r"+i);
			if (bprov.size() == 1) {
				r3h = "rig_edge1(r"+i+",!andN), rig_edge2(r"+i+",!andN), ";
				current = "!andN";
				rew.add("r"+i);
				//rew.add(current);
			} else {
				String vs = bprov.toString().replace("[", "").replace("]", "");
				String[] vsp = vs.split(", ");
				current = vsp[0];
				rew.add(vsp[0]);
				for (int j = 1; j < vsp.length; j++) {
					r3h = r3h + "rig_edge1(" + current + ",!andN" + and + "), rig_edge2(" + vsp[j] + ",!andN" + and
							+ "), ";
					//rew.add(current);
					rew.add(vsp[j]);
					//rew.add("!andN" + and);
					current = "!andN" + and;
					and++;
				}
			}
			
			for (String s : split1) {
				if (!s.contains(")")) {
					heads.add(s + ")");
					s = s + ")";
				} else {
					heads.add(s.replace(".", ""));
					s = s.replace(".", "");
				}
				String[] v1 = s.split("\\(");
				String[] v2 = v1[1].split("\\)");
				String[] var = v2[0].split(",");
				for (String ss : var) {
					vars.add(ss);
				}
			}
			for (String s : split1) {
				// System.out.println(s);
				// System.out.println(vars.toString());
				String k = "";
				k = s.replace("(", "_p(");
				k = k.replace(")", ",?provH" + "" + h + ")");
				rh=k.replace("?provH", "!provH")+":- "+rh;
				r2 = k.replaceAll("!", "?").replaceAll(" ", "");
				r2 = r2.replace("?provH", "!provH");
				rew.add("?provH" + "" + h);
				zew.add("?provH" + "" + h);
				// System.out.println(r2);
				r3h = r3h + "idb_edge(" + current + ",?provH" + "" + h + "), ";
				r3b = r3b + (k.replaceAll("!", "?").replaceAll(" ", "") + ", ");
				//r2 = r2 + " :- " + "r_" + ruleCount + "("
					//	+ vars.toString().replace("[", "").replace("]", "").replaceAll(" ", "").replaceAll("!", "?")
						//+ "," + v + ").";
				r2 = r2.replaceAll(",\\)", "\\)");
				//fw.write(r2 + "\n");
				// System.out.println(r2);
				h++;
			}

			 System.out.println(rh);
			r1 = r2+ " :- " + rb;
			r1 = r1.replaceAll(",\\)", "\\)").replace(".,", ".");
			rh= rh.replace(".", ", Rule(r"+ruleCount+").");
			fw.write(rh + "\n");
			//r2 = r2 + " :- " + "r_" + ruleCount + "("
				//	+ vars.toString().replace("[", "").replace("]", "").replaceAll(" ", "").replaceAll("!", "?") + ","
					//+ v + ").";
		//	r2 = r2.replaceAll(",\\)", "\\)");
			r3b = "r_" + ruleCount + "("
					+ vars.toString().replace("[", "").replace("]", "").replaceAll(" ", "").replaceAll("!", "?") + ","
					+ v + "), " + r3b;
			r3b = r3b.replaceAll(",\\)", "\\)");
			//System.out.println(rew.toString());
			String rews = "r" + rew.size() + "" + "("
					+ rew.toString().replace("[", "").replace("]", "").replaceAll(" ", "") + ")";
			i++;
			String r5 = rews;
			r3b=rews;
			//r5 = r5.replace(", .", ".");
		for (String r : bprov) {
			r5= r5 + ", iterate("+r+")";
		}
			r5 = r5 + " :- " + rh.replace(" :-", ",").replace(".", ", iterate(?provH0).").replace("!provH", "?provH");
			graph.add(rews);
			fw.write(r5 + "\n");
			//System.out.println(r5);
			r3 = r3h + " :- " + r3b + ".";
			r3 = r3.replace(", .", ".");
			r3 = r3.replace(",  :-", " :-");
			if (!rules.contains(r3)) {
			//	System.out.println(r3);
			fw.write(r3+ "\n");
			rules.add(r3);
			}
		//	String[] heads = r3h.split(", ");
	//		String r4 = "";
//			for (String a : heads) {
//				// System.out.println(a);
//				r4 = r4 + a + ",";
//				
//				
//				// System.out.println(r4);
//			}
//			r4= r4+" :- "+ rews;
			//for (String k : zew) {
			//	String r7=r4+", iterate("+k+")";
		//	r7=r7+".";
		//	r7 = r7.replaceAll("!", "?").replace(", :-"," :-");
			//System.out.println(r4.toString());
			//System.out.println(r7.toString());
			//fw.write(r7 + "\n");
		//	}
			//System.out.println(r1.toString());
			
			// fw.write(r3+"\n");
			// System.out.println(r1);
			// System.out.println(r2);
			// System.out.println(r3);
			ruleCount++;
			//System.out.println(graph.toString());
		}
		y.close();
		fw.flush();
		fw.close();
		}
	}

