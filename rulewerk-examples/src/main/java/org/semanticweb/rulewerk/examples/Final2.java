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
import java.util.Random;

public class Final2 {
	final static String baseDir = "C:\\Users\\Ali Elhalawati\\files\\reachability";
	public static void main (String[]args) throws IOException {
		HashSet<String>al= new HashSet<String>();
		int a=20;
		double b = 1.7;
		int d= (int) Math.pow(a, b);
		System.out.println(d);
		FileWriter fw = new FileWriter(new File(baseDir + "/edge3.txt"));
		Random rand = new Random();
		for (int i=d;i>0;i--) {
		int n1 = rand.nextInt(a);
		int n2 = rand.nextInt(a);
		if (n1!=0&&n2!=0&&!al.contains("edge("+n1+","+n2+")")&&n1!=n2) {
			fw.write("edge("+n1+","+n2+")."+"\n");
			al.add("edge("+n1+","+n2+")");
			//System.out.println("edge("+n1+","+n2+").");
		}
		}
		fw.close();
	}

}
