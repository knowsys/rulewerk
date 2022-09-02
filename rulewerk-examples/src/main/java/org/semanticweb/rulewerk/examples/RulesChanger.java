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
import java.util.HashMap;
import java.util.Scanner;

public class RulesChanger {
	final static String baseDir = "C:\\Users\\Ali Elhalawati\\files\\deep";
	public static void main (String[]args) throws IOException {
		
		File x2 = new File(baseDir + "\\rules.txt");
		FileWriter fw = new FileWriter(new File(baseDir + "/rules_ex.txt"));
		Scanner y2 = new Scanner(x2);
		int i = 0;
		while (y2.hasNextLine()) {
			HashMap<String, String>av= new HashMap<String, String>();
			String a = y2.nextLine();
			String[]h=a.split(":-");
			String[]hf2=h[1].split("\\(");
			String[]hf3 =hf2[1].split("\\)");
			String[]hf4=hf3[0].split(",");
			String []k= h[0].split(", ");
			for (String g:k) {
				String m = g;
			String[]h2=g.split("\\(");
			String[]h3 =h2[1].split("\\)");
			String[]h4=h3[0].split(",");
			for (String s:h4) {
				System.out.println(h[1]);
				boolean found =false;
			for (String s2:hf4) {
				System.out.println(s2);
				if (s.equals(s2)) {
				//	System.out.println(s2);
					found = true;
				//	System.out.println(s);
					//System.out.println(s2);
				}
			}
				if (!found) {
					if (!av.containsKey(s)) {
					g=g.replace(s, "a"+i);
				av.put(s, "a"+i);
					i++;
				}else {
					g=g.replace(s, av.get(s));
				}
				//System.out.println(av.toString());
			
			}
			//System.out.println(g);
			}
			a=a.replace(m, g);
			}
			fw.write(a+"\n");
			fw.flush();
		}
		y2.close();
//		File x21 = new File(baseDir + "\\results.txt");
//		Scanner y21 = new Scanner(x21);
//		//int i = 0;
//		while (y21.hasNextLine()) {
//			String a = y21.nextLine();
//			af.add(a);
//			//i++;
//			//kb.addStatement(RuleParser.parseFact("hospital_p(" + a + ",a" + i + ")."));
//			//kb.addStatement(RuleParser.parseFact("edb(" + "a" + i + ")."));
//			//i++;
//		}
	//	y21.close();
		
//		for (String s:av.keySet()) {
//			if (!af.contains(s)) {
//				fw.write(av.get(s)+" \n");
//			}
//		}
		
		fw.close();

	}
	}
