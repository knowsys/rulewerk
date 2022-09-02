package org.semanticweb.rulewerk.examples;

import java.io.BufferedWriter;

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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.semanticweb.rulewerk.parser.RuleParser;

public class RulesCounter {
	static HashMap<String, ArrayList<Double>> ali = new HashMap<String, ArrayList<Double>>();

	public static void main(String[] args) throws IOException {
		File x3 = new File("/mnt/c/Users/Ali Elhalawati/files/el/aaa.txt");
		Scanner y3 = new Scanner(x3);
		int i=0;
		int summo=0;
		while (y3.hasNextLine()) {
			String xx = y3.nextLine();
			//if (y3.hasNextLine()) {
			//String xy = y3.nextLine();
			//}
			//System.out.println(xx);
			//System.out.println(xy);
			if (xx.contains("using rule")) {
				String xy = y3.nextLine();
				String[] m = xx.split("using rule ");
				String[] k = xy.split("Total runtime ");
				i++;
				String[] s = k[1].split(",");

				if (ali.keySet().contains(m[1])) {
					if (s[0].contains("ms")) {
						String num = s[0].replaceAll("ms", "");
						ali.get(m[1]).add(Double.parseDouble(num) / 1000);
					} else {
						String num = s[0].replaceAll("sec", "");
						ali.get(m[1]).add(Double.parseDouble(num));
					}
				} else {
					ArrayList<Double> lol = new ArrayList<Double>();
					if (s[0].contains("ms")) {
						String num = s[0].replaceAll("ms", "");
						lol.add(Double.parseDouble(num) / 1000);
					} else {
						String num = s[0].replaceAll("sec", "");
						lol.add(Double.parseDouble(num));
					}
					ali.put(m[1], lol);
				}
				// System.out.println(m[1]);
			}else {
				if (xx.contains("Cardinality of __Generated__Head__")) {
					String[]ma=xx.split(": ");
					//System.out.println(ma[0]);
					//System.out.println(Integer.parseInt(ma[1]));
					summo+=(Integer.parseInt(ma[1]));
				}
			}
			// System.out.println(xx+" a7a "+xy);
		}
		y3.close();
		LinkedHashMap<String,Double>results= new LinkedHashMap<String,Double>();
		HashMap<String,Integer>results2= new HashMap<String,Integer>();
		double max =0;
		double sum2 =0;
		for (String k : ali.keySet()) {
			double sum =0;
		//	int count =0;
			for (Double d: ali.get(k)) {
				sum+=d;
				sum2+=d;
				//count++;
			}
			if (sum>max) {
				max = sum;
			}
			results.put(k, sum);
			results2.put(k, ali.get(k).size());
			//System.out.println(k + " -> " + ali.get(k));
		}
		PrintWriter bf = new PrintWriter(new File("/mnt/c/Users/Ali Elhalawati/files/el/ali.csv"));
		List<Map.Entry<String, Double>> entries =
				  new ArrayList<Map.Entry<String, Double>>(results.entrySet());
				Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
				  public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b){
				    return a.getValue().compareTo(b.getValue());
				  }
				});
				Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
				//String[]a= {"rule","time","no. of applications"};
				//bf.write(a.toString());
				bf.append("rule");
				bf.append(',');
				bf.append("time");
				bf.append(',');
				bf.append("number of applications");
				bf.append('\n');
				//bf.write("rule, time, no. of applications"+"\n");
				for (Map.Entry<String, Double> entry : entries) {
				  sortedMap.put(entry.getKey(), entry.getValue());
//				  bf.append(entry.getKey());
//				  bf.append(',');
//				  bf.append(entry.getValue().toString());
//				  bf.append("sec");
//				  bf.append(',');
//				  bf.append(results2.get(entry.getKey()).toString()+" applications");
//				  bf.append("\n");
				 bf.write("\""+entry.getKey()+"\""+','+entry.getValue()+"sec"+','+results2.get(entry.getKey())+'\n');
				  
				}
				bf.append("Total time is "+sum2+" sec"+'\n');
				bf.flush();
				bf.close();
				System.out.println(summo);
				//ExamplesUtils.exportQueryAnswersToCSV(null, null, i);
	}
	

}
